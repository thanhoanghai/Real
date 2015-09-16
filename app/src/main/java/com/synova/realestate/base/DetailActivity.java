
package com.synova.realestate.base;

import android.app.ProgressDialog;
import android.location.Location;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.synova.realestate.R;
import com.synova.realestate.adapters.DetailMapInfoWindowAdapter;
import com.synova.realestate.adapters.DetailSlideShowAdapter;
import com.synova.realestate.customviews.AdsImageView;
import com.synova.realestate.customviews.CustomCirclePageIndicator;
import com.synova.realestate.customviews.TouchableWrapperView;
import com.synova.realestate.fragments.RetainMapFragment;
import com.synova.realestate.models.AdsDetailEnt;
import com.synova.realestate.models.DetailData;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.network.model.AdEnt;
import com.synova.realestate.network.model.PublisherDetailEnt;
import com.synova.realestate.utils.DialogUtils;
import com.synova.realestate.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import retrofit.RetrofitError;

/**
 * Created by ducth on 6/17/15.
 */
public class DetailActivity extends BaseActivity implements OnMapReadyCallback,
        View.OnClickListener, ViewPager.OnPageChangeListener, GoogleMap.OnMarkerClickListener {

    private AdsImageView adsView;

    private ActionBar actionBar;

    private TextView tvTitle;
    private TextView tvAddress;
    private TextView tvPrice;

    private GoogleMap map;

    private AutoScrollViewPager slideShowView;
    private DetailSlideShowAdapter slideShowAdapter;
    private ImageButton btnPreSlide;
    private ImageButton btnNextSlide;

    private ViewGroup groupData;
    private ViewGroup groupSellers;

    private int adId;

    private AdsDetailEnt adsDetailEnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        adId = getIntent().getExtras().getInt("adId");

        adsView = (AdsImageView) findViewById(R.id.adsImageView);
        // adsView.setAdsUrl("http://www.webbanner24.com/blog/wp-content/uploads/2014/09/Top-5-Reasons-Why-You-Need-Banner-Ads.jpg");
        // adsView.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // Snackbar.make(adsView, "You've clicked ads banner!", Snackbar.LENGTH_SHORT).show();
        // }
        // });

        tvTitle = (TextView) findViewById(R.id.detail_tvTitle);
        tvAddress = (TextView) findViewById(R.id.detail_tvAddress);
        tvPrice = (TextView) findViewById(R.id.detail_tvPrice);

        setupMap();
        setupActionBar();
        setupSlideShow();
        setupDataList();

        getDetail();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
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

    private void addSellerList(List<PublisherDetailEnt> publishers) {
        groupSellers = (ViewGroup) findViewById(R.id.detail_groupSellers);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (PublisherDetailEnt publisher : publishers) {
            View view = inflater.inflate(R.layout.layout_detail_seller_list_item,
                    groupSellers, false);

            ImageView ivThumbnail = (ImageView) view.findViewById(R.id.detail_seller_ivThumbnail);
            TextView tvTitle = (TextView) view.findViewById(R.id.detail_seller_tvTitle);
            TextView tvPrice = (TextView) view.findViewById(R.id.detail_seller_tvPrice);
            TextView tvPhone = (TextView) view.findViewById(R.id.detail_seller_tvPhone);
            TextView tvMail = (TextView) view.findViewById(R.id.detail_seller_tvMail);
            ViewGroup groupPhone = (ViewGroup) view.findViewById(R.id.detail_seller_groupPhone);
            ViewGroup groupMail = (ViewGroup) view.findViewById(R.id.detail_seller_groupMail);

            if (!Util.isNullOrEmpty(publisher.logoUrl)) {
                ivThumbnail.setImageURI(Uri.parse(publisher.logoUrl));
            }

            tvTitle.setText(publisher.name);
            tvPrice.setText(publisher.price);
            tvPhone.setText(publisher.tel);
            tvMail.setText(publisher.mail);

            groupPhone.setTag(publisher);
            groupPhone.setOnClickListener(this);

            groupMail.setTag(publisher);
            groupMail.setOnClickListener(this);

            groupSellers.addView(view);
        }
    }

    private void getDetail() {
        final ProgressDialog waitDialog = DialogUtils.showWaitDialog(this, true);

        final AdEnt adEnt = new AdEnt();
        adEnt.adId = adId;
        NetworkService.getPublisherDetails(adEnt,
                new NetworkService.NetworkCallback<List<PublisherDetailEnt>>() {
                    @Override
                    public void onSuccess(List<PublisherDetailEnt> publisherDetailEnts) {
                        addSellerList(publisherDetailEnts);

                        NetworkService.getPropertyDetails(adEnt,
                                new NetworkService.NetworkCallback<AdsDetailEnt>() {
                                    @Override
                                    public void onSuccess(AdsDetailEnt adsDetailEnt) {
                                        waitDialog.dismiss();

                                        DetailActivity.this.adsDetailEnt = adsDetailEnt;

                                        if (adsDetailEnt == null) {
                                            return;
                                        }

                                        List<String> images = new ArrayList<>(adsDetailEnt.images
                                                .size());
                                        for (AdsDetailEnt.AdImage image : adsDetailEnt.images) {
                                            images.add(image.imagesUrl);
                                        }
                                        slideShowAdapter.setData(images);
                                        onPageSelected(0);

                                        AdsDetailEnt.AdCharac adCharac = adsDetailEnt.characs
                                                .get(0);
                                        tvTitle.setText(adCharac.title);
                                        tvPrice.setText(adCharac.minMaxPrice);
                                        tvAddress.setText(adCharac.detailCharac);

                                        LatLng latLng = Util
                                                .convertPointGeomToLatLng(adCharac.localisation);
                                        if (map != null) {
                                            createMarker(latLng.latitude, latLng.longitude,
                                                    adCharac.title);
                                            moveCameraToLocation(latLng);
                                        }

                                    }

                                    @Override
                                    public void onFail(Throwable error) {
                                        waitDialog.dismiss();
                                        Toast.makeText(DetailActivity.this, "Fail to load details",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setInfoWindowAdapter(new DetailMapInfoWindowAdapter(this));
        map.setOnMarkerClickListener(this);
    }

    private Marker createMarker(double lat, double lng, String title) {
        return map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_marker_bien))
                .position(new LatLng(lat, lng))
                .anchor(0.1f, 0.9f)
                .title(title));
    }

    private void moveCameraToLocation(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (adsDetailEnt != null && adsDetailEnt.characs != null
                && adsDetailEnt.characs.size() > 0) {

            boolean isFavorite = adsDetailEnt.characs.get(0).isFavorite;
            menu.findItem(R.id.action_favorite).setIcon(
                    isFavorite ? R.drawable.ico_star_full : R.drawable.ico_star_empty);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                if (adsDetailEnt != null && adsDetailEnt.characs != null
                        && adsDetailEnt.characs.size() > 0) {

                    final ProgressDialog waitDialog = DialogUtils.showWaitDialog(this, false);

                    if (adsDetailEnt.characs.get(0).isFavorite) {
                        NetworkService.removeFavorite(RealEstateApplication.deviceId, "" + adId,
                                new NetworkService.NetworkCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean isSuccess) {
                                        waitDialog.dismiss();

                                        adsDetailEnt.characs.get(0).isFavorite = false;
                                        invalidateOptionsMenu();
                                    }

                                    @Override
                                    public void onFail(Throwable error) {
                                        waitDialog.dismiss();
                                        Toast.makeText(DetailActivity.this,
                                                "Remove favorite fail.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        waitDialog.dismiss();
                                        Toast.makeText(DetailActivity.this,
                                                "Remove favorite fail.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        NetworkService.addFavorite(RealEstateApplication.deviceId, "" + adId,
                                new NetworkService.NetworkCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean isSuccess) {
                                        waitDialog.dismiss();

                                        adsDetailEnt.characs.get(0).isFavorite = true;
                                        invalidateOptionsMenu();
                                    }

                                    @Override
                                    public void onFail(Throwable error) {
                                        waitDialog.dismiss();
                                        Toast.makeText(DetailActivity.this,
                                                "Add to favorite fail.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        waitDialog.dismiss();
                                        Toast.makeText(DetailActivity.this,
                                                "Add to favorite fail.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                PublisherDetailEnt publisher = (PublisherDetailEnt) v.getTag();
                Snackbar.make(v, "Call " + publisher.tel, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.detail_seller_groupMail:
                publisher = (PublisherDetailEnt) v.getTag();
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }
}
