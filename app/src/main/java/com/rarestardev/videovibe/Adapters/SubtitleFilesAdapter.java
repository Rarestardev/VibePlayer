package com.rarestardev.videovibe.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.videovibe.Listener.SubtitleFileListener;
import com.rarestardev.videovibe.Model.SubtitleFile;
import com.rarestardev.videovibe.R;

import java.util.List;

public class SubtitleFilesAdapter extends RecyclerView.Adapter<SubtitleFilesAdapter.SubtitleFileViewHolder>{

    private final List<SubtitleFile> subtitleFiles;
    private final SubtitleFileListener listener;

    public SubtitleFilesAdapter(List<SubtitleFile> subtitleFiles, SubtitleFileListener listener) {
        this.subtitleFiles = subtitleFiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubtitleFileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subtitle_file_item,viewGroup,false);
        return new SubtitleFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtitleFileViewHolder subtitleFileViewHolder, int i) {
        subtitleFileViewHolder.tvSubtitleName.setText(subtitleFiles.get(i).getSubName());
        subtitleFileViewHolder.subtitleLayout.setOnClickListener(view ->
                listener.onSubtitleFileClick(subtitleFiles.get(i).getSubPath()));
    }

    @Override
    public int getItemCount() {
        return subtitleFiles.size();
    }


    public static class SubtitleFileViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout subtitleLayout;
        AppCompatTextView tvSubtitleName;

        public SubtitleFileViewHolder(@NonNull View itemView) {
            super(itemView);

            subtitleLayout = itemView.findViewById(R.id.subtitleLayout);
            tvSubtitleName = itemView.findViewById(R.id.tvSubtitleName);
        }
    }
}
