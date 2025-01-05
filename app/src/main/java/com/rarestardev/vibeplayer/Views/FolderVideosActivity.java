package com.rarestardev.vibeplayer.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.vibeplayer.Adapters.VideosAdapter;
import com.rarestardev.vibeplayer.Listener.VideosListener;
import com.rarestardev.vibeplayer.Model.VideoModel;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.Utilities.VideoFoldersRetriever;
import com.rarestardev.vibeplayer.VideoPlayer.VideoPlayerActivity;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SavedVideosDao;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.Utilities.DetailVideosDialog;
import com.rarestardev.vibeplayer.databinding.ActivityFolderVideosBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class FolderVideosActivity extends AppCompatActivity implements VideosAdapter.onVideosSavedListener {

    private VideosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFolderVideosBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_folder_videos);

        String folderPath = getIntent().getStringExtra(Constants.VIDEO_FOLDER_PATH_NAME);
        String folderName = getIntent().getStringExtra(Constants.VIDEO_FOLDER_NAME);
        binding.setFolderName(folderName);

        binding.backActivity.setOnClickListener(view -> finish());
        binding.searchButton.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<VideoModel> video = VideoFoldersRetriever.getVideosFromFolder(folderPath);
        adapter = new VideosAdapter(video, this, this);
        adapter.ViewModeState(1);
        binding.recyclerView.setAdapter(adapter);
        adapter.VideosAdapterActivity(this);
        clickedVideos();
    }

    private void clickedVideos() {
        adapter.VideosListener(new VideosListener() {
            @Override
            public void onClickVideos(VideoModel videoModel) {
                setOnClickVideos(videoModel.getVideoName(), videoModel.getVideoPath());
            }

            @Override
            public void onLongClickVideos(VideoModel videoModel, View anchorView) {
                // not used
            }
        });
    }


    private void setOnClickVideos(String videoName, String path) {
        Intent intent = new Intent(FolderVideosActivity.this, VideoPlayerActivity.class);
        intent.putExtra("VideoName", videoName);
        intent.putExtra("VideoPath", path);
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void addVideos(VideoModel videoModel) {
        AppDatabase appDatabase = DatabaseClient.getInstance(this).getAppDatabase();
        SavedVideosDao videosDao = appDatabase.savedVideosDao();
        Executors.newSingleThreadExecutor().execute(() -> {
            if (videoModel != null) {
                videosDao.addedVideos(videoModel);
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void deleteVideos(VideoModel videoModel) {
        AppDatabase appDatabase = DatabaseClient.getInstance(this).getAppDatabase();
        SavedVideosDao videosDao = appDatabase.savedVideosDao();
        Executors.newSingleThreadExecutor().execute(() -> {
            if (videoModel != null) {
                videosDao.deleteByVideoName(videoModel.getVideoName());
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }
        });
    }

    @Override
    public void shareVideos(VideoModel videoModel) {
        File imageFile = new File(videoModel.getVideoPath());
        Uri imageUri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() + ".fileprovider", imageFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, imageFile.getName());
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType("video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, videoModel.getVideoName()));
    }

    @Override
    public void detailVideo(VideoModel videoModel) {
        DetailVideosDialog dialog = new DetailVideosDialog(videoModel,FolderVideosActivity.this);
        dialog.showDetailDialog();
    }
}