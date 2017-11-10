package com.shoji.example.android.popularmoviesstage1.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shoji.example.android.popularmoviesstage1.R;
import com.shoji.example.android.popularmoviesstage1.data.MovieTrailerDataViewHolder.MovieTrailerViewHolderOnClickListener;

import java.util.ArrayList;


public class MovieDetailsAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements MovieTrailerViewHolderOnClickListener {
    private final static String TAG = MovieDetailsAdapter.class.getSimpleName();
    private static boolean DBG = true;
    private static boolean DBG_VIEWTYPE_MSG = false;
    private static boolean DBG_MOVIEDATA_MSG = false;

    private static final int POSITION_MOVIE_DETAILS = 0;


    private static final int ITEMVIEWTYPE_MOVIE_DETAILS = 0;
    private static final int ITEMVIEWTYPE_MOVIE_TRAILERS = 1;
    private static final int ITEMVIEWTYPE_MOVIE_REVIEWS = 2;
    private static final int ITEMVIEWTYPE_UNKNOWN = -1;



    private Context mContext;



    private MovieData mMovieData;
    private ArrayList<YoutubeTrailerData> mTrailerData;
    private ArrayList<MovieReviewData> mReviewList;

    private MovieTrailerAdapterOnClickHandler mMovieTrailerAdapterOnClickHandler;
    private MovieFavoriteAdapterOnClickHandler mMovieFavoriteAdapterOnClickHandler;

    public interface MovieFavoriteAdapterOnClickHandler {
        void onClickFavoriteButton(Button button);
    }

    public interface MovieTrailerAdapterOnClickHandler {
        void onClickMovieTrailer(YoutubeTrailerData youtubeTrailerData);
    }


    public MovieDetailsAdapter(
            Context context,
            MovieFavoriteAdapterOnClickHandler movieFavoriteAdapterOnClickHandler,
            MovieTrailerAdapterOnClickHandler movieTrailerAdapterOnClickHandler) {
        this.mContext = context;
        mMovieFavoriteAdapterOnClickHandler = movieFavoriteAdapterOnClickHandler;
        this.mMovieTrailerAdapterOnClickHandler = movieTrailerAdapterOnClickHandler;
    }

    public MovieData getMovieData() {
        return mMovieData;
    }

    public void setMovieData(MovieData movieData) {
        this.mMovieData = movieData;
    }

    public ArrayList<YoutubeTrailerData> getTrailerData() { return mTrailerData; }

    public void setTrailerData(ArrayList<YoutubeTrailerData> trailerData) {
        this.mTrailerData = trailerData;
    }

    public ArrayList<MovieReviewData> getReviewData() { return mReviewList; }

    public void setReviewData(ArrayList<MovieReviewData> reviewData) {
        this.mReviewList = reviewData;
    }

    /* [START] Methods to setup the RecyclerView.Adapter */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean attachToRoot = false;
        View itemView = null;
        int layoutId = 0;

        switch(viewType) {

            case ITEMVIEWTYPE_MOVIE_DETAILS:
                layoutId = R.layout.movie_data_details_items;

                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(layoutId, parent, attachToRoot);

                return new MovieDetailsViewHolder(mContext,
                        itemView);


            case ITEMVIEWTYPE_MOVIE_TRAILERS:
                layoutId = R.layout.movie_data_trailers_items;
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(layoutId, parent, attachToRoot);
                //Context context = mContext;
                MovieTrailerViewHolderOnClickListener movieTrailerViewHolderOnClickListener = this;
                return new MovieTrailerDataViewHolder(mContext,
                        itemView,
                        movieTrailerViewHolderOnClickListener);

            case ITEMVIEWTYPE_MOVIE_REVIEWS:
                layoutId = R.layout.movie_data_reviews_items;

                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(layoutId, parent, attachToRoot);

                return new MovieReviewDataViewHolder(mContext,
                        itemView);

            default:
                break;
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Log.d(TAG, "Binding at="+position);
        int positionOffset = 0;
        switch (holder.getItemViewType()) {
            case ITEMVIEWTYPE_MOVIE_DETAILS:
                //Log.d(TAG, "Binding the movie details");
                MovieDetailsViewHolder movieDataHolder = (MovieDetailsViewHolder) holder;
                movieDataHolder.bindViewHolder(mContext, mMovieData);
                movieDataHolder.setOnClickFavoriteButtonListener(new MovieDetailsFavoriteButtonHandler()
                );
                break;

            case ITEMVIEWTYPE_MOVIE_TRAILERS:
                //Log.d(TAG, "Binding the movie trailers");
                positionOffset = getItemCountUpTo(ITEMVIEWTYPE_MOVIE_DETAILS);
                MovieTrailerDataViewHolder trailerViewHolder = (MovieTrailerDataViewHolder) holder;
                YoutubeTrailerData trailerItem = mTrailerData.get(position-positionOffset);
                //Log.d(TAG, "got item i="+position+". value= "+trailerItem.toString());
                trailerViewHolder.bindViewHolder(trailerItem);
                break;

            case ITEMVIEWTYPE_MOVIE_REVIEWS:
                //Log.d(TAG, "Binding the movie trailers");
                positionOffset = getItemCountUpTo(ITEMVIEWTYPE_MOVIE_TRAILERS);
                //Log.d(TAG, "Offset for trailers="+positionOffset);
                MovieReviewDataViewHolder moviewView = (MovieReviewDataViewHolder) holder;
                MovieReviewData reviewItem = mReviewList.get(position-positionOffset);
                //Log.d(TAG, "got item i="+position+". value= "+reviewItem.toString());
                moviewView.bindViewHolder(reviewItem);
                break;
            default:
                break;

        }
    }

    //TODO implement FAVORITE
    private class MovieDetailsFavoriteButtonHandler implements
    MovieDetailsViewHolder.OnClickFavoriteButtonListener {
        @Override
        public void OnClick(View view) {
            if(view.getId() == R.id.act_movie_data_favorite_button) {
                Button button = (Button) view;
                mMovieFavoriteAdapterOnClickHandler.onClickFavoriteButton(button);
            }

        }
    }


    @Override
    public int getItemCount() {
        return getItemCountUpTo(ITEMVIEWTYPE_UNKNOWN);
    }
    /* [END] Methods to setup the RecyclerView.Adapter */

    public int getItemCountUpTo(int itemViewId) {
        int totalCount = 0;

        if(mMovieData != null) {
            totalCount = 1;

            if(itemViewId == ITEMVIEWTYPE_MOVIE_DETAILS) {
                if (DBG_VIEWTYPE_MSG) Log.d(TAG, "count up to movie data:" + totalCount);
                return totalCount;
            }
        }

        if (mTrailerData != null) {
            totalCount += mTrailerData.size();

            if(itemViewId == ITEMVIEWTYPE_MOVIE_TRAILERS) {
                if (DBG_VIEWTYPE_MSG) Log.d(TAG, "count up to trailers:" + totalCount);
                return totalCount;
            }
        }

        if(mReviewList != null) {
            totalCount += mReviewList.size();

            if (itemViewId == ITEMVIEWTYPE_MOVIE_REVIEWS) {
                if (DBG_VIEWTYPE_MSG) Log.d(TAG, "count up to reviews:" + totalCount);
                return totalCount;
            }
        }

        //if(DBG_VIEWTYPE_MSG) Log.d(TAG, "count up to ALL:"+totalCount);
        return totalCount;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = ITEMVIEWTYPE_UNKNOWN;
        //if(DBG_VIEWTYPE_MSG) Log.d(TAG, "getItemViewType - position:"+position);

        int startingPosition = 0;
        int numTrailers = 0;
        int numReviews = 0;

        if(mMovieData != null) {
            startingPosition = POSITION_MOVIE_DETAILS;
        }

        if(mTrailerData != null)
            numTrailers = mTrailerData.size();

        if(mReviewList != null)
            numReviews = mReviewList.size();

        if((startingPosition == POSITION_MOVIE_DETAILS) &&
                (position == startingPosition)) {
            //if(DBG_VIEWTYPE_MSG) Log.d(TAG, "Movie details type");
            viewType = ITEMVIEWTYPE_MOVIE_DETAILS;
        }

        else if((numTrailers != 0) && (startingPosition < position) && (position <= numTrailers)) {
            //if(DBG_VIEWTYPE_MSG) Log.d(TAG, "Movie trailer type");
            viewType = ITEMVIEWTYPE_MOVIE_TRAILERS;
        }
        else if((numReviews != 0) && (numTrailers < position) && (position <= numTrailers + numReviews)) {
            //if(DBG_VIEWTYPE_MSG) Log.d(TAG, "Movie reviews type");
            viewType = ITEMVIEWTYPE_MOVIE_REVIEWS;
        }

        return viewType;
    }

    @Override
    public void onClickMovieTrailer(int position) {
        int positionOffset = 0;
        if(DBG) Log.d(TAG, "onClickMovieTrailer position="+position);

        switch(getItemViewType(position)) {
            case ITEMVIEWTYPE_MOVIE_DETAILS:
                //if(DBG_VIEWTYPE_MSG) Log.d(TAG, "clicked on details part, nothing to be done.");
                break;
            case ITEMVIEWTYPE_MOVIE_TRAILERS:
                positionOffset = getItemCountUpTo(ITEMVIEWTYPE_MOVIE_DETAILS);

                //Log.d(TAG, "onClickMovieTrailer position="+position+". offset is:"+positionOffset);
                YoutubeTrailerData trailerData = mTrailerData.get(position-positionOffset);
                //Log.d(TAG, "Tapped on position=" + positionOffset);
                //Log.d(TAG, "INFO: " + trailerData.toString());
                mMovieTrailerAdapterOnClickHandler.onClickMovieTrailer(trailerData);

                break;
            case ITEMVIEWTYPE_MOVIE_REVIEWS:

                break;
            default:
                break;

        }


    }



}
