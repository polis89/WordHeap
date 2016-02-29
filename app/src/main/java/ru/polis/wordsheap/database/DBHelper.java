package ru.polis.wordsheap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper{
    public static final String TAG = "DBHelperLOG";
    public static final String DATA_BASE_NAME = "appDB";
    public static final String DATA_BASE_NAME_FULL = "appDB.sqlite";
    private String DB_PATH = "/data/data/ru.polis.wordsheap/databases/";
    public static final int DB_VERSION = 1;

    private Context context;

    public DBHelper(Context context) {
        super(context, DATA_BASE_NAME_FULL, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            copydatabase();
            Log.i(TAG, "Database Copied");
        } catch(IOException e) {
            throw new Error("Error copying database");
        }
//        db.execSQL("CREATE TABLE " + DBService.TABLE_VOCABULARY_NAME + " ( " +
//                DBService.TABLE_VOCABULARY_KEY_ID + " integer primary key, " +
//                DBService.TABLE_VOCABULARY_KEY_NAME + " text UNIQUE )");
//        db.execSQL("CREATE TABLE " + DBService.TABLE_WORD_NAME + " ( " +
//                DBService.TABLE_WORD_KEY_ID + " integer primary key, " +
//                DBService.TABLE_WORD_KEY_NAME + " text, " +
//                DBService.TABLE_WORD_KEY_TRANSLATE + " text, " +
//                DBService.TABLE_WORD_KEY_PROGRESS + " INTEGER, " +
//                DBService.TABLE_WORD_KEY_ACTIVE + " INTEGER, " +
//                DBService.TABLE_WORD_KEY_VOCABULARY_ID + " INTEGER, " +
//                "FOREIGN KEY(" + DBService.TABLE_WORD_KEY_VOCABULARY_ID + ") " +
//                "REFERENCES " + DBService.TABLE_VOCABULARY_NAME + "(" + DBService.TABLE_VOCABULARY_KEY_ID + "))");
    }

    private void copydatabase() throws IOException {

        //Open your local db as the input stream
        Log.i(TAG, "copydatabase -> Open local db as the input stream");
        InputStream myInput = context.getAssets().open(DATA_BASE_NAME_FULL);

        // Path to the just created empty db
        Log.i(TAG, "copydatabase -> Get Path to the just created empty db");
        String outFilename =  DB_PATH + DATA_BASE_NAME_FULL;
        Log.i(TAG, "copydatabase -> Path: " + outFilename);


        File outFile = new File(outFilename);
        Log.i(TAG, "copydatabase -> OutFile exist? - " + outFile.exists());

        //Open the empty db as the output stream
        Log.i(TAG, "copydatabase -> Open the empty db as the output stream");
        OutputStream myOutput = new FileOutputStream(outFilename);

        // transfer byte to inputfile to outputfile
        Log.i(TAG, "copydatabase -> transfer byte to inputfile to outputfile");
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0) {
            myOutput.write(buffer,0,length);
            Log.i(TAG, "copydatabase -> coping");
        }

        //Close the streams
        Log.i(TAG, "copydatabase -> Close the streams");
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists" + DBService.TABLE_WORD_NAME);
        db.execSQL("drop table if exists" + DBService.TABLE_VOCABULARY_NAME);
        onCreate(db);
    }
}
