
package com.synova.realestate.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by ducth on 6/13/15.
 */
public class AdsImageView extends ImageView {

    public AdsImageView(Context context) {
        super(context);
        init();
    }

    public AdsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdsImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setScaleType(ScaleType.CENTER_CROP);
    }

    public void setAdsUrl(String url) {
        ImageLoader.getInstance().displayImage(url, this);
    }
}
