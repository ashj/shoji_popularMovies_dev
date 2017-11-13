package com.shoji.example.android.popularmoviesstage1.backgroundtask;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.shoji.example.android.popularmoviesstage1.data.MovieData;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbJsonUtils;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbUtils;

import java.util.ArrayList;


public class TheMovieDb_GetTopRatedMovies
    extends TheMovieDb_LoaderCallBacks {
    private final String TAG = TheMovieDb_GetTopRatedMovies.class.getSimpleName();

    private LoaderCallBacksEx<ArrayList<MovieData>> mFetchMoviesLoaderCallbacks;

    OnLoadFinishedLister mOnLoadFinishedHandler;

    public interface OnLoadFinishedLister {
        void processMovieData(ArrayList<MovieData> movieData);
        void processFinishAll();

    }

    public TheMovieDb_GetTopRatedMovies(Context context,
                                        Bundle args,
                                        LoaderManager loaderManager,
                                        OnLoadFinishedLister onLoadFinishedHandler) {
        super(context, args, loaderManager);
        mOnLoadFinishedHandler = onLoadFinishedHandler;


    }

    @Override
    public void execute() {
        initOrRestartLoader(LoaderIDs.LOADER_ID_FETCH_TOP_RATED_MOVIES,
                mArgs, mFetchMoviesLoaderCallbacks);
    }

    @Override
    protected void createHandlersAndCallbacks() {
        mFetchMoviesLoaderCallbacks =
                new LoaderCallBacksEx<>(mContext, new MovieDataResultHandler());

    }

    private class MovieDataResultHandler
            extends TheMovieDb_LoaderCallBacksListeners<ArrayList<MovieData>>
    {
        private final String TAG = MovieDataResultHandler.class.getSimpleName();


        @Override
        public String fetchJsonString(String param) {
            Log.d(TAG, "Fetching");
            return TheMovieDbUtils.getMovieTopRated();
        }

        @Override
        public ArrayList<MovieData> parseJsonString(String jsonString) {
            Log.d(TAG, jsonString);
            return TheMovieDbJsonUtils.parseMovieListJson(jsonString);
        }

        @Override
        public void onLoadFinished(Context context, ArrayList<MovieData> result) {
            //Log.d(TAG, "Movie json:"+result);
            mOnLoadFinishedHandler.processMovieData(result);
        }
    }
    @Override
    public void resetResults() {
        mFetchMoviesLoaderCallbacks.resetResult();
    }
}
