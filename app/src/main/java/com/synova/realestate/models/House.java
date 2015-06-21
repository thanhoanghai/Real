
package com.synova.realestate.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ducth on 6/13/15.
 */
public class House {

    @SerializedName("adId")
    public long id;
    public String title;
    public float surface;
    public float price;
    @SerializedName("propertyType")
    public HouseType type;
    @SerializedName("nbpieces")
    public int pieces;
    @SerializedName("imgurl")
    public String photo;
    public String distance;

    public enum HouseType {
        BIEN, AGENCE, PARTICULIER, NOTAIRE
    }
}
