package com.shoji.example.android.popularmoviesstage1.database;


import android.provider.BaseColumns;

public class FavoriteMoviesContract {


    private FavoriteMoviesContract() {}

    public static final class FavoriteMoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favoritemovies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
    }
}
