package com.shoji.example.android.popularmoviesstage1.backgroundtask;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.shoji.example.android.popularmoviesstage1.data.MovieData;
import com.shoji.example.android.popularmoviesstage1.data.MovieReviewData;
import com.shoji.example.android.popularmoviesstage1.data.YoutubeTrailerData;
import com.shoji.example.android.popularmoviesstage1.utils.NetworkUtils;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbJsonUtils;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbUtils;

import java.util.ArrayList;



public class TheMovieDb_GetMovieCompleteDetails
    extends TheMovieDb_LoaderCallBacks {


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
        super(context, args, loaderManager);
        mOnLoadFinishedHandler = onLoadFinishedHandler;


    }




    public void execute() {
        initOrRestartLoader(LoaderIDs.LOADER_ID_FETCH_MOVIE_DATA,
                mArgs, mFetchMovieDataByIdLoaderCallbacks);

    }


    @Override
    protected void initOrRestartLoader(int loaderId,
                                       Bundle args,
                                       LoaderManager.LoaderCallbacks callback) {
        if(!NetworkUtils.isNetworkConnected(mContext)) {
            mOnLoadFinishedHandler.processFinishAll();
            return;
        }

        super.initOrRestartLoader(loaderId, args, callback);
    }

    @Override
    protected void createHandlersAndCallbacks() {
        mFetchMovieDataByIdLoaderCallbacks =
                new LoaderCallBacksEx<>(mContext, new MovieDataResultHandler());

        mFetchMovieTrailersLoaderCallbacks =
                new LoaderCallBacksEx<>(mContext, new TrailersResultHandler());

        mFetchMovieReviewsLoaderCallbacks =
                new LoaderCallBacksEx<>(mContext, new ReviewsResultHandler());
    }



    private class MovieDataResultHandler
            extends TheMovieDb_LoaderCallBacksListeners<MovieData>
    {
        private final String TAG = MovieDataResultHandler.class.getSimpleName();


        @Override
        public String fetchJsonString(String param) {
            return TheMovieDbUtils.getMovieDataById(param);
        }

        @Override
        public MovieData parseJsonString(String jsonString) {
            return TheMovieDbJsonUtils.parseSingleMovieData(jsonString);
        }

        @Override
        public void onLoadFinished(Context context, MovieData result) {
            //Log.d(TAG, "Movie json:"+result);
            mOnLoadFinishedHandler.processMovieData(result);
            initOrRestartLoader(LoaderIDs.LOADER_ID_FETCH_MOVIE_TRAILERS,
                    mArgs, mFetchMovieTrailersLoaderCallbacks);
        }
    }


    private class TrailersResultHandler
            extends TheMovieDb_LoaderCallBacksListeners<ArrayList<YoutubeTrailerData>> {
        private final String TAG = TrailersResultHandler.class.getSimpleName();


        @Override
        public String fetchJsonString(String param) {
            return TheMovieDbUtils.getMovieTrailersById(param);
        }

        @Override
        public ArrayList<YoutubeTrailerData> parseJsonString(String jsonString) {
            return TheMovieDbJsonUtils.parseTrailerListJson(jsonString);
        }

        @Override
        public void onLoadFinished(Context context, ArrayList<YoutubeTrailerData> result) {
            if(result != null) {
                mOnLoadFinishedHandler.processMovieTrailers(result);
                initOrRestartLoader(LoaderIDs.LOADER_ID_FETCH_MOVIE_REVIEWS,
                        mArgs, mFetchMovieReviewsLoaderCallbacks);
            }
        }
    }



    private class ReviewsResultHandler
            extends TheMovieDb_LoaderCallBacksListeners<ArrayList<MovieReviewData>> {
        private final String TAG = ReviewsResultHandler.class.getSimpleName();

        @Override
        public String fetchJsonString(String param) {
            return TheMovieDbUtils.getMovieReviewsById(param);
        }

        @Override
        public ArrayList<MovieReviewData> parseJsonString(String jsonString) {
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

    public void resetResults() {
        mFetchMovieDataByIdLoaderCallbacks.resetResult();
        mFetchMovieTrailersLoaderCallbacks.resetResult();
        mFetchMovieReviewsLoaderCallbacks.resetResult();
    }
}
