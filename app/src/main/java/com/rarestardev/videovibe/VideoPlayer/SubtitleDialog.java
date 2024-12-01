package com.rarestardev.videovibe.VideoPlayer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.videovibe.Adapters.SubtitleFilesAdapter;
import com.rarestardev.videovibe.Adapters.SubtitleFolderAdapter;
import com.rarestardev.videovibe.Listener.SubtitleFilesSaveState;
import com.rarestardev.videovibe.Model.SubtitleFile;
import com.rarestardev.videovibe.Model.SubtitleFolder;
import com.rarestardev.videovibe.R;
import com.rarestardev.videovibe.Utilities.SubtitleFinder;
import com.rarestardev.videovibe.databinding.SubtitleDialogLayoutBinding;

import java.util.List;

public class SubtitleDialog extends Dialog {

    private final SubtitleDialogLayoutBinding binding;
    private List<SubtitleFile> subtitleFiles;
    private final SubtitleFilesSaveState state;

    public SubtitleDialog(@NonNull Context context, SubtitleFilesSaveState state) {
        super(context);
        this.state = state;
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.subtitle_dialog_layout, null, false);
        setContentView(binding.getRoot());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetSubtitle();
        binding.closest.setOnClickListener(view -> dismiss());
        binding.back.setOnClickListener(view -> {
            if (binding.showSubtitleRecyclerView.getVisibility() == View.VISIBLE) {
                binding.showSubtitleRecyclerView.setVisibility(View.GONE);
                binding.back.setVisibility(View.GONE);
                binding.setStatus("Subtitle");
                binding.showFolderRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void SetSubtitle() {
        binding.showFolderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<SubtitleFolder> foldersWithSubtitles = SubtitleFinder.getSubtitleFolders(getContext());
        SubtitleFolderAdapter subtitleFolderAdapter = new SubtitleFolderAdapter(foldersWithSubtitles, this::GetSubtitleFiles);
        binding.showFolderRecyclerView.setAdapter(subtitleFolderAdapter);
        binding.setStatus("Subtitle");
    }

    private void GetSubtitleFiles(String folder) {
        binding.setStatus(folder);
        binding.showSubtitleRecyclerView.setVisibility(View.VISIBLE);
        binding.back.setVisibility(View.VISIBLE);
        binding.showFolderRecyclerView.setVisibility(View.GONE);

        if (binding.showSubtitleRecyclerView.getVisibility() == View.VISIBLE) {
            binding.showSubtitleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            subtitleFiles = SubtitleFinder.getSubtitleFiles(folder, getContext());

            SubtitleFilesAdapter subtitleFilesAdapter = new SubtitleFilesAdapter(subtitleFiles, path -> {
                state.subtitlePath(path);
                dismiss();
            });

            binding.showSubtitleRecyclerView.setAdapter(subtitleFilesAdapter);
        } else {
            subtitleFiles.clear();
        }
    }
}
