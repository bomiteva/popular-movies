package com.android.popularmovies.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by bobi on 3/31/2016.
 */
public class JSONDataParser {

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

    /**
     * Given a string of the form returned by the api call:
     * http://api.themoviedb.org/3/movie/id/videos
     * return videos
     */
    public static ArrayList<Video> getMovieVideos(JsonObject jsonObject, Long movieId)
            throws JSONException {

        ArrayList<Video> videos = new ArrayList<Video>();

        if (jsonObject != null) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("results");
            if (jsonArray != null && jsonArray.size() > 0) {

                for (JsonElement videoJson : jsonArray) {
                    JsonObject jsonVideo= videoJson.getAsJsonObject();
                    Video video = new Video(jsonVideo.get("id").getAsString(),
                            movieId,
                            jsonVideo.get("key").getAsString(),
                            jsonVideo.get("name").getAsString(),
                            jsonVideo.get("size").getAsInt());
                    videos.add(video);
                }
            }
        }

        return videos;
    }

    /**
     * Given a string of the form returned by the api call:
     * http://api.themoviedb.org/3/movie/id/reviews
     * return videos
     */
    public static ArrayList<Review> getMovieReviews(JsonObject jsonObject, Long movieId)
            throws JSONException {

        ArrayList<Review> reviews = new ArrayList<Review>();

        if (jsonObject != null) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("results");
            if (jsonArray != null && jsonArray.size() > 0) {

                for (JsonElement reviewJson : jsonArray) {
                    JsonObject jsonReview = reviewJson.getAsJsonObject();
                    Review review = new Review(jsonReview.get("id").getAsString(),
                            movieId,
                            jsonReview.get("author").getAsString(),
                            jsonReview.get("content").getAsString(),
                            jsonReview.get("url").getAsString());
                    reviews.add(review);
                }
            }
        }

        return reviews;
    }
}
