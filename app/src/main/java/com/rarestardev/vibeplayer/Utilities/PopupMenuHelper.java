package com.rarestardev.vibeplayer.Utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;

import com.rarestardev.vibeplayer.Adapters.VideosAdapter;
import com.rarestardev.vibeplayer.Model.VideoModel;
import com.rarestardev.vibeplayer.VideoPlayer.VideoPlayerActivity;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SavedVideosDao;
import com.rarestardev.vibeplayer.R;

import java.util.concurrent.Executors;

public class PopupMenuHelper {

    private final Activity activity;
    private final View anchorView;
    private final VideoModel videoModel;
    private final VideosAdapter adapter;

    public PopupMenuHelper(Activity activity, View anchorView, VideoModel videoModel, VideosAdapter adapter) {
        this.activity = activity;
        this.anchorView = anchorView;
        this.videoModel = videoModel;
        this.adapter = adapter;
    }

    public void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(activity, anchorView);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.long_press_video_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.openVideo) {
                setOnClickVideos(videoModel.getVideoName(), videoModel.getVideoPath());
            } else if (menuItem.getItemId() == R.id.saveVideo) {
                addVideos(videoModel);
            } else if (menuItem.getItemId() == R.id.detailVideo) {
                DetailVideosDialog dialog = new DetailVideosDialog(videoModel,activity);
                dialog.showDetailDialog();
            }
            return true;
        });

        popupMenu.show();
    }

    private void setOnClickVideos(String videoName, String path) {
        Intent intent = new Intent(activity, VideoPlayerActivity.class);
        intent.putExtra("VideoName", videoName);
        intent.putExtra("VideoPath", path);
        activity.startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addVideos(VideoModel videoModel) {
        AppDatabase appDatabase = DatabaseClient.getInstance(activity).getAppDatabase();
        SavedVideosDao videosDao = appDatabase.savedVideosDao();
        Executors.newSingleThreadExecutor().execute(() -> {
            if (videoModel != null) {
                videosDao.addedVideos(videoModel);
                activity.runOnUiThread(adapter::notifyDataSetChanged);
            }
        });
    }

}
