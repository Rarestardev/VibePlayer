package com.rarestardev.videovibe.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.videovibe.Listener.VideoFolderListener;
import com.rarestardev.videovibe.Model.VideoFolderModel;
import com.rarestardev.videovibe.R;

import java.util.ArrayList;

public class VideoFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<VideoFolderModel> videoFolderModels;
    private final VideoFolderListener listener;
    private int stateMode;
    private static final int LinearLayout = 1;
    private static final int GridLayout = 2;

    public VideoFolderAdapter(ArrayList<VideoFolderModel> videoFolderModels, VideoFolderListener listener) {
        this.videoFolderModels = videoFolderModels;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void itemViewState(int data) {
        stateMode = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (stateMode == LinearLayout) {
            return LinearLayout;
        } else {
            return GridLayout;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (stateMode == LinearLayout) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_folder_item, parent, false);
            return new VideoFolderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_folder_item_grid, parent, false);
            return new VideoFolderGridViewHolder(view);
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoFolderModel folderModel = videoFolderModels.get(position);
        if (holder instanceof VideoFolderViewHolder) {
            ((VideoFolderViewHolder) holder).tvVideoFolderName.setText(folderModel.getFolderName());
            ((VideoFolderViewHolder) holder).videoPath.setText(folderModel.getVideoPath());
            ((VideoFolderViewHolder) holder).videoNumber.setText("( " + folderModel.getVideoCount() + " )");
            ((VideoFolderViewHolder) holder).videoFolderLayout.setOnClickListener(view ->
                    listener.onFolderClick(folderModel.getVideoPath(), folderModel.getFolderName()));

        } else if (holder instanceof VideoFolderGridViewHolder) {
            ((VideoFolderGridViewHolder) holder).tvVideoFolderName.setText(folderModel.getFolderName());
            ((VideoFolderGridViewHolder) holder).videoNumber.setText("( " + folderModel.getVideoCount() + " )");
            ((VideoFolderGridViewHolder) holder).videoFolderLayout.setOnClickListener(view ->
                    listener.onFolderClick(folderModel.getVideoPath(), folderModel.getFolderName()));
        }

    }

    @Override
    public int getItemCount() {
        return videoFolderModels.size();
    }

    public static class VideoFolderViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tvVideoFolderName, videoNumber, videoPath;
        ConstraintLayout videoFolderLayout;

        public VideoFolderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVideoFolderName = itemView.findViewById(R.id.tvVideoFolderName);
            videoNumber = itemView.findViewById(R.id.videoNumber);
            videoFolderLayout = itemView.findViewById(R.id.videoFolderLayout);
            videoPath = itemView.findViewById(R.id.videoPath);
        }
    }

    public static class VideoFolderGridViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tvVideoFolderName, videoNumber;
        ConstraintLayout videoFolderLayout;

        public VideoFolderGridViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVideoFolderName = itemView.findViewById(R.id.tvVideoFolderName);
            videoNumber = itemView.findViewById(R.id.videoNumber);
            videoFolderLayout = itemView.findViewById(R.id.videoFolderLayout);
        }
    }
}
