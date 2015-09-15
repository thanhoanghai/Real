
package com.synova.realestate.network;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.synova.realestate.base.RealEstateApplication;
import com.synova.realestate.models.AdsDetailEnt;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.models.MapResponseEnt;
import com.synova.realestate.models.Publisher;
import com.synova.realestate.models.PublisherPropertyResponseEnt;
import com.synova.realestate.network.model.AdEnt;
import com.synova.realestate.network.model.AdsInfoEnt;
import com.synova.realestate.network.model.FavoriteEnt;
import com.synova.realestate.network.model.MapRequestEnt;
import com.synova.realestate.network.model.PublisherDetailEnt;
import com.synova.realestate.network.model.PublisherPropertyEnt;
import com.synova.realestate.network.model.PublisherRequestEnt;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.mime.TypedByteArray;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ducth on 6/22/15.
 */
public class NetworkService {

    public static final String BASE_URL = "http://115.78.234.253:8080/RealEstateWS/service";

    private static RestService restService = new RestAdapter.Builder()
            .setEndpoint(BASE_URL)
            .setConverter(new GsonConverter(RealEstateApplication.GSON))
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setClient(new OkClient(new OkHttpClient()))
            .build().create(RestService.class);

    public static void getAdsInfo(AdsInfoEnt adsInfoEnt,
            final Callback<List<AdsInfoResponseEnt>> callback) {
        restService.getAdsInfo(adsInfoEnt, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement jsonElement, Response response) {
                List<AdsInfoResponseEnt> result = RealEstateApplication.GSON.fromJson(jsonElement,
                        new TypeToken<List<AdsInfoResponseEnt>>() {
                        }.getType());
                if (callback != null) {
                    callback.success(result, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    public static Observable<List<MapResponseEnt>> getMap(final MapRequestEnt mapRequestEnt) {
        return restService.getMap(mapRequestEnt).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void getPropertyDetails(AdEnt adEnt, final NetworkCallback<AdsDetailEnt> callback) {
        restService.getPropertyDetail(adEnt, new NetworkCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                if (callback != null) {
                    if (jsonElement != null) {
                        AdsDetailEnt adsDetailEnt = RealEstateApplication.GSON.fromJson(
                                jsonElement, AdsDetailEnt.class);
                        callback.onSuccess(adsDetailEnt);
                    } else {
                        callback.onFail(new Exception("Fail to get Ads details."));
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    public static void getListPublisher(PublisherRequestEnt publisherRequestEnt,
            final NetworkCallback<List<Publisher>> callback) {
        restService.getListPublisher(publisherRequestEnt, new NetworkCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                List<Publisher> publishers = RealEstateApplication.GSON.fromJson(jsonElement,
                        new TypeToken<List<Publisher>>() {
                        }.getType());
                if (callback != null) {
                    callback.onSuccess(publishers);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    public static void getPublisherProperty(PublisherPropertyEnt publisherPropertyEnt,
            final NetworkCallback<List<PublisherPropertyResponseEnt>> callback) {
        restService.getPublisherProperty(publisherPropertyEnt, new NetworkCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                List<PublisherPropertyResponseEnt> publisherPropertyResponseEnts = RealEstateApplication.GSON
                        .fromJson(jsonElement, new TypeToken<List<PublisherPropertyResponseEnt>>() {
                        }.getType());
                if (callback != null) {
                    callback.onSuccess(publisherPropertyResponseEnts);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    public static void getPublisherDetails(AdEnt adEnt,
            final NetworkCallback<List<PublisherDetailEnt>> callback) {
        restService.getPublisherDetails(adEnt, new NetworkCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                List<PublisherDetailEnt> detailEnts = RealEstateApplication.GSON.fromJson(
                        jsonElement,
                        new TypeToken<List<PublisherDetailEnt>>() {
                        }.getType());
                if (callback != null) {
                    callback.onSuccess(detailEnts);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    public static void addFavorite(String deviceId, String propertyId,
            final NetworkCallback<Boolean> callback) {
        deviceId = "1";
        restService.addFavorite(new FavoriteEnt(deviceId, propertyId),
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        if (callback != null) {
                            String responseString = new String(
                                    ((TypedByteArray) response.getBody()).getBytes());
                            if (responseString.contains("success")) {
                                callback.onSuccess(true);
                            } else {
                                callback.onFail(new Exception("Fail to add favorite."));
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
    }

    public static void removeFavorite(String deviceId, String propertyId,
            final NetworkCallback<Boolean> callback) {
        deviceId = "1";
        restService.removeFavorite(new FavoriteEnt(deviceId, propertyId),
                new NetworkCallback<Response>() {

                    @Override
                    public void onSuccess(Response response) {
                        if (callback != null) {
                            if (callback != null) {
                                String responseString = new String(
                                        ((TypedByteArray) response.getBody()).getBytes());
                                if (responseString.contains("success")) {
                                    callback.onSuccess(true);
                                } else {
                                    callback.onFail(new Exception("Fail to remove favorite."));
                                }
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
    }

    public static void getListFavorite(String deviceId, final NetworkCallback<List<String>> callback) {
        deviceId = "1";
        restService.getListFavorite(new FavoriteEnt(deviceId, "-1"),
                new NetworkCallback<JsonElement>() {

                    class GetListFavoriteResult {
                        String result;
                    }

                    @Override
                    public void onSuccess(JsonElement jsonElement) {
                        if (callback != null) {
                            if (jsonElement != null) {
                                List<GetListFavoriteResult> listResults = RealEstateApplication.GSON
                                        .fromJson(
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

                    @Override
                    public void failure(RetrofitError error) {
                        if (callback != null) {
                            callback.onFail(error);
                        }
                    }
                });
    }

    private interface RestService {

        @POST("/property/getAdsInfo")
        void getAdsInfo(@Body AdsInfoEnt adsInfoEnt, Callback<JsonElement> callback);

        @POST("/property/getDetails")
        void getPropertyDetail(@Body AdEnt adEnt, NetworkCallback<JsonElement> callback);

        @POST("/property/getMap")
        Observable<List<MapResponseEnt>> getMap(@Body MapRequestEnt mapRequestEnt);

        @POST("/publisher/getList")
        void getListPublisher(@Body PublisherRequestEnt publisherRequestEnt,
                NetworkCallback<JsonElement> callback);

        @POST("/publisher/getDetails")
        void getPublisherDetails(@Body AdEnt adEnt, NetworkCallback<JsonElement> callback);

        @POST("/publisher/getProperty")
        void getPublisherProperty(@Body PublisherPropertyEnt publisherPropertyEnt,
                NetworkCallback<JsonElement> callback);

        @POST("/favorite/addFavorite")
        void addFavorite(@Body FavoriteEnt favoriteEnt, NetworkCallback<Response> callback);

        @POST("/favorite/removeFavorite")
        void removeFavorite(@Body FavoriteEnt favoriteEnt, NetworkCallback<Response> callback);

        @POST("/favorite/getList")
        void getListFavorite(@Body FavoriteEnt favoriteEnt, NetworkCallback<JsonElement> callback);

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
