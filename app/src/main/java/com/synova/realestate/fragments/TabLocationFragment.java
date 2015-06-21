
package com.synova.realestate.fragments;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.synova.realestate.R;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.MainActivity;
import com.synova.realestate.models.House;
import com.synova.realestate.utils.Util;

import java.util.Random;

/**
 * Created by ducth on 6/16/15.
 */
public class TabLocationFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        View.OnClickListener {

    private RetainMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Location lastKnownLocation;
    private Location currentLocation;

    private Circle selectedMarkerCircle;

    private ImageView btnMenu;

    private ViewGroup groupDetailBottom;
    private ImageView ivThumbnail;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvPrice;

    private Constants.NetworkLoadingState loadingState = Constants.NetworkLoadingState.NONE;

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_location, container, false);

        setupMap();
        setupGroupDetailBottom();

        btnMenu = (ImageView) rootView.findViewById(R.id.tab_location_btnMenu);
        btnMenu.setOnClickListener(this);

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
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
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
        stopRequestLocationUpdate();
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

    @Override
    public void onConnected(Bundle bundle) {
        startRequestLocationUpdate();

        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentLocation == null) {
            moveCameraToLocation(location);
        }
        currentLocation = location;
        if (loadingState != Constants.NetworkLoadingState.LOADING
                && loadingState != Constants.NetworkLoadingState.LOADED) {
            loadingState = Constants.NetworkLoadingState.LOADING;
            createMockData(location);
        }
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
        addSelectedMarkerCircle(marker.getPosition().latitude, marker.getPosition().longitude, 500);

        groupDetailBottom.setVisibility(View.VISIBLE);
        ImageLoader
                .getInstance()
                .displayImage(
                        "http://images.travelpod.com/tripwow/photos2/ta-02e0-d424-1301/alimetov-yambol-bulgaria+13162716848-tpweb11w-19996.jpg",
                        ivThumbnail);
        tvTitle.setText(marker.getTitle());
        tvPrice.setText("750€");
        String description = "2 piece(s) | 35 m2 | 300 m";
        tvDescription.setText(Util.formatSurfaceSuperScriptText(description));

        return false;
    }

    private Marker createMarker(double lat, double lng, String title, House.HouseType houseType) {
        Bitmap icon = Util.createMarkerBitmapWithBadge(activity, houseType, 999);
        return map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(new LatLng(lat, lng))
                .anchor(0.2f, 0.9f)
                .title(title));
    }

    private void addSelectedMarkerCircle(double lat, double lng, double radius) {
        selectedMarkerCircle = map.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(radius)
                .fillColor(getResources().getColor(R.color.trans_cyan))
                .strokeWidth(0));
    }

    private void createMockData(Location loc) {
        int min = 10;
        int max = 100;

        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            int number = r.nextInt(max - min + 1) + min;
            if (i % 2 == 0) {
                number = -number;
            }
            double lat = loc.getLatitude() + number / 10E3;
            double lng = loc.getLongitude() + number / 10E3;

            createMarker(lat, lng, "House " + i, House.HouseType.BIEN);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab_location_btnMenu:
                ((MainActivity)activity).openDrawer();
                break;
        }
    }
}
