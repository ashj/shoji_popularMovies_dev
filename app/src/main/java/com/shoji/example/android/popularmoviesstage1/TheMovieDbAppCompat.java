package com.shoji.example.android.popularmoviesstage1;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.shoji.example.android.popularmoviesstage1.utils.NetworkUtils;


abstract public class TheMovieDbAppCompat
        extends AppCompatActivity {


    protected boolean arePreconditionsValid(int rootViewId) {
        if (!isNetworkConnected()) {
            handleConnectivityIssue(rootViewId);
            return false;
        }
        else if (!isVerifiedTheMovieDbApiKey()) {
            return false;
        }
        return true;
    }


    protected void handleConnectivityIssue(int rootViewId) {
        Snackbar snackbar = Snackbar.make(findViewById(rootViewId),
                R.string.error_no_network_connectivity_snackbar,
                Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    protected boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(this);
    }

    protected boolean isVerifiedTheMovieDbApiKey() {
        boolean isOk = true;
        if(BuildConfig.THEMOVIEDB_API_KEY == null ||
                BuildConfig.THEMOVIEDB_API_KEY.length() == 0) {
            Toast.makeText(this,
                    getString(R.string.error_invalid_themoviedb_api_key),
                    Toast.LENGTH_LONG)
                    .show();

            isOk = false;
        }

        return isOk;
    }

    protected void initOrRestartLoader(int loaderId,
                                       Bundle args,
                                       LoaderManager.LoaderCallbacks callback) {
        LoaderManager loaderManager = getSupportLoaderManager();
        if(null == loaderManager.getLoader(loaderId)) {
            loaderManager.initLoader(loaderId,
                    args, callback);
        }
        else {
            loaderManager.restartLoader(loaderId,
                    args, callback);
        }
    }
}
