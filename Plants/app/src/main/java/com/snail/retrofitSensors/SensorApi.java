package com.snail.retrofitSensors;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SensorApi {

    @GET("/sensor/{id}")
    Call<Model> getSensorById(@Path("id") int id);
}
