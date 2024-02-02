package com.snail.fragments;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.snail.databases.DBEvents;
import com.snail.databases.DBMyPlants;
import com.snail.databases.DBMyPlantsUpdates;
import com.snail.databases.DBPlants;
import com.snail.databases.DBSensors;
import com.snail.objects.MyPlant;
import com.snail.objects.Plant;
import com.snail.objects.PlantUpdates;
import com.snail.processes.AlarmReceiver;
import com.snail.processes.MyRun;
import com.snail.plants.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static int DEFAULT_TIME_HOUR;
    public static int DEFAULT_TIME_MINUTE;
    public static int HOW_MANY_DAYS;
    public static String LANGUAGE;
    public static final String DEFAULT_HOUR = "hour";
    public static final String DEFAULT_MINUTE = "min";
    public static final String DEFAULT_DAYS = "days";
    public static final String DEFAULT_LANGUAGE = "language";
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sPref = getPreferences(MODE_PRIVATE);
        DEFAULT_TIME_HOUR = Integer.parseInt(sPref.getString(DEFAULT_HOUR, "17"));
        DEFAULT_TIME_MINUTE = Integer.parseInt(sPref.getString(DEFAULT_MINUTE, "0"));
        HOW_MANY_DAYS = Integer.parseInt(sPref.getString(DEFAULT_DAYS, "31"));

        MyRun checkSensors = new MyRun(getApplicationContext());
        new Thread(checkSensors).start();

        DBPlants mDBPlants = new DBPlants(getApplicationContext());
        DBMyPlants mDBMyPlants = new DBMyPlants(getApplicationContext());

        DBEvents mDBEvents = new DBEvents(getApplicationContext());
        DBMyPlantsUpdates mDBUpdates = new DBMyPlantsUpdates(getApplicationContext());

        loadFragment(new MyPlantsFragment());
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        ArrayList<MyPlant> mine = mDBMyPlants.selectAll();

        SimpleDateFormat dateFormatEventName = new SimpleDateFormat("(HH:mm)", Locale.getDefault());

        for (MyPlant now : mine) {
            Plant mPlant = mDBPlants.select(now.getIdAll());

            long maxtimeWater = mDBUpdates.select(now.getId()).getLastWater();
            long startTime = maxtimeWater;

            String eventName = getString(R.string.of_plant) + " \"" + mPlant.getName() + "\" " + dateFormatEventName.format(startTime);
            if (mPlant.getWatering() > 0) {
                maxtimeWater = mDBEvents.insertALotOfTimes(mDBEvents.getLastId() + 1, getString(R.string.your_plant_watering) + " " + eventName, getColor(R.color.watering_day), startTime, now.getId(), mPlant.getWatering(), System.currentTimeMillis() + AlarmManager.INTERVAL_DAY * HOW_MANY_DAYS, getApplicationContext());
            }

            long maxtimeFert = mDBUpdates.select(now.getId()).getLastWater();
            startTime = maxtimeFert;

            if (mPlant.getFertilize() > 0) {
                maxtimeFert = mDBEvents.insertALotOfTimes(mDBEvents.getLastId() + 1, getString(R.string.your_plant_fertilize) + " " + eventName, getColor(R.color.fertilize_day), startTime, now.getId(), mPlant.getFertilize(), System.currentTimeMillis() + AlarmManager.INTERVAL_DAY * HOW_MANY_DAYS, getApplicationContext());
            }

            PlantUpdates changeUpd = new PlantUpdates(now.getId(), maxtimeWater, maxtimeFert);
            mDBUpdates.update(changeUpd);
        }

        BroadcastReceiver br = new AlarmReceiver();
        IntentFilter intFilt = new IntentFilter();
        getApplicationContext().registerReceiver(br, intFilt);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch(menuItem.getItemId()) {
            case R.id.navigation_myPlants:
                fragment = new MyPlantsFragment();
                break;
            case R.id.navigation_allPlants:
                fragment = new AllPlantsFragment();
                break;
            case R.id.navigation_calendar:
                fragment = new CalendarFragment();
                break;
            case R.id.navigation_notes:
                fragment = new NotesFragment();
                break;
            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                break;
        }
        return loadFragment(fragment);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onRestart() {
        super.onRestart();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    public static void setLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}