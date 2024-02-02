package com.snail.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.snail.objects.Plant;

import java.util.ArrayList;

public class DBPlants {

    private static final String DATABASE_NAME = "plants.db";
    private static final int DATABASE_VERSION = 7;
    private static final String TABLE_NAME = "plantsInfo";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "Название";
    private static final String COLUMN_PICTURE = "Фото";
    private static final String COLUMN_TEMPERATURE = "Температура";
    private static final String COLUMN_LIGHT = "Освещение";
    private static final String COLUMN_WATERING = "Полив";
    private static final String COLUMN_HUMIDITY = "Влажность";
    private static final String COLUMN_FERTILIZE = "Удобрение";
    private static final String COLUMN_MINE = "Мои";
    private static final String COLUMN_INFO = "Дополнительно";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_NAME = 1;
    private static final int NUM_COLUMN_PICTURE = 2;
    private static final int NUM_COLUMN_TEMPERATURE = 3;
    private static final int NUM_COLUMN_LIGHT = 4;
    private static final int NUM_COLUMN_WATERING = 5;
    private static final int NUM_COLUMN_HUMIDITY = 6;
    private static final int NUM_COLUMN_FERTILIZE = 7;
    private static final int NUM_COLUMN_MINE = 8;
    private static final int NUM_COLUMN_INFO = 9;

    private SQLiteDatabase mDataBase;

    public DBPlants(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(int id, String name, String picture, int temperature, int light, int watering, int humidity, int fertilize, int mine, String info) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_PICTURE, picture);
        cv.put(COLUMN_TEMPERATURE, temperature);
        cv.put(COLUMN_LIGHT, light);
        cv.put(COLUMN_WATERING, watering);
        cv.put(COLUMN_HUMIDITY, humidity);
        cv.put(COLUMN_FERTILIZE, fertilize);
        cv.put(COLUMN_MINE, mine);
        cv.put(COLUMN_INFO, info);
        return mDataBase.insert(TABLE_NAME, null, cv);
    }

    public int update(Plant md) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, md.getName());
        cv.put(COLUMN_PICTURE, md.getPicture());
        cv.put(COLUMN_TEMPERATURE, md.getTemperature());
        cv.put(COLUMN_LIGHT, md.getLight());
        cv.put(COLUMN_WATERING, md.getWatering());
        cv.put(COLUMN_HUMIDITY, md.getHumidity());
        cv.put(COLUMN_FERTILIZE, md.getFertilize());
        cv.put(COLUMN_MINE, md.getMine());
        cv.put(COLUMN_INFO, md.getInfo());
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] { String.valueOf(md.getId())});
    }

    public void deleteAll() {
        mDataBase.delete(TABLE_NAME, null, null);
    }

    public void delete(int id) {
        mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public Plant select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            mCursor.moveToFirst();
            String name = mCursor.getString(NUM_COLUMN_NAME);
            String picture = mCursor.getString(NUM_COLUMN_PICTURE);
            int temperature = mCursor.getInt(NUM_COLUMN_TEMPERATURE);
            int light = mCursor.getInt(NUM_COLUMN_LIGHT);
            int watering = mCursor.getInt(NUM_COLUMN_WATERING);
            int humidity = mCursor.getInt(NUM_COLUMN_HUMIDITY);
            int fertilize = mCursor.getInt(NUM_COLUMN_FERTILIZE);
            int mine = mCursor.getInt(NUM_COLUMN_MINE);
            String info = mCursor.getString(NUM_COLUMN_INFO);
            return new Plant(id, name, picture, temperature, light, watering, humidity, fertilize, mine, info);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void close() {
        if (mDataBase != null)
            mDataBase.close();
    }

    public Cursor getAllData() {
        return mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public ArrayList<Plant> filter(@Nullable String selection, @Nullable String[] selectionArgs, String orderBy) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);

        ArrayList<Plant> arr = new ArrayList<Plant>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                int id = mCursor.getInt(NUM_COLUMN_ID);
                String name = mCursor.getString(NUM_COLUMN_NAME);
                String picture = mCursor.getString(NUM_COLUMN_PICTURE);
                int temperature = mCursor.getInt(NUM_COLUMN_TEMPERATURE);
                int light = mCursor.getInt(NUM_COLUMN_LIGHT);
                int watering = mCursor.getInt(NUM_COLUMN_WATERING);
                int humidity = mCursor.getInt(NUM_COLUMN_HUMIDITY);
                int mine = mCursor.getInt(NUM_COLUMN_MINE);
                int fertilize = mCursor.getInt(NUM_COLUMN_FERTILIZE);
                String info = mCursor.getString(NUM_COLUMN_INFO);
                arr.add(new Plant(id, name, picture, temperature, light, watering, humidity, fertilize, mine, info));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public ArrayList<Plant> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Plant> arr = new ArrayList<Plant>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                int id = mCursor.getInt(NUM_COLUMN_ID);
                String name = mCursor.getString(NUM_COLUMN_NAME);
                String picture = mCursor.getString(NUM_COLUMN_PICTURE);
                int temperature = mCursor.getInt(NUM_COLUMN_TEMPERATURE);
                int light = mCursor.getInt(NUM_COLUMN_LIGHT);
                int watering = mCursor.getInt(NUM_COLUMN_WATERING);
                int humidity = mCursor.getInt(NUM_COLUMN_HUMIDITY);
                int mine = mCursor.getInt(NUM_COLUMN_MINE);
                int fertilize = mCursor.getInt(NUM_COLUMN_FERTILIZE);
                String info = mCursor.getString(NUM_COLUMN_INFO);
                arr.add(new Plant(id, name, picture, temperature, light, watering, humidity, fertilize, mine, info));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public int getLastId() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
        try {
            mCursor.moveToLast();
            return mCursor.getInt(NUM_COLUMN_ID);
        } catch (Exception NullPointerException) {
            return 0;
        }
    }

    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PICTURE + " TEXT, " +
                    COLUMN_TEMPERATURE + " INTEGER, " +
                    COLUMN_LIGHT + " INTEGER, " +
                    COLUMN_WATERING + " INTEGER, " +
                    COLUMN_HUMIDITY + " INTEGER, " +
                    COLUMN_FERTILIZE + " INTEGER, " +
                    COLUMN_MINE + " INTEGER, " +
                    COLUMN_INFO + " TEXT);";

            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}