package com.shoji.example.android.popularmoviesstage1.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shoji.example.android.popularmoviesstage1.database.FavoriteMoviesContract.FavoriteMoviesEntry;


public class FavoriteMoviesContentProvider extends ContentProvider {
    private static final String TAG = FavoriteMoviesContentProvider.class.getSimpleName();

    private FavoriteMoviesDbHelper mFavoriteMoviesDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int FAVORITES = 500;
    private static final int FAVORITES_WITH_ID = 501;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoriteMoviesDbHelper = new FavoriteMoviesDbHelper(context);
        return true;
    }




    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY,
                FavoriteMoviesContract.PATH_FAVORITE_MOVIES,
                FAVORITES);

        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY,
                FavoriteMoviesContract.PATH_FAVORITE_MOVIES + "/#",
                FAVORITES_WITH_ID);

        return uriMatcher;
    }



    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getReadableDatabase();
        Cursor result;

        int match = sUriMatcher.match(uri);
        switch(match) {
            case FAVORITES:
                result = db.query(FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "Insert -- uri:"+uri);
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();
        Uri result;

        int match = sUriMatcher.match(uri);
        switch(match) {
            case FAVORITES:
                Log.d(TAG, "Insert -- FAVORITES");
                long id = db.insert(FavoriteMoviesEntry.TABLE_NAME, null, values);
                if(id > 0) {
                    result = ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI, id);
                    Log.d(TAG, "Inserted -- result:"+result.toString());
                }
                else {
                    throw new SQLException("Failed to insert row into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "Delete -- uri:"+uri);

        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();
        int numRowsDeleted = 0;

        if (null == selection) selection = "1";

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                Log.d(TAG, "Delete -- FAVORITES");
                numRowsDeleted = db.delete(
                        FavoriteMoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }
        if(numRowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAG, "Deleted -- numRowsDeleted="+numRowsDeleted);
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();
        int numRowsUpdated = 0;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                numRowsUpdated = db.update(
                        FavoriteMoviesEntry.TABLE_NAME,
                        values,
                        "_id=?",
                        new String[] { id });
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }
        if(numRowsUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return numRowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();
        final int NO_MATCH = -1;
        int numRowsInserted = NO_MATCH;


        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                numRowsInserted = 0;

                try {
                    for (ContentValues value : values) {
                        long id = db.insert(FavoriteMoviesEntry.TABLE_NAME,
                                null,
                                value);

                        if (id != -1)
                            numRowsInserted++;

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                break;
        }

        if (numRowsInserted != NO_MATCH) {
            getContext().getContentResolver().notifyChange(uri, null);
            return numRowsInserted;
        }

        return super.bulkInsert(uri, values);
    }
}
