package com.rarestardev.vibeplayer.Adapters.folderViewAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.Utilities.VideoFoldersRetriever;
import com.rarestardev.vibeplayer.VideoPlayer.VideoPlayerActivity;
import com.rarestardev.vibeplayer.Adapters.VideosAdapter;
import com.rarestardev.vibeplayer.Listener.VideosListener;
import com.rarestardev.vibeplayer.Model.VideoFolderModel;
import com.rarestardev.vibeplayer.Model.VideoModel;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.Utilities.PopupMenuHelper;
import com.rarestardev.vibeplayer.Views.FolderVideosActivity;

import java.util.ArrayList;

public class VideoFolderAdapterDetailView extends RecyclerView.Adapter<VideoFolderAdapterDetailView.VideoFolderDetailViewHolder> {

    private final ArrayList<VideoFolderModel> videoFolderModels;
    private final Context context;
    private Activity activity;
    private static final int PAGE_SIZE = 10;

    public VideoFolderAdapterDetailView(ArrayList<VideoFolderModel> videoFolderModels, Context context) {
        this.videoFolderModels = videoFolderModels;
        this.context = context;
    }

    public void getActivity(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public VideoFolderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_folder_item_detail, viewGroup, false);
        return new VideoFolderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoFolderDetailViewHolder holder, int i) {
        VideoFolderModel folderModel = videoFolderModels.get(i);
        holder.tvFolderName.setText(folderModel.getFolderName());
        holder.recyclerView.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.VISIBLE);


        VideoFoldersRetriever.getVideosFromFolderAsync(folderModel.getVideoPath(), videos -> {
            VideosAdapter adapter = getVideosAdapter(videos);
            holder.recyclerView.setAdapter(adapter);

            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        });

        holder.moreVideos.setOnClickListener(view -> {
            Intent intent = new Intent(context, FolderVideosActivity.class);
            intent.putExtra(Constants.VIDEO_FOLDER_NAME, folderModel.getFolderName());
            intent.putExtra(Constants.VIDEO_FOLDER_PATH_NAME, folderModel.getVideoPath());
            context.startActivity(intent);
        });

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

    }

    @Override
    public int getItemCount() {
        return Math.min(videoFolderModels.size(), PAGE_SIZE);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<VideoFolderModel> newVideoFolders) {
        videoFolderModels.clear();
        videoFolderModels.addAll(newVideoFolders);
        notifyDataSetChanged();
    }

    @NonNull
    private VideosAdapter getVideosAdapter(ArrayList<VideoModel> video) {
        VideosAdapter adapter = new VideosAdapter(video, context);

        adapter.VideosListener(new VideosListener() {
            @Override
            public void onClickVideos(VideoModel videoModel) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("VideoName", videoModel.getVideoName());
                intent.putExtra("VideoPath", videoModel.getVideoPath());
                context.startActivity(intent);
            }

            @Override
            public void onLongClickVideos(VideoModel videoModel, View anchorView) {
                PopupMenuHelper menuHelper = new PopupMenuHelper(activity, anchorView, videoModel, adapter);
                menuHelper.showPopupMenu();
            }
        });

        adapter.ViewModeState(2);
        return adapter;
    }


    public static class VideoFolderDetailViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        AppCompatTextView tvFolderName, moreVideos;
        ProgressBar progressBar;

        public VideoFolderDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerView);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);
            moreVideos = itemView.findViewById(R.id.moreVideos);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
