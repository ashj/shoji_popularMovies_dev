package com.shoji.example.android.popularmoviesstage1.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shoji.example.android.popularmoviesstage1.data.MoviesListViewHolder.MovieDataViewHolderOnClickListener;
import com.shoji.example.android.popularmoviesstage1.R;


public class MoviesListAdapter
        extends RecyclerView.Adapter<MoviesListViewHolder>
        implements MoviesListViewHolder.MovieDataViewHolderOnClickListener {
    private final static String TAG = MoviesListAdapter.class.getSimpleName();

    private Context mContext;
    private MovieData[] movieData;

    private MovieDataAdapterOnClickHandler mMovieDataAdapterOnClickHandler;


    public interface MovieDataAdapterOnClickHandler {
        void onClick(MovieData movieData);
    }


    public MoviesListAdapter(
            Context context,
            MovieDataAdapterOnClickHandler movieDataAdapterOnClickHandler) {
        this.mContext = context;
        this.mMovieDataAdapterOnClickHandler = movieDataAdapterOnClickHandler;
    }

    public MovieData[] getMovieData() { return movieData; }

    public void setMovieData(MovieData[] movieData) {
        this.movieData = movieData;
    }

    /* [START] Methods to setup the RecyclerView.Adapter */
    @Override
    public MoviesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean attachToRoot = false;
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movie_data_items, parent, attachToRoot);
        Context context = mContext;
        MovieDataViewHolderOnClickListener movieDataViewHolderOnClickListener = this;
        return new MoviesListViewHolder(context,
                itemView,
                movieDataViewHolderOnClickListener);
    }

    @Override
    public void onBindViewHolder(MoviesListViewHolder holder, int position) {
        MovieData item = movieData[position];

        holder.bindViewHolder(item);
    }

    @Override
    public int getItemCount() {
        if (movieData == null) {
            return 0;
        }

        return movieData.length;
    }
    /* [END] Methods to setup the RecyclerView.Adapter */


    @Override
    public void onClick(int position) {
        MovieData movie = movieData[position];
        //Log.d(TAG, "Tapped on position="+position);
        //Log.d(TAG, "INFO: "+movie.toString());
        mMovieDataAdapterOnClickHandler.onClick(movie);
    }



}
