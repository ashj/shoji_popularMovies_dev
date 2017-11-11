package com.shoji.example.android.popularmoviesstage1.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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

    private Button mFavoriteButton;

    private OnClickFavoriteButtonListener mOnClickFavoriteButtonHandler;

    public interface OnClickFavoriteButtonListener {
        void OnClick(View view);
        void updateUi(Button button);
    }


    public MovieDetailsViewHolder(
            Context context,
            View itemView) {
        super(itemView);

        this.mContext = context;
        mVoteAverage = itemView.findViewById(R.id.act_movie_data_average_vote_tv);
        mTitle = itemView.findViewById(R.id.act_movie_data_title_tv);
        mPosterImage = itemView.findViewById(R.id.act_movie_data_poster_image_view);
        mOverview = itemView.findViewById(R.id.act_movie_data_overview_tv);
        mReleaseDate = itemView.findViewById(R.id.act_movie_data_release_date_tv);
        mDuration = itemView.findViewById(R.id.act_movie_data_duration_tv);
        mFavoriteButton = itemView.findViewById(R.id.act_movie_data_favorite_button);
    }


    public void bindViewHolder(Context context,
                               MovieData movieData,
                               OnClickFavoriteButtonListener onClickFavoriteButtonListener) {
        mVoteAverage.setText(
                context.getString(R.string.average_vote_formatted_value,
                movieData.getVoteAverage(),
                context.getString(R.string.average_vote_maximum_value)));
        mTitle.setText(movieData.getTitle());
        mOverview.setText(movieData.getOverview());
        mReleaseDate.setText(movieData.getReleaseDate());
        mDuration.setText(context.getString(R.string.duration_formatted_value, movieData.getDuration()));


        mFavoriteButton.setOnClickListener(onClickFavorite(context));
        mOnClickFavoriteButtonHandler = onClickFavoriteButtonListener;
        mOnClickFavoriteButtonHandler.updateUi(mFavoriteButton);


        String posterPath = movieData.getPosterPath();
        int posterSize = TheMovieDbUtils.POSTER_SIZE_BIG;
        TheMovieDbUtils.loadImage(context, posterPath, posterSize, mPosterImage);
    }


    public View.OnClickListener onClickFavorite(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickFavoriteButtonHandler.OnClick(view);
            }
        };
    }

}
