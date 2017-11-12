package com.shoji.example.android.popularmoviesstage1.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.shoji.example.android.popularmoviesstage1.database.FavoriteMoviesContract.FavoriteMoviesEntry;


public class FavoriteMoviesContentProvider extends ContentProvider {
    private FavoriteMoviesDbHelper mFavoriteMoviesDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int FAVORITES = 0;
    private static final int FAVORITES_WITH_ID = 1;

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
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
