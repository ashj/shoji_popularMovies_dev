package com.shoji.example.android.popularmoviesstage1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shoji.example.android.popularmoviesstage1.database.FavoriteMoviesContract.FavoriteMoviesEntry;

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
    private final static String TAG = FavoriteMoviesDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "favoritemovies.db";
    private static final int DATABASE_VERSION = 1;

    FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                FavoriteMoviesEntry.TABLE_NAME  + " ( " +
                FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteMoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavoriteMoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL" +
                ")";
        Log.d(TAG, "Create table query: "+SQL_CREATE_TABLE);

        db.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
