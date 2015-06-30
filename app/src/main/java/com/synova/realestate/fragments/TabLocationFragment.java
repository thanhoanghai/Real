
package com.synova.realestate.fragments;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.synova.realestate.R;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.MainActivity;
import com.synova.realestate.base.RealEstateApplication;
import com.synova.realestate.models.MapResponseEnt;
import com.synova.realestate.models.eventbus.NavigationItemSelectedEvent;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.network.model.MapRequestEnt;
import com.synova.realestate.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ducth on 6/16/15.
 */
public class TabLocationFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        View.OnClickListener {

    private static final int BOUNDS_PADDING = 100;
    private RetainMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Location lastKnownLocation;
    private Location currentLocation;

    private Circle selectedMarkerCircle;
    private Polygon selectedMarkerPolygon;

    private ImageView btnMenu;

    private ViewGroup groupDetailBottom;
    private ImageView ivThumbnail;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvPrice;

    private Constants.NetworkLoadingState loadingState = Constants.NetworkLoadingState.NONE;

    private Map<Constants.ElementType, List<Marker>> markers = new HashMap<>();

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_location, container, false);

        setupMap();
        setupGroupDetailBottom();

        btnMenu = (ImageView) rootView.findViewById(R.id.tab_location_btnMenu);
        btnMenu.setOnClickListener(this);

        if (loadingState != Constants.NetworkLoadingState.LOADING
                && loadingState != Constants.NetworkLoadingState.LOADED) {
            loadingState = Constants.NetworkLoadingState.LOADING;

            final MapRequestEnt mapRequestEnt = new MapRequestEnt();
            mapRequestEnt.deviceId = RealEstateApplication.deviceId;
            // mapRequestEnt.xMin = location.getLatitude();
            // mapRequestEnt.yMin = location.getLongitude();
            // mapRequestEnt.xMax = location.getLatitude() + 200/1E6;
            // mapRequestEnt.yMax = location.getLongitude() + 200/1E6;
            mapRequestEnt.xMin = 2.1475;
            mapRequestEnt.yMin = 48.9306;
            mapRequestEnt.xMax = 2.4963;
            mapRequestEnt.yMax = 48.7924;
            mapRequestEnt.adsOffset = 100;
            mapRequestEnt.surfaceMinS = 0 + "";
            mapRequestEnt.surfaceMaxS = 2000 + "";

            NetworkService.getMap(mapRequestEnt, new Callback<List<MapResponseEnt>>() {
                @Override
                public void success(List<MapResponseEnt> mapResponseEnts, Response response) {
                    if (mapResponseEnts != null && mapResponseEnts.size() > 0) {
                        List<LatLng> latLngs = new ArrayList<>();
                        for (MapResponseEnt mapResponseEnt : mapResponseEnts) {
                            if (mapResponseEnt.id != 0 && mapResponseEnt.pointGeom != null
                                    && mapResponseEnt.elementType != null) {
                                Marker marker = createMarker(mapResponseEnt);
                                latLngs.add(marker.getPosition());

                                List<Marker> elementTypeMarkers = markers
                                        .get(mapResponseEnt.elementType);
                                if (elementTypeMarkers == null) {
                                    elementTypeMarkers = new ArrayList<>();
                                    markers.put(mapResponseEnt.elementType, elementTypeMarkers);
                                }
                                elementTypeMarkers.add(marker);
                            }
                        }

                        if (latLngs.size() > 0) {
                            onEventMainThread(new NavigationItemSelectedEvent(
                                    ((MainActivity) activity).getGroupNavigationItems()
                                            .getCheckedRadioButtonId()));
                            moveCameraToBound(latLngs, true);
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }

        return rootView;
    }

    private void setupMap() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.INTERVAL);
        locationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mapFragment = (RetainMapFragment) getChildFragmentManager().findFragmentById(
                R.id.tab_location_mapFragment);
        mapFragment.getMapAsync(this);
    }

    private void setupGroupDetailBottom() {
        groupDetailBottom = (ViewGroup) rootView.findViewById(R.id.tab_location_groupDetailBottom);
        groupDetailBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivThumbnail = (ImageView) rootView.findViewById(R.id.tab_location_bottom_ivThumbnail);
        tvTitle = (TextView) rootView.findViewById(R.id.tab_location_bottom_tvTitle);
        tvDescription = (TextView) rootView.findViewById(R.id.tab_location_bottom_tvDescription);
        tvPrice = (TextView) rootView.findViewById(R.id.tab_location_bottom_tvPrice);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        googleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) activity).enableDrawer();

        if (!Util.isLocationEnabled(activity)) {
            // DialogUtils.showOpenLocationSettingDialog(activity);
        }
        if (googleApiClient.isConnected()) {
            startRequestLocationUpdate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            stopRequestLocationUpdate();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        // map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    private void moveCameraToLocation(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 15));
    }

    private void moveCameraToBound(List<LatLng> latLngs, boolean animate) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();

        if (animate) {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_PADDING));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_PADDING));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startRequestLocationUpdate();

        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentLocation == null) {
            // moveCameraToLocation(location);
        }
        currentLocation = location;
//        if (loadingState != Constants.NetworkLoadingState.LOADING
//                && loadingState != Constants.NetworkLoadingState.LOADED) {
//            loadingState = Constants.NetworkLoadingState.LOADING;
//
//            final MapRequestEnt mapRequestEnt = new MapRequestEnt();
//            mapRequestEnt.deviceId = RealEstateApplication.deviceId;
//            // mapRequestEnt.xMin = location.getLatitude();
//            // mapRequestEnt.yMin = location.getLongitude();
//            // mapRequestEnt.xMax = location.getLatitude() + 200/1E6;
//            // mapRequestEnt.yMax = location.getLongitude() + 200/1E6;
//            mapRequestEnt.xMin = 2.1475;
//            mapRequestEnt.yMin = 48.9306;
//            mapRequestEnt.xMax = 2.4963;
//            mapRequestEnt.yMax = 48.7924;
//            mapRequestEnt.adsOffset = 100;
//            mapRequestEnt.surfaceMinS = 0 + "";
//            mapRequestEnt.surfaceMaxS = 2000 + "";
//
//            NetworkService.getMap(mapRequestEnt, new Callback<List<MapResponseEnt>>() {
//                @Override
//                public void success(List<MapResponseEnt> mapResponseEnts, Response response) {
//                    if (mapResponseEnts != null && mapResponseEnts.size() > 0) {
//                        List<LatLng> latLngs = new ArrayList<>();
//                        for (MapResponseEnt mapResponseEnt : mapResponseEnts) {
//                            if (mapResponseEnt.id != 0 && mapResponseEnt.pointGeom != null
//                                    && mapResponseEnt.elementType != null) {
//                                Marker marker = createMarker(mapResponseEnt);
//                                latLngs.add(marker.getPosition());
//
//                                List<Marker> elementTypeMarkers = markers
//                                        .get(mapResponseEnt.elementType);
//                                if (elementTypeMarkers == null) {
//                                    elementTypeMarkers = new ArrayList<>();
//                                    markers.put(mapResponseEnt.elementType, elementTypeMarkers);
//                                }
//                                elementTypeMarkers.add(marker);
//                            }
//                        }
//
//                        if (latLngs.size() > 0) {
//                            onEventMainThread(new NavigationItemSelectedEvent(
//                                    ((MainActivity) activity).getGroupNavigationItems()
//                                            .getCheckedRadioButtonId()));
//                            moveCameraToBound(latLngs, true);
//                        }
//                    }
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//
//                }
//            });
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void startRequestLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                this);
    }

    private void stopRequestLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        groupDetailBottom.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (selectedMarkerCircle != null) {
            selectedMarkerCircle.remove();
        }
        if (selectedMarkerPolygon != null) {
            selectedMarkerPolygon.remove();
        }

        MapResponseEnt mapResponseEnt = RealEstateApplication.GSON.fromJson(marker.getSnippet(),
                MapResponseEnt.class);

        LatLng[] polygon = Util.convertZoneGeomToLatLngs(mapResponseEnt.zoneGeom);
        addSelectedMarkerPolygon(polygon);

        // addSelectedMarkerCircle(marker.getPosition().latitude, marker.getPosition().longitude,
        // 500);

        groupDetailBottom.setVisibility(View.VISIBLE);
        ImageLoader
                .getInstance()
                .displayImage(
                        "http://images.travelpod.com/tripwow/photos2/ta-02e0-d424-1301/alimetov-yambol-bulgaria+13162716848-tpweb11w-19996.jpg",
                        ivThumbnail);
        tvTitle.setText(marker.getTitle());
        tvPrice.setText("750€");
        String description = String.format(
                activity.getString(R.string.list_item_description_template), 2, 35, 300);
        tvDescription.setText(Html.fromHtml(description));

        return false;
    }

    private Marker createMarker(MapResponseEnt mapResponseEnt) {
        LatLng latLng = Util
                .convertPointGeomToLatLng(mapResponseEnt.pointGeom);
        String title = mapResponseEnt.adminName != null ? mapResponseEnt.adminName
                : "";

        Bitmap icon = null;
        if (mapResponseEnt.nbAds > 0) {
            icon = Util.createMarkerBitmapWithBadge(activity, mapResponseEnt.elementType,
                    mapResponseEnt.nbAds);
        } else {
            icon = Util.createMarkerBitmap(activity, mapResponseEnt.elementType);
        }

        return map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(latLng)
                .anchor(0.2f, 0.9f)
                .title(title)
                .snippet(RealEstateApplication.GSON.toJson(mapResponseEnt)));
    }

    private void addSelectedMarkerCircle(double lat, double lng, double radius) {
        selectedMarkerCircle = map.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(radius)
                .fillColor(getResources().getColor(R.color.trans_cyan))
                .strokeWidth(1)
                .strokeColor(getResources().getColor(R.color.trans_gray)));
    }

    private void addSelectedMarkerPolygon(LatLng[] latLngs) {
        selectedMarkerPolygon = map.addPolygon(new PolygonOptions()
                .add(latLngs)
                .fillColor(getResources().getColor(R.color.trans_cyan))
                .strokeWidth(1)
                .strokeColor(getResources().getColor(R.color.text_gray)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_location_btnMenu:
                ((MainActivity) activity).openDrawer();
                break;
        }
    }

    private void setMarkersVisible(Constants.ElementType visibleType) {
        for (Constants.ElementType type : markers.keySet()) {
            List<Marker> elementTypeMarkers = markers.get(type);
            for (Marker childMarker : elementTypeMarkers) {
                childMarker.setVisible(type == visibleType);
            }
        }

        if (selectedMarkerCircle != null) {
            selectedMarkerCircle.setVisible(false);
        }
        if (selectedMarkerPolygon != null) {
            selectedMarkerPolygon.setVisible(false);
        }
    }

    public void onEventMainThread(NavigationItemSelectedEvent event) {
        switch (event.checkedId) {
            case R.id.navigation_btnBien:
                setMarkersVisible(Constants.ElementType.BIEN);
                break;
            case R.id.navigation_btnAgence:
                setMarkersVisible(Constants.ElementType.AGENCE);
                break;
            case R.id.navigation_btnParticulier:
                setMarkersVisible(Constants.ElementType.PARTICULIER);
                break;
            case R.id.navigation_btnNotaire:
                setMarkersVisible(Constants.ElementType.NOTAIRE);
                break;
        }

    }

}
