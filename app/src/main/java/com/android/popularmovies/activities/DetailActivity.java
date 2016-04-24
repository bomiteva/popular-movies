package com.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.android.popularmovies.R;
import com.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

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
                    .addToBackStack(null)
                    .commit();
        }

        Intent intent = getIntent();

        if (intent != null ) {
            Bundle bundle = intent.getExtras();
            Movie movie = bundle.getParcelable(DetailActivityFragment.PARAM_MOVIE);
            displayData(movie);
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

}
