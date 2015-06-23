
package com.synova.realestate.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synova.realestate.network.model.AdEnt;
import com.synova.realestate.network.model.FavoriteEnt;
import com.synova.realestate.network.model.MapRequestEnt;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by ducth on 6/22/15.
 */
public class NetworkService {

    public static final String BASE_URL = "http://api.tv.zing.vn";

    private static RestService restService = new RestAdapter.Builder()
            .setEndpoint(BASE_URL)
            .setConverter(
                    new GsonConverter(new GsonBuilder().setFieldNamingPolicy(
                            FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()))
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build().create(RestService.class);

    private interface RestService {

        // @POST("/RealEstateWS/service/property/getMap")
        // List<Repo> getMap(@Path("user") String user);

        @POST("/RealEstateWS/service/favorite/AddFavorite")
        void addFavorite(@Body FavoriteEnt favoriteEnt, Callback<String> callback);

        @POST("/RealEstateWS/service/favorite/RemoveFavorite")
        void removeFavorite(@Body FavoriteEnt favoriteEnt, Callback<String> callback);

        @POST("/RealEstateWS/service/publisher/getDetails")
        void getPublisherDetail(@Body AdEnt adEnt, Callback<String> callback);

        @POST("RealEstateWS/service/property/getDetails")
        void getPropertyDetail(@Body AdEnt adEnt, Callback<String> callback);

        @POST("RealEstateWS/service/property/getMap")
        void getMap(@Body MapRequestEnt mapRequestEnt, Callback<String> callback);
    }

}
