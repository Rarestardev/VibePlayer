package com.rarestardev.vibeplayer.Adapters.ImagesViewerAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rarestardev.vibeplayer.Views.ImagePlayerActivity;
import com.rarestardev.vibeplayer.R;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    private final List<String> imageInfoList;
    private final Context context;
    private int maxItems;

    public ImagesAdapter(List<String> imageInfoList, Context context,int maxItems) {
        this.imageInfoList = imageInfoList;
        this.context = context;
        this.maxItems = maxItems;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_items_view, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(context).load(imageInfoList.get(position)).into(holder.images);
        holder.images.setOnClickListener(view -> {
            Intent intent = new Intent(context, ImagePlayerActivity.class);
            intent.putExtra("selectedPosition", position);
            intent.putStringArrayListExtra("imagePaths", new ArrayList<>(imageInfoList));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(imageInfoList.size(),maxItems);
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView images;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.images);
        }
    }
}
