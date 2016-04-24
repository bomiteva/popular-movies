package com.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.popularmovies.R;
import com.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public static final String PARAM_MOVIE = String.format("PARAM:MOVIE:%s", DetailActivityFragment.class.getName());

    private Movie movie;

    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView movieYear;
    private TextView movieRating;
    private TextView movieOverview;


    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        movieYear = (TextView) rootView.findViewById(R.id.movie_year);
        movieRating = (TextView) rootView.findViewById(R.id.movie_rating);
        movieOverview = (TextView) rootView.findViewById(R.id.movie_overview);

        Bundle bundle;
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.getExtras() != null) {
            bundle = intent.getExtras();
        } else {
            bundle = getArguments();
        }

        if (bundle != null) {
            movie = bundle.getParcelable(PARAM_MOVIE);
            displayData(movie);
        }

        return rootView;
    }

    private void displayData(Movie movie) {
        Picasso
                .with(getActivity())
                .load(movie.getPosterPath())
                .into(moviePoster);

        movieTitle.setText(movie.getOriginalTitle());
        movieYear.setText(movie.getReleaseDate());
        movieRating.setText(Html.fromHtml(getString(R.string.rating_text, movie.getRating(), movie.getVoteCount())));
        movieOverview.setText(movie.getOverview());
    }
}
