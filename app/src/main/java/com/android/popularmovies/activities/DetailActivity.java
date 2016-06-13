package com.android.popularmovies.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.popularmovies.R;
import com.android.popularmovies.data.MovieContract;
import com.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private boolean mIsFavourite;
    private FloatingActionButton mFabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, new DetailActivityFragment())
                    .commit();
        }

        Intent intent = getIntent();

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            Movie movie = bundle.getParcelable(DetailActivityFragment.PARAM_MOVIE);
            displayData(movie);
            displayFavouriteButton(movie);
        }
    }

    private void displayData(Movie movie) {
        ImageView backdropImage = (ImageView) findViewById(R.id.backdrop_image);
        Picasso
                .with(getApplicationContext())
                .load(movie.getBackdropPath())
                .fit()
                .into(backdropImage);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(movie.getOriginalTitle());

    }

    private void displayFavouriteButton(final Movie movie) {
        mFabButton = (FloatingActionButton) findViewById(R.id.fab);
        Cursor movieCursor = this.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movie.getId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            mIsFavourite = true;
        } else {
            mIsFavourite = false;
        }

        updateFabButton();

        mFabButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        markAsFavorite(movie);
                    }
                });
    }

    private void markAsFavorite(final Movie movie) {
        if (mIsFavourite) {
            this.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movie.getId(), null);
            Toast.makeText(this, R.string.message_movie_delete, Toast.LENGTH_SHORT).show();
        } else {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                    movie.getId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
                    movie.getOriginalTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                    movie.getPosterPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,
                    movie.getOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING,
                    movie.getRating());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
                    movie.getVoteCount());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                    movie.getReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
                    movie.getBackdropPath());
            this.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );
            Toast.makeText(this, R.string.message_movie_save, Toast.LENGTH_SHORT).show();
        }
        mIsFavourite = !mIsFavourite;
        updateFabButton();
    }

    private void updateFabButton() {
        if (mIsFavourite) {
            mFabButton.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
        } else {
            mFabButton.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
