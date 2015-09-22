
package com.synova.realestate.base;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synova.realestate.utils.PrefUtil;
import com.synova.realestate.utils.Util;
import io.fabric.sdk.android.Fabric;

/**
 * Created by ducth on 6/13/15.
 */
public class RealEstateApplication extends Application {

    public static String deviceId;

    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        Fabric.with(this, new Crashlytics());
        deviceId = Util.getDeviceID(this);
        PrefUtil.init(this);
    }

}
