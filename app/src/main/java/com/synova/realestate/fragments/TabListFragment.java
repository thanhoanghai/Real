
package com.synova.realestate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.synova.realestate.R;
import com.synova.realestate.adapters.HouseListAdapter;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.MainActivity;
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.base.SubscriberImpl;
import com.synova.realestate.customviews.SortBar;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.models.eventbus.AddRemoveFavoriteEvent;
import com.synova.realestate.models.eventbus.ChangeDialogFilterValuesEvent;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.network.model.AdsInfoEnt;
import com.synova.realestate.utils.Util;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Subscription;

/**
 * Created by ducth on 6/12/15.
 */
public class TabListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        OnRecyclerViewItemClickedListener<AdsInfoResponseEnt>,
        SortBar.OnSortBarItemSelectedListener {

    private SortBar sortBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rvItems;
    private HouseListAdapter houseAdapter;

    private Constants.ListLoadingState loadingState = Constants.ListLoadingState.NONE;

    private static final int PAGE_LIMIT = 30;

    private ProgressBar progressBar;
    private Subscription subscription;

    private View.OnClickListener onBtnFavoriteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = Util.findView(v, R.id.list_item_container);
            int position = rvItems.getChildAdapterPosition(view) - 1;
            final AdsInfoResponseEnt house = houseAdapter.getItems().get(position);

            if (house.isFavorite) {
                NetworkService.removeFavorite("" + house.id).subscribe(
                        new SubscriberImpl<Boolean>() {
                            @Override
                            public void onNext(Boolean isSuccess) {
                                if (isSuccess) {
                                    house.isFavorite = false;
                                    houseAdapter.notifyDataSetChanged();

                                    EventBus.getDefault().postSticky(new AddRemoveFavoriteEvent());
                                } else {
                                    Toast.makeText(activity, "Remove favorite fail.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                NetworkService.addFavorite("" + house.id).subscribe(new SubscriberImpl<Boolean>() {
                    @Override
                    public void onNext(Boolean isSuccess) {
                        if (isSuccess) {
                            house.isFavorite = true;
                            houseAdapter.notifyDataSetChanged();

                            EventBus.getDefault().postSticky(new AddRemoveFavoriteEvent());
                        } else {
                            Toast.makeText(activity, "Add favorite fail.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab_list, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        sortBar = (SortBar) rootView.findViewById(R.id.tab_list_sortBar);
        sortBar.setOnSortBarItemSelectedListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.tab_list_swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.cyan);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressViewOffset(false, 0, Util.dpToPx(activity, 60));

        rvItems = (RecyclerView) rootView.findViewById(R.id.tab_list_rvItems);
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
        houseAdapter.setItems(((MainActivity) activity).getListAdsInfoResponse());
        houseAdapter.setOnItemClickedListener(this);
        houseAdapter.setOnBtnFavoriteClickListener(onBtnFavoriteClickListener);
        rvItems.setAdapter(houseAdapter);

        loadingState = Constants.ListLoadingState.NONE;
        // toggleSwipeRefreshLayout(true);
        // loadNewData();

        return rootView;
    }

    @Override
    protected void onPageSelected(int position) {
        toggleSwipeRefreshLayout(false);
        houseAdapter.notifyDataSetChanged();
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

    private void loadNewData() {
        loadingState = Constants.ListLoadingState.SWIPE_REFRESH;

        AdsInfoEnt adsInfoEnt = new AdsInfoEnt();
        adsInfoEnt.offsetS = 0;
        subscription = NetworkService.getAdsInfo(adsInfoEnt).subscribe(
                new SubscriberImpl<List<AdsInfoResponseEnt>>() {
                    @Override
                    public void onNext(List<AdsInfoResponseEnt> adsInfoResponseEnts) {
                        ((MainActivity) activity).setListAdsInfoResponse(adsInfoResponseEnts);
                        houseAdapter.notifyDataSetChanged();

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

    private void loadMore() {
        loadingState = Constants.ListLoadingState.LOAD_MORE;
        progressBar.setVisibility(View.VISIBLE);

        AdsInfoEnt adsInfoEnt = new AdsInfoEnt();
        adsInfoEnt.offsetS = houseAdapter.getItems().size() > 0 ? houseAdapter.getItems().size() + 1
                : 0;
        subscription = NetworkService.getAdsInfo(adsInfoEnt).subscribe(
                new SubscriberImpl<List<AdsInfoResponseEnt>>() {
                    @Override
                    public void onNext(List<AdsInfoResponseEnt> adsInfoResponseEnts) {
                        ((MainActivity) activity).getListAdsInfoResponse().addAll(
                                adsInfoResponseEnts);
                        houseAdapter.notifyDataSetChanged();

                        toggleSwipeRefreshLayout(false);
                        loadingState = Constants.ListLoadingState.NONE;
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        toggleSwipeRefreshLayout(false);
                        loadingState = Constants.ListLoadingState.NONE;
                        progressBar.setVisibility(View.GONE);

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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ChangeDialogFilterValuesEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        toggleSwipeRefreshLayout(true);
        loadNewData();
    }

    public void onEventMainThread(AddRemoveFavoriteEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        toggleSwipeRefreshLayout(true);
        loadNewData();
    }
}
