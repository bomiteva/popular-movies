package com.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bobi on 5/31/2016.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID +" INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE +" TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH +" TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH +" TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW +" TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RATING +" INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE +" TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_COUNT +" INTEGER NOT NULL);" ;

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
