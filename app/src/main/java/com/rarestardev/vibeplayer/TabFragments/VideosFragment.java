package com.rarestardev.vibeplayer.TabFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.rarestardev.vibeplayer.Adapters.folderViewAdapters.VideoFolderAdapterDetailView;
import com.rarestardev.vibeplayer.Adapters.folderViewAdapters.VideoFolderAdapterGridView;
import com.rarestardev.vibeplayer.Adapters.folderViewAdapters.VideoFolderAdapterLinearView;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SettingsDao;
import com.rarestardev.vibeplayer.Listener.VideoFolderListener;
import com.rarestardev.vibeplayer.Model.VideoFolderModel;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.Utilities.VideoFoldersRetriever;
import com.rarestardev.vibeplayer.Views.FolderVideosActivity;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class VideosFragment extends Fragment implements VideoFolderListener, VideoFoldersRetriever.VideoFoldersCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private AppCompatTextView notFoundTv;
    private ProgressBar progressBar;
    private SettingsDao settingsDao;

    private ArrayList<VideoFolderModel> folderModels;
    VideoFolderAdapterDetailView detailView;

    String mParam1;
    String mParam2;

    public VideosFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_videos, container, false);

        recyclerView = itemView.findViewById(R.id.recyclerView);
        notFoundTv = itemView.findViewById(R.id.notFoundTv);
        progressBar = itemView.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();
        settingsDao = db.settingsDao();

        folderModels = VideoFoldersRetriever.getVideoFolders(getContext());


        onChangedRecyclerItem();

        return itemView;
    }

    private void onChangedRecyclerItem() {
        progressBar.setVisibility(View.GONE);
        if (folderModels == null){
            notFoundTv.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            notFoundTv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            getLayoutMode(layoutState -> {
                if (layoutState == null){
                    Log.e(Constants.LOG,"Layout = null");
                }else {
                    switch (layoutState) {
                        case "DetailView":
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            detailView = new VideoFolderAdapterDetailView(new ArrayList<>(),getContext());
                            detailView.getActivity(getActivity());
                            recyclerView.setAdapter(detailView);
                            VideoFoldersRetriever.getVideoFoldersAsync(getContext(),this);
                            break;
                        case "GridView":
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                            VideoFolderAdapterGridView gridView = new VideoFolderAdapterGridView(folderModels,this);
                            recyclerView.setAdapter(gridView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.refreshDrawableState();
                            break;
                        case "LinearView":
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            VideoFolderAdapterLinearView linearView = new VideoFolderAdapterLinearView(folderModels,this);
                            recyclerView.setAdapter(linearView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.refreshDrawableState();
                            break;
                    }
                }
            });
        }// if
    }

    private void getLayoutMode(Consumer<String> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String layoutMode = settingsDao.getLayoutMode();
            getActivity().runOnUiThread(() -> callback.accept(layoutMode));
        });
    }

    @Override
    public void onFolderClick(String folderPath, String folderName) {
        if (folderPath != null && folderName != null) {
            Intent intent = new Intent(getContext(), FolderVideosActivity.class);
            intent.putExtra(Constants.VIDEO_FOLDER_PATH_NAME, folderPath);
            intent.putExtra(Constants.VIDEO_FOLDER_NAME, folderName);
            startActivity(intent);
        }else {
            Log.e(Constants.LOG,"folder path null");
        }
    }

    @Override
    public void onResume() {
        onChangedRecyclerItem();
        super.onResume();
    }

    @Override
    public void onVideoFoldersFound(ArrayList<VideoFolderModel> videoFolders) {
        detailView.updateData(videoFolders);
    }
}