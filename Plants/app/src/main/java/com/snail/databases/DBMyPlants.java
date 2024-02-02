package com.snail.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.snail.objects.MyPlant;

import java.util.ArrayList;

public class DBMyPlants {
    private static final String DATABASE_UPDATE = "myPlants.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "myPlants";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ID_ALL = "id_all";
    private static final String COLUMN_FEELS = "feels";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_ID_ALL = 1;
    private static final int NUM_COLUMN_FEELS = 2;

    private SQLiteDatabase mDataBase;

    public DBMyPlants (Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(int id, long idAll, int feels) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_ID_ALL, idAll);
        cv.put(COLUMN_FEELS, feels);
        return mDataBase.insert(TABLE_NAME, null, cv);
    }

    public int update(MyPlant md) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID_ALL, md.getIdAll());
        cv.put(COLUMN_FEELS, md.getFeels());
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] { String.valueOf(md.getId())});
    }

    public void deleteAll() {
        mDataBase.delete(TABLE_NAME, null, null);
    }

    public void delete(int id) {
        mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public MyPlant select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        long idAll = mCursor.getLong(NUM_COLUMN_ID_ALL);
        int feels = mCursor.getInt(NUM_COLUMN_FEELS);
        return new MyPlant(id, idAll, feels);
    }


    public void close() {
        if (mDataBase != null)
            mDataBase.close();
    }

    public Cursor getAllData() {
        return mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public ArrayList<MyPlant> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<MyPlant> arr = new ArrayList<MyPlant>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                int id = mCursor.getInt(NUM_COLUMN_ID);
                long idAll = mCursor.getLong(NUM_COLUMN_ID_ALL);
                int feels = mCursor.getInt(NUM_COLUMN_FEELS);
                arr.add(new MyPlant(id, idAll, feels));
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
                    COLUMN_ID_ALL + " INTEGER, " +
                    COLUMN_FEELS + " INTEGER);";

            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}