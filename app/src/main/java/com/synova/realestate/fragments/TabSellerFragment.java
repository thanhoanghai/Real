
package com.synova.realestate.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synova.realestate.R;
import com.synova.realestate.adapters.SellerListAdapter;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.customviews.DividerDecoration;
import com.synova.realestate.models.Seller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 6/17/15.
 */
public class TabSellerFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rvItems;
    private SellerListAdapter sellersAdapter;

    private Constants.ListLoadingState loadingState = Constants.ListLoadingState.NONE;

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_seller, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.tab_seller_swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.cyan);
        swipeRefreshLayout.setOnRefreshListener(this);

        rvItems = (RecyclerView) rootView.findViewById(R.id.tab_seller_rvItems);
        LinearLayoutManager manager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false);
        rvItems.setLayoutManager(manager);
        rvItems.addItemDecoration(new DividerDecoration(activity, 0));
        rvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (loadingState == Constants.ListLoadingState.NONE
                        && !recyclerView.canScrollVertically(1)) {
                    loadMore();
                }
            }
        });
        sellersAdapter = new SellerListAdapter();
        rvItems.setAdapter(sellersAdapter);

        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;
        loadNewData();

        return rootView;
    }

    private List<Seller> createMockData() {
        List<Seller> sellers = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            Seller seller = new Seller();
            seller.thumbnail = i % 2 == 0 ? "http://images.travelpod.com/tripwow/photos2/ta-02e0-d424-1301/alimetov-yambol-bulgaria+13162716848-tpweb11w-19996.jpg"
                    : "http://www.meta-project.org/wp-content/uploads/2011/07/SR_DB-2.jpg";
            seller.title = "Seller " + i;
            seller.annonces = i + 10;
            seller.website = "www.realestate.com";
            seller.phone = "51-22-48";
            seller.mail = "mail02@realestate.com";
            sellers.add(seller);
        }

        return sellers;
    }

    private void loadMore() {
        loadingState = Constants.ListLoadingState.LOAD_MORE;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Seller> houses = createMockData();
                sellersAdapter.addItems(houses);

                loadingState = Constants.ListLoadingState.NONE;
            }
        }, 2000);
    }

    private void loadNewData() {
        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Seller> houses = createMockData();
                sellersAdapter.setItems(houses);

                swipeRefreshLayout.setRefreshing(false);
                loadingState = Constants.ListLoadingState.NONE;
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        loadNewData();
    }
}
