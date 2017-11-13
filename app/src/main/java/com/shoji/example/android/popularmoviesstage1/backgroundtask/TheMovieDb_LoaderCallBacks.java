package com.shoji.example.android.popularmoviesstage1.backgroundtask;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;



public abstract class TheMovieDb_LoaderCallBacks {

    protected Context mContext;
    protected LoaderManager mLoaderManager;
    protected Bundle mArgs;


    public TheMovieDb_LoaderCallBacks(Context context,
                                      Bundle args,
                                      LoaderManager loaderManager) {
        mContext = context;
        mArgs = args;
        mLoaderManager = loaderManager;

        createHandlersAndCallbacks();
    }




    public abstract void execute();
    protected abstract void createHandlersAndCallbacks();
    protected abstract void resetResults();



    protected void initOrRestartLoader(int loaderId,
                                       Bundle args,
                                       LoaderManager.LoaderCallbacks callback) {

        if(null == mLoaderManager.getLoader(loaderId)) {
            mLoaderManager.initLoader(loaderId,
                    args, callback);
        }
        else {
            mLoaderManager.restartLoader(loaderId,
                    args, callback);
        }
    }



}
