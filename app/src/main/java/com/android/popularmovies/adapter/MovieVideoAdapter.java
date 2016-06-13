package com.android.popularmovies.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.popularmovies.R;
import com.android.popularmovies.model.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by bobi on 6/1/2016.
 */
public class MovieVideoAdapter extends RecyclerView.Adapter<MovieVideoAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieVideoAdapter.class.getSimpleName();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final ImageView mVideoThumbnailView;
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mVideoThumbnailView = (ImageView)view.findViewById(R.id.video_thumbnail);
        }
    }

    public interface PlayVideoCallback {
        void play(Video video);
    }

    private ArrayList<Video> mVideos;
    private PlayVideoCallback mCallback;

    public MovieVideoAdapter(ArrayList<Video> videos, PlayVideoCallback playVideoCallback) {
        mVideos = videos;
        mCallback = playVideoCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Video video = mVideos.get(position);
        final Context context = holder.mVideoThumbnailView.getContext();

        String thumbnailUrl = "http://img.youtube.com/vi/" + video.getKey() + "/0.jpg";

        Picasso.with(context)
                .load(thumbnailUrl)
                .config(Bitmap.Config.RGB_565)
                .into(holder.mVideoThumbnailView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.play(video);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public ArrayList<Video> getVideos() {
        return mVideos;
    }

    public void addAll(ArrayList<Video> videos) {
        mVideos.clear();
        mVideos.addAll(videos);
        notifyDataSetChanged();
    }
}
