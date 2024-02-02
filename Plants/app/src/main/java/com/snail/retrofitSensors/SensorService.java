package com.snail.retrofitSensors;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SensorService {
    private static SensorService mInstance;
    private static final String BASE_URL = "http://10.130.204.66:8080";
    private Retrofit mRetrofit;

    private SensorService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static SensorService getInstance() {
        if (mInstance == null) {
            mInstance = new SensorService();
        }
        return mInstance;
    }

    public SensorApi getSensorApi() {
        return mRetrofit.create(SensorApi.class);
    }
}

