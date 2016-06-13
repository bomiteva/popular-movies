package com.android.popularmovies.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.popularmovies.R;
import com.android.popularmovies.data.MovieContract;
import com.android.popularmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobi on 3/31/2016.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    // Custom constructor
    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        // Gets the Movie object from the ArrayAdapter at the appropriate position
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_movie, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.movieIcon = (ImageView) convertView.findViewById(R.id.list_item_movie_imageview);
            viewHolder.movieTitle = (TextView)  convertView.findViewById(R.id.list_item_movie_textview);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        Picasso
                .with(getContext())
                .load(movie.getPosterPath())
                .into(viewHolder.movieIcon,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                viewHolder.movieIcon.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                viewHolder.movieTitle.setVisibility(View.VISIBLE);
                            }
                        });

        viewHolder.movieTitle.setText(movie.getOriginalTitle());

        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     */
    private static class ViewHolder {
        TextView movieTitle;
        ImageView movieIcon;
    }

    public void addData(Cursor cursor) {
        List<Movie> movies = new ArrayList<Movie>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                String originalTitle = cursor.getString(MovieContract.MovieEntry.COL_ORIGINAL_TITLE);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COL_POSTER_PATH);
                String overview = cursor.getString(MovieContract.MovieEntry.COL_OVERVIEW);
                String rating = cursor.getString(MovieContract.MovieEntry.COL_RATING);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_RELEASE_DATE);
                String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_BACKDROP_PATH);
                String voteCount = cursor.getString(MovieContract.MovieEntry.COL_VOTE_COUNT);
                Movie movie = new Movie(id, posterPath, backdropPath, originalTitle, overview, rating, releaseDate, voteCount);
                movies.add(movie);
            } while (cursor.moveToNext());
        }
        addAll(movies);
    }
}
