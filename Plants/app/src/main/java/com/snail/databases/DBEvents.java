package com.snail.databases;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.snail.processes.AlarmReceiver;
import com.snail.objects.EventInfo;

import java.util.ArrayList;

public class DBEvents {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "myevents";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "Название";
    private static final String COLUMN_COLOR = "Цвет";
    private static final String COLUMN_TIME = "Время";
    private static final String COLUMN_PLANT = "Растение";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_NAME = 1;
    private static final int NUM_COLUMN_COLOR = 2;
    private static final int NUM_COLUMN_TIME = 3;
    private static final int NUM_COLUMN_PLANT = 4;


    private SQLiteDatabase mDataBase;


    public DBEvents(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insertALotOfTimes(long id, String name, int color, long time, long plant, int interval, long until, Context context) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_COLOR, color);
        cv.put(COLUMN_PLANT, plant);
        String text = name;

        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        time += AlarmManager.INTERVAL_DAY * interval;

        while (time < until) {
            cv.put(COLUMN_ID, (int) getLastId() + 1);
            cv.put(COLUMN_TIME, time);
            Intent intentAlarm = new Intent(context, AlarmReceiver.class);
            intentAlarm.putExtra("text", text);
            intentAlarm.putExtra("id", getLastId() + 1);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int) (getLastId() + 1), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            mDataBase.insert(TABLE_NAME, null, cv);
            time += AlarmManager.INTERVAL_DAY * interval;
        }
        return time - AlarmManager.INTERVAL_DAY * interval;
    }

    public long insert(long id, String name, int color, long time, long plant, Context context) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_COLOR, color);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_PLANT, plant);
        if (time > System.currentTimeMillis()) {
            Intent intentAlarm = new Intent(context, AlarmReceiver.class);
            intentAlarm.putExtra("text", name.substring(0, name.lastIndexOf("(")));
            intentAlarm.putExtra("id", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) (id), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
        return mDataBase.insert(TABLE_NAME, null, cv);
    }

    public int update(long id, String newName, int newColor, long newTime, long newPlant) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, newName);
        cv.put(COLUMN_COLOR, newColor);
        cv.put(COLUMN_TIME, newTime);
        cv.put(COLUMN_PLANT, newPlant);
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
    }

    public void deleteAll() {
        mDataBase.delete(TABLE_NAME, null, null);
    }

    public void delete(long id, Context context) {
        Intent intentAlarm = new Intent(context, AlarmReceiver.class);
        Event event = this.select(id);
        String name = ((EventInfo)event.getData()).getName();
        intentAlarm.putExtra("text", name.substring(0, name.lastIndexOf("(")));
        intentAlarm.putExtra("id", (int) id);
        intentAlarm.putExtra("plant", ((EventInfo)event.getData()).getConnectedWithPlant());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int) id, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {
                String.valueOf(id) });
    }

    public void deleteByPlant(long plant, Context context) {
        Intent intentAlarm = new Intent(context, AlarmReceiver.class);
        intentAlarm.putExtra("plant", plant);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        mDataBase.delete(TABLE_NAME, COLUMN_PLANT + " = ?", new String[] {String.valueOf(plant) });
    }

    public Event select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String name = mCursor.getString(NUM_COLUMN_NAME);
        int color = mCursor.getInt(NUM_COLUMN_COLOR);
        long time = mCursor.getLong(NUM_COLUMN_TIME);
        long plant = mCursor.getLong(NUM_COLUMN_PLANT);
        return new Event(color, time, new EventInfo(name, id, plant));
    }

    public ArrayList<Event> selectByDate(long time1) {
        long time2 = time1 + AlarmManager.INTERVAL_DAY;
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_TIME + " >= ? AND " + COLUMN_TIME  + " <= ?", new String[] {String.valueOf(time1), String.valueOf(time2)}, null, null, null, null);

        ArrayList<Event> arr = new ArrayList<>();

        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String name = mCursor.getString(NUM_COLUMN_NAME);
                int color = mCursor.getInt(NUM_COLUMN_COLOR);
                long time = mCursor.getLong(NUM_COLUMN_TIME);
                long plant = mCursor.getLong(NUM_COLUMN_PLANT);
                arr.add(new Event(color, time, new EventInfo(name, id, plant)));
            } while (mCursor.moveToNext());
        }

        return arr;
    }


    public void close() {
        if (mDataBase != null) mDataBase.close();
    }

    public Cursor getAllData() {
        return mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public ArrayList<Event> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Event> arr = new ArrayList<>();

        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String name = mCursor.getString(NUM_COLUMN_NAME);
                int color = mCursor.getInt(NUM_COLUMN_COLOR);
                long time = mCursor.getLong(NUM_COLUMN_TIME);
                long plant = mCursor.getLong(NUM_COLUMN_PLANT);
                arr.add(new Event(color, time, new EventInfo(name, id, plant)));
            } while (mCursor.moveToNext());
        }

        return arr;
    }

    public long selectByTime(long timeInMillis, String name) {
        Cursor mCursor =  mDataBase.query(TABLE_NAME, null, COLUMN_TIME + " = ? AND " + COLUMN_NAME + " = ?", new String[]{String.valueOf(timeInMillis), name}, null, null, null);
        mCursor.moveToFirst();
        long id = mCursor.getInt(NUM_COLUMN_ID);
        return id;
    }

    public long getLastId() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
        mCursor.moveToLast();
        try {
            return mCursor.getLong(NUM_COLUMN_ID);
        } catch (Exception NullPointerException) {return 0;}
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
                    COLUMN_COLOR + " INTEGER, " +
                    COLUMN_TIME + " INTEGER, " +
                    COLUMN_PLANT + " INTEGER);";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}