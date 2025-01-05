package com.rarestardev.vibeplayer.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SavedVideosDao;
import com.rarestardev.vibeplayer.Listener.VideosListener;
import com.rarestardev.vibeplayer.Model.VideoModel;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.Utilities.FileFormater;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<VideoModel> videos;
    private final Context context;
    private VideosListener listener;

    private int ViewMode;
    private static final int VIEW_MODE_DEFAULT = 1;
    private static final int VIEW_MODE_DETAIL = 2;
    private SavedVideosDao savedVideosDao;
    private onVideosSavedListener videosSavedListener;
    private Activity activity;

    public VideosAdapter(ArrayList<VideoModel> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    public VideosAdapter(ArrayList<VideoModel> videos, Context context, onVideosSavedListener videosSavedListener) {
        this.videos = videos;
        this.context = context;
        this.videosSavedListener = videosSavedListener;
    }

    public void VideosAdapterActivity(Activity activity) {
        this.activity = activity;
    }

    public void VideosListener(VideosListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void ViewModeState(int mode) {
        ViewMode = mode;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (ViewMode == VIEW_MODE_DEFAULT) {
            return VIEW_MODE_DEFAULT;
        } else {
            return VIEW_MODE_DETAIL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (ViewMode == VIEW_MODE_DEFAULT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_videos_item, viewGroup, false);
            return new VideosViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_videos_item_details, viewGroup, false);
            return new DetailVideos(view);
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        AppDatabase appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        savedVideosDao = appDatabase.savedVideosDao();
        VideoModel videoModel = videos.get(i);

        if (viewHolder instanceof VideosViewHolder) {
            getAllVideos(videoModels -> {
                if (!videoModels.isEmpty()) {
                    for (VideoModel items : videoModels) {
                        if (videoModel.getVideoName().equals(items.getVideoName())){
                            ((VideosViewHolder) viewHolder).deletedVideo.setVisibility(View.VISIBLE);
                            ((VideosViewHolder) viewHolder).saveVideo.setVisibility(View.GONE);
                            ((VideosViewHolder) viewHolder).deletedVideo.setOnClickListener(view -> {
                                videosSavedListener.deleteVideos(videoModel);
                                ((VideosViewHolder) viewHolder).deletedVideo.setVisibility(View.GONE);
                                ((VideosViewHolder) viewHolder).saveVideo.setVisibility(View.VISIBLE);
                            });
                            break;
                        }else {
                            ((VideosViewHolder) viewHolder).deletedVideo.setVisibility(View.GONE);
                            ((VideosViewHolder) viewHolder).saveVideo.setVisibility(View.VISIBLE);
                            ((VideosViewHolder) viewHolder).saveVideo.setOnClickListener(view -> {
                                videosSavedListener.addVideos(videoModel);
                                ((VideosViewHolder) viewHolder).deletedVideo.setVisibility(View.VISIBLE);
                                ((VideosViewHolder) viewHolder).saveVideo.setVisibility(View.GONE);
                            });
                        }
                    }
                }else {
                    ((VideosViewHolder) viewHolder).deletedVideo.setVisibility(View.GONE);
                    ((VideosViewHolder) viewHolder).saveVideo.setVisibility(View.VISIBLE);
                    ((VideosViewHolder) viewHolder).saveVideo.setOnClickListener(view -> {
                        videosSavedListener.addVideos(videoModel);
                        ((VideosViewHolder) viewHolder).deletedVideo.setVisibility(View.VISIBLE);
                        ((VideosViewHolder) viewHolder).saveVideo.setVisibility(View.GONE);
                    });
                }
            });


            ((VideosViewHolder) viewHolder).videoName.setText(videoModel.getVideoName());
            ((VideosViewHolder) viewHolder).videoInfo.setText(FileFormater.formatTime(videoModel.getDuration()) +
                    " -- " + FileFormater.formatFileSize(videoModel.getSize()));

            Glide.with(context)
                    .load(videoModel.getVideoPath())
                    .into(((VideosViewHolder) viewHolder).thumbnailVideosImageView);

            ((VideosViewHolder) viewHolder).videoItem.setOnClickListener(view ->
                    listener.onClickVideos(videoModel));

            ((VideosViewHolder) viewHolder).detailVideo.setOnClickListener(view -> videosSavedListener.detailVideo(videoModel));
            ((VideosViewHolder) viewHolder).shareVideo.setOnClickListener(view -> videosSavedListener.shareVideos(videoModel));
            ((VideosViewHolder) viewHolder).videoItem.setOnLongClickListener(view -> {
                listener.onLongClickVideos(videoModel,((VideosViewHolder) viewHolder).videoItem);
                return true;
            });

        } else if (viewHolder instanceof DetailVideos) {
            ((DetailVideos) viewHolder).videoName.setText(videoModel.getVideoName());
            ((DetailVideos) viewHolder).tvDurationVideo.setText(FileFormater.formatTime(videoModel.getDuration()));
            Glide.with(context)
                    .load(videoModel.getVideoPath())
                    .into(((DetailVideos) viewHolder).thumbnailVideosImageView);

            ((DetailVideos) viewHolder).videoItem.setOnClickListener(view -> listener.onClickVideos(videoModel));
            ((DetailVideos) viewHolder).videoItem.setOnLongClickListener(view -> {
                listener.onLongClickVideos(videoModel,((DetailVideos) viewHolder).videoItem);
                return true;
            });
        }
    }

    private void getAllVideos(Consumer<List<VideoModel>> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<VideoModel> videos = savedVideosDao.getAllSavedVideos();
            activity.runOnUiThread(() -> callback.accept(videos));
        });
    }

    public interface onVideosSavedListener {
        void addVideos(VideoModel videoModel);

        void deleteVideos(VideoModel videoModel);

        void shareVideos(VideoModel videoModel);

        void detailVideo(VideoModel videoModel);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideosViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView thumbnailVideosImageView;
        AppCompatTextView videoName, videoInfo;
        ConstraintLayout videoItem;
        AppCompatImageView saveVideo, shareVideo, deletedVideo,detailVideo;

        public VideosViewHolder(@NonNull View itemView) {
            super(itemView);

            videoItem = itemView.findViewById(R.id.videoItem);
            videoName = itemView.findViewById(R.id.videoName);
            videoInfo = itemView.findViewById(R.id.videoInfo);
            saveVideo = itemView.findViewById(R.id.saveVideo);
            shareVideo = itemView.findViewById(R.id.shareVideo);
            deletedVideo = itemView.findViewById(R.id.deletedVideo);
            detailVideo = itemView.findViewById(R.id.detailVideo);
            thumbnailVideosImageView = itemView.findViewById(R.id.thumbnailVideosImageView);
        }
    }

    public static class DetailVideos extends RecyclerView.ViewHolder {
        LinearLayoutCompat videoItem;
        AppCompatTextView videoName, tvDurationVideo;
        RoundedImageView thumbnailVideosImageView;

        public DetailVideos(@NonNull View itemView) {
            super(itemView);

            videoItem = itemView.findViewById(R.id.videoItem);
            videoName = itemView.findViewById(R.id.videoName);
            thumbnailVideosImageView = itemView.findViewById(R.id.thumbnailVideosImageView);
            tvDurationVideo = itemView.findViewById(R.id.tvDurationVideo);
        }
    }
}
