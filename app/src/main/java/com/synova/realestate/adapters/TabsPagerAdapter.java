
package com.synova.realestate.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.fragments.TabFavoriteFragment;
import com.synova.realestate.fragments.TabGridFragment;
import com.synova.realestate.fragments.TabListFragment;
import com.synova.realestate.fragments.TabLocationFragment;
import com.synova.realestate.fragments.TabSellerBaseFragment;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.HashMap;

/**
 * Created by ducth on 8/10/15.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    private HashMap<String, BaseFragment> pages = new HashMap<>();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public BaseFragment getPageAtIndex(int index){
        return pages.get(Constants.TabBar.values()[index].name());
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;
        Constants.TabBar tab = Constants.TabBar.values()[position];
        switch (tab) {
            case GRID:
                fragment = new TabGridFragment();
                break;
            case LIST:
                fragment = new TabListFragment();
                break;
            case LOCATION:
                fragment = new TabLocationFragment();
                break;
            case ALERT:
                fragment = new TabSellerBaseFragment();
                break;
            case FAVORITE:
                fragment = new TabFavoriteFragment();
                break;
        }

        pages.put(tab.name(), fragment);

        return fragment;
    }

    public HashMap<String, BaseFragment> getPages() {
        return pages;
    }

    @Override
    public int getIconResId(int position) {
        return Constants.TabBar.values()[position].getResId();
    }

    @Override
    public int getCount() {
        return Constants.TabBar.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
