
package com.synova.realestate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synova.realestate.R;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.network.model.PublisherRequestEnt;

/**
 * Created by ducth on 8/11/15.
 */
public class TabSellerBaseFragment extends BaseFragment {

    private TabSellerFragment tabSellerFragment;

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_seller_base, container, false);

        showSellerFragment();

        return rootView;
    }

    public void showSellerFragment() {
        if (tabSellerFragment == null) {
            tabSellerFragment = new TabSellerFragment();
        }
        pushChildFragment(tabSellerFragment, Constants.TransitionType.NONE, true);
    }

    public void showSellerPropertyFragment(PublisherRequestEnt publisherRequestEnt) {
        SellerPropertyFragment sellerPropertyFragment = new SellerPropertyFragment();
        sellerPropertyFragment.setPublisherRequestEnt(publisherRequestEnt);
        pushChildFragment(sellerPropertyFragment, Constants.TransitionType.NONE, true);
    }

}
