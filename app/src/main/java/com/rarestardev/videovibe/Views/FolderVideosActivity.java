package com.rarestardev.videovibe.Views;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.videovibe.Adapters.VideosAdapter;
import com.rarestardev.videovibe.Model.VideoModel;
import com.rarestardev.videovibe.R;
import com.rarestardev.videovibe.Utilities.VideoFoldersRetriever;
import com.rarestardev.videovibe.VideoPlayer.VideoPlayerActivity;
import com.rarestardev.videovibe.databinding.ActivityFolderVideosBinding;

import java.util.ArrayList;

public class FolderVideosActivity extends AppCompatActivity {

    private ActivityFolderVideosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_folder_videos);


        String folderPath = getIntent().getStringExtra(MainActivity.VIDEO_FOLDER_PATH_NAME);
        String folderName = getIntent().getStringExtra(MainActivity.VIDEO_FOLDER_NAME);
        binding.setFolderName(folderName);

        binding.backActivity.setOnClickListener(view -> finish());

        ArrayList<VideoModel> video;
        video = VideoFoldersRetriever.getVideosFromFolder(folderPath);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        VideosAdapter adapter = new VideosAdapter(video, this, (videoPath, videoName) -> {
            Intent intent = new Intent(FolderVideosActivity.this, VideoPlayerActivity.class);
            intent.putExtra("VideoName",videoName);
            intent.putExtra("VideoPath",videoPath);
            startActivity(intent);
        });

        binding.recyclerView.setAdapter(adapter);

    }
}