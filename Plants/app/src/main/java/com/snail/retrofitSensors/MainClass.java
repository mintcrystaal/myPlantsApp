package com.snail.retrofitSensors;

import android.support.annotation.NonNull;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainClass {
    private static int value = -100;

    public int getValue(int id){
        SensorService.getInstance()
                .getSensorApi()
                .getSensorById(id)
                .enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(@NonNull Call<Model> call, @NonNull Response<Model> response) {
                        Model post = response.body();
                        value = post.getValue();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Model> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
        return value;
    }
}
