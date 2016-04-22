package com.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bobi on 3/31/2016.
 */
public class Movie implements Parcelable {

    private Long id;
    private String posterPath;
    private String backdropPath;
    private String originalTitle;
    private String overview;
    private String rating;
    private String releaseDate;
    private String voteCount;

    public Movie(Long id, String posterPath, String backdropPath, String originalTitle,
                 String overview, String rating, String releaseDate, String voteCount) {
        this.id = id;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.voteCount = voteCount;
    }


    /**
     * This will be used only by the MyCreator
     *
     * @param source
     */
    public Movie(Parcel source) {
        // Reconstruct from the Parcel
        id = source.readLong();
        posterPath = source.readString();
        backdropPath = source.readString();
        originalTitle = source.readString();
        overview = source.readString();
        rating = source.readString();
        releaseDate = source.readString();
        voteCount = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(rating);
        dest.writeString(releaseDate);
        dest.writeString(voteCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * It will be required during un-marshaling data stored in a Parcel
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Long getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVoteCount() {
        return voteCount;
    }
}
