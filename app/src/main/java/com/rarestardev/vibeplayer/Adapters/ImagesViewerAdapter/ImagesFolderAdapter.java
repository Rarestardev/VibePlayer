package com.rarestardev.vibeplayer.Adapters.ImagesViewerAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.vibeplayer.Utilities.ImagesRetriever;
import com.rarestardev.vibeplayer.Views.ImageViewerActivity;
import com.rarestardev.vibeplayer.Model.ImageFolder;
import com.rarestardev.vibeplayer.R;

import java.util.List;

public class ImagesFolderAdapter extends RecyclerView.Adapter<ImagesFolderAdapter.ImagesViewHolder> {

    private final Context context;
    private final List<ImageFolder> imageFolder;

    public ImagesFolderAdapter(Context context, List<ImageFolder> imageFolder) {
        this.imageFolder = imageFolder;
        this.context = context;
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.image_folder_view, viewGroup, false);
        return new ImagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        ImageFolder folder = imageFolder.get(position);

        String FolderName = folder.getFolderName() + "  " + "( " + folder.getImageCount() + " )";
        holder.tvFolderName.setText(FolderName);

        holder.moreImages.setOnClickListener(view -> {
            Intent intent = new Intent(context, ImageViewerActivity.class);
            intent.putExtra("ImageFolder", folder.getFolderName());
            context.startActivity(intent);
        });

        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 4));

        List<String> imagePaths = ImagesRetriever.getImageInFolder(context, folder.getFolderName());

        ImagesAdapter adapter = new ImagesAdapter(imagePaths, context,20);

        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return imageFolder.size();
    }


    public static class ImagesViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tvFolderName, moreImages;
        RecyclerView recyclerView;

        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);
            moreImages = itemView.findViewById(R.id.moreImages);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }

}
