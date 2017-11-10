package com.shoji.example.android.popularmoviesstage1;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.shoji.example.android.popularmoviesstage1.backgroundtask.LoaderCallBacksEx;
import com.shoji.example.android.popularmoviesstage1.backgroundtask.TheMovieDb_LoaderCallBacksEx_Listeners;
import com.shoji.example.android.popularmoviesstage1.data.MovieData;
import com.shoji.example.android.popularmoviesstage1.data.MovieReviewData;
import com.shoji.example.android.popularmoviesstage1.data.MovieDetailsAdapter;
import com.shoji.example.android.popularmoviesstage1.data.YoutubeTrailerData;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbJsonUtils;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbUtils;
import com.shoji.example.android.popularmoviesstage1.utils.UrlStringMaker;

import java.util.ArrayList;
import java.util.List;


public class MovieDataActivity
        extends TheMovieDbAppCompat
        implements MovieDetailsAdapter.MovieTrailerAdapterOnClickHandler{
    private static final String TAG = MovieDataActivity.class.getSimpleName();

    public static final String MOVIEDATA = "movie_data";




    private final static int LOADER_ID_FETCH_MOVIE_DATA = 10001;
    private final static int LOADER_ID_FETCH_MOVIE_TRAILERS = 10002;
    private final static int LOADER_ID_FETCH_MOVIE_REVIEWS = 10003;

    private RecyclerView mMovieTrailerRecyclerView;
    private MovieDetailsAdapter mMovieDetailsAdapter;



    private Bundle mArgs;
    private LoaderCallBacksEx<MovieData> mFetchMovieDataByIdLoaderCallbacks;
    private LoaderCallBacksEx<ArrayList<YoutubeTrailerData>> mFetchMovieTrailersLoaderCallbacks;
    private LoaderCallBacksEx<ArrayList<MovieReviewData>> mFetchMovieReviewsLoaderCallbacks;

    private MovieData mMovieData;
    private ArrayList<YoutubeTrailerData> mTrailerList;
    private ArrayList<MovieReviewData> mReviewList;


    private final static String SAVE_INSTANCE_STATE_LIST_POSITION_KEY = "list-position";
    private final static String SAVE_INSTANCE_STATE_MOVIE_DATA_KEY = "movie_data";
    private final static String SAVE_INSTANCE_STATE_MOVIE_TRAILERS_KEY = "movie_trailers";
    private final static String SAVE_INSTANCE_STATE_MOVIE_REVIEWS_KEY = "movie_reviews";

    private boolean mIsFetchMovieDetailNeeded;
    private boolean mIsFetchMovieTrailersNeeded;
    private boolean mIsFetchMovieReviewsNeeded;

    /*private TextView mVoteAverage;
    private TextView mTitle;
    private ImageView mPosterImage;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mDuration;*/
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_movie_data);

        Intent intent = getIntent();
        if(intent == null || !intent.hasExtra(MOVIEDATA))
            return;
        mMovieData = intent.getParcelableExtra(MOVIEDATA);


        mIsFetchMovieDetailNeeded = true;
        mIsFetchMovieTrailersNeeded = true;
        mIsFetchMovieReviewsNeeded = true;


        createMovieDataRecyclerView();
        createFetchListenerAndCallback();



        restoreIntenceState(bundle);


        if(mIsFetchMovieDetailNeeded == true)
            doFetchMovieTrailersAndReview();
        else {
            Log.d(TAG, "Recovered info from instance");
            mMovieDetailsAdapter.setMovieData(mMovieData);
            mMovieDetailsAdapter.setTrailerData(mTrailerList);
            mMovieDetailsAdapter.setReviewData(mReviewList);

            mMovieTrailerRecyclerView.setAdapter(mMovieDetailsAdapter);

            if (bundle != null && bundle.containsKey(SAVE_INSTANCE_STATE_LIST_POSITION_KEY)) {
                Parcelable listState = bundle.getParcelable(SAVE_INSTANCE_STATE_LIST_POSITION_KEY);
                if (listState != null)
                    mMovieTrailerRecyclerView.getLayoutManager().onRestoreInstanceState(listState);

            }
        }
    }



    private void createMovieDataRecyclerView() {
        Context context = this;

        mMovieTrailerRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies_trailer);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mMovieTrailerRecyclerView.setLayoutManager(linearLayoutManager);

        mMovieTrailerRecyclerView.setHasFixedSize(true);

        /* set the adapter */
        MovieDetailsAdapter.MovieTrailerAdapterOnClickHandler movieTrailerAdapterOnClickHandler = this;
        mMovieDetailsAdapter = new MovieDetailsAdapter(context, movieTrailerAdapterOnClickHandler);
        mMovieTrailerRecyclerView.setAdapter(mMovieDetailsAdapter);
    }



    private void createFetchListenerAndCallback() {
        Context context = this;

        mArgs = new Bundle();
        mArgs.putString(TheMovieDb_LoaderCallBacksEx_Listeners.STRING_PARAM,
                mMovieData.getId());

        mFetchMovieDataByIdLoaderCallbacks =
                new LoaderCallBacksEx<>(context, new MovieDataResultListener());


        mFetchMovieTrailersLoaderCallbacks =
                new LoaderCallBacksEx<>(context, new TrailersResultListener());


        mFetchMovieReviewsLoaderCallbacks =
                new LoaderCallBacksEx<>(context, new ReviewsResultListener());
    }

    private void doFetchMovieTrailersAndReview() {

        if(!isNetworkConnected()) {
            return;
        }
        initOrRestartLoader(LOADER_ID_FETCH_MOVIE_DATA,
                mArgs, mFetchMovieDataByIdLoaderCallbacks);

        /*initOrRestartLoader(LOADER_ID_FETCH_MOVIE_TRAILERS,
                mArgs, mFetchMovieTrailersLoaderCallbacks);*/

        /*initOrRestartLoader(LOADER_ID_FETCH_MOVIE_REVIEWS,
                mArgs, mFetchMovieReviewsLoaderCallbacks);*/
    }

    private void restoreIntenceState(Bundle state) {
        if(state == null)
            return;

        if(state.containsKey(SAVE_INSTANCE_STATE_MOVIE_DATA_KEY)) {
            mMovieData = state.getParcelable(SAVE_INSTANCE_STATE_MOVIE_DATA_KEY);
            mIsFetchMovieDetailNeeded = false;
        }
        if(state.containsKey(SAVE_INSTANCE_STATE_MOVIE_TRAILERS_KEY)) {
            mTrailerList = state.getParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_TRAILERS_KEY);
            mIsFetchMovieTrailersNeeded = false;
        }
        if(state.containsKey(SAVE_INSTANCE_STATE_MOVIE_REVIEWS_KEY)) {
            mReviewList = state.getParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_REVIEWS_KEY);
            mIsFetchMovieReviewsNeeded = false;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Parcelable listState = mMovieTrailerRecyclerView.getLayoutManager().onSaveInstanceState();
        if(listState != null)
            outState.putParcelable(SAVE_INSTANCE_STATE_LIST_POSITION_KEY, listState);
        if(mMovieData != null) {
            //Log.d(TAG, "Saved movie detail");
            outState.putParcelable(SAVE_INSTANCE_STATE_MOVIE_DATA_KEY, mMovieData);
        }
        if(mTrailerList != null) {
            //Log.d(TAG, "Saved movie trailers");
            outState.putParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_TRAILERS_KEY, mTrailerList);
        }
        if(mReviewList != null) {
            //Log.d(TAG, "Saved movie reviews");
            outState.putParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_REVIEWS_KEY, mReviewList);
        }
    }

    @Override
    public void onClickMovieTrailer(YoutubeTrailerData trailerData) {
        Log.d(TAG, "Tapped at trailer:"+trailerData.toString());
        String video_key = trailerData.getKey();

        String trailerUriString = UrlStringMaker.createYoutubeUriString(video_key);
        Log.d(TAG, "YT uri="+trailerUriString);
        Intent appIntent = new Intent (Intent.ACTION_VIEW, Uri.parse(trailerUriString));

        if(isIntentSafe(appIntent)) {
                startActivity(appIntent);
        }
        else {
            String trailerUrlString = UrlStringMaker.createYoutubeUrlString(video_key);
            Log.d(TAG, "YT url=" + trailerUrlString);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrlString));
            if(isIntentSafe(webIntent))
                startActivity(webIntent);
            else
                Log.w(TAG, "Could not open trailer");
        }


    }

    private boolean isIntentSafe(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return activities.size() > 0;
    }

    // [START] Implement fetch, parse for json and data processing
    private class MovieDataResultListener
            extends TheMovieDb_LoaderCallBacksEx_Listeners<MovieData>
    {
        private final String TAG = MovieDataResultListener.class.getSimpleName();


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
            Log.d(TAG, "Movie json:"+result);
            mMovieData = result;
            mMovieDetailsAdapter.setMovieData(mMovieData);

            initOrRestartLoader(LOADER_ID_FETCH_MOVIE_TRAILERS,
                    mArgs, mFetchMovieTrailersLoaderCallbacks);
        }
    }


    private class TrailersResultListener
            extends TheMovieDb_LoaderCallBacksEx_Listeners<ArrayList<YoutubeTrailerData>> {
        private final String TAG = TrailersResultListener.class.getSimpleName();


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
                mTrailerList = result;
                for (int i = 0; i < result.size(); i++)
                    Log.d(TAG, "Trailers (" + i + ") json:" + result.get(i).toString());

                mTrailerList = result;
                mMovieDetailsAdapter.setTrailerData(mTrailerList);


                initOrRestartLoader(LOADER_ID_FETCH_MOVIE_REVIEWS,
                        mArgs, mFetchMovieReviewsLoaderCallbacks);
            }
        }
    }



    private class ReviewsResultListener
            extends TheMovieDb_LoaderCallBacksEx_Listeners<ArrayList<MovieReviewData>> {
        private final String TAG = ReviewsResultListener.class.getSimpleName();

        @Override
        public String fetchJsonString(String param) {
            return TheMovieDbUtils.getMovieReviewsById(param);
        }

        @Override
        public ArrayList<MovieReviewData> parseJsonString(String jsonString) {
            //Log.d(TAG, "AAAA: "+jsonString);
            return TheMovieDbJsonUtils.parseMovieReviewJson(jsonString);
        }

        @Override
        public void onLoadFinished(Context context, ArrayList<MovieReviewData> result) {
            if(result != null) {
                mReviewList = result;
                for (int i = 0; i < result.size(); i++)
                    Log.d(TAG, "Reviews (" + i + ") json:" + result.get(i).toString());
                mMovieDetailsAdapter.setReviewData(mReviewList);
                mMovieTrailerRecyclerView.setAdapter(mMovieDetailsAdapter);
            }
        }

    }
    // [END] Implement fetch, parse for json and data processing
}
