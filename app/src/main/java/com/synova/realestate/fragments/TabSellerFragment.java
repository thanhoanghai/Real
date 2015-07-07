
package com.synova.realestate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.synova.realestate.R;
import com.synova.realestate.adapters.SellerListAdapter;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.MainActivity;
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.customviews.DividerDecoration;
import com.synova.realestate.models.Publisher;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.network.model.PublisherRequestEnt;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by ducth on 6/17/15.
 */
public class TabSellerFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, OnRecyclerViewItemClickedListener<Publisher> {
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rvItems;
    private SellerListAdapter sellersAdapter;

    private Constants.ListLoadingState loadingState = Constants.ListLoadingState.NONE;

    private PublisherRequestEnt publisherRequestEnt;

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
                    // loadMore();
                }
            }
        });
        sellersAdapter = new SellerListAdapter();
        sellersAdapter.setOnRecyclerViewItemClickedListener(this);
        rvItems.setAdapter(sellersAdapter);

        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;
        toggleSwipeRefreshLayout(true);
        loadNewData();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) activity).disableDrawer();
        toggleSwipeRefreshLayout(false);
    }

    private void toggleSwipeRefreshLayout(final boolean isRefreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    // private void loadMore() {
    // loadingState = Constants.ListLoadingState.LOAD_MORE;
    //
    // new Handler().postDelayed(new Runnable() {
    // @Override
    // public void run() {
    // List<Publisher> houses = createMockData();
    // sellersAdapter.addItems(houses);
    //
    // loadingState = Constants.ListLoadingState.NONE;
    // }
    // }, 2000);
    // }

    private void loadNewData() {
        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;

        publisherRequestEnt = new PublisherRequestEnt();
        publisherRequestEnt.publisherId = 16547;
        publisherRequestEnt.xLocalisation = 2.1475;
        publisherRequestEnt.yLocalisation = 48.9306;
        publisherRequestEnt.polygon = "POLYGON((2.1475 48.9306,2.4963 48.7924,2.4963 48.9306,2.1475 48.9306))";
        publisherRequestEnt.adminId = 1656;
        publisherRequestEnt.offsetS = 100;
        publisherRequestEnt.propertyTypeS = "p1";
        publisherRequestEnt.rentSaleS = "1";
        publisherRequestEnt.businessTypeS = "b";
        publisherRequestEnt.surfaceMinS = "20";
        publisherRequestEnt.surfaceMaxS = "70";
        publisherRequestEnt.priceMinS = "1";
        publisherRequestEnt.priceMaxS = "9";
        publisherRequestEnt.codePostalS = "111";
        publisherRequestEnt.roomNumberS = "1239";
        publisherRequestEnt.keyWordS = "P";

        NetworkService.getListPublisher(publisherRequestEnt,
                new NetworkService.NetworkCallback<List<Publisher>>() {
                    @Override
                    public void onSuccess(List<Publisher> publishers) {
                        sellersAdapter.setItems(publishers);

                        toggleSwipeRefreshLayout(false);
                        loadingState = Constants.ListLoadingState.NONE;
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        toggleSwipeRefreshLayout(false);
                        loadingState = Constants.ListLoadingState.NONE;

                        Toast.makeText(activity, "Failed to get data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRefresh() {
        loadNewData();
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, View view, int position, long id,
            Publisher publisher) {
        SellerPropertyFragment fragment = new SellerPropertyFragment();
        fragment.setPublisherRequestEnt(publisherRequestEnt);
        activity.pushFragment(fragment, Constants.TransitionType.SLIDE_IN_RIGHT_TO_LEFT, true);
    }
}
