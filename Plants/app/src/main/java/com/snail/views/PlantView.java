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

import com.snail.databases.DBPlants;
import com.snail.objects.Plant;
import com.snail.plants.R;

public class PlantView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_view);

        Intent intent = getIntent();

        DBPlants mDBPlants = new DBPlants(getApplicationContext());
        Plant mPlant = mDBPlants.select(intent.getLongExtra("id", -1));

        TextView name = findViewById(R.id.plant_view_name);
        ImageView picture = findViewById(R.id.plant_view_image);
        TextView temperature = findViewById(R.id.plant_view_temperature);
        TextView light = findViewById(R.id.plant_view_light);
        TextView watering = findViewById(R.id.plant_view_watering);
        TextView humidity = findViewById(R.id.plant_view_humidity);
        TextView fertilize = findViewById(R.id.plant_view_fertilize);
        TextView mine = findViewById(R.id.plant_view_mine);
        TextView info = findViewById(R.id.plant_view_info);
        Button back = findViewById(R.id.plant_view_back);

        name.setText(mPlant.getName());
        info.setText(mPlant.getInfo());

        byte[] data = Base64.decode(mPlant.getPicture(), Base64.DEFAULT);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        Bitmap plantPic = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        picture.setImageBitmap(plantPic);

        temperature.setText(Integer.toString(mPlant.getTemperature()) + "±5°C");
        final String LIGHT[] = {getString(R.string.light1), getString(R.string.light2), getString(R.string.light3)};
        light.setText(LIGHT[mPlant.getLight()]);

        int mWatering = mPlant.getWatering();
        if (mWatering == 0)
            watering.setText(getString(R.string.not_needed));
        else {
            if (mWatering % 10 == 1) {
                if (mWatering == 1)
                    watering.setText(getString(R.string.every_day));
                else
                    watering.setText(getString(R.string.every1water) + " " + mWatering + " " + getString(R.string.day1));
            } else {
                if ((mWatering < 5 || mWatering > 21) && (mWatering % 10 == 2 || mWatering % 10 == 3 || mWatering % 10 == 4))
                    watering.setText(getString(R.string.every2water) + " " + mWatering + " " + getString(R.string.day2));
                else
                    watering.setText(getString(R.string.every2water) + " " + mWatering + " " + getString(R.string.day3));
            }
        }

        int mFertilize = mPlant.getFertilize();
        if (mFertilize == 0)
            watering.setText(getString(R.string.not_needed));
        else {
            if (mFertilize == 21) {
                fertilize.setText(getString(R.string.every1fertilize) + " " + mFertilize + " " + getString(R.string.day1));
            } else {
                fertilize.setText(getString(R.string.every2fertilize) + " " + mFertilize + " " + getString(R.string.day3));
            }
        }

        if (mPlant.getHumidity() == -1)
            humidity.setText(getString(R.string.unpretentious));
        else
            humidity.setText(mPlant.getHumidity() + "±15%");
        mine.setText(Integer.toString(mPlant.getMine()));

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}