package com.rarestardev.videovibe.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.videovibe.Adapters.VideoFolderAdapter;
import com.rarestardev.videovibe.Enum.OrderStateEnum;
import com.rarestardev.videovibe.Enum.SharedPrefEnum;
import com.rarestardev.videovibe.Enum.SharedPrefKeyEnum;
import com.rarestardev.videovibe.Enum.ViewStateEnum;
import com.rarestardev.videovibe.Model.VideoFolderModel;
import com.rarestardev.videovibe.R;
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
    private int stateMode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        doInitialize();
        getVideoFolder();
    }


    private void doInitialize() {
        binding.imageViewSettings.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        binding.imageViewSearch.setOnClickListener(view -> {
            Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
            searchIntent.putExtra("SearchState","Folder");
            startActivity(searchIntent);
        });

        securePreferences = new SecurePreferences(this);
        String ViewStateLayout = securePreferences.getSecureString(SharedPrefEnum.LayoutView.name(), SharedPrefKeyEnum.ViewState.name());
        String orderState = securePreferences.getSecureString(SharedPrefEnum.Order.name(), SharedPrefKeyEnum.BasedState.name());

        if (TextUtils.isEmpty(ViewStateLayout) && TextUtils.isEmpty(orderState)){
            securePreferences.saveSecureString(SharedPrefEnum.LayoutView.name(), SharedPrefKeyEnum.ViewState.name(), ViewStateEnum.List.name());
            securePreferences.saveSecureString(SharedPrefEnum.Order.name(), SharedPrefKeyEnum.BasedState.name(), OrderStateEnum.Newest.name());
        }

        binding.imageViewChangeLayout.setOnClickListener(view -> {
            ViewStateCustomDialog viewStateCustomDialog = new ViewStateCustomDialog(this);
            viewStateCustomDialog.setCancelable(true);
            viewStateCustomDialog.getWindow().setGravity(Gravity.BOTTOM);
            viewStateCustomDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT ,ViewGroup.LayoutParams.WRAP_CONTENT);
            viewStateCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            viewStateCustomDialog.getWindow().getAttributes().windowAnimations= R.style.DialogAnimation;
            viewStateCustomDialog.show();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getVideoFolder() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<VideoFolderModel> folderModels = VideoFoldersRetriever.getVideoFolders(this);

        VideoFolderAdapter videoFolderAdapter = new VideoFolderAdapter(folderModels, (folderPath, folderName) -> {
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