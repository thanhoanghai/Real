
package com.synova.realestate.models;

import com.google.gson.annotations.SerializedName;
import com.synova.realestate.base.Constants;

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
    public Constants.ElementType type;
    @SerializedName("nbpieces")
    public int pieces;
    @SerializedName("imgurl")
    public String photo;
    public String distance;

}
