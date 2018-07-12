package com.example.cchiv.kanema.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.cchiv.kanema.data.MovieContract.MovieEntry;

public class KanemaProvider extends ContentProvider {

    private MovieDbHelper movieDbHelper;

    @Override
    public boolean onCreate() {
        this.movieDbHelper = new MovieDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String orderBy) {
        SQLiteDatabase sqLiteDatabase = movieDbHelper.getReadableDatabase();

        return sqLiteDatabase.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = movieDbHelper.getWritableDatabase();

        long inserted = sqLiteDatabase.insert(MovieEntry.TABLE_NAME, null, contentValues);

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = movieDbHelper.getWritableDatabase();

        int nbRowsDeleted = sqLiteDatabase.delete(MovieEntry.TABLE_NAME, whereClause, whereArgs);
        if(nbRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return nbRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String whereClause, @Nullable String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = movieDbHelper.getWritableDatabase();

        int nbRowsUpdated = sqLiteDatabase.update(MovieEntry.TABLE_NAME, contentValues, whereClause, whereArgs);

        return nbRowsUpdated;
    }
}
