
package com.synova.realestate.base;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.synova.realestate.models.eventbus.LocationSettingsAllowanceEvent;
import com.synova.realestate.utils.LogUtil;

import de.greenrobot.event.EventBus;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by ducth on 10/11/15.
 */
public class LocationService {

    private static final String TAG = LocationService.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 1000;

    private static LocationService locationService;

    public static LocationService getInstance() {
        if (locationService == null) {
            locationService = new LocationService();
        }
        return locationService;
    }

    public void checkLocationSettings(final Activity activity) {
        final LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(5)
                .setSmallestDisplacement(10)
                .setInterval(100);

        final ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(activity);
        locationProvider
                .checkLocationSettings(
                        new LocationSettingsRequest.Builder()
                                .addLocationRequest(request)
                                .setAlwaysShow(true)
                                .build()
                ).doOnNext(new Action1<LocationSettingsResult>() {
                    @Override
                    public void call(LocationSettingsResult locationSettingsResult) {
                        Status status = locationSettingsResult.getStatus();
                        if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException th) {
                                LogUtil.e(TAG, th);
                            }
                        } else if (status.getStatusCode() == LocationSettingsStatusCodes.SUCCESS) {
                            EventBus.getDefault().postSticky(
                                    new LocationSettingsAllowanceEvent(true));
                        }
                    }
                }).subscribe(new SubscriberImpl<LocationSettingsResult>());
    }

    public Observable<Location> getLastKnownLocation(Activity activity) {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(activity);
        return prepareObservable(locationProvider.getLastKnownLocation());
    }

    public Observable<Location> getDefaultLocationUpdate(Activity activity) {
        return getUpdatedLocation(activity, LocationRequest.PRIORITY_HIGH_ACCURACY, 5,
                10, 100);
    }

    public Observable<Location> getUpdatedLocation(final Activity activity, int priority,
            int numUpdates,
            float smallestDisplacement, long interval) {
        LocationRequest request = LocationRequest.create()
                .setPriority(priority)
                .setNumUpdates(numUpdates)
                .setSmallestDisplacement(smallestDisplacement)
                .setInterval(interval);

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(activity);
        return locationProvider.getUpdatedLocation(request);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);// intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                // Refrence:
                // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsApi
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.d(TAG, "User enabled location");
                        EventBus.getDefault().postSticky(new LocationSettingsAllowanceEvent(true));
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d(TAG, "User Cancelled enabling location");
                        EventBus.getDefault().postSticky(new LocationSettingsAllowanceEvent(false));
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private <T> Observable<T> prepareObservable(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.newThread()).observeOn(
                AndroidSchedulers.mainThread());
    }
}
