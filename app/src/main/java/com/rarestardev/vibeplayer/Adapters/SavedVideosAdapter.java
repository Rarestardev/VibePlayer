package com.rarestardev.vibeplayer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.vibeplayer.Model.VideoModel;
import com.rarestardev.vibeplayer.Utilities.FileFormater;
import com.rarestardev.vibeplayer.R;

import java.util.List;

public class SavedVideosAdapter extends RecyclerView.Adapter<SavedVideosAdapter.SavedVideosViewHolder> {

    private final List<VideoModel> videos;
    private final Context context;
    private final onSavedVideoListener listener;

    public SavedVideosAdapter(Context context, List<VideoModel> videos, onSavedVideoListener listener) {
        this.videos = videos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SavedVideosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_videos_item, viewGroup, false);
        return new SavedVideosViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull SavedVideosViewHolder holder, int i) {
        VideoModel videoModel = videos.get(i);
        holder.videoName.setText(videoModel.getVideoName());
        holder.videoInfo.setText(String.format("%s -- %s", FileFormater.formatTime(videoModel.getDuration()), FileFormater.formatFileSize(videoModel.getSize())));

        Glide.with(context)
                .load(videoModel.getVideoPath())
                .into(holder.thumbnailVideosImageView);

        holder.deletedVideo.setOnClickListener(view -> {
            listener.onDeletedVideoFromFavoriteList(videoModel, i);
            videos.remove(i);
        });

        holder.videoItem.setOnClickListener(view -> listener.onClickVideos(videoModel.getVideoPath(), videoModel.getVideoName()));
    }


    public interface onSavedVideoListener {
        void onClickVideos(String videoPath, String videoName);

        void onDeletedVideoFromFavoriteList(VideoModel videoModel, int position);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class SavedVideosViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView thumbnailVideosImageView;
        AppCompatTextView videoName, videoInfo;
        ConstraintLayout videoItem;
        AppCompatImageView deletedVideo;

        public SavedVideosViewHolder(@NonNull View itemView) {
            super(itemView);

            videoItem = itemView.findViewById(R.id.videoItem);
            videoName = itemView.findViewById(R.id.videoName);
            videoInfo = itemView.findViewById(R.id.videoInfo);
            deletedVideo = itemView.findViewById(R.id.deletedVideo);
            thumbnailVideosImageView = itemView.findViewById(R.id.thumbnailVideosImageView);
        }
    }
}
