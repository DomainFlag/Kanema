package com.example.cchiv.kanema.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.cchiv.kanema.data.ContentContract.ContentEntry;

public class KanemaProvider extends ContentProvider {

    private ContentDbHelper contentDbHelper;

    @Override
    public boolean onCreate() {
        this.contentDbHelper = new ContentDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String orderBy) {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(ContentEntry.TABLE_NAME, projection, selection,
                selectionArgs,
                null, null, orderBy);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getWritableDatabase();

        long insertedRows = sqLiteDatabase.insert(ContentEntry.TABLE_NAME, null, contentValues);
        if(insertedRows != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, insertedRows);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getWritableDatabase();

        int nbRowsDeleted = sqLiteDatabase.delete(ContentEntry.TABLE_NAME, whereClause, whereArgs);
        if(nbRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return nbRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String whereClause, @Nullable String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getWritableDatabase();

        int nbRowsUpdated = sqLiteDatabase.update(ContentEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
        if(nbRowsUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return nbRowsUpdated;
    }
}
