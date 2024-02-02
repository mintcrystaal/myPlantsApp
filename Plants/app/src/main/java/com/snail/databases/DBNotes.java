package com.snail.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.snail.objects.Note;

import java.util.ArrayList;

public class DBNotes {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "mnotes";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "Название";
    private static final String COLUMN_TEXT = "Заметка";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_NAME = 1;
    private static final int NUM_COLUMN_TEXT = 2;


    private SQLiteDatabase mDataBase;


    public DBNotes(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(long id, String name, String text) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_TEXT, text);
        return mDataBase.insert(TABLE_NAME, null, cv);
    }

    public long update(long id, String newName, String newText) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, newName);
        cv.put(COLUMN_TEXT, newText);
        return mDataBase.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
    }

    public void deleteAll() {
        mDataBase.delete(TABLE_NAME, null, null);
    }

    public void delete(long id) {
        mDataBase.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {
                String.valueOf(id) });
    }

    public Note select(long id) {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String name = mCursor.getString(NUM_COLUMN_NAME);
        String text = mCursor.getString(NUM_COLUMN_TEXT);
        return new Note(id, name, text);
    }


    public void close() {
        if (mDataBase != null) mDataBase.close();
    }

    public Cursor getAllData() {
        return mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public ArrayList<Note> selectAll() {
        Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Note> arr = new ArrayList<>();

        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getInt(NUM_COLUMN_ID);
                String name = mCursor.getString(NUM_COLUMN_NAME);
                String text = mCursor.getString(NUM_COLUMN_TEXT);
                arr.add(new Note(id, name, text));
            } while (mCursor.moveToNext());
        }

        return arr;
    }

    public long getLastId() {
        try {
            Cursor mCursor = mDataBase.query(TABLE_NAME, null, null, null, null, null, null);
            mCursor.moveToLast();
            return mCursor.getInt(NUM_COLUMN_ID);
        }
        catch (Exception NullPointerException) {
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
                    COLUMN_TEXT + " TEXT);";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}