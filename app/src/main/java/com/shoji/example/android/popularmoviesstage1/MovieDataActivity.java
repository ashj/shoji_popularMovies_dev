package com.shoji.example.android.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.shoji.example.android.popularmoviesstage1.backgroundtask.LoaderCallBacksEx;
import com.shoji.example.android.popularmoviesstage1.backgroundtask.TheMovieDb_GetMovieCompleteDetails;
import com.shoji.example.android.popularmoviesstage1.backgroundtask.TheMovieDb_LoaderCallBacksEx_Listeners;
import com.shoji.example.android.popularmoviesstage1.backgroundtask.TheMovieDb_GetMovieCompleteDetails.TheMovieDbOnLoadFinishedLister;
import com.shoji.example.android.popularmoviesstage1.data.MovieData;
import com.shoji.example.android.popularmoviesstage1.data.MovieReviewData;
import com.shoji.example.android.popularmoviesstage1.data.MovieDetailsAdapter;
import com.shoji.example.android.popularmoviesstage1.data.YoutubeTrailerData;
import com.shoji.example.android.popularmoviesstage1.utils.UrlStringMaker;

import java.util.ArrayList;
import java.util.List;


public class MovieDataActivity
        extends TheMovieDbAppCompat
        implements MovieDetailsAdapter.MovieTrailerAdapterOnClickHandler,
        MovieDetailsAdapter.MovieFavoriteAdapterOnClickHandler,
        TheMovieDbOnLoadFinishedLister {
    private static final String TAG = MovieDataActivity.class.getSimpleName();

    public static final String MOVIEDATA = "movie_data";






    private RecyclerView mMovieTrailerRecyclerView;
    private MovieDetailsAdapter mMovieDetailsAdapter;

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

    private TheMovieDb_GetMovieCompleteDetails mFetchMovieCompleteDetailsTasker;

    // TODO implement FAVORITE
    private int state = 0;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_movie_data);

        Intent intent = getIntent();
        if(intent == null || !intent.hasExtra(MOVIEDATA))
            return;

        createBackgroundTask(intent);

        createMovieDataRecyclerView();

        restoreInstanceState(bundle);

        if(mIsFetchMovieDetailNeeded ||
                mIsFetchMovieTrailersNeeded||
                mIsFetchMovieReviewsNeeded) {
            doFetchMovieTrailersAndReview();
        }
        else {
            Log.d(TAG, "Recovered info from instance");
            mMovieDetailsAdapter.setMovieData(mMovieData);
            mMovieDetailsAdapter.setTrailerData(mTrailerList);
            mMovieDetailsAdapter.setReviewData(mReviewList);

            mMovieTrailerRecyclerView.setAdapter(mMovieDetailsAdapter);

            restoreRecyclerViewInstanceState(bundle);
        }
    }



    private void createMovieDataRecyclerView() {
        Context context = this;

        mMovieTrailerRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies_trailer);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mMovieTrailerRecyclerView.setLayoutManager(linearLayoutManager);

        mMovieTrailerRecyclerView.setHasFixedSize(true);

        /* set the adapter */
        MovieDetailsAdapter.MovieTrailerAdapterOnClickHandler
                movieTrailerAdapterOnClickHandler = this;
        MovieDetailsAdapter.MovieFavoriteAdapterOnClickHandler
                movieFavoriteAdapterOnClickHandler = this;
        mMovieDetailsAdapter = new MovieDetailsAdapter(context,
                movieFavoriteAdapterOnClickHandler,
                movieTrailerAdapterOnClickHandler);
        mMovieTrailerRecyclerView.setAdapter(mMovieDetailsAdapter);
    }

    private void createBackgroundTask(Intent intent) {
        MovieData movieData = intent.getParcelableExtra(MOVIEDATA);
        Bundle args = new Bundle();
        args.putString(TheMovieDb_LoaderCallBacksEx_Listeners.STRING_PARAM,
                movieData.getId());

        TheMovieDb_GetMovieCompleteDetails.TheMovieDbOnLoadFinishedLister processResults = this;
        mFetchMovieCompleteDetailsTasker = new TheMovieDb_GetMovieCompleteDetails(this,
                args, getSupportLoaderManager(),
                processResults);
    }



    private void doFetchMovieTrailersAndReview() {

        if(!arePreconditionsValid(R.id.activity_movie_data_root_layout_id)) {
            return;
        }
        mFetchMovieCompleteDetailsTasker.execute();

    }



    // [START] Recover/Save Instance state
    private void restoreInstanceState(Bundle state) {
        mIsFetchMovieDetailNeeded = true;
        mIsFetchMovieTrailersNeeded = true;
        mIsFetchMovieReviewsNeeded = true;

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

    private void restoreRecyclerViewInstanceState(Bundle bundle) {
        if (bundle != null && bundle.containsKey(SAVE_INSTANCE_STATE_LIST_POSITION_KEY)) {
            Parcelable listState = bundle.getParcelable(SAVE_INSTANCE_STATE_LIST_POSITION_KEY);
            if (listState != null)
                mMovieTrailerRecyclerView.getLayoutManager().onRestoreInstanceState(listState);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mMovieData == null || mTrailerList == null || mReviewList == null)
            return;

        Parcelable listState = mMovieTrailerRecyclerView.getLayoutManager().onSaveInstanceState();
        if(listState != null)
            outState.putParcelable(SAVE_INSTANCE_STATE_LIST_POSITION_KEY, listState);

        //Log.d(TAG, "Saved movie detail");
        outState.putParcelable(SAVE_INSTANCE_STATE_MOVIE_DATA_KEY, mMovieData);

        //Log.d(TAG, "Saved movie trailers");
        outState.putParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_TRAILERS_KEY, mTrailerList);

        //Log.d(TAG, "Saved movie reviews");
        outState.putParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_REVIEWS_KEY, mReviewList);
    }
    // [END] Recover/Save Instance state




    // [START] Favorite button implementation
    // TODO implement FAVORITE
    @Override
    public void onClickFavoriteButton(Button button) {
        Log.d(TAG, "Clicked on favorite");
        if (state == 0) {
            button.setText(getString(R.string.unmark_as_favorite));
            state = 1;
        } else if (state == 1) {
            button.setText(getString(R.string.mark_as_favorite));
            state = 0;
        }

    }
    // [END] Favorite button implementation



    // [START] Launch intents to watch the trailers
    @Override
    public void onClickMovieTrailer(YoutubeTrailerData trailerData) {
        //Log.d(TAG, "Tapped at trailer:"+trailerData.toString());
        String video_key = trailerData.getKey();

        String trailerUriString = UrlStringMaker.createYoutubeUriString(video_key);
        //Log.d(TAG, "YT uri="+trailerUriString);
        Intent appIntent = new Intent (Intent.ACTION_VIEW, Uri.parse(trailerUriString));

        if(isIntentSafe(appIntent)) {
                startActivity(appIntent);
        }
        else {
            String trailerUrlString = UrlStringMaker.createYoutubeUrlString(video_key);
            //Log.d(TAG, "YT url=" + trailerUrlString);
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
    // [END] Launch intents to watch the trailers




    // [START] Results processing for each step
    @Override
    public void processMovieData(MovieData movieData) {
        mMovieData = movieData;
        mMovieDetailsAdapter.setMovieData(mMovieData);
    }

    @Override
    public void processMovieTrailers(ArrayList<YoutubeTrailerData> trailerList) {
        //for (int i = 0; i < trailerList.size(); i++)
        //    Log.d(TAG, "Trailers (" + i + ") json:" + trailerList.get(i).toString());

        mTrailerList = trailerList;
        mMovieDetailsAdapter.setTrailerData(mTrailerList);
    }

    @Override
    public void processMovieReviews(ArrayList<MovieReviewData> reviewsList) {
        mReviewList = reviewsList;
        for (int i = 0; i < reviewsList.size(); i++)
            Log.d(TAG, "Reviews (" + i + ") json:" + reviewsList.get(i).toString());
        mMovieDetailsAdapter.setReviewData(mReviewList);
    }

    @Override
    public void processFinishAll() {
        mMovieTrailerRecyclerView.setAdapter(mMovieDetailsAdapter);
    }
    // [END] Results processing for each step
}
