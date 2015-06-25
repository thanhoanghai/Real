
package com.synova.realestate.base;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.synova.realestate.R;
import com.synova.realestate.adapters.DetailMapInfoWindowAdapter;
import com.synova.realestate.adapters.DetailSlideShowAdapter;
import com.synova.realestate.customviews.AdsImageView;
import com.synova.realestate.customviews.CustomCirclePageIndicator;
import com.synova.realestate.customviews.TouchableWrapperView;
import com.synova.realestate.fragments.RetainMapFragment;
import com.synova.realestate.models.DetailData;
import com.synova.realestate.models.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by ducth on 6/17/15.
 */
public class DetailActivity extends BaseActivity implements OnMapReadyCallback,
        View.OnClickListener, ViewPager.OnPageChangeListener {

    private AdsImageView adsView;

    private GoogleMap map;

    private AutoScrollViewPager slideShowView;
    private DetailSlideShowAdapter slideShowAdapter;
    private ImageButton btnPreSlide;
    private ImageButton btnNextSlide;

    private ViewGroup groupData;
    private ViewGroup groupSellers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        adsView = (AdsImageView) findViewById(R.id.adsImageView);
        // adsView.setAdsUrl("http://www.webbanner24.com/blog/wp-content/uploads/2014/09/Top-5-Reasons-Why-You-Need-Banner-Ads.jpg");
        adsView.setImageResource(R.drawable.img_ads_banner);
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
        setupSellerList();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.drawable.ico_navbar_logo);
    }

    private void setupSlideShow() {
        btnPreSlide = (ImageButton) findViewById(R.id.detail_btnPreSlide);
        btnPreSlide.setOnClickListener(this);
        btnNextSlide = (ImageButton) findViewById(R.id.detail_btnNextSlide);
        btnNextSlide.setOnClickListener(this);

        slideShowView = (AutoScrollViewPager) findViewById(R.id.detail_slideShow);
        slideShowView.setInterval(5000);
        slideShowView.startAutoScroll();
        slideShowView.addOnPageChangeListener(this);

        slideShowAdapter = new DetailSlideShowAdapter();
        slideShowView.setAdapter(slideShowAdapter);

        CustomCirclePageIndicator slideShowIndicator = (CustomCirclePageIndicator) findViewById(R.id.detail_slideshow_indicator);
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
        onPageSelected(0);
    }

    private void setupMap() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.detail_scrollView);
        TouchableWrapperView mapTouchableWrapperView = (TouchableWrapperView) findViewById(R.id.detail_mapTouchableWrapperView);
        mapTouchableWrapperView.setScrollableView(scrollView);

        RetainMapFragment mapFragment = (RetainMapFragment) getSupportFragmentManager()
                .findFragmentById(
                        R.id.detail_mapFragment);
        mapFragment.getMapAsync(this);
    }

    private void setupDataList() {
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

        groupData = (LinearLayout) findViewById(R.id.detail_groupData);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < data.size(); i++) {
            if (i > 0) {
                View divider = new View(this);
                divider.setBackgroundResource(R.drawable.shape_cyan_divider);
                groupData.addView(divider);
            }
            View item = inflater.inflate(R.layout.layout_detail_data_list_item, groupData, false);
            TextView tvTitle = (TextView) item.findViewById(R.id.detail_data_item_tvTitle);
            TextView tvQuantity = (TextView) item.findViewById(R.id.detail_data_item_tvQuantity);

            tvTitle.setText(data.get(i).title);
            tvQuantity.setText(data.get(i).quantity + "");

            groupData.addView(item);
        }
    }

    private void setupSellerList() {
        List<Publisher> publishers = new ArrayList<>();

        Publisher publisher = new Publisher();
        publisher.thumbnail = "";
        publisher.title = "Nom Adresse 1";
        publisher.website = "http://google.com";
        publisher.phone = "51-22-48";
        publisher.mail = "mail01@realestate.com";
        publisher.annonces = 100;
        publishers.add(publisher);

        publisher = new Publisher();
        publisher.thumbnail = "";
        publisher.title = "Nom Adresse 2";
        publisher.website = "http://google.com";
        publisher.phone = "51-22-49";
        publisher.mail = "mail02@realestate.com";
        publisher.annonces = 100;
        publishers.add(publisher);

        publisher = new Publisher();
        publisher.thumbnail = "";
        publisher.title = "Nom Adresse 3";
        publisher.website = "http://google.com";
        publisher.phone = "51-22-50";
        publisher.mail = "mail03@realestate.com";
        publisher.annonces = 100;
        publishers.add(publisher);

        groupSellers = (ViewGroup) findViewById(R.id.detail_groupSellers);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Publisher s : publishers) {
            View view = inflater.inflate(R.layout.layout_detail_seller_list_item,
                    groupSellers, false);

            ImageView ivThumbnail = (ImageView) view.findViewById(R.id.detail_seller_ivThumbnail);
            TextView tvTitle = (TextView) view.findViewById(R.id.detail_seller_tvTitle);
            TextView tvPrice = (TextView) view.findViewById(R.id.detail_seller_tvPrice);
            TextView tvPhone = (TextView) view.findViewById(R.id.detail_seller_tvPhone);
            TextView tvMail = (TextView) view.findViewById(R.id.detail_seller_tvMail);
            ViewGroup groupPhone = (ViewGroup) view.findViewById(R.id.detail_seller_groupPhone);
            ViewGroup groupMail = (ViewGroup) view.findViewById(R.id.detail_seller_groupMail);

            ImageLoader.getInstance().displayImage(s.thumbnail, ivThumbnail);
            tvTitle.setText(s.title);
            // tvPrice.setText(s.);
            tvPhone.setText(s.phone);
            tvMail.setText(s.mail);

            groupPhone.setTag(s);
            groupPhone.setOnClickListener(this);

            groupMail.setTag(s);
            groupMail.setOnClickListener(this);

            groupSellers.addView(view);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Location location = new Location("manual");
        location.setLatitude(37.30925);
        location.setLongitude(-122.0436444);

        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setInfoWindowAdapter(new DetailMapInfoWindowAdapter(this));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 16));

        createMapMockData(location);
    }

    private Marker createMarker(double lat, double lng, String title) {
        return map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_marker_bien))
                .position(new LatLng(lat, lng))
                .anchor(0.1f, 0.9f)
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_btnPreSlide:
                slideShowView.setCurrentItem(slideShowView.getCurrentItem() - 1);
                slideShowView.stopAutoScroll();
                slideShowView.startAutoScroll();
                break;
            case R.id.detail_btnNextSlide:
                slideShowView.setCurrentItem(slideShowView.getCurrentItem() + 1);
                slideShowView.stopAutoScroll();
                slideShowView.startAutoScroll();
                break;
            case R.id.detail_seller_groupPhone:
                Publisher publisher = (Publisher) v.getTag();
                Snackbar.make(v, "Call " + publisher.phone, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.detail_seller_groupMail:
                publisher = (Publisher) v.getTag();
                Snackbar.make(v, "Send mail to " + publisher.mail, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            btnPreSlide.setVisibility(View.GONE);
            btnNextSlide.setVisibility(View.VISIBLE);
        } else if (position == slideShowAdapter.getCount() - 1) {
            btnPreSlide.setVisibility(View.VISIBLE);
            btnNextSlide.setVisibility(View.GONE);
        } else {
            btnPreSlide.setVisibility(View.VISIBLE);
            btnNextSlide.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
