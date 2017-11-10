package com.shoji.example.android.popularmoviesstage1.utils;

import android.content.Context;
import android.net.Uri;

/**
 * Created by lsitec333.asato on 10/11/2017.
 */

public class UrlStringMaker {
    public static final String YT_BASE_URI = "vnd.youtube:";
    public static final String YT_BASE_URL = "https://www.youtube.com/watch";
    public static final String YT_PATH_WATCH = "watch";
    public static final String YT_PARAM_VIDEO_KEY = "v";

    public static String createYoutubeUriString(String videoKey) {
        return YT_BASE_URI + videoKey;
    }

    public static String createYoutubeUrlString(String videoKey) {
        Uri builtUri = Uri.parse(YT_BASE_URL).buildUpon()
                .appendPath(YT_PATH_WATCH)
                .appendQueryParameter(YT_PARAM_VIDEO_KEY, videoKey)
                .build();
        return builtUri.toString();
    }
}
