package com.rarestardev.vibeplayer.Views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rarestardev.vibeplayer.Database.entity.ImagesEntity;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.Utilities.FileFormater;
import com.rarestardev.vibeplayer.Adapters.ImagesViewerAdapter.ImagePagerAdapter;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SavedImagesDao;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.databinding.ActivityImagePlayerBinding;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

public class ImagePlayerActivity extends AppCompatActivity implements ImagePagerAdapter.photoViewListener {

    private ActivityImagePlayerBinding binding;
    private List<String> imagePaths;
    private String fileName, format, resolution, path;
    private String fileSizeInKB;
    private SavedImagesDao savedImagesDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_player);

        AppDatabase appDatabase = DatabaseClient.getInstance(this).getAppDatabase();
        savedImagesDao = appDatabase.savedImagesDao();

        doInitialize();
        onClicksViews();
    }

    private void doInitialize() {
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        int selectedPosition = getIntent().getIntExtra("selectedPosition", 0);

        checkSavedImages(imagePaths.get(selectedPosition));
        File file = new File(imagePaths.get(selectedPosition));
        binding.imageName.setText(file.getName());

        getImageDetails(imagePaths.get(selectedPosition));

        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imagePaths, this);
        binding.showImagesViewPager.setAdapter(adapter);
        binding.showImagesViewPager.setCurrentItem(selectedPosition);

        binding.showImagesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                getImageDetails(imagePaths.get(i));
                path = imagePaths.get(i);
                checkSavedImages(imagePaths.get(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void checkSavedImages(String path) {
        Executors.newSingleThreadExecutor().execute(() -> {
            int image = savedImagesDao.isImageExists(path);
            runOnUiThread(() -> {
                if (image > 0) {
                    binding.favoriteIcon.setImageResource(R.drawable.icon_bookmark_added);
                    binding.addFavorite.setOnClickListener(view -> Executors.newSingleThreadExecutor().execute(() -> {
                        savedImagesDao.deleteImages(fileName);
                        runOnUiThread(() -> {
                            Log.d(Constants.LOG, "Image removed from database.");
                            binding.favoriteIcon.setImageResource(R.drawable.ic_bookmark);
                        });
                    }));
                } else {
                    binding.favoriteIcon.setImageResource(R.drawable.ic_bookmark);
                    binding.addFavorite.setOnClickListener(view -> Executors.newSingleThreadExecutor().execute(() -> {
                        ImagesEntity images = new ImagesEntity();
                        images.setImage_path(path);
                        images.setImage_name(fileName);

                        savedImagesDao.insertImages(images);
                        runOnUiThread(() -> {
                            Log.d(Constants.LOG, "Image added to database.");
                            binding.favoriteIcon.setImageResource(R.drawable.icon_bookmark_added);
                        });
                    }));
                }
            });
        });
    }

    private void onClicksViews() {
        binding.backActivity.setOnClickListener(view -> finish());
        binding.imageDetails.setOnClickListener(view -> showDialogDetails());
        binding.imageShare.setOnClickListener(view -> shareImage(path));
    }

    private void shareImage(String path) {
        if (!path.isEmpty()) {
            File imageFile = new File(path);
            Uri imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", imageFile);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, imageFile.getName());
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, imageFile.getName()));
        }
    }

    private void showDialogDetails() {
        String msgDialog = "File name = " + fileName + "\n\n" + "Format = " + format + "\n\n" + "Size = "
                + fileSizeInKB + "\n\n" + "Resolution = " + resolution + "\n\n" + "Path = " + path;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Details");
        builder.setMessage(msgDialog);
        builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setCancelable(true);
        builder.show();
    }

    private void getImageDetails(String filePath) {
        File imageFile = new File(filePath);
        path = filePath;
        String imageName = imageFile.getName();
        fileName = imageFile.getName();
        binding.imageName.setText(imageName);
        format = imageName.substring(imageName.lastIndexOf('.') + 1);

        long fileSizeInBytes = imageFile.length();
        fileSizeInKB = FileFormater.formatFileSize(fileSizeInBytes);

        Glide.with(this)
                .asBitmap()
                .load(imageFile)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        resolution = width + "x" + height;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable drawable) {

                    }
                });
    }

    @Override
    public void onClickListener() {
        if (binding.tools.getVisibility() == View.VISIBLE) {
            binding.tools.setVisibility(View.GONE);
            binding.actionBar.setVisibility(View.GONE);
        } else {
            binding.tools.setVisibility(View.VISIBLE);
            binding.actionBar.setVisibility(View.VISIBLE);
        }
    }
}