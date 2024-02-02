package com.snail.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.snail.objects.Sensor;

import java.util.ArrayList;

public class DBSensors {
    private static final String DATABASE_UPDATE = "sensors.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "sensors";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PLANT = "id_plant";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_VALUE = "value";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_PLANT = 1;
    private static final int NUM_COLUMN_TYPE = 2;
    private static final int NUM_COLUMN_VALUE = 3;

    private SQLiteDatabase mDataBase;

    public DBSensors(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(long id, long idPlant, int type, int value) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_PLANT, idPlant);
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_VALUE, value);
        return mDataBase.insert(TABLE_NAME, null, cv);
    }

    public int update(Sensor md) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PLANT, md.getPlant());
        cv.put(COLUMN_TYPE, md.getType());
        cv.put(COLUMN_VALUE, md.getValue());
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(md.getId())});
    }

    public void deleteAll() {
        mDataBase.delete(TABLE_NAME, null, null);
    }

    public void delete(long id) {
        mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Sensor select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        mCursor.moveToFirst();
        long idPlant = mCursor.getLong(NUM_COLUMN_PLANT);
        int type = mCursor.getInt(NUM_COLUMN_TYPE);
        int value = mCursor.getInt(NUM_COLUMN_VALUE);
        return new Sensor(id, idPlant, type, value);
    }

    public ArrayList<Sensor> selectByPlant(long plant) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_PLANT + " = ?", new String[]{String.valueOf(plant)}, null, null, null);

        ArrayList<Sensor> arr = new ArrayList<Sensor>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                long idPlant = mCursor.getLong(NUM_COLUMN_PLANT);
                int type = mCursor.getInt(NUM_COLUMN_TYPE);
                int value = mCursor.getInt(NUM_COLUMN_VALUE);
                arr.add(new Sensor(id, idPlant, type, value));
            } while (mCursor.moveToNext());
        }
        return arr;
    }
    public void close() {
        if (mDataBase != null)
            mDataBase.close();
    }

    public Cursor getAllData() {
        return mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public ArrayList<Sensor> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Sensor> arr = new ArrayList<Sensor>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                long idPlant = mCursor.getLong(NUM_COLUMN_PLANT);
                int type = mCursor.getInt(NUM_COLUMN_TYPE);
                int value = mCursor.getInt(NUM_COLUMN_VALUE);
                arr.add(new Sensor(id, idPlant, type, value));
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
            super(context, DATABASE_UPDATE, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PLANT + " INTEGER, " +
                    COLUMN_TYPE + " INTEGER, " +
                    COLUMN_VALUE + " INTEGER);";

            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}