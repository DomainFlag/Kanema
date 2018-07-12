package com.example.cchiv.kanema.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cchiv.kanema.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kanema.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COL_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COL_MOVIE_POSTER_PATH + " TEXT, " +
                MovieEntry.COL_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COL_MOVIE_VOTE_AVERAGE + " REAL, " +
                MovieEntry.COL_MOVIE_RELEASE_DATE + " TEXT, " +
                MovieEntry.COL_MOVIE_GENRES + " TEXT, " +
                MovieEntry.COL_MOVIE_TAGLINE + " TEXT" +
        ")";

        sqLiteDatabase.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
