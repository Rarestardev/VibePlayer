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

public class VideoFolderAdapterGridView extends RecyclerView.Adapter<VideoFolderAdapterGridView.VideoFolderGridView>{

    private final ArrayList<VideoFolderModel> folderModel;
    private final VideoFolderListener listener;

    public VideoFolderAdapterGridView(ArrayList<VideoFolderModel> folderModel, VideoFolderListener listener) {
        this.folderModel = folderModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoFolderGridView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_folder_item_grid, viewGroup, false);
        return new VideoFolderGridView(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VideoFolderGridView videoFolderGridView, int i) {
        VideoFolderModel folder = folderModel.get(i);
        videoFolderGridView.tvVideoFolderName.setText(folder.getFolderName());
        videoFolderGridView.videoNumber.setText("( " + folder.getVideoCount() + " )");
        videoFolderGridView.videoFolderLayout.setOnClickListener(view ->
                listener.onFolderClick(folder.getVideoPath(), folder.getFolderName()));
    }

    @Override
    public int getItemCount() {
        return folderModel.size();
    }

    public static class VideoFolderGridView extends RecyclerView.ViewHolder{

        AppCompatTextView tvVideoFolderName, videoNumber;
        ConstraintLayout videoFolderLayout;

        public VideoFolderGridView(@NonNull View itemView) {
            super(itemView);
            tvVideoFolderName = itemView.findViewById(R.id.tvVideoFolderName);
            videoNumber = itemView.findViewById(R.id.videoNumber);
            videoFolderLayout = itemView.findViewById(R.id.videoFolderLayout);
        }
    }
}
