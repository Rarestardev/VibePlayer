package com.rarestardev.videovibe.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.videovibe.Listener.VideosListener;
import com.rarestardev.videovibe.Model.VideoModel;
import com.rarestardev.videovibe.R;

import java.util.ArrayList;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {

    private final ArrayList<VideoModel> videos;
    private final Context context;
    private final VideosListener listener;

    public VideosAdapter(ArrayList<VideoModel> videos, Context context, VideosListener listener) {
        this.videos = videos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_videos_item, viewGroup, false);
        return new VideosViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder videosViewHolder, int i) {
        VideoModel videoModel = videos.get(i);

        videosViewHolder.videoName.setText(videoModel.getVideoName());
        videosViewHolder.videoPath.setText(videoModel.getVideoPath());
        videosViewHolder.videoInfo.setText(formatDuration(videoModel.getDuration()) + " -- " + formatSize(videoModel.getSize()));

        Glide.with(context)
                .load(videoModel.getVideoFile())
                .into(videosViewHolder.thumbnailVideosImageView);

        videosViewHolder.videoItem.setOnClickListener(view -> listener.onClickVideos(videoModel.getVideoPath(),videoModel.getVideoName()));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    @SuppressLint("DefaultLocale")
    private String formatSize(long size) {
        double fileSizeInMB = size / (1024.0 * 1024.0);
        return String.format("%.2f MB", fileSizeInMB);
    }

    @SuppressLint("DefaultLocale")
    private String formatDuration(long duration) {
        long hour = duration / 1000;
        long min = (duration / 1000) / 60;
        long sec = (duration / 1000) % 60;
        return String.format("%02d:%02d",min,sec);
    }

    public static class VideosViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView thumbnailVideosImageView;
        AppCompatTextView videoName, videoPath,videoInfo;
        ConstraintLayout videoItem;

        public VideosViewHolder(@NonNull View itemView) {
            super(itemView);

            videoItem = itemView.findViewById(R.id.videoItem);
            videoName = itemView.findViewById(R.id.videoName);
            videoPath = itemView.findViewById(R.id.videoPath);
            videoInfo = itemView.findViewById(R.id.videoInfo);
            thumbnailVideosImageView = itemView.findViewById(R.id.thumbnailVideosImageView);
        }
    }
}
