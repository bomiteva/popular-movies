package com.android.popularmovies.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by bobi on 3/31/2016.
 */
public class MovieDataParser {

    private static final String POSTER_PATH = "http://image.tmdb.org/t/p/w185";
    private static final String BACKDROP_PATH = "http://image.tmdb.org/t/p/w780";

    /**
     * Given a string of the form returned by the api call:
     * http://api.themoviedb.org/3/movie/popular or http://api.themoviedb.org/3/movie/top_rated
     * return movies
     */
    public static ArrayList<Movie> getMovies(JsonObject jsonObject)
            throws JSONException {

        ArrayList<Movie> movies = new ArrayList<Movie>();

        if (jsonObject != null) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("results");
            if (jsonArray != null && jsonArray.size() > 0) {

                for (JsonElement movieJson : jsonArray) {
                    JsonObject jsonMovie = movieJson.getAsJsonObject();
                    Movie movie = new Movie(jsonMovie.get("id").getAsLong(),
                            POSTER_PATH + jsonMovie.get("poster_path").getAsString(),
                            BACKDROP_PATH + jsonMovie.get("backdrop_path").getAsString(),
                            jsonMovie.get("original_title").getAsString(),
                            jsonMovie.get("overview").getAsString(),
                            jsonMovie.get("vote_average").getAsString(),
                            jsonMovie.get("release_date").getAsString(),
                            jsonMovie.get("vote_count").getAsString());
                    movies.add(movie);
                }
            }
        }

        return movies;
    }
}
