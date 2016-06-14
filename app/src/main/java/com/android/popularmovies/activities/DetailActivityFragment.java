package com.android.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.popularmovies.R;
import com.android.popularmovies.adapter.MovieReviewAdapter;
import com.android.popularmovies.adapter.MovieVideoAdapter;
import com.android.popularmovies.model.JSONDataParser;
import com.android.popularmovies.model.Movie;
import com.android.popularmovies.model.Review;
import com.android.popularmovies.model.Video;
import com.android.popularmovies.service.ApiService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements MovieVideoAdapter.PlayVideoCallback {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public static final String PARAM_MOVIE = String.format("PARAM:MOVIE:%s", DetailActivityFragment.class.getName());
    public static final String PARAM_MOVIE_VIDEOS = "MOVIE_VIDEOS";
    public static final String PARAM_MOVIE_REVIEWS = "MOVIE_REVIEWS";

    public static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";

    private Movie mMovie;
    private Video mFirstVideo;

    private ImageView mMoviePoster;
    private TextView mMovieTitle;
    private TextView mMovieYear;
    private TextView mMovieRating;
    private TextView mMovieOverview;
    private RecyclerView mMovieVideos;
    private RecyclerView mMovieReviews;

    private MovieVideoAdapter mMovieVideosAdapter;
    private MovieReviewAdapter mMovieReviewsAdapter;

    private ShareActionProvider mShareActionProvider;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        mMovieYear = (TextView) rootView.findViewById(R.id.movie_year);
        mMovieRating = (TextView) rootView.findViewById(R.id.movie_rating);
        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_overview);

        mMovieVideos = (RecyclerView) rootView.findViewById(R.id.movie_videos);
        mMovieVideosAdapter = new MovieVideoAdapter(new ArrayList<Video>(), this);
        mMovieVideos.setAdapter(mMovieVideosAdapter);

        mMovieVideos.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mMovieVideos.setNestedScrollingEnabled(false);

        mMovieReviews = (RecyclerView) rootView.findViewById(R.id.movie_reviews);
        mMovieReviewsAdapter = new MovieReviewAdapter(new ArrayList<Review>());
        mMovieReviews.setAdapter(mMovieReviewsAdapter);

        Bundle bundle;
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.getExtras() != null) {
            bundle = intent.getExtras();
        } else {
            bundle = getArguments();
        }

        if (bundle != null) {
            mMovie = bundle.getParcelable(PARAM_MOVIE);
            displayData(mMovie);

            if (mMovie != null) {
                // Fetch videos only if savedInstanceState == null
                if (savedInstanceState != null && savedInstanceState.containsKey(PARAM_MOVIE_VIDEOS)) {
                    ArrayList<Video> videos = savedInstanceState.getParcelableArrayList(PARAM_MOVIE_VIDEOS);
                    mMovieVideosAdapter.addAll(videos);
                } else {
                    executeMovieVideoTask(mMovie.getId());
                }

                if (savedInstanceState != null && savedInstanceState.containsKey(PARAM_MOVIE_REVIEWS)) {
                    ArrayList<Review> reviews = savedInstanceState.getParcelableArrayList(PARAM_MOVIE_REVIEWS);
                    mMovieReviewsAdapter.addAll(reviews);
                } else {
                    executeMovieReviewTask(mMovie.getId());
                }
            }
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    private Intent createShareVideoIntent(Video video) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovie.getOriginalTitle());
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, video.getName() + ": "
                + Uri.parse(YOUTUBE_URL + video.getKey()));
        return shareIntent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Video> videos = mMovieVideosAdapter.getVideos();
        if (videos != null && !videos.isEmpty()) {
            outState.putParcelableArrayList(PARAM_MOVIE_VIDEOS, videos);
        }

        ArrayList<Review> reviews = mMovieReviewsAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(PARAM_MOVIE_REVIEWS, reviews);
        }
    }

    private void displayData(Movie movie) {
        Picasso
                .with(getActivity())
                .load(movie.getPosterPath())
                .into(mMoviePoster);

        mMovieTitle.setText(movie.getOriginalTitle());
        mMovieYear.setText(movie.getReleaseDate());
        mMovieRating.setText(Html.fromHtml(getString(R.string.rating_text, movie.getRating(), movie.getVoteCount())));
        mMovieOverview.setText(movie.getOverview());
    }

    private void executeMovieVideoTask(Long movieId) {
        ApiService apiService = ApiService.getInstance();

        apiService.getVideosByMovieId(movieId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onCompleted);
    }

    Observer<Response<JsonElement>> onCompleted = new Observer<Response<JsonElement>>() {
        @Override
        public void onCompleted() {
            Log.w(LOG_TAG, "onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(getContext(), getActivity().getString(R.string.message_no_videos_data_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(Response<JsonElement> response) {
            if (response.isSuccessful()) {
                JsonObject json = null;
                JsonElement body = response.body();
                if (body != null) {
                    json = body.getAsJsonObject();
                }

                if (json != null) {
                    try {
                        mMovieVideosAdapter.addAll(JSONDataParser.getMovieVideos(json, mMovie.getId()));
                        if (mMovieVideosAdapter.getVideos() != null &&  mMovieVideosAdapter.getVideos().size() > 0) {
                            mFirstVideo = mMovieVideosAdapter.getVideos().get(0);
                            mShareActionProvider.setShareIntent(createShareVideoIntent(mFirstVideo));
                        }
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Error ", e);
                    }
                }
            }
        }
    };


    private void executeMovieReviewTask(Long movieId) {
        ApiService apiService = ApiService.getInstance();

        apiService.getReviewsByMovieId(movieId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onReviewsCompleted);
    }

    Observer<Response<JsonElement>> onReviewsCompleted = new Observer<Response<JsonElement>>() {
        @Override
        public void onCompleted() {
            Log.w(LOG_TAG, "onReviewsCompleted");
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(getContext(), getActivity().getString(R.string.message_no_reviews_data_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(Response<JsonElement> response) {
            if (response.isSuccessful()) {
                JsonObject json = null;
                JsonElement body = response.body();
                if (body != null) {
                    json = body.getAsJsonObject();
                }

                if (json != null) {
                    try {
                        mMovieReviewsAdapter.addAll(JSONDataParser.getMovieReviews(json, mMovie.getId()));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Error ", e);
                    }
                }
            }
        }
    };

    @Override
    public void play(Video video) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + video.getKey())));
    }
}
