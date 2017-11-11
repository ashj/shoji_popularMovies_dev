package com.shoji.example.android.popularmoviesstage1.backgroundtask;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.shoji.example.android.popularmoviesstage1.data.MovieData;
import com.shoji.example.android.popularmoviesstage1.data.MovieReviewData;
import com.shoji.example.android.popularmoviesstage1.data.YoutubeTrailerData;
import com.shoji.example.android.popularmoviesstage1.utils.NetworkUtils;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbJsonUtils;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbUtils;

import java.util.ArrayList;



public class TheMovieDb_GetMovieCompleteDetails {
    private final static int LOADER_ID_FETCH_MOVIE_DATA = 10001;
    private final static int LOADER_ID_FETCH_MOVIE_TRAILERS = 10002;
    private final static int LOADER_ID_FETCH_MOVIE_REVIEWS = 10003;


    private Context mContext;
    private LoaderManager mLoaderManager;

    private Bundle mArgs;
    private LoaderCallBacksEx<MovieData> mFetchMovieDataByIdLoaderCallbacks;
    private LoaderCallBacksEx<ArrayList<YoutubeTrailerData>> mFetchMovieTrailersLoaderCallbacks;
    private LoaderCallBacksEx<ArrayList<MovieReviewData>> mFetchMovieReviewsLoaderCallbacks;


    TheMovieDbOnLoadFinishedLister mOnLoadFinishedHandler;

    public interface TheMovieDbOnLoadFinishedLister {
        void processMovieData(MovieData movieData);
        void processMovieTrailers(ArrayList<YoutubeTrailerData> trailerList);
        void processMovieReviews(ArrayList<MovieReviewData> reviewsList);

        void processFinishAll();

    }


    public TheMovieDb_GetMovieCompleteDetails(Context context,
                                              Bundle args,
                                              LoaderManager loaderManager,
                                              TheMovieDbOnLoadFinishedLister onLoadFinishedHandler) {
        mContext = context;
        mArgs = args;
        mLoaderManager = loaderManager;
        mOnLoadFinishedHandler = onLoadFinishedHandler;

        createFetchHandlersAndCallback();
    }




    public void execute() {
        initOrRestartLoader(LOADER_ID_FETCH_MOVIE_DATA,
                mArgs, mFetchMovieDataByIdLoaderCallbacks);

    }


    protected void initOrRestartLoader(int loaderId,
                                       Bundle args,
                                       LoaderManager.LoaderCallbacks callback) {
        if(!NetworkUtils.isNetworkConnected(mContext)) {
            mOnLoadFinishedHandler.processFinishAll();
            return;
        }

        if(null == mLoaderManager.getLoader(loaderId)) {
            mLoaderManager.initLoader(loaderId,
                    args, callback);
        }
        else {
            mLoaderManager.restartLoader(loaderId,
                    args, callback);
        }
    }


    private void createFetchHandlersAndCallback() {
        mFetchMovieDataByIdLoaderCallbacks =
                new LoaderCallBacksEx<>(mContext, new MovieDataResultHandler());

        mFetchMovieTrailersLoaderCallbacks =
                new LoaderCallBacksEx<>(mContext, new TrailersResultHandler());

        mFetchMovieReviewsLoaderCallbacks =
                new LoaderCallBacksEx<>(mContext, new ReviewsResultHandler());
    }



    private class MovieDataResultHandler
            extends TheMovieDb_LoaderCallBacksEx_Listeners<MovieData>
    {
        private final String TAG = MovieDataResultHandler.class.getSimpleName();


        @Override
        public String fetchJsonString(String param) {
            return TheMovieDbUtils.getMovieDataById(param);
        }

        @Override
        public MovieData parseJsonString(String jsonString, int flag) {
            return TheMovieDbJsonUtils.parseSingleMovieData(jsonString);
        }

        @Override
        public void onLoadFinished(Context context, MovieData result) {
            Log.d(TAG, "Movie json:"+result);
            mOnLoadFinishedHandler.processMovieData(result);
            initOrRestartLoader(LOADER_ID_FETCH_MOVIE_TRAILERS,
                    mArgs, mFetchMovieTrailersLoaderCallbacks);
        }
    }


    private class TrailersResultHandler
            extends TheMovieDb_LoaderCallBacksEx_Listeners<ArrayList<YoutubeTrailerData>> {
        private final String TAG = TrailersResultHandler.class.getSimpleName();


        @Override
        public String fetchJsonString(String param) {
            return TheMovieDbUtils.getMovieTrailersById(param);
        }

        @Override
        public ArrayList<YoutubeTrailerData> parseJsonString(String jsonString, int flag) {
            return TheMovieDbJsonUtils.parseTrailerListJson(jsonString);
        }

        @Override
        public void onLoadFinished(Context context, ArrayList<YoutubeTrailerData> result) {
            if(result != null) {
                mOnLoadFinishedHandler.processMovieTrailers(result);
                initOrRestartLoader(LOADER_ID_FETCH_MOVIE_REVIEWS,
                        mArgs, mFetchMovieReviewsLoaderCallbacks);
            }
        }
    }



    private class ReviewsResultHandler
            extends TheMovieDb_LoaderCallBacksEx_Listeners<ArrayList<MovieReviewData>> {
        private final String TAG = ReviewsResultHandler.class.getSimpleName();

        @Override
        public String fetchJsonString(String param) {
            return TheMovieDbUtils.getMovieReviewsById(param);
        }

        @Override
        public ArrayList<MovieReviewData> parseJsonString(String jsonString, int flag) {
            return TheMovieDbJsonUtils.parseMovieReviewJson(jsonString);
        }

        @Override
        public void onLoadFinished(Context context, ArrayList<MovieReviewData> result) {
            if(result != null) {
                mOnLoadFinishedHandler.processMovieReviews(result);
                mOnLoadFinishedHandler.processFinishAll();
            }
        }

    }
}
