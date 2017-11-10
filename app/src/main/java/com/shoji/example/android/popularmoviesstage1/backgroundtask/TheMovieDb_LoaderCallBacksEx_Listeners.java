package com.shoji.example.android.popularmoviesstage1.backgroundtask;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public abstract class TheMovieDb_LoaderCallBacksEx_Listeners<Result>
        implements LoaderCallBacksListenersInterface<Result> {
    private static final String TAG = TheMovieDb_LoaderCallBacksEx_Listeners.class.getSimpleName();


    public static final String STRING_PARAM = "string_param";

    @Override
    public void onStartLoading(Context context) {}

    @Override
    public Result onLoadInBackground(Context context, Bundle args) {
        Log.d(TAG, "Called");
        Result result = null;
        String jsonString = null;
        if (args != null && args.containsKey(STRING_PARAM)) {
            String param = args.getString(STRING_PARAM);

            if(param != null && param.length() != 0) {
                jsonString = fetchJsonString(param);
            }
        }
        if(jsonString != null && jsonString.length() != 0) {
            result = parseJsonString(jsonString);
        }

        return result;
    }

    protected abstract String fetchJsonString(String param);
    protected abstract Result parseJsonString(String jsonString);

    @Override
    public void onLoadFinished(Context context, Result result) {}
}
