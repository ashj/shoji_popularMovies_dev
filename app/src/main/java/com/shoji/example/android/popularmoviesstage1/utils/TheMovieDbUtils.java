package com.shoji.example.android.popularmoviesstage1.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.shoji.example.android.popularmoviesstage1.BuildConfig;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TheMovieDbUtils {
    private static final String TAG = TheMovieDbUtils.class.getSimpleName();

    private final static String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";

    private final static String BASE_POSTER_SIZE_W342 = "w342";
    private final static String BASE_POSTER_SIZE_W185 = "w185";
    public final static int POSTER_SIZE_SMALL = 0;
    public final static int POSTER_SIZE_BIG = 1;

    private final static String API_BASE_URL = "https://api.themoviedb.org/3/";

    private final static String MOVIE = "movie/";
    private final static String SEPARATOR = "/";
    private final static String MOVIE_POPULAR = "movie/popular";
    private final static String MOVIE_TOP_RATED = "movie/top_rated";

    private final static String MOVIE_TRAILERS = "videos";
    private final static String MOVIE_REVIEWS = "reviews";


    private final static String QUERY_AUTHKEY = "api_key";
    private final static String QUERY_AUTHKEY_VALUE = BuildConfig.THEMOVIEDB_API_KEY;

    /*  */
    private final static String API_BASE_MOVIE_URL = API_BASE_URL + MOVIE;

    private final static String API_THEMOVIEDB_POPULAR_URL = API_BASE_URL + MOVIE_POPULAR;
    private final static String API_THEMOVIEDB_TOP_RATED_URL = API_BASE_URL + MOVIE_TOP_RATED;



    public static String getPosterURL(String posterPath) {
        return getPosterURL(posterPath, POSTER_SIZE_SMALL);
    }
    public static String getPosterURL(String posterPath, int posterSize) {
        String posterSizeStr = BASE_POSTER_SIZE_W185;
        switch(posterSize) {
            case POSTER_SIZE_SMALL:
                break;

            case POSTER_SIZE_BIG:
                posterSizeStr = BASE_POSTER_SIZE_W342;
                break;
            default:
                break;
        }
        return BASE_POSTER_URL + posterSizeStr + "/" + posterPath;
    }

    private static URL simpleURLBuilder(String urlString) {
        Log.d(TAG, "simpleURLBuilder - urlString="+urlString);

        Uri builtUri = Uri.parse(urlString).buildUpon()
                .appendQueryParameter(QUERY_AUTHKEY, QUERY_AUTHKEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }

        Log.v(TAG, "Built URI " + url);

        return url;

    }

    private static String getDataFromUrlString(String urlString) {
        String movieData = null;

        try {
            URL url = simpleURLBuilder(urlString);
            if (url != null)
                movieData = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        return movieData;

    }

    public static String getMoviePopular() {
        return getDataFromUrlString(API_THEMOVIEDB_POPULAR_URL);
    }

    public static String getMovieTopRated() {
        return getDataFromUrlString(API_THEMOVIEDB_TOP_RATED_URL);
    }

    public static void loadImage(Context context, String posterPath, int posterSize, ImageView imageView) {
        String url = TheMovieDbUtils.getPosterURL(posterPath, posterSize);
        Picasso.with(context).load(url).into(imageView);
    }

    public static String getMovieDataById(String id) {
        String urlString = API_BASE_MOVIE_URL + id;
        return getDataFromUrlString(urlString);
    }

    public static String getMovieTrailersById(String id) {
        String urlString = API_BASE_MOVIE_URL + id + SEPARATOR + MOVIE_TRAILERS;
        return getDataFromUrlString(urlString);
    }

    public static String getMovieReviewsById(String id) {
        String urlString = API_BASE_MOVIE_URL + id + SEPARATOR + MOVIE_REVIEWS;
        return getDataFromUrlString(urlString);
    }

}
