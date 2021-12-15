package com.swiftdating.app.data.network;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swiftdating.app.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallServer {

    //Live
   // public static final String BaseImage = BuildConfig.SERVER_URL;//"https://prod.swiftdatingapp.com/";  //For live

    //Dev
    //public static final String BaseImage = "http://dev.swiftdatingapp.com/";  //for Dev
    //Staging
    public static final String BaseImage = "https://staging.amaredatingapp.com/"; // for stag

    public static final String BASE_URL = (BuildConfig.SERVER_URL+"api/");

    // Web Socket Url for Chat
    public static final String SocketUrl = "ws://52.54.232.96:8443";
    //                                      ws://50.17.231.64:8443 //for production
//    public static final String SocketUrl = "ws://50.17.231.64:8443";  //for staging

    /*
    * swift
 "ws://50.17.231.64:8443"      Development
 "ws://52.54.232.96:8443"       Production      Production
    * */
    //public static final String SocketUrl = "ws://54.234.31.215:8443"; //for dev

    //public static final String SocketUrl = "ws://23.21.208.109:8443";  //for 3staging
    public static String serverError = "Server not responding. Please try again later.";
    public static String somethingWentWrong = "Something went wrong Please try again later.";
    private static CallServer instance;
    private ApiUtils utils;

    /* buildConfigField "String", "SERVER_URL", '"http://34.233.197.108/api/"'*/
    //buildConfigField "String", "SERVER_URL", '"http://staging.blackgentryapp.com/api/"'
    // public static final String BaseUrl = "https://app.blackgentryapp.com/api/";
    // for stage
    //public static final String SocketUrl = "ws://54.146.135.128:8443";  1staging
    /*public static final String SocketUrl = "ws://34.233.197.108:8443";*/  /// 2staging
    //public static final String BaseImage = "http://34.233.197.108/";
    //public static final String BaseUrl = "http://192.168.43.6:3020/api/";
    //public static final String BaseImage = "http://192.168.43.6:3020/";
    /**
     * Constructor
     */
    private CallServer() {
        buildRetrofitServices();
    }

    /**
     * @return The instance of this Singleton
     */
    public static CallServer get() {
        if (instance == null) {
            synchronized (CallServer.class) {
                if (instance == null) {
                    instance = new CallServer();
                }
            }
        }
        return instance;
    }

    private void buildRetrofitServices() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();
        /*Gson gson = new GsonBuilder().serializeNulls()
                .create();*/
        this.utils = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiUtils.class);
    }

    @NonNull
    public ApiUtils getAPIName() {
        return utils;
    }

    public static Retrofit getClient() {
        Retrofit retrofit = null;
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .build();
            Gson gson = new GsonBuilder().serializeNulls().setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BASE_URL)
                    .client(client)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retrofit;
    }
}
