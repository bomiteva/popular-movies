package com.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.popularmovies.R;
import com.android.popularmovies.model.Movie;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailActivityFragment.PARAM_MOVIE, movie);

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
            displayEmptyState(false);
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void displayEmptyState(boolean isEmptyView) {
        TextView emptyDetailsView = (TextView) findViewById(R.id.empty_details_view);
        FrameLayout detailsContainer = (FrameLayout) findViewById(R.id.movie_detail_container);
        if (mTwoPane && isEmptyView) {
            emptyDetailsView.setVisibility(View.VISIBLE);
            detailsContainer.setVisibility(View.GONE);
        } else if (mTwoPane && !isEmptyView) {
            emptyDetailsView.setVisibility(View.GONE);
            detailsContainer.setVisibility(View.VISIBLE);
        }
    }
}
