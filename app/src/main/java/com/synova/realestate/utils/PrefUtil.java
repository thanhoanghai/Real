
package com.synova.realestate.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.RealEstateApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 12/06/2015.
 */
public class PrefUtil {

    private static final String TAG = PrefUtil.class.getSimpleName();

    private static final String PREFERENCE_NAME = "RealEstate-Prefs";
    private static final String KEY_ACHAT_LOCATION = "1";
    private static final String KEY_MOTS_CLES = "2";
    private static final String KEY_TYPE_DE_BIENS = "3";
    private static final String KEY_DISTANCE = "4";
    private static final String KEY_PRIX_MIN_MAX = "5";
    private static final String KEY_SURFACE_MIN_MAX = "6";
    private static final String KEY_ORDER_BY = "7";
    private static final String KEY_ROOM_NUMBERS = "8";
    private static final String KEY_POSTAL_CODE = "9";

    private static SharedPreferences pref = null;

    public static void init(Context context) {
        pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static void setAchatLocation(Constants.AchatLocation type) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_ACHAT_LOCATION, type.name());
        editor.commit();
    }

    public static Constants.AchatLocation getAchatLocation() {
        String type = pref.getString(KEY_ACHAT_LOCATION, Constants.AchatLocation.LOCATION.name());
        return Constants.AchatLocation.valueOf(type);
    }

    public static void setMotsCles(String motsCles) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_MOTS_CLES, motsCles);
        editor.commit();
    }

    public static String getMotsCles() {
        return pref.getString(KEY_MOTS_CLES, "");
    }

    public static void setTypeDeBiens(List<Constants.PropertyType> types) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_TYPE_DE_BIENS, RealEstateApplication.GSON.toJson(types));
        editor.commit();
    }

    public static List<Constants.PropertyType> getTypeDeBiens() {
        String json = pref.getString(KEY_TYPE_DE_BIENS, "[\"Appartement\"]");
        List<Constants.PropertyType> types;
        try {
            types = RealEstateApplication.GSON.fromJson(json,
                    new TypeToken<List<Constants.PropertyType>>() {
                    }.getType());
        } catch (Exception e) {
            types = new ArrayList<>();
            LogUtil.e(TAG, e);
        }

        return types;
    }

    public static void setDistance(String distance) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_DISTANCE, distance);
        editor.commit();
    }

    public static String getDistance() {
        return pref.getString(KEY_DISTANCE, "0");
    }

    public static void setPrixMinMax(String prixMinMax) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_PRIX_MIN_MAX, prixMinMax);
        editor.commit();
    }

    public static String getPrixMinMax() {
        return pref.getString(KEY_PRIX_MIN_MAX, " - ");
    }

    public static void setSurfaceMinMax(String surfaceMinMax) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_SURFACE_MIN_MAX, surfaceMinMax);
        editor.commit();
    }

    public static String getSurfaceMinMax() {
        return pref.getString(KEY_SURFACE_MIN_MAX, " - ");
    }

    public static void setOrderBy(Constants.FilterOrderType orderBy) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_ORDER_BY, RealEstateApplication.GSON.toJson(orderBy));
        editor.commit();
    }

    public static Constants.FilterOrderType getOrderBy() {
        String orderBy = pref.getString(KEY_ORDER_BY,
                RealEstateApplication.GSON.toJson(Constants.FilterOrderType.DISTANCE_ASC));
        return RealEstateApplication.GSON.fromJson(orderBy, Constants.FilterOrderType.class);
    }

    public static void setRoomNumbers(int roomNumbers) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(KEY_ROOM_NUMBERS, roomNumbers);
        editor.commit();
    }

    public static int getRoomNumbers() {
        return pref.getInt(KEY_ROOM_NUMBERS, 1);
    }

    public static String getPostalCode() {
        return pref.getString(KEY_POSTAL_CODE, "");
    }

    public static void setPostalCode(String postalCode) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_POSTAL_CODE, postalCode);
        editor.commit();
    }
}
