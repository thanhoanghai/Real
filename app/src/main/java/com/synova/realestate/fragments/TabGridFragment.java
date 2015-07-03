
package com.synova.realestate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.synova.realestate.R;
import com.synova.realestate.adapters.HouseGridAdapter;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.MainActivity;
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.customviews.SortBar;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.models.House;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.network.model.AdsInfoEnt;
import com.synova.realestate.utils.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ducth on 6/12/15.
 */
public class TabGridFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        OnRecyclerViewItemClickedListener<AdsInfoResponseEnt>,
        SortBar.OnSortBarItemSelectedListener {

    private static final int GRID_COLUMN_COUNT = 3;

    private SortBar sortBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rvItems;
    private HouseGridAdapter houseAdapter;

    private Constants.ListLoadingState loadingState = Constants.ListLoadingState.NONE;

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_grid, container, false);

        sortBar = (SortBar) rootView.findViewById(R.id.tab_grid_sortBar);
        sortBar.setOnSortBarItemSelectedListener(this);
        sortBar.selectItem(0);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.tab_grid_swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.cyan);
        swipeRefreshLayout.setOnRefreshListener(this);

        rvItems = (RecyclerView) rootView.findViewById(R.id.tab_grid_rvItems);
        GridLayoutManager manager = new GridLayoutManager(activity, GRID_COLUMN_COUNT,
                GridLayoutManager.VERTICAL, false);
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
        houseAdapter = new HouseGridAdapter();
        houseAdapter.setOnItemClickedListener(this);
        rvItems.setAdapter(houseAdapter);

        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;
        loadNewData();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) activity).disableDrawer();
        swipeRefreshLayout.setRefreshing(false);
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
        // loadingState = Constants.ListLoadingState.LOAD_MORE;
        //
        // new Handler().postDelayed(new Runnable() {
        // @Override
        // public void run() {
        // List<House> houses = createMockData();
        // houseAdapter.addItems(houses);
        //
        // loadingState = Constants.ListLoadingState.NONE;
        // }
        // }, 2000);
    }

    private void loadNewData() {
        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;

        AdsInfoEnt adsInfoEnt = new AdsInfoEnt();
        NetworkService.getAdsInfo(adsInfoEnt, new Callback<List<AdsInfoResponseEnt>>() {
            @Override
            public void success(List<AdsInfoResponseEnt> adsInfoResponseEnts, Response response) {
                houseAdapter.setItems(adsInfoResponseEnts);

                swipeRefreshLayout.setRefreshing(false);
                loadingState = Constants.ListLoadingState.NONE;
            }

            @Override
            public void failure(RetrofitError error) {
                swipeRefreshLayout.setRefreshing(false);
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
            AdsInfoResponseEnt data) {
        activity.showDetailActivity(data);
    }

    @Override
    public void onSortBarItemSelected(int position, final boolean isSortAsc, int segmentId) {
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
