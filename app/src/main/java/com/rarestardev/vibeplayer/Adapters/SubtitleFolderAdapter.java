package com.rarestardev.vibeplayer.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.vibeplayer.Listener.SubtitleFolderListener;
import com.rarestardev.vibeplayer.Model.SubtitleFolder;
import com.rarestardev.vibeplayer.R;

import java.util.List;

public class SubtitleFolderAdapter extends RecyclerView.Adapter<SubtitleFolderAdapter.SubtitleFolderViewHolder> {

    private final List<SubtitleFolder> subtitleFolders;
    private final SubtitleFolderListener listener;


    public SubtitleFolderAdapter(List<SubtitleFolder> subtitleFolders, SubtitleFolderListener listener) {
        this.subtitleFolders = subtitleFolders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubtitleFolderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subtitle_folder_item, viewGroup, false);
        return new SubtitleFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtitleFolderViewHolder holder, int i) {

        SubtitleFolder subtitleFolder = subtitleFolders.get(i);

        holder.tvFolderName.setText(subtitleFolder.getFolderName());
        holder.tvFolderPath.setText(subtitleFolder.getFolderPath());

        holder.folderLayout.setOnClickListener(view ->
                listener.onSubtitleFolderClick(subtitleFolder.getFolderName()));

    }

    @Override
    public int getItemCount() {
        return subtitleFolders.size();
    }


    public static class SubtitleFolderViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout folderLayout;
        AppCompatTextView tvFolderName, tvFolderPath;

        public SubtitleFolderViewHolder(@NonNull View itemView) {
            super(itemView);

            folderLayout = itemView.findViewById(R.id.folderLayout);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);
            tvFolderPath = itemView.findViewById(R.id.tvFolderPath);
        }
    }
}
