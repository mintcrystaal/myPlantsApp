package com.snail.dialogs;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.snail.databases.DBEvents;
import com.snail.databases.DBMyPlants;
import com.snail.databases.DBMyPlantsUpdates;
import com.snail.databases.DBPlants;
import com.snail.plants.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.snail.fragments.MainActivity.DEFAULT_TIME_HOUR;
import static com.snail.fragments.MainActivity.DEFAULT_TIME_MINUTE;
import static com.snail.fragments.MainActivity.HOW_MANY_DAYS;

public class CreatePlantActivity extends Activity {

    private String name = "";
    private int temperature = 0;
    private int light = 0;
    private int watering = 0;
    private int fertilize = 0;
    private int humidity = 0;
    private String info = "";
    private String picture = "";
    private int mine = 1;

    private EditText nameInp;
    private EditText infoInp;
    private ImageView plantPic;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_plant_activity);

        nameInp = findViewById(R.id.plant_name);
        infoInp = findViewById(R.id.plant_info);
        plantPic = findViewById(R.id.plant_pic);
        Button choosePic = findViewById(R.id.choose_pic);
        Button ok = findViewById(R.id.ok_plant);
        Button cancel = findViewById(R.id.cancel_plant);

        Spinner chooseTemperature = findViewById(R.id.choose_temperature);
        final ArrayList<String> temperatures = new ArrayList<>();
        for (int i = 5; i < 41; i++)
            temperatures.add(Integer.toString(i) + "°C");

        final ArrayAdapter myAdapterTemperatures = new ArrayAdapter<String>(getApplicationContext(),  R.layout.item_choose, R.id.this_item, temperatures);

        chooseTemperature.setAdapter(myAdapterTemperatures);

        chooseTemperature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temperature = (int) id + 5;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        nameInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                name = nameInp.getText().toString();
            }
        });


        infoInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                info = infoInp.getText().toString();
            }
        });

        Spinner chooseWatering = findViewById(R.id.choose_watering);
        final ArrayList<String> waterings = new ArrayList<>();
        waterings.add(getString(R.string.not_needed));
        for (int i = 1; i < 22; i++)
            waterings.add(Integer.toString(i));
        final ArrayAdapter myAdapterWatering = new ArrayAdapter<>(getApplicationContext(), R.layout.item_choose, R.id.this_item, waterings);
        chooseWatering.setAdapter(myAdapterWatering);
        chooseWatering.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                watering = (int) id;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner chooseFertilize = findViewById(R.id.choose_fertilize);
        final ArrayList<String> fertilizes = new ArrayList<>();
        fertilizes.add(getString(R.string.not_needed));
        for (int i = 10; i < 22; i++)
            fertilizes.add(Integer.toString(i));
        final ArrayAdapter myAdapterFertilize = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_choose, R.id.this_item, fertilizes);
        chooseFertilize.setAdapter(myAdapterFertilize);
        chooseFertilize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((int) id == 0)
                    fertilize = 0;
                else
                    fertilize = 9 + (int) id;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner chooseHumidity = findViewById(R.id.choose_humidity);
        final ArrayList<String> humidities = new ArrayList<>();
        humidities.add(getString(R.string.unpretentious));
        for (int i = 0; i < 101; i++)
            humidities.add(Integer.toString(i) + "%");
        final ArrayAdapter myAdapterHumidity = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_choose, R.id.this_item, humidities);
        chooseHumidity.setAdapter(myAdapterHumidity);
        chooseHumidity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                humidity = (int) id - 1;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Spinner chooseLight = findViewById(R.id.choose_light);
        final ArrayList<String> lights = new ArrayList<>();
        lights.add(getString(R.string.light1));
        lights.add(getString(R.string.light2));
        lights.add(getString(R.string.light3));
        final ArrayAdapter myAdapterLight = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_choose, R.id.this_item, lights);
        chooseLight.setAdapter(myAdapterLight);
        chooseLight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                light = (int) id;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBPlants mDBPlants = new DBPlants(getApplicationContext());
                if (name.length() == 0) {
                    name = getString(R.string.my_plant) + (mDBPlants.getLastId() + 1);
                }
                if (picture.length() == 0) {
                    Drawable drawable = getResources().getDrawable(R.drawable.plant);
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    byte[] pic = ImagePicker.bitmapToByteArray(bitmap);
                    picture = Base64.encodeToString(pic, Base64.DEFAULT);
                }
                mDBPlants.insert(mDBPlants.getLastId() + 1, name, picture, temperature, light, watering, humidity, fertilize, mine, info);

                Intent intent = new Intent("plantAdded");
                getApplicationContext().sendBroadcast(intent);

                Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
                currentCalender.set(Calendar.HOUR_OF_DAY, DEFAULT_TIME_HOUR);
                currentCalender.set(Calendar.MINUTE, DEFAULT_TIME_MINUTE);
                currentCalender.set(Calendar.SECOND, 0);
                long timeInMillis = currentCalender.getTimeInMillis();

                DBMyPlants mDBMyPlants = new DBMyPlants(getApplicationContext());
                mDBMyPlants.insert(mDBMyPlants.getLastId() + 1, mDBPlants.getLastId(), 0);
                DBMyPlantsUpdates mDBUpdates = new DBMyPlantsUpdates(getApplicationContext());

                SimpleDateFormat dateFormatEventName = new SimpleDateFormat("(HH:mm)", Locale.getDefault());
                String eventName = getString(R.string.of_plant) + " \"" + name + "\" " + dateFormatEventName.format(timeInMillis);

                DBEvents mDBEvents = new DBEvents(getApplicationContext());

                long maxtimeWater = timeInMillis, maxtimeFert = timeInMillis;

                if (watering > 0)
                    maxtimeWater = mDBEvents.insertALotOfTimes(mDBEvents.getLastId() + 1, getString(R.string.your_plant_watering) + " " + eventName, getColor(R.color.watering_day), timeInMillis, mDBMyPlants.getLastId(), watering, timeInMillis + AlarmManager.INTERVAL_DAY * HOW_MANY_DAYS, getApplicationContext());

                if (fertilize > 0)
                    maxtimeFert = mDBEvents.insertALotOfTimes(mDBEvents.getLastId() + 1, getString(R.string.your_plant_fertilize) + " " + eventName, getColor(R.color.fertilize_day), timeInMillis, mDBMyPlants.getLastId(), fertilize, timeInMillis + AlarmManager.INTERVAL_DAY * HOW_MANY_DAYS, getApplicationContext());

                mDBUpdates.insert(mDBMyPlants.getLastId(), maxtimeWater, maxtimeFert);

                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void selectImage() {
        Intent takeImageIntent = ImagePicker.getPickImageIntent(getApplicationContext());
        if (takeImageIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takeImageIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getBitmapFromResult(getApplicationContext(), resultCode, data);
        if (null != bitmap && resultCode == RESULT_OK) {
            plantPic.setImageBitmap(bitmap);
            byte[] pic = ImagePicker.bitmapToByteArray(bitmap);
            picture = Base64.encodeToString(pic, Base64.DEFAULT);
        }
    }
}
