package com.shoji.example.android.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.shoji.example.android.popularmoviesstage1.backgroundtask.LoaderCallBacksEx;
import com.shoji.example.android.popularmoviesstage1.backgroundtask.TheMovieDb_LoaderCallBacksEx_Listeners;
import com.shoji.example.android.popularmoviesstage1.data.MoviesListAdapter;
import com.shoji.example.android.popularmoviesstage1.data.MoviesListAdapter.MovieDataAdapterOnClickHandler;
import com.shoji.example.android.popularmoviesstage1.data.MovieData;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbJsonUtils;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbUtils;

import java.util.ArrayList;


public class MainActivity
        extends TheMovieDbAppCompat
        implements MovieDataAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int LOADER_ID_FETCH_MOVIE_DATA_BY_CRITERION = 10000;

    private final static String SAVE_INSTANCE_STATE_MOVIE_DATA_KEY = "movie-data-array-key";
    private final static String SAVE_INSTANCE_STATE_LIST_POSITION_KEY = "list-position-key";

    private TextView textView;

    private ProgressBar mLoadingMovieDataProgressBar;

    private RecyclerView mMovieDataRecyclerView;
    private MoviesListAdapter mMoviesListAdapter;


    private SharedPreferences mSharedPreference;
    private static boolean mRefreshMovieList;

    private LoaderCallBacksEx<ArrayList<MovieData>> mFetchMovieDataLoaderCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean fetchMovieDataFromNetwork = true;
        mRefreshMovieList = false;

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
        createFetchListenerAndCallback();
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

        mFetchMovieDataLoaderCallbacks =
                new LoaderCallBacksEx<>(context, new MovieDataResultListener());
    }

    private void doFetchMovieData() {
        //Log.d(TAG, "Running doFetchMovieData");

        if (!arePreconditionsValid(R.id.activity_main_root_layout_id)) {
            return;
        }

        Bundle args = createArgs();

        initOrRestartLoader(LOADER_ID_FETCH_MOVIE_DATA_BY_CRITERION,
                args, mFetchMovieDataLoaderCallbacks);
    }

    private Bundle createArgs() {
        String criterion = mSharedPreference.getString(
                getString(R.string.pref_sort_criterion_key),
                getString(R.string.pref_sort_criterion_default_value));
        //Log.d(TAG, "createCriterionBundle -- criterion="+criterion);
        int filterFlags = TheMovieDbJsonUtils.FLAGS_NO_FLAGS;

        Bundle args = new Bundle();
        args.putString(TheMovieDb_LoaderCallBacksEx_Listeners.STRING_PARAM, criterion);
        return args;
    }



    @Override
    public void onClick(MovieData movieData) {
        //Log.d(TAG, "At main: "+movieData);
        Context context = this;
        Class destinationClass = MovieDataActivity.class;

        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(MovieDataActivity.MOVIEDATA, movieData);

        startActivity(intent);
    }

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreference.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void redoFetchMovieData() {
        /* Drop any previous Result then fetch the data. */
        Log.d(TAG, "Calling redoFetchMovieData()");
        mMoviesListAdapter.setMovieData(null);
        mMovieDataRecyclerView.setAdapter(mMoviesListAdapter);
        mFetchMovieDataLoaderCallbacks.resetResult();
        doFetchMovieData();
    }
    // [END] Listen for Preferences changes


    // [START] Implement fetch, parse for json and data processing
    private class MovieDataResultListener
            extends TheMovieDb_LoaderCallBacksEx_Listeners<ArrayList<MovieData>>
    {
        private final String TAG = MovieDataResultListener.class.getSimpleName();

        @Override
        public void onStartLoading(Context context) {
            textView.setText(null);
            mLoadingMovieDataProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public String fetchJsonString(String param) {
            String jsonString = null;

            if(TextUtils.equals(getString(R.string.pref_sort_by_popularity_value), param))
                jsonString = TheMovieDbUtils.getMoviePopular();
            else if(TextUtils.equals(getString(R.string.pref_sort_by_top_rated_value), param))
                jsonString = TheMovieDbUtils.getMovieTopRated();

            return jsonString;
        }

        @Override
        public ArrayList<MovieData> parseJsonString(String jsonString, int flag) {
            return TheMovieDbJsonUtils.parseMovieListJson(jsonString, flag);
        }

        @Override
        public void onLoadFinished(Context context, ArrayList<MovieData> result) {
            if(result == null || result.size() == 0) {
                textView.setText(R.string.error_null_json_returned);
                mLoadingMovieDataProgressBar.setVisibility(View.INVISIBLE);
            }
            else {
                swapMovieData(result);
            }
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
    protected boolean isVerifiedTheMovieDbApiKey() {
        textView.setText(R.string.error_invalid_themoviedb_api_key);
        mLoadingMovieDataProgressBar.setVisibility(View.INVISIBLE);

        return super.isVerifiedTheMovieDbApiKey();
    }
    // [END] Handle connectivity issues
}
