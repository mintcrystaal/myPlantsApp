package com.snail.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.databases.DBMyPlants;
import com.snail.databases.DBPlants;
import com.snail.databases.DBSensors;
import com.snail.objects.MyPlant;
import com.snail.objects.Plant;
import com.snail.objects.Sensor;
import com.snail.plants.R;

import java.util.List;

public class MyPlantView extends AppCompatActivity {

    private Plant mPlant;
    TextView name;
    ImageView picture;
    TextView temperature;
    TextView humidity;
    Button getPlantInfo;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_plant_view);

        Intent intent = getIntent();
        DBMyPlants myDBPlants = new DBMyPlants(getApplicationContext());
        DBPlants mDBPlants = new DBPlants((getApplicationContext()));
        MyPlant myPlant = myDBPlants.select(intent.getLongExtra("id", -1));
        mPlant = mDBPlants.select(myPlant.getIdAll());

        name = findViewById(R.id.plant_view_name);
        picture = findViewById(R.id.plant_view_image);
        temperature = findViewById(R.id.plant_view_temperature);
        humidity = findViewById(R.id.plant_view_humidity);
        getPlantInfo = findViewById(R.id.plant_view_info);
        back = findViewById(R.id.plant_view_back);

        name.setText(mPlant.getName());
        temperature.setText(getString(R.string.add_sensor));
        humidity.setText(getString(R.string.add_sensor));

        DBSensors mDBSensors = new DBSensors(getApplicationContext());
        List<Sensor> sensors = mDBSensors.selectByPlant(myPlant.getId());
        for (Sensor now : sensors) {
            int type = now.getType();
            switch (type) {
                case (0):
                    temperature.setText(now.getValue() + "°C");
                    if (now.getValue() > mPlant.getTemperature() + 5 || now.getValue() < mPlant.getTemperature() - 5) {
                        temperature.setTextColor(getResources().getColor(R.color.pay_attention));
                    }
                    else
                        temperature.setTextColor(getResources().getColor(R.color.feels_ok));
                    break;
                case (1):
                    humidity.setText(now.getValue() + "%");
                    if (now.getValue() > mPlant.getHumidity() + 15 || now.getValue() < mPlant.getHumidity() - 15) {
                        humidity.setTextColor(getResources().getColor(R.color.pay_attention));
                    }
                    else
                        humidity.setTextColor(getResources().getColor(R.color.feels_ok));
                    break;
            }
        }

        byte[] data = Base64.decode(mPlant.getPicture(), Base64.DEFAULT);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        Bitmap plantPic = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        picture.setImageBitmap(plantPic);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        getPlantInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlantView.class);
                intent.putExtra("id", mPlant.getId());
                startActivity(intent);
            }
        });
    }
}