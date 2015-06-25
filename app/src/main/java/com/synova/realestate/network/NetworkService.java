
package com.synova.realestate.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.synova.realestate.network.model.AdEnt;
import com.synova.realestate.network.model.AdsInfoEnt;
import com.synova.realestate.network.model.FavoriteEnt;
import com.synova.realestate.network.model.MapRequestEnt;
import com.synova.realestate.network.model.PublisherRequestEnt;
import com.synova.realestate.network.model.PublisherPropertyEnt;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by ducth on 6/22/15.
 */
public class NetworkService {

    public static final String BASE_URL = "http://115.78.234.253:8080/RealEstateWS/service";
    private static final Gson GSON = new Gson();

    private static RestService restService = new RestAdapter.Builder()
            .setEndpoint(BASE_URL)
            .setConverter(
                    new GsonConverter(new GsonBuilder().setFieldNamingPolicy(
                            FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()))
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build().create(RestService.class);

    public static void addFavorite(String deviceId, String propertyId,
            final NetworkCallback<Boolean> callback) {
        restService.addFavorite(new FavoriteEnt(deviceId, propertyId),
                new NetworkCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (callback != null) {
                            if (s != null && s.length() > 0) {
                                callback.onSuccess(true);
                            } else {
                                callback.onFail(new Exception("Fail to add favorite."));
                            }
                        }
                    }
                });
    }

    public static void removeFavorite(String deviceId, String propertyId,
            final NetworkCallback<Boolean> callback) {
        restService.removeFavorite(new FavoriteEnt(deviceId, propertyId),
                new NetworkCallback<String>() {

                    @Override
                    public void onSuccess(String s) {
                        if (callback != null) {
                            if (s != null && s.length() > 0) {
                                callback.onSuccess(true);
                            } else {
                                callback.onFail(new Exception("Fail to remove favorite."));
                            }
                        }
                    }
                });
    }

    public static void getListFavorite(String deviceId, String propertyId,
            final NetworkCallback<List<String>> callback) {
        restService.getListFavorite(new FavoriteEnt(deviceId, propertyId),
                new NetworkCallback<JsonElement>() {

                    class GetListFavoriteResult {
                        String result;
                    }

                    @Override
                    public void onSuccess(JsonElement jsonElement) {
                        if (callback != null) {
                            if (jsonElement != null) {
                                List<GetListFavoriteResult> listResults = GSON.fromJson(
                                        jsonElement,
                                        new TypeToken<List<GetListFavoriteResult>>() {
                                        }.getType());
                                List<String> publisherIds = new ArrayList<>(listResults.size());
                                for (GetListFavoriteResult result : listResults) {
                                    publisherIds.add(result.result);
                                }

                                callback.onSuccess(publisherIds);
                            } else {
                                callback.onFail(new Exception("Fail to get list favorite."));
                            }
                        }
                    }
                });
    }

    public static void getListPublisher(PublisherRequestEnt publisherRequestEnt, NetworkCallback<JsonElement> callback) {

    }

    private interface RestService {

        @POST("/favorite/AddFavorite")
        void addFavorite(@Body FavoriteEnt favoriteEnt, NetworkCallback<String> callback);

        @POST("/favorite/RemoveFavorite")
        void removeFavorite(@Body FavoriteEnt favoriteEnt, NetworkCallback<String> callback);

        @POST("/favorite/getList")
        void getListFavorite(@Body FavoriteEnt favoriteEnt, NetworkCallback<JsonElement> callback);

        @POST("/publisher/getList")
        void getListPublisher(@Body PublisherRequestEnt publisherRequestEnt, NetworkCallback<JsonElement> callback);

        @POST("/publisher/getDetails")
        void getPublisherDetail(@Body AdEnt adEnt, NetworkCallback<String> callback);

        @POST("/publisher/getProperty")
        void getPublisherProperty(@Body PublisherPropertyEnt publisherPropertyEnt,
                NetworkCallback<String> callback);

        @POST("/property/getMap")
        void getMap(@Body MapRequestEnt mapRequestEnt, NetworkCallback<String> callback);

        @POST("/property/getDetails")
        void getPropertyDetail(@Body AdEnt adEnt, NetworkCallback<String> callback);

        @POST("/property/getAdsInfo")
        void getAdsInfo(@Body AdsInfoEnt adsInfoEnt, NetworkCallback<String> callback);
    }

    public static abstract class NetworkCallback<T> implements Callback<T> {

        @Override
        public void success(T t, Response response) {
            onSuccess(t);
        }

        @Override
        public void failure(RetrofitError error) {
        }

        public abstract void onSuccess(T t);

        public void onFail(Throwable error) {
        }
    }

}
