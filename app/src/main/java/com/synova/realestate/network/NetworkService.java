
package com.synova.realestate.network;

import com.synova.realestate.models.network.AdEnt;
import com.synova.realestate.models.network.FavoriteEnt;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by ducth on 6/22/15.
 */
public class NetworkService {

    public static final String BASE_URL = "http://api.tv.zing.vn";

    private static RestService restService = new RestAdapter.Builder()
            .setEndpoint(BASE_URL)
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

    }

}
