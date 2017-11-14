package com.shoji.example.android.popularmoviesstage1;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shoji.example.android.popularmoviesstage1.backgroundtask.FavoriteMoviesCursorLoader;
import com.shoji.example.android.popularmoviesstage1.backgroundtask.LoaderCallBacksEx;
import com.shoji.example.android.popularmoviesstage1.backgroundtask.LoaderCallBacksListenersInterface;
import com.shoji.example.android.popularmoviesstage1.backgroundtask.TheMovieDb_GetPopularMovies;
import com.shoji.example.android.popularmoviesstage1.backgroundtask.TheMovieDb_GetTopRatedMovies;
import com.shoji.example.android.popularmoviesstage1.data.MoviesListAdapter;
import com.shoji.example.android.popularmoviesstage1.data.MoviesListAdapter.MovieDataAdapterOnClickHandler;
import com.shoji.example.android.popularmoviesstage1.data.MovieData;
import com.shoji.example.android.popularmoviesstage1.database.FavoriteMoviesContract;

import java.util.ArrayList;


public class MainActivity
        extends TheMovieDbAppCompat
        implements MovieDataAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = MainActivity.class.getSimpleName();


    private final static String SAVE_INSTANCE_STATE_MOVIE_DATA_KEY = "movie-data-array-key";
    private final static String SAVE_INSTANCE_STATE_LIST_POSITION_KEY = "list-position-key";

    private TextView textView;

    private ProgressBar mLoadingMovieDataProgressBar;

    private RecyclerView mMovieDataRecyclerView;
    private MoviesListAdapter mMoviesListAdapter;


    private SharedPreferences mSharedPreference;
    private static boolean mRefreshMovieList;

    private LoaderCallBacksEx<ArrayList<MovieData>> mFetchMovieDataLoaderCallbacks;

    private TheMovieDb_GetPopularMovies mFetchPopularMoviesLoaderCallBacks;
    private TheMovieDb_GetPopularMovies.OnLoadFinishedLister mPopularMoviesHandler;
    private TheMovieDb_GetTopRatedMovies mFetchTopRatedMoviesLoaderCallBacks;
    private TheMovieDb_GetTopRatedMovies.OnLoadFinishedLister mTopRatedMoviesHandler;

    private static boolean sIsShowFavoritesOnly;
    private ArrayList<MovieData> mFavoritesMovieData;
    private Context mContext;

    private static FavoriteContentObserver sFavoriteContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean fetchMovieDataFromNetwork = true;
        mRefreshMovieList = false;
        mContext = this;
        sFavoriteContentObserver = new FavoriteContentObserver(new Handler());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createGeneralViews();
        createMovieDataRecyclerView();

        if(savedInstanceState != null) {
            /* Restore movie data to save network usage */
            if(savedInstanceState.containsKey(SAVE_INSTANCE_STATE_MOVIE_DATA_KEY)) {
                Log.d(TAG, "savedInstanceState containsKey: "+SAVE_INSTANCE_STATE_MOVIE_DATA_KEY);
                ArrayList<MovieData> movieData = savedInstanceState.getParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_DATA_KEY);
                if(movieData != null && movieData.size() != 0) {
                    swapMovieData(movieData);
                    fetchMovieDataFromNetwork = false;
                    Log.d(TAG, "Restored movie data from "+SAVE_INSTANCE_STATE_MOVIE_DATA_KEY);
                }
            }

            /* Restore scroll position of Recycler View */
            if(savedInstanceState.containsKey(SAVE_INSTANCE_STATE_LIST_POSITION_KEY)) {
                Log.d(TAG, "savedInstanceState containsKey: "+SAVE_INSTANCE_STATE_LIST_POSITION_KEY);
                Parcelable savedState = savedInstanceState.getParcelable(SAVE_INSTANCE_STATE_LIST_POSITION_KEY);
                mMovieDataRecyclerView.getLayoutManager().onRestoreInstanceState(savedState);
            }
        }

        /* Get json in a background thread */

        if (fetchMovieDataFromNetwork) {
            Log.d(TAG, "Calling doFetchMovieData()");
            doFetchMovieData();
        }
    }



    private void createGeneralViews() {
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreference.registerOnSharedPreferenceChangeListener(this); //Listen for Preferences changes

        textView = (TextView) findViewById(R.id.hello_text);
        mLoadingMovieDataProgressBar = (ProgressBar) findViewById(R.id.pb_loading_progress);

    }

    private void createMovieDataRecyclerView() {
        Context context = this;

        mMovieDataRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        Integer numCols = numberOfColumns();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numCols);
        mMovieDataRecyclerView.setLayoutManager(gridLayoutManager);

        mMovieDataRecyclerView.setHasFixedSize(true);

        /* set the adapter */
        MovieDataAdapterOnClickHandler movieDataAdapterOnClickHandler = this;
        mMoviesListAdapter = new MoviesListAdapter(context, movieDataAdapterOnClickHandler);
        mMovieDataRecyclerView.setAdapter(mMoviesListAdapter);
    }

    // Set number of columns programmatically for the Recycler View.
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = getResources().getInteger(R.integer.main_activity_columns_width_divider);
        int minNumColumns = getResources().getInteger(R.integer.main_activity_columns_minimum_num_columns);

        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < minNumColumns)
            return minNumColumns;
        return nColumns;
    }


    private void createFetchListenerAndCallback() {
        Context context = this;
        Bundle args = null;

        mPopularMoviesHandler = new PopularMoviesHandler();
        mFetchPopularMoviesLoaderCallBacks = new TheMovieDb_GetPopularMovies(context, args, getSupportLoaderManager(), mPopularMoviesHandler);

        mTopRatedMoviesHandler = new TopRatedMoviesHandler();
        mFetchTopRatedMoviesLoaderCallBacks = new TheMovieDb_GetTopRatedMovies(context, args, getSupportLoaderManager(), mTopRatedMoviesHandler);
    }

    private void doFetchMovieData() {
        //Log.d(TAG, "Running doFetchMovieData");

        if (!arePreconditionsValid(R.id.activity_main_root_layout_id)) {
            return;
        }
        String criterion = getCriterion();
        createFetchListenerAndCallback();
        if(TextUtils.equals(getString(R.string.pref_sort_by_favorites_only_value), criterion)) {
            Log.d(TAG, "Running doFetchMovieData - favorites");
            sIsShowFavoritesOnly = true;
            mFavoritesMovieData = null;

            sFavoriteContentObserver.register();
            mFetchPopularMoviesLoaderCallBacks.execute();
        }
        else {
            sIsShowFavoritesOnly = false;
            sFavoriteContentObserver.unregister();

            if (TextUtils.equals(getString(R.string.pref_sort_by_popularity_value), criterion)) {
                Log.d(TAG, "Running doFetchMovieData - popular");
                mFetchPopularMoviesLoaderCallBacks.execute();
            }
            else if (TextUtils.equals(getString(R.string.pref_sort_by_top_rated_value), criterion)) {
                Log.d(TAG, "Running doFetchMovieData - top rated");
                mFetchTopRatedMoviesLoaderCallBacks.execute();
            }
        }
    }

    private String getCriterion() {
        return mSharedPreference.getString(
                getString(R.string.pref_sort_criterion_key),
                getString(R.string.pref_sort_criterion_default_value));
    }

    /* Tap on a movie poster */
    @Override
    public void onClick(MovieData movieData) {
        //Log.d(TAG, "At main: "+movieData);
        Context context = this;
        Class destinationClass = MovieDataActivity.class;

        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(MovieDataActivity.MOVIEDATA, movieData);

        startActivity(intent);
    }

    /* Set new data in the adapter and refresh the recycler view */
    private void swapMovieData(ArrayList<MovieData> newMovieData) {
        //Log.d(TAG, "swapMovieData");
        mMoviesListAdapter.setMovieData(newMovieData);
        mMovieDataRecyclerView.setAdapter(mMoviesListAdapter);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVE_INSTANCE_STATE_LIST_POSITION_KEY, mMovieDataRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(SAVE_INSTANCE_STATE_MOVIE_DATA_KEY, mMoviesListAdapter.getMovieData());
    }


    // [START] Activity menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_activity_action_open_preferences:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                return true;
            case R.id.main_activity_action_refresh_movie_list:
                redoFetchMovieData();
                return true;

            default: break;
        }
        return super.onOptionsItemSelected(item);
    }
    // [END] Activity menu bar


    // [START] Listen for Preferences changes
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(TextUtils.equals(key, getString(R.string.pref_sort_criterion_key)))
            mRefreshMovieList = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mRefreshMovieList) {
            //Log.d(TAG, "onStart -- mRefreshMovieList is true");
            redoFetchMovieData();
            mRefreshMovieList = false;
        }
        if(sIsShowFavoritesOnly) {
            if(sFavoriteContentObserver != null) {
                sFavoriteContentObserver.register();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreference.unregisterOnSharedPreferenceChangeListener(this);
        if(sFavoriteContentObserver != null)
            sFavoriteContentObserver.unregister();
    }

    private void redoFetchMovieData() {
        /* Drop any previous Result then fetch the data. */
        Log.d(TAG, "Calling redoFetchMovieData()");
        mFavoritesMovieData = null;
        mMoviesListAdapter.setMovieData(null);
        mMovieDataRecyclerView.setAdapter(mMoviesListAdapter);
        if(mFetchMovieDataLoaderCallbacks != null)
            mFetchMovieDataLoaderCallbacks.resetResult();
        if(mFetchTopRatedMoviesLoaderCallBacks != null)
            mFetchTopRatedMoviesLoaderCallBacks.resetResults();
        doFetchMovieData();
    }
    // [END] Listen for Preferences changes


    // [START] Implement fetch, parse for json and data processing
    private class PopularMoviesHandler implements TheMovieDb_GetPopularMovies.OnLoadFinishedLister {
        @Override
        public void processMovieData(ArrayList<MovieData> result) {
            mLoadingMovieDataProgressBar.setVisibility(View.INVISIBLE);
            if(result == null || result.size() == 0) {
                textView.setText(R.string.error_null_json_returned);

                if(sIsShowFavoritesOnly) {
                    Log.d(TAG, "--- CASES FAVORITE ONLY #1a ----");
                    // try next step
                    mFetchTopRatedMoviesLoaderCallBacks.execute();
                }
            }
            else if(sIsShowFavoritesOnly) {
                Log.d(TAG, "--- CASES FAVORITE ONLY #1b ----");
                mFavoritesMovieData = result;
                mFetchTopRatedMoviesLoaderCallBacks.execute();
            }
            else {
                Log.d(TAG, "--- CASE POPULAR ----");
                swapMovieData(result);
            }
        }

        @Override
        public void processFinishAll() { }
    }
    // [START] Implement fetch, parse for json and data processing
    private class TopRatedMoviesHandler implements TheMovieDb_GetTopRatedMovies.OnLoadFinishedLister {
        @Override
        public void processMovieData(ArrayList<MovieData> result) {
            mLoadingMovieDataProgressBar.setVisibility(View.INVISIBLE);
            if(result == null || result.size() == 0) {
                textView.setText(R.string.error_null_json_returned);

                if(sIsShowFavoritesOnly) {
                    // add list here, might have something
                    Log.d(TAG, "--- CASES FAVORITE ONLY #2a----");

                    mFavoritesMovieData = mergeArrayListUnique(mFavoritesMovieData, result);
                    swapMovieData(mFavoritesMovieData);
                }
            }
            else {
                // TODO test apply Favorites
                if(sIsShowFavoritesOnly) {
                    Log.d(TAG, "--- CASES FAVORITE ONLY #2b----");


                    mFavoritesMovieData = mergeArrayListUnique(mFavoritesMovieData, result);

                    /* Query the database to get the favorites */
                    FavoriteMoviesHandler handler = new FavoriteMoviesHandler(mFavoritesMovieData);
                    FavoriteMoviesCursorLoader loader = new FavoriteMoviesCursorLoader(mContext, handler);
                    loader.queryFavoriteMovies(getSupportLoaderManager());

                }
                else {
                    swapMovieData(result);
                }

            }
        }

        @Override
        public void processFinishAll() { }
    }

    private ArrayList<MovieData> mergeArrayListUnique(ArrayList<MovieData> a, ArrayList<MovieData> b) {

        if(a == null) {
            a = b;
        }
        else if(b != null) {
            for(MovieData data : b) {
                if(!a.contains(data))
                    a.add(data);
            }
        }
        return a;
    }


    private class FavoriteMoviesHandler
        implements LoaderCallBacksListenersInterface<Cursor> {
        ArrayList<MovieData> mMovieDataList;
        FavoriteMoviesHandler(ArrayList<MovieData> list) {
            mMovieDataList = list;
        }

        @Override
        public void onStartLoading(Context context) { }


        @Override
        public Cursor onLoadInBackground(Context context, Bundle args) {
            return null;
        }

        @Override
        public void onLoadFinished(Context context, Cursor cursor) {
            if (cursor == null && !cursor.moveToFirst())
                return;

            if(mFavoritesMovieData != null && mFavoritesMovieData.size() > 0) {
                ArrayList<MovieData> finalFavoriteList = new ArrayList<>();
                int titleIdx = cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE);
                int idIdx = cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID);
                int listIdx;

                while (cursor.moveToNext()) {
                    String movieTitle = cursor.getString(titleIdx);
                    String movieId = cursor.getString(idIdx);
                    MovieData movie = new MovieData();
                    movie.setId(movieId);
                    Log.d(TAG, "Movie in DB: " + movieTitle);

                    if ((listIdx = mFavoritesMovieData.indexOf(movie)) > 0) {
                        finalFavoriteList.add(mFavoritesMovieData.get(listIdx));
                        Log.d(TAG, "Added: " + movieTitle);
                    } else {
                        Log.d(TAG, "Discarted: " + movieTitle);
                    }


                }
                mFavoritesMovieData = finalFavoriteList;
                cursor.close();
            }

            swapMovieData(mFavoritesMovieData);
        }
    }

    // [END] Implement fetch, parse for json and data processing


    // [START] Handle connectivity issues
    @Override
    protected void handleConnectivityIssue(int rootViewId) {
        textView.setText(R.string.error_no_network_connectivity);
        mLoadingMovieDataProgressBar.setVisibility(View.INVISIBLE);

        super.handleConnectivityIssue(rootViewId);
    }

    @Override
    protected boolean arePreconditionsValid(int rootViewId) {
        if (!isNetworkConnected()) {
            handleConnectivityIssue(rootViewId);
            return false;
        }
        else if (!isVerifiedTheMovieDbApiKey()) {
            textView.setText(R.string.error_invalid_themoviedb_api_key);
            mLoadingMovieDataProgressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        return true;
    }

    // [END] Handle connectivity issues

    private class FavoriteContentObserver extends ContentObserver {
        private final String TAG = FavoriteContentObserver.class.getSimpleName();
        public FavoriteContentObserver(Handler handler) {
            super(handler);
        }

        public void register() {
            Log.d(TAG, "Register Content Observer");
            mContext.getContentResolver().registerContentObserver(
                    FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                    false,
                    this
            );
        }
        public void unregister() {
            Log.d(TAG, "Unregister Content Observer");
            mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange) {
            redoFetchMovieData();
        }
    }
}
