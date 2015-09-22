
package com.synova.realestate.network;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Cache;
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
import com.synova.realestate.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.ErrorHandler;
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
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ducth on 6/22/15.
 */
public class NetworkService {

    private static final String TAG = NetworkService.class.getSimpleName();

    public static final String BASE_URL = "http://37.187.43.23:8080/RealEstateWS/service";

    private interface RestService {
        @POST("/property/getAdsInfo")
        Observable<JsonElement> getAdsInfo(@Body AdsInfoEnt adsInfoEnt);

        @POST("/property/getDetails")
        Observable<JsonElement> getPropertyDetail(@Body AdEnt adEnt);

        @POST("/property/getMap")
        Observable<List<MapResponseEnt>> getMap(@Body MapRequestEnt mapRequestEnt);

        @POST("/publisher/getList")
        Observable<JsonElement> getListPublisher(@Body PublisherRequestEnt publisherRequestEnt);

        @POST("/publisher/getDetails")
        Observable<JsonElement> getPublisherDetails(@Body AdEnt adEnt);

        @POST("/publisher/getProperty")
        Observable<JsonElement> getPublisherProperty(@Body PublisherPropertyEnt publisherPropertyEnt);

        @POST("/favorite/addFavorite")
        Observable<Response> addFavorite(@Body FavoriteEnt favoriteEnt);

        @POST("/favorite/removeFavorite")
        Observable<Response> removeFavorite(@Body FavoriteEnt favoriteEnt);

        @POST("/favorite/getList")
        Observable<JsonElement> getListFavorite(@Body FavoriteEnt favoriteEnt);

    }

    private static RestService restService = new RestAdapter.Builder()
            .setEndpoint(BASE_URL)
            .setConverter(new GsonConverter(RealEstateApplication.GSON))
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setClient(
                    new OkClient(new OkHttpClient().setCache(new Cache(new File(System
                            .getProperty("java.io.tmpdir"), "okhttp-cache"), 10L * 1024 * 1024))))
            .setErrorHandler(new ErrorHandler() {
                @Override
                public Throwable handleError(RetrofitError cause) {
                    LogUtil.e(TAG, cause);
                    return null;
                }
            })
            .build().create(RestService.class);

    public static Observable<List<AdsInfoResponseEnt>> getAdsInfo(AdsInfoEnt adsInfoEnt) {
        return request(restService.getAdsInfo(adsInfoEnt)).flatMap(
                new Func1<JsonElement, Observable<List<AdsInfoResponseEnt>>>() {
                    @Override
                    public Observable<List<AdsInfoResponseEnt>> call(JsonElement jsonElement) {
                        List<AdsInfoResponseEnt> result = RealEstateApplication.GSON.fromJson(
                                jsonElement,
                                new TypeToken<List<AdsInfoResponseEnt>>() {
                                }.getType());
                        if (result == null) {
                            result = new ArrayList<>();
                        }
                        return Observable.just(result);
                    }
                });
    }

    public static Observable<List<MapResponseEnt>> getMap(final MapRequestEnt mapRequestEnt) {
        return request(restService.getMap(mapRequestEnt));
    }

    public static Observable<AdsDetailEnt> getPropertyDetails(AdEnt adEnt) {
        return request(restService.getPropertyDetail(adEnt)).flatMap(
                new Func1<JsonElement, Observable<AdsDetailEnt>>() {
                    @Override
                    public Observable<AdsDetailEnt> call(JsonElement jsonElement) {
                        AdsDetailEnt adsDetailEnt = RealEstateApplication.GSON.fromJson(
                                jsonElement, AdsDetailEnt.class);
                        if (adsDetailEnt == null) {
                            adsDetailEnt = new AdsDetailEnt();
                        }
                        return Observable.just(adsDetailEnt);
                    }
                });
    }

    public static Observable<List<Publisher>> getListPublisher(
            PublisherRequestEnt publisherRequestEnt) {
        return request(restService.getListPublisher(publisherRequestEnt)).flatMap(
                new Func1<JsonElement, Observable<List<Publisher>>>() {
                    @Override
                    public Observable<List<Publisher>> call(JsonElement jsonElement) {
                        List<Publisher> publishers = RealEstateApplication.GSON.fromJson(
                                jsonElement,
                                new TypeToken<List<Publisher>>() {
                                }.getType());
                        if (publishers == null) {
                            publishers = new ArrayList<>();
                        }
                        return Observable.just(publishers);
                    }
                });
    }

    public static Observable<List<PublisherPropertyResponseEnt>> getPublisherProperty(
            PublisherPropertyEnt publisherPropertyEnt) {
        return request(restService.getPublisherProperty(publisherPropertyEnt)).flatMap(
                new Func1<JsonElement, Observable<List<PublisherPropertyResponseEnt>>>() {
                    @Override
                    public Observable<List<PublisherPropertyResponseEnt>> call(
                            JsonElement jsonElement) {
                        List<PublisherPropertyResponseEnt> publisherPropertyResponseEnts = RealEstateApplication.GSON
                                .fromJson(jsonElement,
                                        new TypeToken<List<PublisherPropertyResponseEnt>>() {
                                        }.getType());
                        if (publisherPropertyResponseEnts == null) {
                            publisherPropertyResponseEnts = new ArrayList<>();
                        }
                        return Observable.just(publisherPropertyResponseEnts);
                    }
                });
    }

    public static Observable<List<PublisherDetailEnt>> getPublisherDetails(AdEnt adEnt) {
        return request(restService.getPublisherDetails(adEnt)).flatMap(
                new Func1<JsonElement, Observable<List<PublisherDetailEnt>>>() {
                    @Override
                    public Observable<List<PublisherDetailEnt>> call(JsonElement jsonElement) {
                        List<PublisherDetailEnt> detailEnts = RealEstateApplication.GSON.fromJson(
                                jsonElement,
                                new TypeToken<List<PublisherDetailEnt>>() {
                                }.getType());
                        if (detailEnts == null) {
                            detailEnts = new ArrayList<>();
                        }
                        return Observable.just(detailEnts);
                    }
                });
    }

    public static Observable<Boolean> addFavorite(String propertyId) {
        return request(
                restService
                        .addFavorite(new FavoriteEnt(RealEstateApplication.deviceId, propertyId)))
                .flatMap(
                        new Func1<Response, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(Response response) {
                                String responseString = new String(
                                        ((TypedByteArray) response.getBody()).getBytes());

                                return Observable.just(responseString.contains("success"));
                            }
                        });
    }

    public static Observable<Boolean> removeFavorite(String propertyId) {
        return request(
                restService.removeFavorite(new FavoriteEnt(RealEstateApplication.deviceId,
                        propertyId))).flatMap(
                new Func1<Response, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Response response) {
                        String responseString = new String(
                                ((TypedByteArray) response.getBody()).getBytes());

                        return Observable.just(responseString.contains("success"));
                    }
                });
    }

    public static Observable<List<AdsInfoResponseEnt>> getListFavorite() {
        return request(
                restService.getListFavorite(new FavoriteEnt(RealEstateApplication.deviceId, "-1")))
                .flatMap(
                        new Func1<JsonElement, Observable<List<AdsInfoResponseEnt>>>() {
                            @Override
                            public Observable<List<AdsInfoResponseEnt>> call(JsonElement jsonElement) {
                                List<AdsInfoResponseEnt> listFavorite = RealEstateApplication.GSON
                                        .fromJson(jsonElement,
                                                new TypeToken<List<AdsInfoResponseEnt>>() {
                                                }.getType());
                                if (listFavorite == null) {
                                    listFavorite = new ArrayList<>();
                                }
                                return Observable.just(listFavorite);
                            }
                        });
    }

    private static <T> Observable<T> request(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.newThread()).observeOn(
                AndroidSchedulers.mainThread());
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
