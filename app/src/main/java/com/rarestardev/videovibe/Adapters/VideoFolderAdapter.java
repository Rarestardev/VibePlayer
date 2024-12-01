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

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.VideoFolderViewHolder>{

    private final ArrayList<VideoFolderModel> videoFolderModels;
    private final VideoFolderListener listener;
    private int stateMode;

    public VideoFolderAdapter(ArrayList<VideoFolderModel> videoFolderModels,VideoFolderListener listener) {
        this.videoFolderModels = videoFolderModels;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public VideoFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_folder_item,parent,false);
        return new VideoFolderViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull VideoFolderViewHolder holder, int position) {
        VideoFolderModel folderModel = videoFolderModels.get(position);
        holder.tvVideoFolderName.setText(folderModel.getFolderName());
        holder.videoPath.setText(folderModel.getVideoPath());
        holder.videoNumber.setText("( " + folderModel.getVideoCount() + " )");
        holder.videoFolderLayout.setOnClickListener(view -> listener.onFolderClick(folderModel.getVideoPath(),folderModel.getFolderName()));
    }

    @Override
    public int getItemCount() {
        return videoFolderModels.size();
    }

    public static class VideoFolderViewHolder extends RecyclerView.ViewHolder{

        AppCompatTextView tvVideoFolderName,videoNumber,videoPath;
        ConstraintLayout videoFolderLayout;

        public VideoFolderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVideoFolderName = itemView.findViewById(R.id.tvVideoFolderName);
            videoNumber = itemView.findViewById(R.id.videoNumber);
            videoFolderLayout = itemView.findViewById(R.id.videoFolderLayout);
            videoPath = itemView.findViewById(R.id.videoPath);
        }
    }
}
