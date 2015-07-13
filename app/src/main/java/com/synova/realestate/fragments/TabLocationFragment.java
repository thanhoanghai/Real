
package com.synova.realestate.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.google.android.gms.maps.model.CameraPosition;
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
        View.OnClickListener, GoogleMap.OnCameraChangeListener {

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

    private boolean isFirstTimeLoadData = true;

    private Map<Constants.ElementType, List<Marker>> markers = new HashMap<>();

    /**
     * Latitude = Y-axis, Longitude = X-axis.
     */
    private LatLng currentMin = new LatLng(48.9306, 2.1475);
    private LatLng currentMax = new LatLng(48.7924, 2.4963);
    private boolean isTouchingMap;

    private boolean isForceMoveMap;

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_location, container, false);

        setupMap();
        setupGroupDetailBottom();

        btnMenu = (ImageView) rootView.findViewById(R.id.tab_location_btnMenu);
        btnMenu.setOnClickListener(this);

        TouchableWrapper touchableWrapper = new TouchableWrapper(getActivity());
        touchableWrapper.addView(rootView);

        rootView = touchableWrapper;

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
        groupDetailBottom.setOnClickListener(this);
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

        if (map != null && markers.size() == 0
                && loadingState == Constants.NetworkLoadingState.NONE) {
            getMap();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            stopRequestLocationUpdate();
        }
    }

    private void getMap() {
        loadingState = Constants.NetworkLoadingState.LOADING;

        final ProgressDialog waitDialog = new ProgressDialog(activity);
        waitDialog.setMessage("Loading...");
        waitDialog.setCancelable(false);
        waitDialog.show();

        final MapRequestEnt mapRequestEnt = new MapRequestEnt();
        mapRequestEnt.deviceId = RealEstateApplication.deviceId;
        mapRequestEnt.xMin = currentMin.longitude;
        mapRequestEnt.yMin = currentMin.latitude;
        mapRequestEnt.xMax = currentMax.longitude;
        mapRequestEnt.yMax = currentMax.latitude;
        mapRequestEnt.adsOffset = 100;
        mapRequestEnt.surfaceMinS = 0 + "";
        mapRequestEnt.surfaceMaxS = 2000 + "";

        NetworkService.getMap(mapRequestEnt, new Callback<List<MapResponseEnt>>() {
            @Override
            public void success(List<MapResponseEnt> mapResponseEnts, Response response) {
                markers.clear();
                map.clear();

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
                        for (Constants.ElementType key : MainActivity.markersVisibility.keySet()) {
                            if (!MainActivity.markersVisibility.get(key)) {
                                setMarkersVisible(key, false);
                            }
                        }

                        if (isFirstTimeLoadData) {
                            isFirstTimeLoadData = false;
                            moveCameraToBound(latLngs, true);
                        }
                    }
                }

                loadingState = Constants.NetworkLoadingState.LOADED;
                waitDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                loadingState = Constants.NetworkLoadingState.NONE;
                waitDialog.dismiss();
            }
        });
    }

    private void calculateNewMinMaxPoints(LatLng center) {
        double xMin = center.longitude - (currentMax.longitude - currentMin.longitude) / 2;
        double yMin = center.latitude - (currentMax.latitude - currentMin.latitude) / 2;
        currentMin = new LatLng(yMin, xMin);

        double xMax = center.longitude + (currentMax.longitude - currentMin.longitude) / 2;
        double yMax = center.latitude + (currentMax.latitude - currentMin.latitude) / 2;
        currentMax = new LatLng(yMax, xMax);
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
        map.setOnCameraChangeListener(this);

        getMap();
    }

    private void moveCameraToLocation(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 15));
    }

    private void moveCameraToBound(List<LatLng> latLngs, boolean animate) {
        isForceMoveMap = true;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();

        if (animate) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 14),
                    new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isForceMoveMap = false;
                                }
                            }, 1000);
                        }

                        @Override
                        public void onCancel() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isForceMoveMap = false;
                                }
                            }, 1000);
                        }
                    });
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 14));
            isForceMoveMap = false;
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
        // if (currentLocation == null) {
        // // moveCameraToLocation(location);
        // }
        // currentLocation = location;
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
        groupDetailBottom.setTag(mapResponseEnt.id);

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

        return true;
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
            case R.id.tab_location_groupDetailBottom:
                int adId = (int) groupDetailBottom.getTag();
                // activity.showDetailActivity(adId);
                break;
        }
    }

    private void setMarkersVisible(Constants.ElementType type, boolean isChecked) {
        List<Marker> elementTypeMarkers = markers.get(type);
        if (elementTypeMarkers == null){
            return;
        }
        
        for (Marker childMarker : elementTypeMarkers) {
            childMarker.setVisible(isChecked);
        }

        if (selectedMarkerCircle != null) {
            selectedMarkerCircle.setVisible(false);
        }
        if (selectedMarkerPolygon != null) {
            selectedMarkerPolygon.setVisible(false);
        }
    }

    public void onEventMainThread(NavigationItemSelectedEvent event) {
        setMarkersVisible(event.type, event.isChecked);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (currentLocation == null) {
            currentLocation = new Location("manual");
            currentLocation.setLatitude(cameraPosition.target.latitude);
            currentLocation.setLongitude(cameraPosition.target.longitude);
            return;
        }

        if (!isTouchingMap && !isForceMoveMap
                && checkDragDistanceValid(cameraPosition.target)) {
            currentLocation = new Location("manual");
            currentLocation.setLatitude(cameraPosition.target.latitude);
            currentLocation.setLongitude(cameraPosition.target.longitude);
            calculateNewMinMaxPoints(cameraPosition.target);
            getMap();
        }
    }

    private boolean checkDragDistanceValid(LatLng center) {
        Location newLocation = new Location("manual");
        newLocation.setLatitude(center.latitude);
        newLocation.setLongitude(center.longitude);

        return currentLocation.distanceTo(newLocation) > 1500;
    }

    private class TouchableWrapper extends FrameLayout {

        public TouchableWrapper(Context context) {
            super(context);
        }

        public TouchableWrapper(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TouchableWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouchingMap = true;
                    break;
                case MotionEvent.ACTION_UP:
                    isTouchingMap = false;
                    break;
            }

            return super.dispatchTouchEvent(ev);
        }

    }
}
