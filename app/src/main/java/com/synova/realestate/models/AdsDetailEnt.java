
package com.synova.realestate.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ducth on 7/2/15.
 */
public class AdsDetailEnt {

    @SerializedName("getADCharacFromId")
    public List<AdCharac> characs;

    @SerializedName("getImagesFromId")
    public List<AdImage> images;

    public class AdCharac {
        public int codePostal;
        public boolean isFavorite;
        public String businessType;
        public String localisation;
        public String detailCharac;
        public String minMaxPrice;
        public String description;
        public String title;
        public String rentSale;
    }

    public class AdImage {
        public String imagesUrl;
    }
}
