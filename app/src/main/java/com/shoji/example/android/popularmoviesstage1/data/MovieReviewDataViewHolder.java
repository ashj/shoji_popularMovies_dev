package com.shoji.example.android.popularmoviesstage1.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shoji.example.android.popularmoviesstage1.R;


public class MovieReviewDataViewHolder
        extends RecyclerView.ViewHolder {
    private final static String TAG = MovieReviewDataViewHolder.class.getSimpleName();
    private Context mContext;
    private MovieReviewData mMovieReviewData;


    private TextView mLabel_tv;
    private TextView mAuthor_tv;
    private TextView mContent_tv;


    public MovieReviewDataViewHolder(
            Context context,
            View itemView) {
        super(itemView);

        this.mContext = context;
        mLabel_tv = itemView.findViewById(R.id.movie_review_title_tv);
        mAuthor_tv = itemView.findViewById(R.id.movie_review_author_tv);
        mContent_tv = itemView.findViewById(R.id.movie_review_content_tv);

    }

    public void bindViewHolder(MovieReviewData reviewData) {
        mMovieReviewData = reviewData;

        mAuthor_tv.setText(reviewData.getAuthor());
        mContent_tv.setText(reviewData.getContent());
    }

    public void showTitleLabel(int numItems) {
        if(numItems > 0) {
            mLabel_tv.setText(mContext.getResources().getQuantityString(R.plurals.review_plurals_label, numItems));
            mLabel_tv.setVisibility(View.VISIBLE);
        }
    }
}
