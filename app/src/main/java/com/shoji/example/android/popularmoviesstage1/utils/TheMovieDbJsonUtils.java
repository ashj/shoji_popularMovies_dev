package com.shoji.example.android.popularmoviesstage1.utils;

import android.util.Log;

import com.shoji.example.android.popularmoviesstage1.data.MovieData;
import com.shoji.example.android.popularmoviesstage1.data.MovieReviewData;
import com.shoji.example.android.popularmoviesstage1.data.YoutubeTrailerData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TheMovieDbJsonUtils {
    private static final String TAG = TheMovieDbJsonUtils.class.getSimpleName();

    public final static int FLAGS_NO_FLAGS = 0x0000;
    public final static int FLAGS_FILTER_FAVORITES_ONLY = 0x0001;


    private static final String JSON_PAGE = "page";

    private static final String JSON_RESULTS = "results";

    private static final String JSON_ID = "id";
    private static final String JSON_VOTE_AVERAGE = "vote_average";
    private static final String JSON_TITLE = "title";
    private static final String JSON_POSTER_PATH = "poster_path";

    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_RELEASE_DATE = "release_date";

    private static final String JSON_DURATION = "runtime";

    private static final String JSON_NAME = "name";
    private static final String JSON_KEY = "key";
    private static final String JSON_SITE = "site";
    private static final String JSON_YOUTUBE = "YouTube";

    private static final String JSON_AUTHOR = "author";
    private static final String JSON_CONTENT = "content";


    public static ArrayList<MovieData> parseMovieListJson(String jsonStr, int flag) {

        ArrayList<MovieData> movieDataList = null;
        Log.d(TAG, "Starting parseMovieListJson");
        try {
            JSONObject movieJson = new JSONObject(jsonStr);

            if(movieJson.has(JSON_RESULTS)) {

                JSONArray resultsArray = movieJson.getJSONArray(JSON_RESULTS);

                Log.d(TAG, "Total result in this page=" + resultsArray.length());
                movieDataList = new ArrayList<>();

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject movieJsonObject = resultsArray.getJSONObject(i);

                    MovieData movieData = getSingleMovieData(movieJsonObject);
                    if(movieJsonObject.has(JSON_ID) &&
                            shouldAddMovie(movieData, flag)) {


                        movieDataList.add(movieData);

                        //Log.d(TAG, "Parsed: " + movieData);
                    }
                }
            }
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse the json");
            Log.e(TAG, jsone.getMessage());
        }

        if(movieDataList == null)
            Log.d(TAG, "movie data is null");
        else
            Log.d(TAG, "size="+movieDataList.size());
        return movieDataList;

    }

    public static MovieData parseSingleMovieData(String jsonStr) {
        MovieData movieData = null;
        try {
            JSONObject movieJsonObject = new JSONObject(jsonStr);

            if(movieJsonObject.has(JSON_ID)) {
                movieData = getSingleMovieData(movieJsonObject);
                movieData.setDuration(getString(movieJsonObject, JSON_DURATION));
            }
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse the json");
            Log.e(TAG, jsone.getMessage());
        }
        //Log.d(TAG, "Parsed: single " + movieData);
        return movieData;
    }

    private static MovieData getSingleMovieData(JSONObject movieJsonObject)
                                                                throws JSONException {
        MovieData movieData = new MovieData();

        movieData.setId(getString(movieJsonObject, JSON_ID));
        movieData.setVoteAverage(getString(movieJsonObject, JSON_VOTE_AVERAGE));
        movieData.setTitle(getString(movieJsonObject, JSON_TITLE));
        movieData.setPosterPath(getString(movieJsonObject, JSON_POSTER_PATH));
        movieData.setOverview(getString(movieJsonObject, JSON_OVERVIEW));
        movieData.setReleaseDate(getString(movieJsonObject, JSON_RELEASE_DATE));

        return movieData;
    }

    public static ArrayList<YoutubeTrailerData> parseTrailerListJson(String jsonStr) {

        ArrayList<YoutubeTrailerData> youtubeTrailers = null;
        Log.d(TAG, "Starting parseMovieListJson");
        try {
            JSONObject movieJson = new JSONObject(jsonStr);

            if(movieJson.has(JSON_RESULTS)) {

                JSONArray resultsArray = movieJson.getJSONArray(JSON_RESULTS);

                Log.d(TAG, "Total result in this page=" + resultsArray.length());
                youtubeTrailers = new ArrayList<YoutubeTrailerData>();

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject trailerJsonObject = resultsArray.getJSONObject(i);

                    String siteValue = getString(trailerJsonObject, JSON_SITE);
                    if(siteValue.length() == 0 || !siteValue.equals(JSON_YOUTUBE))
                        continue;

                    YoutubeTrailerData trailerData = new YoutubeTrailerData();

                    trailerData.setTitle(getString(trailerJsonObject, JSON_NAME));
                    trailerData.setKey(getString(trailerJsonObject, JSON_KEY));

                    youtubeTrailers.add(trailerData);
                    //Log.d(TAG, "Parsed trailer list: " + trailerData);
                }
            }
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse the json");
            Log.e(TAG, jsone.getMessage());
        }

        return youtubeTrailers;
    }

    public static ArrayList<MovieReviewData> parseMovieReviewJson(String jsonStr) {

        ArrayList<MovieReviewData> reviewList = null;
        Log.d(TAG, "Starting parseMovieListJson");
        try {
            JSONObject movieJson = new JSONObject(jsonStr);

            if(movieJson.has(JSON_RESULTS)) {

                JSONArray resultsArray = movieJson.getJSONArray(JSON_RESULTS);

                //Log.d(TAG, "Total result in this page=" + resultsArray.length());
                reviewList = new ArrayList<MovieReviewData>();

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject trailerJsonObject = resultsArray.getJSONObject(i);

                    MovieReviewData reviewData = new MovieReviewData();

                    reviewData.setAuthor(getString(trailerJsonObject, JSON_AUTHOR));
                    reviewData.setContent(getString(trailerJsonObject, JSON_CONTENT));

                    reviewList.add(reviewData);
                    //Log.d(TAG, "Parsed review: " + reviewData);
                }
            }
        } catch (JSONException jsone) {
            Log.e(TAG, "Failed to parse the json");
            Log.e(TAG, jsone.getMessage());
        }

        return reviewList;
    }

    private static String getString(JSONObject jsonObject, String name) throws JSONException {
        if(jsonObject.has(name))
            return jsonObject.getString(name);
        //Log.w(TAG, "jsonObject does not contain name="+name);
        return "";
    }

    // [START] filter movie list according to flag
    private static boolean shouldAddMovie(MovieData movieData, int flag) {
        Log.d(TAG, "Movie list flag="+flag);
        boolean result = true;

        if( (flag & FLAGS_FILTER_FAVORITES_ONLY) == FLAGS_FILTER_FAVORITES_ONLY ) {
            Log.d(TAG, "FAVORITE FILTER");

            String movieId = movieData.getId();
            if(movieId.length() == 0)
                result = false;
            else {
                result = isMovieFavorite(movieId);
                movieData.setIsFavorite( (result==true) ? 1 : 0 );
                Log.d(TAG, "Movie isFavorite set to:"+movieData.getIsFavorite());
            }
        }
        else {
            Log.d(TAG, "NO FILTER");
        }

        return result;
    }

    // TODO implement FAVORITE
    private static boolean isMovieFavorite(String movieId) {
        //return (mMovieData!= null && mMovieData.getIsFavorite() == 1);
        boolean isFavorite = ((int) (Math.random() * 2)) == 1;
        Log.d(TAG, "MovieId:"+movieId+" favorite:"+isFavorite);
        return isFavorite; // testing
    }
    // [END] filter movie list according to flag
}
