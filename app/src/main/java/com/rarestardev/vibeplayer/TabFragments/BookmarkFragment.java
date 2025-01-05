package com.rarestardev.vibeplayer.TabFragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rarestardev.vibeplayer.Model.VideoModel;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.VideoPlayer.VideoPlayerActivity;
import com.rarestardev.vibeplayer.Adapters.ImagesViewerAdapter.ImagesAdapter;
import com.rarestardev.vibeplayer.Adapters.SavedVideosAdapter;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SavedImagesDao;
import com.rarestardev.vibeplayer.Database.dao.SavedVideosDao;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.databinding.FragmentBookmarkBinding;

import java.util.List;
import java.util.concurrent.Executors;

public class BookmarkFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String mParam1;
    String mParam2;

    private FragmentBookmarkBinding binding;
    private SavedVideosAdapter adapter;
    private ImagesAdapter imagesAdapter;

    private SavedVideosDao savedVideosDao;
    private SavedImagesDao savedImagesDao;

    public BookmarkFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewImages.setLayoutManager(new GridLayoutManager(getContext(), 4));

        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();
        savedVideosDao = db.savedVideosDao();
        savedImagesDao = db.savedImagesDao();

        tabInitialize();

        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void tabInitialize() {
        binding.savedImages.setTextColor(getActivity().getColor(R.color.tabUnselectedColor));
        binding.savedVideo.setTextColor(getActivity().getColor(R.color.tabSelectedColor));
        binding.savedVideo.setBackground(getActivity().getDrawable(R.drawable.video_folder_item_shape));
        binding.savedImages.setBackground(null);
        binding.recyclerViewImages.setVisibility(View.GONE);
        showVideos();

        binding.savedVideo.setOnClickListener(view -> {
            if (binding.recyclerViewImages.getVisibility() == View.VISIBLE) {
                binding.recyclerViewImages.setVisibility(View.GONE);
                binding.notFoundTvImages.setVisibility(View.GONE);
                binding.savedImages.setBackground(null);
                binding.savedImages.setTextColor(getActivity().getColor(R.color.tabUnselectedColor));
                binding.savedVideo.setTextColor(getActivity().getColor(R.color.tabSelectedColor));
            } else {
                binding.savedImages.setBackground(null);
                binding.savedImages.setTextColor(getActivity().getColor(R.color.tabUnselectedColor));
                binding.savedVideo.setTextColor(getActivity().getColor(R.color.tabSelectedColor));
            }
            binding.savedVideo.setBackground(getActivity().getDrawable(R.drawable.video_folder_item_shape));
            showVideos();

        });

        binding.savedImages.setOnClickListener(view -> {

            if (binding.recyclerView.getVisibility() == View.VISIBLE) {
                binding.recyclerView.setVisibility(View.GONE);
                binding.notFoundTv.setVisibility(View.GONE);
                binding.savedVideo.setBackground(null);
                binding.savedVideo.setTextColor(getActivity().getColor(R.color.tabUnselectedColor));
                binding.savedImages.setTextColor(getActivity().getColor(R.color.tabSelectedColor));
                binding.savedImages.setBackground(getActivity().getDrawable(R.drawable.video_folder_item_shape));
                showImages();
            } else {
                binding.savedVideo.setBackground(null);
                binding.savedVideo.setTextColor(getActivity().getColor(R.color.tabUnselectedColor));
                binding.savedImages.setTextColor(getActivity().getColor(R.color.tabSelectedColor));
                binding.savedImages.setBackground(getActivity().getDrawable(R.drawable.video_folder_item_shape));
                binding.notFoundTv.setVisibility(View.GONE);
                showImages();
            }

        });
    }

    private void showImages() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        getAllImages(images -> {
            if (!images.isEmpty()) {
                binding.recyclerViewImages.setVisibility(View.VISIBLE);
                binding.notFoundTvImages.setVisibility(View.GONE);
                imagesAdapter = new ImagesAdapter(images, getContext(), images.size());
                progressDialog.dismiss();
            } else {
                Log.d(Constants.LOG, "Images saved database is empty");
                binding.recyclerViewImages.setVisibility(View.GONE);
                binding.notFoundTvImages.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
            binding.recyclerViewImages.setAdapter(imagesAdapter);
        });
    }

    private void getAllImages(Consumer<List<String>> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> images = savedImagesDao.getAllImages();
            getActivity().runOnUiThread(() -> callback.accept(images));
        });
    }

    private void showVideos() {
        getAllVideos(videos -> {
            if (!videos.isEmpty()) {
                binding.notFoundTv.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                adapter = new SavedVideosAdapter(getContext(), videos, new SavedVideosAdapter.onSavedVideoListener() {
                    @Override
                    public void onClickVideos(String videoPath, String videoName) {
                        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                        intent.putExtra("VideoName", videoName);
                        intent.putExtra("VideoPath", videoPath);
                        startActivity(intent);
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDeletedVideoFromFavoriteList(VideoModel videoModel, int position) {
                        AppDatabase appDatabase = DatabaseClient.getInstance(getContext()).getAppDatabase();
                        SavedVideosDao videosDao = appDatabase.savedVideosDao();
                        Executors.newSingleThreadExecutor().execute(() -> {
                            if (videoModel != null) {
                                videosDao.deleteByVideoName(videoModel.getVideoName());
                                getActivity().runOnUiThread(() -> adapter.notifyItemRemoved(position));
                                binding.recyclerView.refreshDrawableState();
                                showVideos();
                            }
                        });
                    }
                });
            } else {
                Log.d(Constants.LOG, "Saved Videos is empty");
                binding.notFoundTv.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }
            binding.recyclerView.setAdapter(adapter);
        });
    }

    private void getAllVideos(Consumer<List<VideoModel>> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<VideoModel> videos = savedVideosDao.getAllSavedVideos();
            getActivity().runOnUiThread(() -> callback.accept(videos));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        tabInitialize();
    }
}