package com.rarestardev.vibeplayer.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.vibeplayer.Adapters.VideosAdapter;
import com.rarestardev.vibeplayer.Listener.VideosListener;
import com.rarestardev.vibeplayer.Model.VideoModel;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.VideoPlayer.VideoPlayerActivity;
import com.rarestardev.vibeplayer.Adapters.ImagesViewerAdapter.ImagesAdapter;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SavedVideosDao;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.Utilities.DetailVideosDialog;
import com.rarestardev.vibeplayer.databinding.ActivitySearchBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity implements VideosAdapter.onVideosSavedListener {

    private ActivitySearchBinding binding;
    private VideosAdapter adapter;
    private ArrayList<VideoModel> videoModels;
    private List<String> imagePath;
    private String searchState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        // default search
        searchState = getString(R.string.searchHintVideos);

        doInitializeViews();

        binding.editTextSearch.setFocusable(true);

        binding.tvNoResult.setVisibility(View.VISIBLE);
        binding.searchRecycler.setVisibility(View.GONE);
        binding.searchRecyclerImages.setVisibility(View.GONE);

        videoModels = new ArrayList<>();

        binding.searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.searchRecyclerImages.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new VideosAdapter(videoModels, SearchActivity.this, this);
        adapter.VideosAdapterActivity(SearchActivity.this);
        adapter.VideosListener(new VideosListener() {
            @Override
            public void onClickVideos(VideoModel videoModel) {
                Intent intent = new Intent(SearchActivity.this, VideoPlayerActivity.class);
                intent.putExtra("VideoPath", videoModel.getVideoPath());
                intent.putExtra("VideoName", videoModel.getVideoName());
                startActivity(intent);
            }

            @Override
            public void onLongClickVideos(VideoModel videoModel, View anchorView) {
                // not used
            }
        });

        adapter.ViewModeState(1);
        binding.searchRecycler.setAdapter(adapter);

        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    if (searchState.equals(getString(R.string.searchHintVideos))) {
                        searchForVideo(charSequence.toString());
                    } else if (searchState.equals(getString(R.string.searchHintImages))) {
                        searchForImages(charSequence.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void doInitializeViews() {
        binding.filterView.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(SearchActivity.this, view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.filter_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.videosMenu) {
                    searchState = getString(R.string.searchHintVideos);
                    binding.editTextSearch.setHint(getString(R.string.searchHintVideos));
                    binding.searchRecyclerImages.setVisibility(View.GONE);
                    binding.searchRecycler.setVisibility(View.VISIBLE);
                    imagePath.clear();
                } else if (menuItem.getItemId() == R.id.imagesMenu) {
                    searchState = getString(R.string.searchHintImages);
                    binding.editTextSearch.setHint(getString(R.string.searchHintImages));
                    binding.searchRecyclerImages.setVisibility(View.VISIBLE);
                    binding.searchRecycler.setVisibility(View.GONE);
                    videoModels.clear();
                }
                binding.tvNoResult.setVisibility(View.VISIBLE);
                if (!binding.editTextSearch.getText().toString().isEmpty()) {
                    binding.editTextSearch.setText("");
                    binding.editTextSearch.setHint(searchState);
                }
                return true;
            });

            popupMenu.show();
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
                    videoModels.add(new VideoModel(title, data, duration, size));
                    binding.tvNoResult.setVisibility(View.GONE);
                    binding.searchRecycler.setVisibility(View.VISIBLE);
                } while (cursor.moveToNext());
                adapter.notifyDataSetChanged();
            } else {
                binding.tvNoResult.setVisibility(View.VISIBLE);
                binding.searchRecycler.setVisibility(View.GONE);
                binding.searchRecyclerImages.setVisibility(View.GONE);
            }
        }
    }

    private void searchForImages(String query) {
        imagePath = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};
        Cursor cursor = getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs, null
        );

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            while (cursor.moveToNext()) {
                String path = cursor.getString(columnIndex);
                imagePath.add(path);
                ImagesAdapter imagesAdapter = new ImagesAdapter(imagePath, SearchActivity.this, imagePath.size());
                binding.searchRecyclerImages.setAdapter(imagesAdapter);
                binding.tvNoResult.setVisibility(View.GONE);
                binding.searchRecycler.setVisibility(View.GONE);
                binding.searchRecyclerImages.setVisibility(View.VISIBLE);
                Log.d(Constants.LOG, "Images search" + path);
            }
            cursor.close();
        } else {
            binding.tvNoResult.setVisibility(View.VISIBLE);
            binding.searchRecycler.setVisibility(View.GONE);
            binding.searchRecyclerImages.setVisibility(View.GONE);
            Log.e(Constants.LOG, "Images search null");
        }
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
        Uri imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", imageFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, imageFile.getName());
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType("video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Video ..."));
    }

    @Override
    public void detailVideo(VideoModel videoModel) {
        DetailVideosDialog dialog = new DetailVideosDialog(videoModel, SearchActivity.this);
        dialog.showDetailDialog();
    }
}