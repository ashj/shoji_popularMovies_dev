package com.shoji.example.android.popularmoviesstage1.backgroundtask;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

public class LoaderCallBacksEx<Result>
        implements LoaderManager.LoaderCallbacks<Result> {
    private static final String TAG = LoaderCallBacksEx.class.getSimpleName();
    private static final boolean DBG = false;

    private Context mContext;
    private LoaderCallBacksListenersInterface<Result> mLoaderCallBacksListeners;


    private Result mResult;

    public LoaderCallBacksEx(
            Context context,
            LoaderCallBacksListenersInterface<Result>
                    loaderCallBacksListeners)
    {
        mContext = context;
        mLoaderCallBacksListeners = loaderCallBacksListeners;

        mResult = null;
    }

    /* In case it is being used as static to preserve the Result,
    allow a manual way to reset it.
     */
    public void resetResult() {
        mResult = null;
    }

    @Override
    public Loader<Result> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Result>(mContext) {
            @Override
            protected void onStartLoading() {
                if(DBG) Log.d(TAG, "onCreateLoader // onStartLoading");
                super.onStartLoading();
                if(mResult == null) {
                    if(DBG) Log.d(TAG, "onCreateLoader // calling forceLoad()");
                    if(mLoaderCallBacksListeners != null)
                        mLoaderCallBacksListeners.onStartLoading(mContext);
                    forceLoad();
                }
                else {
                    if(DBG) Log.d(TAG, "onCreateLoader // calling deliverResult()");
                    deliverResult(mResult);
                }
            }

            @Override
            public Result loadInBackground() {
                if(DBG) Log.d(TAG, "onCreateLoader // loadInBackground");
                if(mLoaderCallBacksListeners != null) {
                    return mLoaderCallBacksListeners.onLoadInBackground(mContext, args);
                }
                return null;
            }
            @Override
            public void deliverResult(Result result) {
                if(DBG) Log.d(TAG, "onCreateLoader // deliverResult");
                super.deliverResult(result);
                mResult = result;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Result> loader, Result result) {
        if(mLoaderCallBacksListeners != null) {
            mLoaderCallBacksListeners.onLoadFinished(mContext, result);
        }
    }

    @Override
    public void onLoaderReset(Loader<Result> loader) {
        // do nothing
    }
}
