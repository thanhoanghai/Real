
package com.synova.realestate.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.synova.realestate.R;

/**
 * Created by ducth on 6/17/15.
 */
public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SimpleDraweeView splashView = (SimpleDraweeView) findViewById(R.id.splashView);
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(R.drawable.splash))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                // .setControllerListener(new BaseControllerListener<ImageInfo>() {
                //
                // @Override
                // public void onFinalImageSet(String id, ImageInfo imageInfo,
                // Animatable animatable) {
                // super.onFinalImageSet(id, imageInfo, animatable);
                // if (animatable != null) {
                // animatable.start();
                // }
                //
                // }
                // })
                .build();
        splashView.setController(controller);

        splashView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, 5000);
    }

    @Override
    public void onBackPressed() {
        // Prevent press back key
    }
}
