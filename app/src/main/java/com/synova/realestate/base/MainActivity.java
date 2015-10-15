
package com.synova.realestate.base;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.synova.realestate.R;
import com.synova.realestate.adapters.TabsPagerAdapter;
import com.synova.realestate.customviews.AdsImageView;
import com.synova.realestate.customviews.CustomTabPageIndicator;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.models.eventbus.NavigationItemSelectedEvent;
import com.synova.realestate.utils.DialogUtils;
import com.synova.realestate.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private ActionBar actionBar;
    private PopupMenu popupMenu;

    private AdsImageView adsView;

    private ViewPager viewPager;
    private TabsPagerAdapter pagerAdapter;
    private CustomTabPageIndicator tabPageIndicator;

    public static HashMap<Constants.ElementType, Boolean> markersVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markersVisibility = new HashMap<>();
        markersVisibility.put(Constants.ElementType.BIEN, true);
        markersVisibility.put(Constants.ElementType.AGENCE, true);
        markersVisibility.put(Constants.ElementType.PARTICULIER, true);
        markersVisibility.put(Constants.ElementType.NOTAIRE, true);

        setupActionBar();
        setupDrawer();

        pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);

        tabPageIndicator = (CustomTabPageIndicator) findViewById(R.id.pager_indicator);
        tabPageIndicator.setFillViewport(true);
        tabPageIndicator.setViewPager(viewPager);
        tabPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BaseFragment baseFragment = pagerAdapter.getPageAtIndex(position);
                if (baseFragment != null){
                    baseFragment.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        adsView = (AdsImageView) findViewById(R.id.adsImageView);
        // adsView.setAdsUrl("http://www.webbanner24.com/blog/wp-content/uploads/2014/09/Top-5-Reasons-Why-You-Need-Banner-Ads.jpg");
        adsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Snackbar.make(adsView, "You've clicked ads banner!",
                // Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private List<AdsInfoResponseEnt> listAdsInfoResponse = new ArrayList<>();

    public List<AdsInfoResponseEnt> getListAdsInfoResponse() {
        return listAdsInfoResponse;
    }

    public void setListAdsInfoResponse(List<AdsInfoResponseEnt> listAdsInfoResponse) {
        this.listAdsInfoResponse.clear();
        this.listAdsInfoResponse.addAll(listAdsInfoResponse);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.drawable.ico_navbar_logo);
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);

        CheckBox ckbBien = (CheckBox) findViewById(R.id.navigation_btnBien);
        CheckBox ckbAgence = (CheckBox) findViewById(R.id.navigation_btnAgence);
        CheckBox ckbParticulier = (CheckBox) findViewById(R.id.navigation_btnParticulier);
        CheckBox ckbNotaire = (CheckBox) findViewById(R.id.navigation_btnNotaire);

        ckbBien.setOnCheckedChangeListener(this);
        ckbAgence.setOnCheckedChangeListener(this);
        ckbParticulier.setOnCheckedChangeListener(this);
        ckbNotaire.setOnCheckedChangeListener(this);
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // boolean drawerOpen = drawerLayout.isDrawerOpen(GravityCompat.START);
        // menu.findItem(R.id.action_filter).setVisible(!drawerOpen);
        // menu.findItem(R.id.action_overflow).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerLayout.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_UNLOCKED
                && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_filter:
                DialogUtils.showDialogFilter(this);
                return true;
            case R.id.action_overflow:
                if (popupMenu == null) {
                    View menuItemView = findViewById(R.id.action_overflow);
                    popupMenu = new PopupMenu(this, menuItemView);
                    popupMenu.inflate(R.menu.menu_overflow);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_share_fb:
                                    Util.shareViaFacebook(MainActivity.this, null);
                                    break;
                            }
                            return true;
                        }
                    });
                }
                popupMenu.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        closeDrawer();

        Constants.ElementType type = null;
        switch (buttonView.getId()) {
            case R.id.navigation_btnBien:
                type = Constants.ElementType.BIEN;
                markersVisibility.put(Constants.ElementType.BIEN, isChecked);
                break;
            case R.id.navigation_btnAgence:
                type = Constants.ElementType.AGENCE;
                markersVisibility.put(Constants.ElementType.AGENCE, isChecked);
                break;
            case R.id.navigation_btnParticulier:
                type = Constants.ElementType.PARTICULIER;
                markersVisibility.put(Constants.ElementType.PARTICULIER, isChecked);
                break;
            case R.id.navigation_btnNotaire:
                type = Constants.ElementType.NOTAIRE;
                markersVisibility.put(Constants.ElementType.NOTAIRE, isChecked);
                break;

        }

        if (type != null) {
            EventBus.getDefault().post(new NavigationItemSelectedEvent(type, isChecked));
        }
    }

    private int backPressedCount = 0;

    @Override
    public void onBackPressed() {
        BaseFragment currentTabFragment = pagerAdapter.getPageAtIndex(viewPager.getCurrentItem());
        if (currentTabFragment != null && !currentTabFragment.onBackPressed()) {
            if (currentTabFragment.childFragments != null
                    && currentTabFragment.childFragments.size() > 1) {
                currentTabFragment.popChildFragment();
            } else {
                backPressedCount++;
                if (backPressedCount == 2) {
                    backPressedCount = 0;
                    finish();
                } else {
                    Toast.makeText(this, "Press back again to quit app", Toast.LENGTH_SHORT).show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            backPressedCount = 0;
                        }
                    }, 2000);
                }
            }
        }
    }
}
