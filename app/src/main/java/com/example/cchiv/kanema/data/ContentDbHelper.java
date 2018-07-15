package com.example.cchiv.kanema.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cchiv.kanema.data.ContentContract.ContentEntry;

public class ContentDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kanema.db";

    public ContentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery = "CREATE TABLE " + ContentEntry.TABLE_NAME + " (" +
                ContentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContentEntry.COL_CONTENT_TITLE + " TEXT NOT NULL, " +
                ContentEntry.COL_CONTENT_POSTER_PATH + " TEXT, " +
                ContentEntry.COL_CONTENT_OVERVIEW + " TEXT NOT NULL, " +
                ContentEntry.COL_CONTENT_VOTE_AVERAGE + " REAL, " +
                ContentEntry.COL_CONTENT_RELEASE_DATE + " TEXT, " +
                ContentEntry.COL_CONTENT_GENRES + " TEXT, " +
                ContentEntry.COL_CONTENT_TAGLINE + " TEXT" +
        ")";

        sqLiteDatabase.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
