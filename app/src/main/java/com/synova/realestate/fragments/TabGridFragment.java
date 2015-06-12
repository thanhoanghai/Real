
package com.synova.realestate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synova.realestate.R;
import com.synova.realestate.base.BaseFragment;

/**
 * Created by ducth on 6/12/15.
 */
public class TabGridFragment extends BaseFragment {

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_grid, container, false);
        return rootView;
    }
}
