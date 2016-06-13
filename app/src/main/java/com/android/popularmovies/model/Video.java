package com.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bobi on 3/31/2016.
 */
public class Video implements Parcelable {

    private String id;
    private Long movieId;
    private String key;
    private String name;
    private Integer size;

    public Video(String id, Long movieId, String key, String name, Integer size) {
        this.id = id;
        this.movieId = movieId;
        this.key = key;
        this.name = name;
        this.size = size;
    }


    /**
     * This will be used only by the MyCreator
     *
     * @param source
     */
    public Video(Parcel source) {
        // Reconstruct from the Parcel
        id = source.readString();
        movieId = source.readLong();
        key = source.readString();
        name = source.readString();
        size = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(movieId);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeInt(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * It will be required during un-marshaling data stored in a Parcel
     */
    public static final Creator CREATOR = new Creator() {
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getId() {
        return id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Integer getSize() {
        return size;
    }
}
