
package com.synova.realestate.base;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.synova.realestate.R;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.models.eventbus.LocationSettingsAllowanceEvent;
import com.synova.realestate.models.eventbus.ReceivedCurrentLocationEvent;
import com.synova.realestate.utils.PrefUtil;

import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;
import rx.Subscription;

/**
 * Created by ducth on 6/17/15.
 */
public class SplashActivity extends Activity {

    private CountDownLatch countDownLatch = new CountDownLatch(2);
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LocationService.getInstance().checkLocationSettings(this, true);

        final SimpleDraweeView splashView = (SimpleDraweeView) findViewById(R.id.splashView);
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(R.drawable.anim_splash))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        splashView.setController(controller);

        splashView.postDelayed(new Runnable() {
            @Override
            public void run() {
                countDownLatch.countDown();
                checkToNavigateToNextScreen();
            }
        }, 5000);

        AdsInfoResponseEnt.deleteOldReadAds();
    }

    private void getLocation() {
        subscription = LocationService.getInstance().getDefaultLocationUpdate(this)
                .subscribe(new SubscriberImpl<Location>() {
                    @Override
                    public void onNext(Location location) {
                        RealEstateApplication.currentLocation = location;
                        RealEstateApplication.isMyLocationParis = false;

                        if (PrefUtil.getPostalCode().length() == 0) {
                            LocationService
                                    .getInstance()
                                    .getAddressFromLocation(SplashActivity.this,
//                                            48.9306, 2.1475
                                            location.getLatitude(), location.getLongitude()
                                    )
                                    .subscribe(new SubscriberImpl<Address>() {
                                        @Override
                                        public void onNext(Address address) {
                                            String postalCode = address.getPostalCode();
                                            if (postalCode != null && postalCode.length() > 0) {
                                                PrefUtil.setPostalCode(postalCode);
                                            }

                                            navigateToNextScreen();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            navigateToNextScreen();
                                        }
                                    });
                        } else {
                            navigateToNextScreen();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        navigateToNextScreen();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LocationService.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onStop();
    }

    public void onEventMainThread(LocationSettingsAllowanceEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        if (event.isLocationEnabled) {
            getLocation();
        } else {
            EventBus.getDefault().postSticky(new ReceivedCurrentLocationEvent());
            RealEstateApplication.isMyLocationParis = true;

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    private void navigateToNextScreen() {
        EventBus.getDefault().postSticky(new ReceivedCurrentLocationEvent());

        countDownLatch.countDown();
        checkToNavigateToNextScreen();
    }

    private void checkToNavigateToNextScreen() {
        if (countDownLatch.getCount() == 0) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // Prevent press back key
    }
}
