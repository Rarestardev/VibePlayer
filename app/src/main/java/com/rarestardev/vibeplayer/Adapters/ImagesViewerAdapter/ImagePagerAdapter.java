package com.rarestardev.vibeplayer.Adapters.ImagesViewerAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    private final List<String> imagePaths;
    private final Context context;
    private final photoViewListener listener;

    public ImagePagerAdapter(Context context, List<String> imagePaths,photoViewListener listener) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView imageView = new PhotoView(context);
        Glide.with(context).load(new File(imagePaths.get(position))).into(imageView);
        container.addView(imageView);
        imageView.setOnClickListener(view -> listener.onClickListener());
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public interface photoViewListener{
        void onClickListener();
    }
}
