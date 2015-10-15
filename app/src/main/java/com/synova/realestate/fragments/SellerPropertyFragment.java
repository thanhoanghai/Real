
package com.synova.realestate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.synova.realestate.R;
import com.synova.realestate.adapters.PublisherPropertyAdapter;
import com.synova.realestate.base.BaseFragment;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.base.RealEstateApplication;
import com.synova.realestate.base.SubscriberImpl;
import com.synova.realestate.models.PublisherPropertyResponseEnt;
import com.synova.realestate.network.NetworkService;
import com.synova.realestate.network.model.PublisherPropertyEnt;
import com.synova.realestate.network.model.PublisherRequestEnt;

import java.util.List;

import rx.Subscription;

/**
 * Created by ducth on 7/7/15.
 */
public class SellerPropertyFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        OnRecyclerViewItemClickedListener<PublisherPropertyResponseEnt> {

    private static final int GRID_COLUMN_COUNT = 3;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rvItems;
    private PublisherPropertyAdapter houseAdapter;

    private Constants.ListLoadingState loadingState = Constants.ListLoadingState.NONE;

    private PublisherRequestEnt publisherRequestEnt;

    private static final int PAGE_LIMIT = 49;

    private ProgressBar progressBar;

    private Subscription subscription;

    @Override
    protected View onFirstTimeCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_seller_property, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.seller_property_swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.cyan);
        swipeRefreshLayout.setOnRefreshListener(this);

        rvItems = (RecyclerView) rootView.findViewById(R.id.seller_property_rvItems);
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
        houseAdapter = new PublisherPropertyAdapter();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    public void setPublisherRequestEnt(PublisherRequestEnt publisherRequestEnt) {
        this.publisherRequestEnt = publisherRequestEnt;
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

        PublisherPropertyEnt requestEnt = new PublisherPropertyEnt();
        requestEnt.cellPhoneIdI = RealEstateApplication.deviceId;
        requestEnt.publisherIdI = publisherRequestEnt.publisherId;
        requestEnt.xLocalisation = publisherRequestEnt.xLocalisation;
        requestEnt.yLocalisation = publisherRequestEnt.yLocalisation;
//        requestEnt.polygon = publisherRequestEnt.polygon;
        requestEnt.adminId = publisherRequestEnt.adminId;
        requestEnt.offsetS = houseAdapter.getItems().size() + PAGE_LIMIT;
        requestEnt.propertyTypeS = publisherRequestEnt.propertyTypeS;
        requestEnt.rentSaleS = publisherRequestEnt.rentSaleS;
        requestEnt.businessTypeS = publisherRequestEnt.businessTypeS;
        requestEnt.surfaceMinS = publisherRequestEnt.surfaceMinS;
        requestEnt.surfaceMaxS = publisherRequestEnt.surfaceMaxS;
        requestEnt.priceMinS = publisherRequestEnt.priceMinS;
        requestEnt.priceMaxS = publisherRequestEnt.priceMaxS;
        requestEnt.codePostalS = publisherRequestEnt.codePostalS;
        requestEnt.roomNumberS = publisherRequestEnt.roomNumberS;
        requestEnt.keyWordS = publisherRequestEnt.keyWordS;
        requestEnt.orderByS = publisherRequestEnt.orderByS;

        subscription = NetworkService.getPublisherProperty(requestEnt).subscribe(
                new SubscriberImpl<List<PublisherPropertyResponseEnt>>() {
                    @Override
                    public void onNext(
                            List<PublisherPropertyResponseEnt> publisherPropertyResponseEnts) {
                        houseAdapter.setItems(publisherPropertyResponseEnts);

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

        PublisherPropertyEnt requestEnt = new PublisherPropertyEnt();
        requestEnt.cellPhoneIdI = RealEstateApplication.deviceId;
        requestEnt.publisherIdI = publisherRequestEnt.publisherId;
        requestEnt.xLocalisation = publisherRequestEnt.xLocalisation;
        requestEnt.yLocalisation = publisherRequestEnt.yLocalisation;
//        requestEnt.polygon = publisherRequestEnt.polygon;
        requestEnt.adminId = publisherRequestEnt.adminId;
        requestEnt.offsetS = houseAdapter.getItems().size() + PAGE_LIMIT;
        requestEnt.propertyTypeS = publisherRequestEnt.propertyTypeS;
        requestEnt.rentSaleS = publisherRequestEnt.rentSaleS;
        requestEnt.businessTypeS = publisherRequestEnt.businessTypeS;
        requestEnt.surfaceMinS = publisherRequestEnt.surfaceMinS;
        requestEnt.surfaceMaxS = publisherRequestEnt.surfaceMaxS;
        requestEnt.priceMinS = publisherRequestEnt.priceMinS;
        requestEnt.priceMaxS = publisherRequestEnt.priceMaxS;
        requestEnt.codePostalS = publisherRequestEnt.codePostalS;
        requestEnt.roomNumberS = publisherRequestEnt.roomNumberS;
        requestEnt.keyWordS = publisherRequestEnt.keyWordS;
        requestEnt.orderByS = publisherRequestEnt.orderByS;

        subscription = NetworkService.getPublisherProperty(requestEnt).subscribe(
                new SubscriberImpl<List<PublisherPropertyResponseEnt>>() {
                    @Override
                    public void onNext(
                            List<PublisherPropertyResponseEnt> publisherPropertyResponseEnts) {
                        houseAdapter.getItems().addAll(publisherPropertyResponseEnts);
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
            PublisherPropertyResponseEnt adEnt) {
        activity.showDetailActivity(adEnt.adId);
    }
}
