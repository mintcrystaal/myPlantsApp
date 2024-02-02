package com.snail.processes;

import android.content.Context;
import android.util.Log;

import com.snail.databases.DBMyPlants;
import com.snail.databases.DBPlants;
import com.snail.databases.DBSensors;
import com.snail.objects.MyPlant;
import com.snail.objects.Plant;
import com.snail.objects.Sensor;
import com.snail.retrofitSensors.MainClass;

import java.util.List;

public class MyRun implements Runnable {
    private Context context;

    public MyRun(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {}
        while (true) {
            DBSensors mDBSensors = new DBSensors(context);
            DBPlants mDBPlants = new DBPlants(context);
            DBMyPlants mDBMyPlants = new DBMyPlants(context);
            List<MyPlant> myPlants = mDBMyPlants.selectAll();
            MainClass retrofit = new MainClass();

            for (MyPlant myPlant : myPlants) {
                List<Sensor> sensors = mDBSensors.selectByPlant(myPlant.getId());
                boolean feelsBad = false;

                for (Sensor sensor : sensors) {
                    int value = retrofit.getValue((int) sensor.getId());

                    if (value != -100) {
                        sensor.setValue(value);
                        mDBSensors.update(sensor);
                        Plant thisPlant = mDBPlants.select(mDBMyPlants.select(sensor.getPlant()).getIdAll());

                        switch (sensor.getType()) {
                            case (0):
                                if (thisPlant.getTemperature() > value + 5
                                        || thisPlant.getTemperature() < value - 5)
                                    feelsBad = true;
                                break;
                            case (1):
                                if (thisPlant.getHumidity() > value + 15
                                        || thisPlant.getHumidity() < value - 15)
                                    feelsBad = true;
                                break;
                        }
                    }
                }
                if (feelsBad)
                    myPlant.setFeels(1);
                else
                    myPlant.setFeels(0);
                mDBMyPlants.update(myPlant);
            }
             try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        }
    }
}
