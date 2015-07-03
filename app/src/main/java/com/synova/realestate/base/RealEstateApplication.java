
package com.synova.realestate.base;

import android.app.Application;
import android.graphics.Bitmap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.synova.realestate.R;
import com.synova.realestate.utils.Util;

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
        initImageLoader();
        deviceId = Util.getDeviceID(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImageLoader.getInstance().clearMemoryCache();
    }

    private void initImageLoader() {
        DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .delayBeforeLoading(500)
                .showImageForEmptyUri(R.drawable.img_default_list)
                .showImageOnLoading(R.drawable.img_default_list)
                .showImageOnFail(R.drawable.img_default_list)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultDisplayImageOptions)
                .build();

        ImageLoader.getInstance().init(config);
    }
}
