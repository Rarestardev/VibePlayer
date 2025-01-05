package com.rarestardev.vibeplayer.Views;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.vibeplayer.Adapters.ImagesViewerAdapter.ImagesAdapter;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.Utilities.ImagesRetriever;

import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        AppCompatImageView backActivity = findViewById(R.id.backActivity);
        AppCompatTextView tvTitle = findViewById(R.id.tvTitle);
        AppCompatTextView tvNoResult = findViewById(R.id.tvNoResult);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        String folder = getIntent().getStringExtra("ImageFolder");
        tvTitle.setText(folder);

        backActivity.setOnClickListener(view -> finish());

        List<String> imageList = ImagesRetriever.getImagesFromFolder(this, folder);

        if (imageList.isEmpty()) {
            tvNoResult.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoResult.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        ImagesAdapter adapter = new ImagesAdapter(imageList, this,imageList.size());
        recyclerView.setAdapter(adapter);
    }
}