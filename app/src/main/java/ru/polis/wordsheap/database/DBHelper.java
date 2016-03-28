package ru.polis.wordsheap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
    public static final String TAG = "DBHelperLOG";
    public static final String DATA_BASE_NAME_FULL = "appDB.sqlite";
    public static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATA_BASE_NAME_FULL, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "---Create Database---");
        db.execSQL("CREATE TABLE " + DBService.TABLE_LANGUAGE_NAME + " ( " +
                DBService.TABLE_LANGUAGE_KEY_ID + " integer primary key, " +
                DBService.TABLE_LANGUAGE_KEY_NAME + " text UNIQUE )");
        db.execSQL("CREATE TABLE " + DBService.TABLE_VOCABULARY_NAME + " ( " +
                DBService.TABLE_VOCABULARY_KEY_ID + " integer primary key, " +
                DBService.TABLE_VOCABULARY_KEY_NAME + " text UNIQUE, " +
                DBService.TABLE_VOCABULARY_KEY_LANGUAGE_ID + " INTEGER, " +
                "FOREIGN KEY(" + DBService.TABLE_VOCABULARY_KEY_LANGUAGE_ID + ") " +
                "REFERENCES " + DBService.TABLE_LANGUAGE_NAME + "(" + DBService.TABLE_LANGUAGE_KEY_ID + "))");
        db.execSQL("CREATE TABLE " + DBService.TABLE_WORD_NAME + " ( " +
                DBService.TABLE_WORD_KEY_ID + " integer primary key, " +
                DBService.TABLE_WORD_KEY_NAME + " text, " +
                DBService.TABLE_WORD_KEY_TRANSLATE + " text, " +
                DBService.TABLE_WORD_KEY_PROGRESS + " INTEGER, " +
                DBService.TABLE_WORD_KEY_ACTIVE + " INTEGER, " +
                DBService.TABLE_WORD_KEY_VOCABULARY_ID + " INTEGER, " +
                "FOREIGN KEY(" + DBService.TABLE_WORD_KEY_VOCABULARY_ID + ") " +
                "REFERENCES " + DBService.TABLE_VOCABULARY_NAME + "(" + DBService.TABLE_VOCABULARY_KEY_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "---Update Database---");
        db.execSQL("drop table if exists " + DBService.TABLE_WORD_NAME);
        db.execSQL("drop table if exists " + DBService.TABLE_VOCABULARY_NAME);
        onCreate(db);
    }
}
