package com.shoji.example.android.popularmoviesstage1.backgroundtask;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbJsonUtils;

public abstract class TheMovieDb_LoaderCallBacksEx_Listeners<Result>
        implements LoaderCallBacksListenersInterface<Result> {
    private static final String TAG = TheMovieDb_LoaderCallBacksEx_Listeners.class.getSimpleName();


    public static final String STRING_PARAM = "string_param";
    public static final String INTEGER_FLAG = "string_FLAG";

    @Override
    public void onStartLoading(Context context) {}

    @Override
    public Result onLoadInBackground(Context context, Bundle args) {
        Log.d(TAG, "Called");
        Result result = null;
        String jsonString = null;

        // Fetch json string.
        if (args != null && args.containsKey(STRING_PARAM)) {
            String param = args.getString(STRING_PARAM);

            if(param != null && param.length() != 0) {
                jsonString = fetchJsonString(param);
            }
        }
        // Parse jsonString
        if(jsonString != null && jsonString.length() != 0) {
            int flag = TheMovieDbJsonUtils.FLAGS_NO_FLAGS;

            if(args != null && args.containsKey(INTEGER_FLAG))
                flag = args.getInt(INTEGER_FLAG);

            result = parseJsonString(jsonString, flag);
        }

        return result;
    }

    protected abstract String fetchJsonString(String param);
    protected abstract Result parseJsonString(String jsonString, int flags);

    @Override
    public void onLoadFinished(Context context, Result result) {}
}
