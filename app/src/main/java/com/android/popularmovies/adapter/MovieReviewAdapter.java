package com.android.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.popularmovies.R;
import com.android.popularmovies.model.Review;

import java.util.ArrayList;

/**
 * Created by bobi on 6/1/2016.
 */
public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieReviewAdapter.class.getSimpleName();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final TextView mReviewAuthorView;
        public final TextView mReviewContentView;

        public ViewHolder(View view) {
            super(view);
            mReviewAuthorView = (TextView) view.findViewById(R.id.review_author);
            mReviewContentView = (TextView) view.findViewById(R.id.review_content);
        }
    }

    private ArrayList<Review> mReviews;

    public MovieReviewAdapter(ArrayList<Review> reviews) {
        mReviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Review review = mReviews.get(position);

        holder.mReviewAuthorView.setText(review.getAuthor());
        holder.mReviewContentView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public ArrayList<Review> getReviews() {
        return mReviews;
    }

    public void addAll(ArrayList<Review> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }
}
