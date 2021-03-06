package com.android.popularmovies.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.popularmovies.R;
import com.android.popularmovies.adapter.MovieAdapter;
import com.android.popularmovies.data.MovieContract;
import com.android.popularmovies.model.JSONDataParser;
import com.android.popularmovies.model.Movie;
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
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final int MOVIES_LOADER = 0;
    private final String POPULAR_ACTION = "popular";
    private final String TOP_RATED_ACTION = "top_rated";
    private final String FAVOURITE_ACTION = "favourite";
    private final String PARAM_ACTION = String.format("PARAM_ACTION:%s", this.getClass().getName());

    private MovieAdapter moviesAdapter;
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

        /**
         * MainFragmentCallback for when an menu item has been selected.
         */
        public void displayEmptyState(boolean isEmptyView);

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

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView grid = (GridView) rootView.findViewById(R.id.gridview_movies);

        moviesAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        grid.setAdapter(moviesAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = moviesAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });
        grid.setEmptyView(rootView.findViewById(R.id.empty_list_view));

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

        if (null == actionType) {
            executeMoviesTask(POPULAR_ACTION);
        } else {
            switch (actionType) {
                case POPULAR_ACTION:
                    executeMoviesTask(POPULAR_ACTION);
                    break;
                case TOP_RATED_ACTION:
                    executeMoviesTask(TOP_RATED_ACTION);
                    break;
                case FAVOURITE_ACTION:
                    getLoaderManager().initLoader(MOVIES_LOADER, null, this);
                    break;
            }
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
        // display empty view
        ((Callback) getActivity()).displayEmptyState(true);

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            getLoaderManager().destroyLoader(MOVIES_LOADER);
            executeMoviesTask(POPULAR_ACTION);
            return true;
        }

        if (id == R.id.action_top_rated) {
            getLoaderManager().destroyLoader(MOVIES_LOADER);
            executeMoviesTask(TOP_RATED_ACTION);
            return true;
        }

        if (id == R.id.action_favorites) {
            getLoaderManager().initLoader(MOVIES_LOADER, null, this);
            setActionType(FAVOURITE_ACTION);
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
            Toast.makeText(getContext(), getActivity().getString(R.string.message_no_movie_data_error), Toast.LENGTH_SHORT).show();
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
                        moviesAdapter.addAll(JSONDataParser.getMovies(json));
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesAdapter.clear();
        moviesAdapter.addData(data);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.action_favorites);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
