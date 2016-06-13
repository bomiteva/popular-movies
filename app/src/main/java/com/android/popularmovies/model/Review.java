package com.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bobi on 3/31/2016.
 */
public class Review implements Parcelable {

    private String id;
    private Long movieId;
    private String author;
    private String content;
    private String url;

    public Review(String id, Long movieId, String author, String content, String url) {
        this.id = id;
        this.movieId = movieId;
        this.author = author;
        this.content = content;
        this.url = url;
    }


    /**
     * This will be used only by the MyCreator
     *
     * @param source
     */
    public Review(Parcel source) {
        // Reconstruct from the Parcel
        id = source.readString();
        movieId = source.readLong();
        author = source.readString();
        content = source.readString();
        url = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(movieId);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * It will be required during un-marshaling data stored in a Parcel
     */
    public static final Creator CREATOR = new Creator() {
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getId() {
        return id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
