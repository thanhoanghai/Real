
package com.synova.realestate.base;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.synova.realestate.R;
import com.synova.realestate.adapters.DetailDataAdapter;
import com.synova.realestate.adapters.DetailSlideShowAdapter;
import com.synova.realestate.customviews.AdsImageView;
import com.synova.realestate.customviews.CustomScrollView;
import com.synova.realestate.customviews.DividerDecoration;
import com.synova.realestate.fragments.RetainMapFragment;
import com.synova.realestate.models.DetailData;
import com.synova.realestate.utils.Util;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by ducth on 6/17/15.
 */
public class DetailActivity extends BaseActivity implements OnMapReadyCallback {

    private AdsImageView adsView;

    private ActionBar actionBar;

    private AutoScrollViewPager slideShowView;
    private CirclePageIndicator slideShowIndicator;
    private DetailSlideShowAdapter slideShowAdapter;

    private RecyclerView rvData;
    private DetailDataAdapter dataAdapter;

    private CustomScrollView scrollView;

    private RetainMapFragment mapFragment;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        adsView = (AdsImageView) findViewById(R.id.adsImageView);
        adsView.setAdsUrl("http://www.webbanner24.com/blog/wp-content/uploads/2014/09/Top-5-Reasons-Why-You-Need-Banner-Ads.jpg");
        adsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(adsView, "You've clicked ads banner!", Snackbar.LENGTH_SHORT).show();
            }
        });

        setupMap();
        setupActionBar();
        setupSlideShow();
        setupDataList();

        scrollView = (CustomScrollView) findViewById(R.id.detail_scrollView);
        scrollView.addInterceptScrollView(mapFragment.getView());

    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.mipmap.ic_launcher);
    }

    private void setupSlideShow() {
        slideShowView = (AutoScrollViewPager) findViewById(R.id.detail_slideShow);
        slideShowView.setInterval(5000);
        slideShowView.startAutoScroll();
        slideShowAdapter = new DetailSlideShowAdapter();
        slideShowView.setAdapter(slideShowAdapter);

        slideShowIndicator = (CirclePageIndicator) findViewById(R.id.detail_slideshow_indicator);
        slideShowIndicator.setViewPager(slideShowView);
        slideShowIndicator.setSnap(true);

        List<String> photoUrls = new ArrayList<>();
        photoUrls
                .add("http://imactoy.com/wp-content/uploads/2013/04/modern-homes-often-feature-furniture-with-concrete-details-and-structures-600x300.jpg");
        photoUrls
                .add("http://beacont.com/wp-content/uploads/2013/07/Clean-White-Bathroom-with-Gray-and-Purple-Accent-600x300.jpg");
        photoUrls
                .add("http://rumahinteriorminimalis.com/wp-content/uploads/2014/09/modern-sunroom-designs-covered-jessica-dauray-with-beige-tones-600x300.jpg");

        slideShowAdapter.setData(photoUrls);
    }

    private void setupMap() {
        mapFragment = (RetainMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.detail_mapFragment);
        mapFragment.getMapAsync(this);
    }

    private void setupDataList() {
        rvData = (RecyclerView) findViewById(R.id.detail_rvData);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);
        rvData.setLayoutManager(manager);
        rvData.addItemDecoration(new DividerDecoration(this, R.drawable.shape_cyan_divider));

        dataAdapter = new DetailDataAdapter();
        rvData.setAdapter(dataAdapter);

        List<DetailData> data = new ArrayList<>();

        DetailData detailData = new DetailData();
        detailData.title = "Capacité d'hébergement";
        detailData.quantity = 6;
        data.add(detailData);

        detailData = new DetailData();
        detailData.title = "Chambre à Coucher";
        detailData.quantity = 3;
        data.add(detailData);

        detailData = new DetailData();
        detailData.title = "Lits";
        detailData.quantity = 3;
        data.add(detailData);

        detailData = new DetailData();
        detailData.title = "Salle de Bain";
        detailData.quantity = 3;
        data.add(detailData);

        dataAdapter.setData(data);

        rvData.setMinimumHeight(data.size() * Util.dpToPx(this, 30));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Location location = new Location("manual");
        location.setLatitude(37.30925);
        location.setLongitude(-122.0436444);

        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 16));

        createMapMockData(location);
    }

    private Marker createMarker(double lat, double lng, String title) {
        return map.addMarker(new MarkerOptions()
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_diadiem))
                .position(new LatLng(lat, lng))
                .title(title));
    }

    private void createMapMockData(Location loc) {
        int min = 10;
        int max = 100;

        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            int number = r.nextInt(max - min + 1) + min;
            if (i % 2 == 0) {
                number = -number;
            }
            double lat = loc.getLatitude() + number / 10E4;
            double lng = loc.getLongitude() + number / 10E4;

            createMarker(lat, lng, "House " + i);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
