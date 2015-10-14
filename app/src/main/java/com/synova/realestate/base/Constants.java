
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
        GRID(R.drawable.ico_tabbar_grid),
        LIST(R.drawable.ico_tabbar_list),
        LOCATION(R.drawable.ico_tabbar_location),
        ALERT(R.drawable.ico_tabbar_sellers_list),
        FAVORITE(R.drawable.ico_star_empty);

        private int resId;

        TabBar(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
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

    public enum ElementType {
        @SerializedName("bien")
        BIEN,
        @SerializedName("agence")
        AGENCE,
        @SerializedName("particulier")
        PARTICULIER,
        @SerializedName("notaire")
        NOTAIRE
    }

    public enum AchatLocation {
        ACHAT("true"),
        LOCATION("false");

        private String paramName;

        AchatLocation(String paramName) {
            this.paramName = paramName;
        }

        public String getParamName() {
            return paramName;
        }
    }

    public enum PropertyType {
        /** For UI purpose only */
        NONE(""),

        ALL("All"),
        @SerializedName("Appartement")
        APPARTEMENT("Appartement"),
        @SerializedName("Maison")
        MAISON("Maison"),
        @SerializedName("Parking")
        PARKING("Parking"),
        @SerializedName("Bureau")
        BUREAU("Bureau"),
        @SerializedName("Terrain")
        TERRAIN("Terrain"),
        @SerializedName("Commerce")
        COMMERCE("Commerce"),
        @SerializedName("Loft")
        LOFT("Loft"),
        @SerializedName("Immeuble")
        IMMEUBLE("Immeuble"),
        @SerializedName("Château")
        CHATEAU("Château"),
        @SerializedName("Bâtiment")
        BATIMENT("Bâtiment"),
        @SerializedName("Hôtel particulier")
        HOTEL_PARTICULIER("Hôtel particulier"),
        @SerializedName("Autre")
        AUTRE("Autre");

        private String name;

        PropertyType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum FilterOrderType {
        @SerializedName("distance")
        DISTANCE_ASC,
        @SerializedName("distance desc")
        DISTANCE_DESC,
        @SerializedName("price")
        PRICE_ASC,
        @SerializedName("price desc")
        PRICE_DESC,
        @SerializedName("extract_time")
        DATE_ASC,
        @SerializedName("extract_time desc")
        DATE_DESC,
    }

    public enum ListLoadingState {
        NONE,
        SWIPE_REFRESH,
        LOAD_MORE
    }

    public enum NetworkLoadingState {
        NONE,
        LOADING,
        LOADED,
        LOAD_MORE,
        FAIL
    }

    public enum RecyclerViewType {
        HEADER,
        ITEM,
        FOOTER
    }
}
