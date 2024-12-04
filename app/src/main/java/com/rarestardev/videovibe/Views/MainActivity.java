package com.rarestardev.videovibe.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.videovibe.Adapters.VideoFolderAdapter;
import com.rarestardev.videovibe.Model.VideoFolderModel;
import com.rarestardev.videovibe.R;
import com.rarestardev.videovibe.Utilities.Constants;
import com.rarestardev.videovibe.Utilities.ViewStateCustomDialog;
import com.rarestardev.videovibe.Utilities.SecurePreferences;
import com.rarestardev.videovibe.Utilities.VideoFoldersRetriever;
import com.rarestardev.videovibe.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static final String VIDEO_FOLDER_PATH_NAME = "folderPath";
    public static final String VIDEO_FOLDER_NAME = "folderName";
    private SecurePreferences securePreferences;
    private ViewStateCustomDialog viewStateCustomDialog;
    private VideoFolderAdapter videoFolderAdapter;
    private ArrayList<VideoFolderModel> folderModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        doInitialize();
        getVideoFolder();
        initDefaultDataOnSettings();
    }

    private void initDefaultDataOnSettings() {
        securePreferences = new SecurePreferences(this);
        String Layout = securePreferences.getSecureString(Constants.PREF_LAYOUT_MANAGER, Constants.PREF_LAYOUT_MANAGER_KEY);
        String orderState = securePreferences.getSecureString(Constants.PREF_ORDER_VIEW, Constants.PREF_ORDER_VIEW_KEY);

        if (TextUtils.isEmpty(Layout) && TextUtils.isEmpty(orderState)) {
            final String defaultLayout = "Linear";
            final String defaultOrder = "DESC";
            securePreferences.saveSecureString(Constants.PREF_LAYOUT_MANAGER,Constants.PREF_LAYOUT_MANAGER_KEY, defaultLayout);
            securePreferences.saveSecureString(Constants.PREF_ORDER_VIEW, Constants.PREF_ORDER_VIEW_KEY, defaultOrder);
        }
        onChangedRecyclerItem();
        onFileOrderChange();
        getVideoFolder();
    }

    private void doInitialize() {
        binding.imageViewSettings.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        binding.imageViewSearch.setOnClickListener(view -> {
            Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
            searchIntent.putExtra("SearchState", "Folder");
            startActivity(searchIntent);
        });

        binding.imageViewChangeLayout.setOnClickListener(view -> {
            viewStateCustomDialog = new ViewStateCustomDialog(this);
            viewStateCustomDialog.setCancelable(true);
            viewStateCustomDialog.getWindow().setGravity(Gravity.BOTTOM);
            viewStateCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewStateCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            viewStateCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            viewStateCustomDialog.show();

            viewStateCustomDialog.onDismissDialogListener(dialogInterface -> {
                onChangedRecyclerItem();
                onFileOrderChange();
            });
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void onFileOrderChange() {
        String order = securePreferences.getSecureString(Constants.PREF_ORDER_VIEW, Constants.PREF_ORDER_VIEW_KEY);
        if (order.equals("DESC")) {
            String sortByOrder = MediaStore.Files.FileColumns.DATE_ADDED;
            folderModels = VideoFoldersRetriever.getVideoFolders(this,sortByOrder);
            videoFolderAdapter.notifyDataSetChanged();
            getVideoFolder();
        } else if (order.equals("ASC")) {
            String sortByOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
            folderModels = VideoFoldersRetriever.getVideoFolders(this,sortByOrder);
            videoFolderAdapter.notifyDataSetChanged();
            getVideoFolder();
        }
    }

    private void onChangedRecyclerItem(){
        String layoutState = securePreferences.getSecureString(Constants.PREF_LAYOUT_MANAGER, Constants.PREF_LAYOUT_MANAGER_KEY);
        final String defaultLayout = "Linear";
        if (layoutState.equals(defaultLayout)){
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            videoFolderAdapter.itemViewState(1);
        }else if (layoutState.equals("Grid")){
            binding.recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
            videoFolderAdapter.itemViewState(2);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getVideoFolder() {
        videoFolderAdapter = new VideoFolderAdapter(folderModels, (folderPath, folderName) -> {
            if (folderPath != null) {
                Intent intent = new Intent(MainActivity.this, FolderVideosActivity.class);
                intent.putExtra(VIDEO_FOLDER_PATH_NAME, folderPath);
                intent.putExtra(VIDEO_FOLDER_NAME, folderName);
                startActivity(intent);
            }
        });

        binding.recyclerView.setAdapter(videoFolderAdapter);
    }

}