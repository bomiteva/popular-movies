package com.android.popularmovies.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by bobi on 4/18/2016.
 */
public final class ApiService {

    private final String LOG_TAG = ApiService.class.getSimpleName();

    private static String API_END_POINT = "http://api.themoviedb.org";
    private static String API_KEY_VALUE = "3391f86c8306a66b34a63587c8c1b035";
    private static String API_KEY = "api_key";

    private static volatile ApiService instance;
    private static final Object LOCKER = new Object();

    private final ExecutorService retrofitExecutorService;
    private final Scheduler retrofitScheduler;

    private final PopularMoviesApi popularMoviesApi;


    ApiService() {
        this.retrofitExecutorService = Executors.newFixedThreadPool(3, new ThreadFactory() {
            private final int priority = Thread.NORM_PRIORITY;
            private final AtomicInteger threadNum = new AtomicInteger(0);

            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                Thread thread = new Thread(runnable);

                thread.setDaemon(true);
                thread.setPriority(priority);
                thread.setName("APIService-thread-" + threadNum.getAndIncrement());
                thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {
                        Log.e(LOG_TAG, "Error in: " + thread.getName(), ex);
                    }
                });

                return thread;
            }
        });
        this.retrofitScheduler = Schedulers.from(this.retrofitExecutorService);

        final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(180, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(180, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(180, TimeUnit.SECONDS);
        httpClientBuilder.addInterceptor(new OkHttpClientTokenInterceptor());

        OkHttpClient httpClient = httpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_END_POINT)
                .callbackExecutor(retrofitExecutorService)
                .client(httpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        popularMoviesApi = retrofit.create(PopularMoviesApi.class);
    }

    public static ApiService getInstance() {
        if (instance == null) {
            synchronized (LOCKER) {
                if (instance == null) {
                    instance = new ApiService();
                }
            }
        }

        return instance;
    }


    private static class OkHttpClientTokenInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request apiRequest = chain.request();
            HttpUrl originalHttpUrl = apiRequest.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter(API_KEY, API_KEY_VALUE)
                    .build();


            Request.Builder apiHelperBuilder = apiRequest.newBuilder();
            apiHelperBuilder
                    .url(url)
                    .method(apiRequest.method(), apiRequest.body());
            Request headerApiRequest = apiHelperBuilder.build();

            return chain.proceed(headerApiRequest);
        }
    }

    public Observable<Response<JsonElement>> getPopularMovies() {
        return popularMoviesApi.getPopularMovies();
    }


    public Observable<Response<JsonElement>> getTopRatedMovies() {
        return popularMoviesApi.getTopRatedMovies();
    }

    private interface PopularMoviesApi {
        // GET http://api.themoviedb.org/3/movie/popular
        @GET("/3/movie/popular")
        Observable<Response<JsonElement>> getPopularMovies();

        // GET http://api.themoviedb.org/3/movie/top_rated
        @GET("/3/movie/top_rated")
        Observable<Response<JsonElement>> getTopRatedMovies();

    }
}
