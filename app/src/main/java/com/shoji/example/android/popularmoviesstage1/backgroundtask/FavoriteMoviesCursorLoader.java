package com.shoji.example.android.popularmoviesstage1.backgroundtask;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.shoji.example.android.popularmoviesstage1.database.FavoriteMoviesContract;
import com.shoji.example.android.popularmoviesstage1.database.FavoriteMoviesContract.FavoriteMoviesEntry;


public class FavoriteMoviesCursorLoader
    implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FavoriteMoviesCursorLoader.class.getSimpleName();
    public static final int LOADER_ID_QUERY_FAVORITE_MOVIES = 10004;
    public static final int LOADER_ID_QUERY_FAVORITE_MOVIES_WITH_ID = 10005;

    public static final String PARAM_MOVIE_ID = "movie_id";

    Context mContext;
    private LoaderCallBacksListenersInterface<Cursor> mLoaderCallBacksListeners;

    public FavoriteMoviesCursorLoader(Context context,
                                      LoaderCallBacksListenersInterface<Cursor> loaderCallBacksListeners) {
        mContext = context;
        mLoaderCallBacksListeners = loaderCallBacksListeners;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Loader<Cursor> loader = null;

        Uri uri = FavoriteMoviesEntry.CONTENT_URI;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        if(loaderId == LOADER_ID_QUERY_FAVORITE_MOVIES) {
            // use default values
        }
        else if(loaderId == LOADER_ID_QUERY_FAVORITE_MOVIES_WITH_ID) {
            if(args != null && args.containsKey(PARAM_MOVIE_ID)) {
                String movieId = args.getString(PARAM_MOVIE_ID);
                selection = FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=" + movieId;
            }
        }

        else {
            throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

        return new CursorLoader(mContext, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(mLoaderCallBacksListeners != null)
            mLoaderCallBacksListeners.onLoadFinished(mContext, cursor);

        if(cursor == null && !cursor.moveToFirst())
            return;

        int index = cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE);
        while(cursor.moveToNext()) {

            String movieTitle = cursor.getString(index);

            Log.d(TAG, "Movie in DB: "+movieTitle);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void queryFavoriteMovies(LoaderManager loaderManager) {
        if (null == loaderManager.getLoader(LOADER_ID_QUERY_FAVORITE_MOVIES)) {
            loaderManager.initLoader(LOADER_ID_QUERY_FAVORITE_MOVIES, null, this);
        } else {
            loaderManager.restartLoader(LOADER_ID_QUERY_FAVORITE_MOVIES, null, this);
        }
    }
    public void queryFavoriteMoviesById(LoaderManager loaderManager, String movieId) {
        Bundle args = new Bundle();
        args.putString(PARAM_MOVIE_ID, movieId);

        if (null == loaderManager.getLoader(LOADER_ID_QUERY_FAVORITE_MOVIES_WITH_ID)) {
            loaderManager.initLoader(LOADER_ID_QUERY_FAVORITE_MOVIES_WITH_ID, args, this);
        } else {
            loaderManager.restartLoader(LOADER_ID_QUERY_FAVORITE_MOVIES_WITH_ID, args, this);
        }
    }
}
