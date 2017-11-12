package com.shoji.example.android.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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


    private TheMovieDb_GetMovieCompleteDetails mFetchMovieCompleteDetailsTasker;


    private boolean mIsFavorite;

    private static Menu mMenu;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_movie_data);

        Intent intent = getIntent();
        if(intent == null || !intent.hasExtra(MOVIEDATA))
            return;

        createBackgroundTask(intent);

        createMovieDataRecyclerView();

        boolean isInstanceStateDataPresent = isRestoreInstanceStatePossible(bundle);

        mIsFavorite = true; // TODO implement FAVORITE

        if(false == isInstanceStateDataPresent) {
            Log.d(TAG, "Fetching info from network");
            doFetchMovieTrailersAndReview();
        }
        else {
            Log.d(TAG, "Recovered info from instance");
            processFinishAll();
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
    private boolean isRestoreInstanceStatePossible(Bundle state) {

        if (state == null ||
                !state.containsKey(SAVE_INSTANCE_STATE_MOVIE_DATA_KEY) ||
                !state.containsKey(SAVE_INSTANCE_STATE_MOVIE_TRAILERS_KEY) ||
                !state.containsKey(SAVE_INSTANCE_STATE_MOVIE_REVIEWS_KEY)) {
            return false;
        }

        mMovieData = state.getParcelable(SAVE_INSTANCE_STATE_MOVIE_DATA_KEY);
        mTrailerList = state.getParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_TRAILERS_KEY);
        mReviewList = state.getParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_REVIEWS_KEY);

        return true;
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
        updateUi(button);
    }


    @Override
    public void updateUi(Button button) {
        Log.d(TAG, "updateFavoriteButtonUi");
        if (mIsFavorite == true) {
            Log.d(TAG, "Change to on");
            button.setBackground(getResources().getDrawable(android.R.drawable.btn_star_big_on));
            mIsFavorite = false;
        } else {
            Log.d(TAG, "Change to off");
            button.setBackground(getResources().getDrawable(android.R.drawable.btn_star_big_off));
            mIsFavorite = true;
        }
    }

    private boolean isFavorite() {
        Intent intent = getIntent();
        boolean result = false;

        if(intent != null) {
            MovieData fromIntentData = intent.getParcelableExtra(MOVIEDATA);
            if(fromIntentData != null)
                result = (fromIntentData.getIsFavorite() == 1 ? true : false);
        }

        return (result);
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
    }

    @Override
    public void processMovieTrailers(ArrayList<YoutubeTrailerData> trailerList) {
        mTrailerList = trailerList;
    }

    @Override
    public void processMovieReviews(ArrayList<MovieReviewData> reviewsList) {
        mReviewList = reviewsList;
    }

    @Override
    public void processFinishAll() {
        mIsFavorite = isFavorite();

        mMovieDetailsAdapter.setMovieData(mMovieData);
        mMovieDetailsAdapter.setTrailerData(mTrailerList);
        mMovieDetailsAdapter.setReviewData(mReviewList);

        mMovieTrailerRecyclerView.setAdapter(mMovieDetailsAdapter);

        createShareTrailer();
    }
    // [END] Results processing for each step

    // [START] Activity menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Setting menu");
        getMenuInflater().inflate(R.menu.movie_data_activity_menu_, menu);
        mMenu = menu;
        createShareTrailer(); // temp

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.movie_data_action_refresh_movie_list:
                redoFetchMovieData();
                return true;

            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createShareTrailer() {
        ArrayList<YoutubeTrailerData> trailerList = null;
        YoutubeTrailerData trailerData = null;
        Log.d(TAG, "createShareTrailer");
        if (mMenu == null) return;

        if(mMovieDetailsAdapter != null)
            trailerList = mMovieDetailsAdapter.getTrailerData();
        if(trailerList != null && trailerList.size() != 0)
            trailerData = trailerList.get(0);
        if(trailerData != null) {


            String trailerUrlString = UrlStringMaker.createYoutubeUrlString(trailerData.getKey());
            Intent intent = ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setChooserTitle(trailerUrlString)
                    .setText(trailerUrlString)
                    .getIntent();

            MenuItem menuItem = mMenu.findItem(R.id.movie_data_action_share_first_trailer);
            menuItem.setVisible(true);
            menuItem.setIntent(intent);
        }
    }

    private void redoFetchMovieData() {
        /* Drop any previous Result then fetch the data. */
        Log.d(TAG, "Calling redoFetchMovieData()");
        mMovieDetailsAdapter.setMovieData(null);
        mMovieDetailsAdapter.setTrailerData(null);
        mMovieDetailsAdapter.setReviewData(null);
        mMovieTrailerRecyclerView.setAdapter(mMovieDetailsAdapter);
        mFetchMovieCompleteDetailsTasker.resetResults();
        doFetchMovieTrailersAndReview();
    }
    // [END] Activity menu bar
}
