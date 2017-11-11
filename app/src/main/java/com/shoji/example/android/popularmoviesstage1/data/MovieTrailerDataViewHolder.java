package com.shoji.example.android.popularmoviesstage1.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shoji.example.android.popularmoviesstage1.R;


public class MovieTrailerDataViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    private final static String TAG = MovieTrailerDataViewHolder.class.getSimpleName();
    private Context mContext;
    private YoutubeTrailerData mTrailerData;

    private MovieTrailerViewHolderOnClickListener mMovieTrailerViewHolderOnClickListener;

    private TextView mTitle_tv;

    public interface MovieTrailerViewHolderOnClickListener {
        void onClickMovieTrailer(int position);
    }

    public MovieTrailerDataViewHolder(
            Context context,
            View itemView,
            MovieTrailerViewHolderOnClickListener movieTrailerViewHolderOnClickListener) {
        super(itemView);

        this.mContext = context;

        mTitle_tv = itemView.findViewById(R.id.movie_trailer_title_tv);

        itemView.setOnClickListener(this);
        mMovieTrailerViewHolderOnClickListener = movieTrailerViewHolderOnClickListener;
    }

    public void bindViewHolder(YoutubeTrailerData trailerData) {
        mTrailerData = trailerData;
        mTitle_tv.setText(trailerData.getTitle());

    }


    @Override
    public void onClick(View view) {
        int adapterPosition = getAdapterPosition();
        Log.d(TAG, "Tapped on position="+adapterPosition);

        mMovieTrailerViewHolderOnClickListener.onClickMovieTrailer(adapterPosition);
    }
}
