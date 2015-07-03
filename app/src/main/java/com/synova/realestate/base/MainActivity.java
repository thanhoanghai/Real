
package com.synova.realestate.base;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.synova.realestate.R;
import com.synova.realestate.customviews.AdsImageView;
import com.synova.realestate.customviews.ReclickableTabHost;
import com.synova.realestate.fragments.TabGridFragment;
import com.synova.realestate.fragments.TabListFragment;
import com.synova.realestate.fragments.TabLocationFragment;
import com.synova.realestate.fragments.TabSellerFragment;
import com.synova.realestate.models.eventbus.NavigationItemSelectedEvent;
import com.synova.realestate.utils.DialogUtils;

import java.util.HashMap;
import java.util.Stack;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener,
        RadioGroup.OnCheckedChangeListener {

    private HashMap<String, Stack<BaseFragment>> fragmentStacks;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RadioGroup groupNavigationItems;
    private ReclickableTabHost tabHost;
    private String currentTabTag = Constants.TabBar.GRID.name();

    private ActionBar actionBar;
    private PopupMenu popupMenu;

    private AdsImageView adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();
        setupDrawer();
        setupTabHost();

        adsView = (AdsImageView) findViewById(R.id.adsImageView);
        // adsView.setAdsUrl("http://www.webbanner24.com/blog/wp-content/uploads/2014/09/Top-5-Reasons-Why-You-Need-Banner-Ads.jpg");
        adsView.setImageResource(R.drawable.img_ads_banner);
        adsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(adsView, "You've clicked ads banner!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        disableDrawer();
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
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.drawable.ico_navbar_logo);
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        groupNavigationItems = (RadioGroup) navigationView
                .findViewById(R.id.navigation_groupNavigationItems);
        groupNavigationItems.setOnCheckedChangeListener(this);
        selectNavigationItem(R.id.navigation_btnBien);
    }

    private void setupTabHost() {
        fragmentStacks = new HashMap<>();
        fragmentStacks.put(Constants.TabBar.GRID.name(), new Stack<BaseFragment>());
        fragmentStacks.put(Constants.TabBar.LIST.name(), new Stack<BaseFragment>());
        fragmentStacks.put(Constants.TabBar.LOCATION.name(), new Stack<BaseFragment>());
        fragmentStacks.put(Constants.TabBar.ALERT.name(), new Stack<BaseFragment>());
        fragmentStacks.put(Constants.TabBar.FAVORITE.name(), new Stack<BaseFragment>());

        tabHost = (ReclickableTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.setOnTabChangedListener(this);

        addTab(Constants.TabBar.GRID.name(), R.drawable.ico_tabbar_grid);
        addTab(Constants.TabBar.LIST.name(), R.drawable.ico_tabbar_list);
        addTab(Constants.TabBar.LOCATION.name(), R.drawable.ico_tabbar_location);
        addTab(Constants.TabBar.ALERT.name(), R.drawable.ico_tabbar_sellers_list);
        addTab(Constants.TabBar.FAVORITE.name(), R.drawable.ico_star_empty);
    }

    private void addTab(String title, int iconRes) {
        TabHost.TabSpec spec = tabHost.newTabSpec(title);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.container);
            }
        });
        spec.setIndicator(createTabView(iconRes));
        tabHost.addTab(spec);
    }

    private View createTabView(int iconRes) {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.layout_tab_item, null);
        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_ivIcon);
        tabIcon.setImageResource(iconRes);
        return view;
    }

    @Override
    public void onTabChanged(String tag) {
        currentTabTag = tag;
        Constants.TabBar tab = Constants.TabBar.valueOf(tag);
        if (fragmentStacks.get(tag).size() == 0) {
            switch (tab) {
                case GRID:
                    pushFragment(new TabGridFragment(), Constants.TransitionType.NONE, true);
                    break;
                case LIST:
                    pushFragment(new TabListFragment(), Constants.TransitionType.NONE, true);
                    break;
                case LOCATION:
                    pushFragment(new TabLocationFragment(), Constants.TransitionType.NONE, true);
                    break;
                case ALERT:
                    pushFragment(new TabSellerFragment(), Constants.TransitionType.NONE, true);
                    break;
                case FAVORITE:
                    pushFragment(new TabListFragment(), Constants.TransitionType.NONE, true);
                    break;
            }
        } else {
            pushFragment(getCurrentFragment(), Constants.TransitionType.NONE, false);
        }
    }

    public void pushFragment(BaseFragment fragment,
            Constants.TransitionType transitionType,
            boolean addToStack) {
        fragment.transitionInType = transitionType;

        if (addToStack) {
            fragmentStacks.get(currentTabTag).push(fragment);
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(transitionType.transitionInResId,
                transitionType.transitionOutResId);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    public void popFragment() {
        /* Select the second last fragment in stack */
        BaseFragment fragment = fragmentStacks.get(currentTabTag).elementAt(
                fragmentStacks.get(currentTabTag).size() - 2);

        /* Remove current fragment from manually managed stack */
        BaseFragment currentFragment = fragmentStacks.get(currentTabTag).pop();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        Constants.TransitionType transitionType = reverseTransitionType(currentFragment.transitionInType);
        ft.setCustomAnimations(transitionType.transitionInResId,
                transitionType.transitionOutResId);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    public void enableDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void disableDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private BaseFragment getCurrentFragment() {
        return fragmentStacks.get(currentTabTag).peek();
    }

    @Override
    public void onBackPressed() {
        BaseFragment currentFragment = getCurrentFragment();
        if (!currentFragment.onBackPressed()) {
            if (fragmentStacks.get(currentTabTag).size() > 1) {
                popFragment();
            } else {
                // DialogUtils.showOKCancelDialog(this, getString(R.string.notice),
                // getString(R.string.quit_app_prompt), new View.OnClickListener() {
                // @Override
                // public void onClick(View v) {
                finish();
                // }
                // }, null);
            }
        }
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
                            Snackbar.make(drawerLayout, item.getTitle(), Snackbar.LENGTH_SHORT)
                                    .show();
                            return false;
                        }
                    });
                }
                popupMenu.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public RadioGroup getGroupNavigationItems() {
        return groupNavigationItems;
    }

    public void selectNavigationItem(int id) {
        groupNavigationItems.check(-1);
        groupNavigationItems.check(id);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        closeDrawer();
        EventBus.getDefault().post(new NavigationItemSelectedEvent(checkedId));
    }
}
