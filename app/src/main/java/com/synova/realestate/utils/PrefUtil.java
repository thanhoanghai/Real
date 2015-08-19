
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

    private static final String PREFERENCE_NAME = "RealEstate-Prefs";
    private static final String KEY_ACHAT_LOCATION = "1";
    private static final String KEY_MOTS_CLES = "2";
    private static final String KEY_TYPE_DE_BIENS = "3";
    private static final String KEY_DISTANCE = "4";
    private static final String KEY_PRIX_MIN_MAX = "5";
    private static final String KEY_SURFACE_MIN_MAX = "6";

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
        String json = pref.getString(KEY_TYPE_DE_BIENS, "");
        if (json.length() == 0) {
            List<Constants.PropertyType> types = new ArrayList<>();
            types.add(Constants.PropertyType.ALL);
            return types;
        }

        return RealEstateApplication.GSON.fromJson(json,
                new TypeToken<List<Constants.PropertyType>>() {
                }.getType());
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
        return pref.getString(KEY_PRIX_MIN_MAX, "200-600");
    }

    public static void setSurfaceMinMax(String surfaceMinMax) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_SURFACE_MIN_MAX, surfaceMinMax);
        editor.commit();
    }

    public static String getSurfaceMinMax() {
        return pref.getString(KEY_SURFACE_MIN_MAX, "10-300");
    }
}
