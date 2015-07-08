
package com.synova.realestate.fragments;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

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
import com.synova.realestate.base.MainActivity;
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.base.RealEstateApplication;
import com.synova.realestate.customviews.SortBar;
import com.synova.realestate.models.AdsDetailEnt;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.models.House;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.network.model.AdEnt;
import com.synova.realestate.utils.Util;

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

        rvItems = (RecyclerView) rootView.findViewById(R.id.tab_favorite_rvItems);
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

        NetworkService.getListFavorite(RealEstateApplication.deviceId,
                new NetworkService.NetworkCallback<List<String>>() {
                    @Override
                    public void onSuccess(List<String> adIds) {
                        for (final String adId : adIds) {
                            AdEnt adEnt = new AdEnt();
                            adEnt.adId = Integer.parseInt(adId);
                            NetworkService.getPropertyDetails(adEnt,
                                    new NetworkService.NetworkCallback<AdsDetailEnt>() {
                                        @Override
                                        public void onSuccess(AdsDetailEnt adsDetailEnt) {
                                            if (adsDetailEnt == null
                                                    || adsDetailEnt.characs == null
                                                    || adsDetailEnt.characs.size() == 0) {
                                                return;
                                            }

                                            AdsDetailEnt.AdCharac charac = adsDetailEnt.characs
                                                    .get(0);
                                            AdsInfoResponseEnt item = new AdsInfoResponseEnt();
                                            item.id = Integer.parseInt(adId);
                                            item.imageUrl = (adsDetailEnt.images != null && adsDetailEnt.images
                                                    .size() > 0) ? adsDetailEnt.images.get(0).imagesUrl
                                                    : "";
                                            item.title = charac.title;
                                            item.mminMaxPrice = charac.minMaxPrice;
//                                            item.roomNumber = charac.

//                                            houseAdapter.addItem(item);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {

                                        }
                                    });

                            toggleSwipeRefreshLayout(false);
                            loadingState = Constants.ListLoadingState.NONE;
                        }
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
