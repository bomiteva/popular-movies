package com.android.popularmovies.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.android.popularmovies.R;
import com.android.popularmovies.adapter.MovieAdapter;
import com.android.popularmovies.model.Movie;
import com.android.popularmovies.model.MovieDataParser;
import com.android.popularmovies.service.ApiService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    private final String POPULAR_ACTION = "popular";
    private final String TOP_RATED_ACTION = "top_rated";
    private final String PARAM_ACTION = String.format("PARAM_ACTION:%s", this.getClass().getName());

    private ArrayAdapter<Movie> moviesAdapter;
    private String actionType;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * MainFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        GridView rootView = (GridView) inflater.inflate(R.layout.fragment_main, container, false);

        moviesAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        rootView.setAdapter(moviesAdapter);
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = moviesAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });

        if (savedInstanceState != null) {
            actionType = savedInstanceState.getString(PARAM_ACTION);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            actionType = savedInstanceState.getString(PARAM_ACTION);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (actionType == null || actionType.equals(POPULAR_ACTION)) {
            executeMoviesTask(POPULAR_ACTION);
        } else {
            executeMoviesTask(TOP_RATED_ACTION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAM_ACTION, actionType);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            actionType = savedInstanceState.getString(PARAM_ACTION);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            executeMoviesTask(POPULAR_ACTION);
            return true;
        }

        if (id == R.id.action_top_rated) {
            executeMoviesTask(TOP_RATED_ACTION);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void executeMoviesTask(String action) {
        ApiService apiService = ApiService.getInstance();

        if (action.equals(POPULAR_ACTION)) {
            apiService.getPopularMovies().subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onCompleted);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.action_popular);

        } else {
            apiService.getTopRatedMovies().subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onCompleted);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.action_top_rated);
        }

        setActionType(action);

    }

    Observer<Response<JsonElement>> onCompleted = new Observer<Response<JsonElement>>() {
        @Override
        public void onCompleted() {
            Log.w(LOG_TAG, "onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, e.toString());
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
                        moviesAdapter.clear();
                        moviesAdapter.addAll(MovieDataParser.getMovies(json));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Error ", e);
                    }
                }
            }
        }
    };

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
