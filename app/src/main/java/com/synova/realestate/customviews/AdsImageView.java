
package com.synova.realestate.customviews;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.synova.realestate.R;
import com.synova.realestate.utils.Util;

/**
 * Created by ducth on 6/13/15.
 */
public class AdsImageView extends SimpleDraweeView {

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
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setPlaceholderImage(getResources().getDrawable(R.drawable.img_ads_banner),
                        ScalingUtils.ScaleType.FIT_XY)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        setHierarchy(hierarchy);
    }

    public void setAdsUrl(String url) {
        if (!Util.isNullOrEmpty(url)) {
            setImageURI(Uri.parse(url));
        }
    }
}
