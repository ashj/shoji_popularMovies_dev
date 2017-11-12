package com.shoji.example.android.popularmoviesstage1.database;


import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteMoviesContract {
    public static final String AUTHORITY = "com.shoji.example.android.popularmoviesstage1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITE_MOVIES = FavoriteMoviesEntry.TABLE_NAME;

    private FavoriteMoviesContract() {}

    public static final class FavoriteMoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIES)
                .build();

        public static final String TABLE_NAME = "favoritemovies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
    }
}
