package com.snail.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.snail.objects.PlantUpdates;

import java.util.ArrayList;

public class DBMyPlantsUpdates {
    private static final String DATABASE_WATER = "plantsUpdates.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "myPlantsUpdates";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_WATER = "Полив";
    private static final String COLUMN_FERTILIZE = "Удобрение";


    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_WATER = 1;
    private static final int NUM_COLUMN_FERTILIZE = 2;


    private SQLiteDatabase mDataBase;

    public DBMyPlantsUpdates(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(int id, long water, long fert) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_WATER, water);
        cv.put(COLUMN_FERTILIZE, fert);
        return mDataBase.insert(TABLE_NAME, null, cv);
    }

    public int update(PlantUpdates md) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_WATER, md.getLastWater());
        cv.put(COLUMN_FERTILIZE, md.getLastFert());
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] { String.valueOf(md.getId())});
    }

    public void deleteAll() {
        mDataBase.delete(TABLE_NAME, null, null);
    }

    public void delete(int id) {
        mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public PlantUpdates select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        long water = mCursor.getLong(NUM_COLUMN_WATER);
        long fert = mCursor.getLong(NUM_COLUMN_FERTILIZE);
        return new PlantUpdates(id, water, fert);
    }


    public void close() {
        if (mDataBase != null)
            mDataBase.close();
    }

    public Cursor getAllData() {
        return mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public ArrayList<PlantUpdates> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<PlantUpdates> arr = new ArrayList<PlantUpdates>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                int id = mCursor.getInt(NUM_COLUMN_ID);
                long water = mCursor.getLong(NUM_COLUMN_WATER);
                long fert = mCursor.getLong(NUM_COLUMN_FERTILIZE);
                arr.add(new PlantUpdates(id, water, fert));
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
            super(context, DATABASE_WATER, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_WATER + " INTEGER, " +
                    COLUMN_FERTILIZE + " INTEGER);";

            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}