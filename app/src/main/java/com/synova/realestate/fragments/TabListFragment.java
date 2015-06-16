
package com.synova.realestate.fragments;

import android.app.ProgressDialog;
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
import com.synova.realestate.adapters.HouseListAdapter;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.models.House;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 6/12/15.
 */
public class TabListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rvItems;
    private HouseListAdapter houseAdapter;

    private Constants.ListLoadingState loadingState = Constants.ListLoadingState.NONE;

    private ProgressDialog loadingDialog;

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_grid, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.tab_grid_swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.cyan);
        swipeRefreshLayout.setOnRefreshListener(this);

        rvItems = (RecyclerView) rootView.findViewById(R.id.tab_grid_rvItems);
        LinearLayoutManager manager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false);
        rvItems.setLayoutManager(manager);
        rvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (loadingState == Constants.ListLoadingState.NONE
                        && !recyclerView.canScrollVertically(1)) {
                    loadMore();
                }
            }
        });
        houseAdapter = new HouseListAdapter();
        rvItems.setAdapter(houseAdapter);

        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.setMessage("Please wait...");

        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;
        loadingDialog.show();
        loadNewData();

        return rootView;
    }

    private List<House> createMockData() {
        List<House> houses = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            House house = new House();
            house.price = 750;
            house.photo = i % 2 == 0 ? "http://images.travelpod.com/tripwow/photos2/ta-02e0-d424-1301/alimetov-yambol-bulgaria+13162716848-tpweb11w-19996.jpg"
                    : "http://www.meta-project.org/wp-content/uploads/2011/07/SR_DB-2.jpg";
            house.title = "Test long house title name";
            house.pieces = 2;
            house.surface = 30.5f;
            house.distance = "500";
            houses.add(house);
        }

        return houses;
    }

    private void loadMore() {
        loadingState = Constants.ListLoadingState.LOAD_MORE;
        loadingDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<House> houses = createMockData();
                houseAdapter.addItems(houses);

                loadingState = Constants.ListLoadingState.NONE;
                loadingDialog.dismiss();
            }
        }, 2000);
    }

    private void loadNewData() {
        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<House> houses = createMockData();
                houseAdapter.setItems(houses);

                swipeRefreshLayout.setRefreshing(false);
                loadingState = Constants.ListLoadingState.NONE;
                loadingDialog.dismiss();
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        loadNewData();
    }
}
