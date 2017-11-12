package com.shoji.example.android.popularmoviesstage1.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoji.example.android.popularmoviesstage1.R;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbUtils;


public class MoviesListViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    private final static String TAG = MoviesListViewHolder.class.getSimpleName();
    private Context mContext;
    private MovieData mMovieData;

    private MovieDataViewHolderOnClickListener mMovieDataViewHolderOnClickListener;

    private ImageView mPoster_image;

    public interface MovieDataViewHolderOnClickListener {
        void onClick(int position);
    }

    public MoviesListViewHolder(
            Context context,
            View itemView,
            MovieDataViewHolderOnClickListener movieDataViewHolderOnClickListener) {
        super(itemView);

        this.mContext = context;
        mPoster_image = itemView.findViewById(R.id.movie_poster_image_view);

        itemView.setOnClickListener(this);
        mMovieDataViewHolderOnClickListener = movieDataViewHolderOnClickListener;
    }

    public void bindViewHolder(MovieData movieData) {
        mMovieData = movieData;

        TheMovieDbUtils.loadImage(mContext,
                mMovieData.getPosterPath(),
                TheMovieDbUtils.POSTER_SIZE_BIG,
                mPoster_image);
    }


@Override
    public void onClick(View view) {
        int adapterPosition = getAdapterPosition();
        //Log.d(TAG, "Tapped on position="+adapterPosition);
        mMovieDataViewHolderOnClickListener.onClick(adapterPosition);
    }
}
