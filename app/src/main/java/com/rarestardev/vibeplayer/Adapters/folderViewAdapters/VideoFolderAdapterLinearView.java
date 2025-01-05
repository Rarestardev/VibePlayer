package com.rarestardev.vibeplayer.Adapters.folderViewAdapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.vibeplayer.Listener.VideoFolderListener;
import com.rarestardev.vibeplayer.Model.VideoFolderModel;
import com.rarestardev.vibeplayer.R;

import java.util.ArrayList;

public class VideoFolderAdapterLinearView extends RecyclerView.Adapter<VideoFolderAdapterLinearView.VideoFolderViewHolder>{
    private final ArrayList<VideoFolderModel> videoFolderModels;
    private final VideoFolderListener listener;

    public VideoFolderAdapterLinearView(ArrayList<VideoFolderModel> videoFolderModels, VideoFolderListener listener) {
        this.videoFolderModels = videoFolderModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoFolderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_folder_item,viewGroup,false);
        return new VideoFolderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VideoFolderViewHolder videoFolderViewHolder, int i) {
        VideoFolderModel folderModel = videoFolderModels.get(i);
        videoFolderViewHolder.tvVideoFolderName.setText(folderModel.getFolderName());
        videoFolderViewHolder.videoPath.setText(folderModel.getVideoPath());
        videoFolderViewHolder.videoNumber.setText("( " + folderModel.getVideoCount() + " )");
        videoFolderViewHolder.videoFolderLayout.setOnClickListener(view ->
                listener.onFolderClick(folderModel.getVideoPath(), folderModel.getFolderName()));
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
}
