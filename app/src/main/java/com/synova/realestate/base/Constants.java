
package com.synova.realestate.base;

import com.google.gson.annotations.SerializedName;
import com.synova.realestate.R;

/**
 * Created by ducth on 6/12/15.
 */
public class Constants {

    public static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";
    public static final String DATE_ONLY_PATTERN = "dd/MM/yyyy";
    public static final String TIME_ONLY_PATTERN = "HH:mm";

    public static final long INTERVAL = 1000 * 10;
    public static final long FASTEST_INTERVAL = 1000 * 5;
    public static final int LOCATION_CHANGE_THRESHOLD = 500;

    public static final int HOUSE_PRICE_LIMIT = 10000;

    public enum TabBar {
        GRID, LIST, LOCATION, ALERT, FAVORITE
    }

    public enum TransitionType {
        NONE(R.anim.stand_still, R.anim.no_animation),
        SLIDE_IN_RIGHT_TO_LEFT(R.anim.slide_in_right, R.anim.slide_out_left),
        SLIDE_IN_LEFT_TO_RIGHT(R.anim.slide_in_left, R.anim.slide_out_right),
        SLIDE_IN_BOTTOM(R.anim.slide_in_bottom, R.anim.stand_still),
        SLIDE_OUT_BOTTOM(R.anim.stand_still, R.anim.slide_out_bottom);

        public int transitionInResId;
        public int transitionOutResId;

        private TransitionType(int transitionInResId, int transitionOutResId) {
            this.transitionInResId = transitionInResId;
            this.transitionOutResId = transitionOutResId;
        }
    }

    public enum ElementType{
        @SerializedName("bien")
        BIEN,
        @SerializedName("agence")
        AGENCE,
        @SerializedName("particulier")
        PARTICULIER,
        @SerializedName("notaire")
        NOTAIRE
    }

    public enum ListLoadingState {
        NONE, SWIPE_REFRESH, LOAD_MORE
    }

    public enum NetworkLoadingState {
        NONE, LOADING, LOADED, LOAD_MORE, FAIL
    }

    public enum RecyclerViewType {
        HEADER, ITEM, FOOTER
    }
}
