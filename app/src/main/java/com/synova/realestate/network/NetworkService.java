
package com.synova.realestate.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.synova.realestate.network.model.AdEnt;
import com.synova.realestate.network.model.AdsInfoEnt;
import com.synova.realestate.network.model.FavoriteEnt;
import com.synova.realestate.network.model.MapRequestEnt;
import com.synova.realestate.network.model.PublisherEnt;
import com.synova.realestate.network.model.PublisherPropertyEnt;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by ducth on 6/22/15.
 */
public class NetworkService {

    public static final String BASE_URL = "http://115.78.234.253:8080/RealEstateWS/service";

    private static RestService restService = new RestAdapter.Builder()
            .setEndpoint(BASE_URL)
            .setConverter(
                    new GsonConverter(new GsonBuilder().setFieldNamingPolicy(
                            FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()))
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build().create(RestService.class);

    private interface RestService {

        @POST("/favorite/AddFavorite")
        void addFavorite(@Body FavoriteEnt favoriteEnt, Callback<String> callback);

        @POST("/favorite/RemoveFavorite")
        void removeFavorite(@Body FavoriteEnt favoriteEnt, Callback<String> callback);

        @POST("/favorite/getList")
        void getListFavorite(@Body FavoriteEnt favoriteEnt, Callback<String> callback);

        @POST("/publisher/getList")
        void getListPublisher(@Body PublisherEnt publisherEnt, Callback<String> callback);

        @POST("/publisher/getDetails")
        void getPublisherDetail(@Body AdEnt adEnt, Callback<String> callback);

        @POST("/publisher/getProperty")
        void getPublisherProperty(@Body PublisherPropertyEnt publisherPropertyEnt,
                Callback<String> callback);

        @POST("/property/getMap")
        void getMap(@Body MapRequestEnt mapRequestEnt, Callback<String> callback);

        @POST("/property/getDetails")
        void getPropertyDetail(@Body AdEnt adEnt, Callback<String> callback);

        @POST("/property/getAdsInfo")
        void getAdsInfo(@Body AdsInfoEnt adsInfoEnt, Callback<String> callback);
    }

}
