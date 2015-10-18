
package com.synova.realestate.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.synova.realestate.R;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.LocationService;
import com.synova.realestate.base.MainActivity;
import com.synova.realestate.base.RealEstateApplication;
import com.synova.realestate.base.SubscriberImpl;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.models.MapResponseEnt;
import com.synova.realestate.models.Publisher;
import com.synova.realestate.models.eventbus.ChangeDialogFilterValuesInTabLocationEvent;
import com.synova.realestate.models.eventbus.NavigationItemSelectedEvent;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.network.model.AdsInfoEnt;
import com.synova.realestate.network.model.MapRequestEnt;
import com.synova.realestate.network.model.PublisherRequestEnt;
import com.synova.realestate.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import rx.Subscription;

/**
 * Created by ducth on 6/16/15.
 */
public class TabLocationFragment extends BaseFragment implements OnMapReadyCallback,
        View.OnClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraChangeListener {

    private static final int BOUNDS_PADDING = 100;
    private SupportMapFragment mapFragment;
    private GoogleMap map;

    private Circle selectedMarkerCircle;
    private Polygon selectedMarkerPolygon;

    private ImageView btnMenu;

    private ViewGroup groupDetailBottom;
    private ImageView ivThumbnail;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvPrice;

    private Constants.NetworkLoadingState loadingState = Constants.NetworkLoadingState.NONE;

    private boolean isFirstTimeLoadData = false;

    private Map<Constants.ElementType, List<Marker>> markers = new HashMap<>();

    /**
     * Latitude = Y-axis, Longitude = X-axis.
     */
    // private LatLng currentMin = new LatLng(48.9306, 2.1475);
    // private LatLng currentMax = new LatLng(48.7924, 2.4963);
    private boolean isTouchingMap;

    private boolean isForceMoveMap;

    private ProgressBar progressBar;

    private int currentZoomLevel = 14;

    /** Set true to set my location is in Paris (for testing purpose only) */
    // private boolean isMyLocationParis = true;
    private Subscription subscription;

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_location, container, false);

        setupMap();
        setupGroupDetailBottom();

        btnMenu = (ImageView) rootView.findViewById(R.id.tab_location_btnMenu);
        btnMenu.setOnClickListener(this);

        progressBar = (ProgressBar) rootView.findViewById(R.id.tab_location_progressBar);

        TouchableWrapper touchableWrapper = new TouchableWrapper(getActivity());
        touchableWrapper.addView(rootView);

        rootView = touchableWrapper;

        return rootView;
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(
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
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (map != null && markers.size() == 0
                && loadingState == Constants.NetworkLoadingState.NONE) {
            getMap();
        }
    }

    @Override
    protected void onPageSelected(int position) {
        super.onPageSelected(position);
        LocationService.getInstance().checkLocationSettings(activity, false);
    }

    private void getMap() {
        loadingState = Constants.NetworkLoadingState.LOADING;

        progressBar.setVisibility(View.VISIBLE);

        final MapRequestEnt mapRequestEnt = new MapRequestEnt();
        mapRequestEnt.xMin = RealEstateApplication.currentMin.longitude;
        mapRequestEnt.yMin = RealEstateApplication.currentMin.latitude;
        mapRequestEnt.xMax = RealEstateApplication.currentMax.longitude;
        mapRequestEnt.yMax = RealEstateApplication.currentMax.latitude;

        subscription = NetworkService.getMap(mapRequestEnt)
                .subscribe(new SubscriberImpl<List<MapResponseEnt>>() {
                    @Override
                    public void onNext(List<MapResponseEnt> mapResponseEnts) {
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
                                for (Constants.ElementType key : MainActivity.markersVisibility
                                        .keySet()) {
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
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingState = Constants.NetworkLoadingState.NONE;
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void calculateNewMinMaxPoints(LatLng center, LatLngBounds bounds) {
        double xMin = bounds.southwest.longitude;
        double yMin = bounds.southwest.latitude;

        double xMax = bounds.northeast.longitude;
        double yMax = bounds.northeast.latitude;

        RealEstateApplication.currentMin = new LatLng(yMin, xMin);
        RealEstateApplication.currentMax = new LatLng(yMax, xMax);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnCameraChangeListener(this);

        moveCameraToLocation(RealEstateApplication.currentLocation);
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
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), currentZoomLevel),
                    new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isForceMoveMap = false;
                                }
                            }, 2000);
                        }

                        @Override
                        public void onCancel() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isForceMoveMap = false;
                                }
                            }, 2000);
                        }
                    });
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), currentZoomLevel));
            isForceMoveMap = false;
        }
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

        if (mapResponseEnt.zoneGeom != null) {
            LatLng[] polygon = Util.convertZoneGeomToLatLngs(mapResponseEnt.zoneGeom);
            addSelectedMarkerPolygon(polygon);
        }

        switch (mapResponseEnt.elementType) {
            case BIEN:
                AdsInfoEnt adsInfoEnt = new AdsInfoEnt();
                adsInfoEnt.propertyTypeS = Constants.PropertyType.APPARTEMENT.getName();
                adsInfoEnt.xLocalisation = marker.getPosition().longitude;
                adsInfoEnt.yLocalisation = marker.getPosition().latitude;

                subscription = NetworkService.getAdsInfo(adsInfoEnt).subscribe(
                        new SubscriberImpl<List<AdsInfoResponseEnt>>() {
                            @Override
                            public void onNext(List<AdsInfoResponseEnt> adsInfoResponseEnts) {
                                if (adsInfoResponseEnts.size() == 0) {
                                    return;
                                }

                                AdsInfoResponseEnt adsInfoResponseEnt = adsInfoResponseEnts
                                        .get(0);

                                if (!Util.isNullOrEmpty(adsInfoResponseEnt.imageUrl)) {
                                    ivThumbnail.setImageURI(Uri
                                            .parse(adsInfoResponseEnt.imageUrl));
                                }

                                tvTitle.setText(adsInfoResponseEnt.title);
                                tvPrice.setText(adsInfoResponseEnt.mminMaxPrice);

                                String description = String.format(activity
                                        .getString(R.string.list_item_description_template),
                                        adsInfoResponseEnt.roomNumber,
                                        adsInfoResponseEnt.surface, adsInfoResponseEnt.distance);

                                tvDescription.setText(description);

                                groupDetailBottom.setVisibility(View.VISIBLE);
                                groupDetailBottom.setTag(adsInfoResponseEnt.id);
                            }
                        });
                break;
            case AGENCE:
            case PARTICULIER:
            case NOTAIRE:
                PublisherRequestEnt publisherRequestEnt = new PublisherRequestEnt();
                publisherRequestEnt.publisherId = mapResponseEnt.id;
                publisherRequestEnt.propertyTypeS = Constants.PropertyType.APPARTEMENT.getName();
                publisherRequestEnt.xLocalisation = marker.getPosition().longitude;
                publisherRequestEnt.yLocalisation = marker.getPosition().latitude;

                subscription = NetworkService.getListPublisher(publisherRequestEnt).subscribe(
                        new SubscriberImpl<List<Publisher>>() {
                            @Override
                            public void onNext(List<Publisher> publishers) {
                                if (publishers.size() == 0) {
                                    return;
                                }

                                Publisher publisher = publishers.get(0);

                                if (!Util.isNullOrEmpty(publisher.logoUrl)) {
                                    ivThumbnail.setImageURI(Uri.parse(publisher.logoUrl));
                                }

                                tvTitle.setText(publisher.name);
                                tvPrice.setText(Util.formatPriceNumber(publisher.amount) + "€");

                                tvDescription.setText(publisher.address);

                                groupDetailBottom.setVisibility(View.VISIBLE);
                                groupDetailBottom.setTag(publisher.nbAds);
                            }
                        });
                break;
        }

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
                activity.showDetailActivity(adId);
                break;
        }
    }

    private void setMarkersVisible(Constants.ElementType type, boolean isChecked) {
        List<Marker> elementTypeMarkers = markers.get(type);
        if (elementTypeMarkers == null) {
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

    float limitX, limitY;

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (!isTouchingMap
                && !isForceMoveMap
                && (checkDragDistanceValid(cameraPosition.target) || cameraPosition.zoom != currentZoomLevel)) {
            LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

            RealEstateApplication.currentLocation.setLatitude(cameraPosition.target.latitude);
            RealEstateApplication.currentLocation.setLongitude(cameraPosition.target.longitude);
            calculateNewMinMaxPoints(cameraPosition.target, bounds);

            float[] distanceX = new float[1];
            Location.distanceBetween(bounds.northeast.latitude, bounds.northeast.longitude,
                    bounds.northeast.latitude, bounds.southwest.longitude, distanceX);

            float[] distanceY = new float[1];
            Location.distanceBetween(bounds.northeast.latitude, bounds.northeast.longitude,
                    bounds.southwest.latitude, bounds.northeast.longitude, distanceY);

            limitX = distanceX[0] * 0.1f;
            limitY = distanceY[0] * 0.1f;

            getMap();
        }
    }

    private boolean checkDragDistanceValid(LatLng center) {
        Location newLocation = new Location("manual");
        newLocation.setLatitude(center.latitude);
        newLocation.setLongitude(center.longitude);

        float distance = RealEstateApplication.currentLocation.distanceTo(newLocation);
        return distance > limitX || distance > limitY;
    }

    public void onEventMainThread(NavigationItemSelectedEvent event) {
        setMarkersVisible(event.type, event.isChecked);
    }

    public void onEventMainThread(ChangeDialogFilterValuesInTabLocationEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        getMap();
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
