package com.rarestardev.videovibe.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.videovibe.Adapters.VideosAdapter;
import com.rarestardev.videovibe.Model.VideoModel;
import com.rarestardev.videovibe.R;
import com.rarestardev.videovibe.VideoPlayer.VideoPlayerActivity;

import java.io.File;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private VideosAdapter adapter;
    private ArrayList<VideoModel> videoModels;
    private AppCompatTextView tvNoResult;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        AppCompatImageView backActivity = findViewById(R.id.backActivity);
        AppCompatEditText editTextSearch = findViewById(R.id.editTextSearch);
        tvNoResult = findViewById(R.id.tvNoResult);
        recyclerView = findViewById(R.id.searchRecycler);

        editTextSearch.setFocusable(true);

        tvNoResult.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        videoModels = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VideosAdapter(videoModels, SearchActivity.this, (videoPath, videoName) -> {
            Intent intent = new Intent(SearchActivity.this, VideoPlayerActivity.class);
            intent.putExtra("VideoPath", videoPath);
            intent.putExtra("VideoName", videoName);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        backActivity.setOnClickListener(view -> {
            videoModels.clear();
            finish();
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    searchForVideo(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchForVideo(String query) {
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE};
        String selection = MediaStore.Video.Media.TITLE + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};
        Uri collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        try (Cursor cursor = getContentResolver().query(collection, projection, selection, selectionArgs, null)) {
            videoModels.clear();
            if (cursor != null && cursor.moveToFirst()) {
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                do {
                    String title = cursor.getString(titleColumn);
                    String data = cursor.getString(dataColumn);
                    long duration = cursor.getLong(durationColumn);
                    long size = cursor.getLong(sizeColumn);
                    File videoFile = new File(data);
                    videoModels.add(new VideoModel(title, data, videoFile, duration, size));
                    tvNoResult.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } while (cursor.moveToNext());
                adapter.notifyDataSetChanged();
            } else {
                tvNoResult.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }
}