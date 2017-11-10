package com.shoji.example.android.popularmoviesstage1.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoji.example.android.popularmoviesstage1.R;
import com.shoji.example.android.popularmoviesstage1.utils.TheMovieDbUtils;


public class MovieDetailsViewHolder
        extends RecyclerView.ViewHolder {
    private final static String TAG = MovieDetailsViewHolder.class.getSimpleName();
    private Context mContext;

    private TextView mVoteAverage;
    private TextView mTitle;
    private ImageView mPosterImage;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mDuration;

    public MovieDetailsViewHolder(
            Context context,
            View itemView) {
        super(itemView);

        this.mContext = context;
        mVoteAverage = (TextView) itemView.findViewById(R.id.act_movie_data_average_vote_tv);
        mTitle = (TextView) itemView.findViewById(R.id.act_movie_data_title_tv);
        mPosterImage = (ImageView) itemView.findViewById(R.id.act_movie_data_poster_image_view);
        mOverview = (TextView) itemView.findViewById(R.id.act_movie_data_overview_tv);
        mReleaseDate = (TextView) itemView.findViewById(R.id.act_movie_data_release_date_tv);
        mDuration = (TextView) itemView.findViewById(R.id.act_movie_data_duration_tv);



    }

    public void bindViewHolder(Context context, MovieData movieData) {
        mVoteAverage.setText(
                context.getString(R.string.average_vote_formatted_value,
                movieData.getVoteAverage(),
                context.getString(R.string.average_vote_maximum_value)));
        mTitle.setText(movieData.getTitle());
        mOverview.setText(movieData.getOverview());
        mReleaseDate.setText(movieData.getReleaseDate());
        mDuration.setText(context.getString(R.string.duration_formatted_value, movieData.getDuration()));

        String posterPath = movieData.getPosterPath();
        int posterSize = TheMovieDbUtils.POSTER_SIZE_BIG;
        TheMovieDbUtils.loadImage(context, posterPath, posterSize, mPosterImage);

    }

}
