
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
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.base.SubscriberImpl;
import com.synova.realestate.customviews.DividerDecoration;
import com.synova.realestate.models.Publisher;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.network.model.PublisherRequestEnt;

import java.util.List;

import rx.Subscription;

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
    private Subscription subscription;

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
        toggleSwipeRefreshLayout(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
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
        publisherRequestEnt.orderByS = Constants.FilterOrderType.DISTANCE_ASC;

        subscription = NetworkService.getListPublisher(publisherRequestEnt).subscribe(
                new SubscriberImpl<List<Publisher>>() {
                    @Override
                    public void onNext(List<Publisher> publishers) {
                        sellersAdapter.setItems(publishers);

                        toggleSwipeRefreshLayout(false);
                        loadingState = Constants.ListLoadingState.NONE;
                    }

                    @Override
                    public void onError(Throwable e) {
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
        publisherRequestEnt.publisherId = publisher.pid;
        ((TabSellerBaseFragment) getParentFragment())
                .showSellerPropertyFragment(publisherRequestEnt);
    }
}
