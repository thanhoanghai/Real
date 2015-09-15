
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
import com.synova.realestate.adapters.HouseListAdapter;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.base.RealEstateApplication;
import com.synova.realestate.customviews.SortBar;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.utils.Util;

import java.util.List;

/**
 * Created by ducth on 6/12/15.
 */
public class TabFavoriteFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        OnRecyclerViewItemClickedListener<AdsInfoResponseEnt>,
        SortBar.OnSortBarItemSelectedListener {

    private SortBar sortBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rvItems;
    private HouseListAdapter houseAdapter;

    private Constants.ListLoadingState loadingState = Constants.ListLoadingState.NONE;

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_favorite, container, false);

        sortBar = (SortBar) rootView.findViewById(R.id.tab_favorite_sortBar);
        sortBar.setOnSortBarItemSelectedListener(this);
        sortBar.selectItem(0);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.tab_favorite_swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.cyan);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressViewOffset(false, 0, Util.dpToPx(activity, 60));

        rvItems = (RecyclerView) rootView.findViewById(R.id.tab_favorite_rvItems);
        LinearLayoutManager manager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false);
        rvItems.setLayoutManager(manager);
        rvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (loadingState == Constants.ListLoadingState.NONE
                        && !recyclerView.canScrollVertically(1)) {
                    // loadMore();
                }

                if (dy > 0) {
                    if (sortBar.getY() > -sortBar.getHeight()) {
                        sortBar.setY(sortBar.getY() - dy);
                    } else {
                        sortBar.setY(-sortBar.getHeight());
                    }
                } else if (dy < 0) {
                    if (sortBar.getY() < 0) {
                        sortBar.setY(sortBar.getY() - dy);
                    } else {
                        sortBar.setY(0);
                    }
                } else {
                    sortBar.setY(0);
                }
            }
        });
        houseAdapter = new HouseListAdapter();
        houseAdapter.setOnItemClickedListener(this);
        rvItems.setAdapter(houseAdapter);

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

    private void toggleSwipeRefreshLayout(final boolean isRefreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }

    private void loadNewData() {
        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;

        NetworkService.getListFavorite(RealEstateApplication.deviceId,
                new NetworkService.NetworkCallback<List<AdsInfoResponseEnt>>() {
                    @Override
                    public void onSuccess(List<AdsInfoResponseEnt> adsInfoResponseEnts) {
                        toggleSwipeRefreshLayout(false);

                        houseAdapter.setItems(adsInfoResponseEnts);
                        loadingState = Constants.ListLoadingState.NONE;
                    }

                    @Override
                    public void onFail(Throwable error) {
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
            AdsInfoResponseEnt adEnt) {
        activity.showDetailActivity(adEnt.id);
    }

    @Override
    public void onSortBarItemSelected(int position, boolean isSortAsc, int segmentId) {
        List<AdsInfoResponseEnt> ads = houseAdapter.getItems();
        switch (segmentId) {
            case R.id.segment_distance:
                Util.sortAdsByDistance(ads, isSortAsc);
                houseAdapter.notifyDataSetChanged();
                break;
            case R.id.segment_price:
                Util.sortAdsByPrice(ads, isSortAsc);
                houseAdapter.notifyDataSetChanged();
                break;
            case R.id.segment_date:
                break;
        }
    }
}
