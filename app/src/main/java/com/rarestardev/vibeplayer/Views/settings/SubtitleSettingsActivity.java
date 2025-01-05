package com.rarestardev.vibeplayer.Views.settings;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.rarestardev.vibeplayer.Database.dao.SettingsDao;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.Adapters.SubtitleColorAdapter;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.databinding.ActivitySubtitleSettingsBinding;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class SubtitleSettingsActivity extends AppCompatActivity {

    private ActivitySubtitleSettingsBinding binding;
    private final ArrayList<Integer> colorList = new ArrayList<>();
    private SubtitleColorAdapter subtitleColorAdapter;
    private SettingsDao settingsDao;

    private static final String[] TEXT_STYLES = {"normal", "bold", "italic"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subtitle_settings);

        subtitleColorAdapter = new SubtitleColorAdapter(colorList, this);

        setColorOnSubtitle();
        setSubtitleTextStyle();
        setSubtitleFont();

        doInitialize();
    }

    private void doInitialize() {
        binding.backActivity.setOnClickListener(view -> finish());

        AppDatabase db = DatabaseClient.getInstance(this).getAppDatabase();
        settingsDao = db.settingsDao();

        int subColor = getIntent().getIntExtra("subColor", 0);
        getSubtitleColor(subColor);

        int subTextSize = getIntent().getIntExtra("subTextSize", 0);
        setSubtitleTextSize(subTextSize);

        String subTextStyle = getIntent().getStringExtra("subTextStyle");
        getSubtitleTextStyle(subTextStyle);

        String subTextFont = getIntent().getStringExtra("subTextFont");
        getSubtitleFont(subTextFont);
    }

    private void setSubtitleTextSize(int textSize) {
        if (textSize != 0) {
            binding.setTextSize(String.valueOf(textSize));
            binding.subtitleDisplay.setTextSize(textSize);
        }

        binding.textSizeNumberPicker.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, view);
            for (int i = 15; i <= 50; i += 5) {
                popupMenu.getMenu().add(String.valueOf(i));
            }

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                String text = (String) menuItem.getTitle();
                int size = Integer.parseInt(text);
                binding.setTextSize(text);
                binding.subtitleDisplay.setTextSize(size);
                Executors.newSingleThreadExecutor().execute(() -> settingsDao.updateSubtitleTextSize(size));
                return true;
            });
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getSubtitleColor(int subtitleColor) {
        if (subtitleColor == 0){
            binding.subtitleDisplay.setTextColor(getColor(R.color.subtitleColor4));
        }else {
            binding.subtitleDisplay.setTextColor(subtitleColor);
            Log.d(Constants.LOG,"Color : " + subtitleColor);
            subtitleColorAdapter.getSelectedColor(subtitleColor);
            subtitleColorAdapter.notifyDataSetChanged();
            binding.colorRecycler.refreshDrawableState();
        }
    }

    private void setColorOnSubtitle() {
        binding.colorRecycler.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));

        colorList.add(getColor(R.color.subtitleColor1));
        colorList.add(getColor(R.color.subtitleColor2));
        colorList.add(getColor(R.color.subtitleColor3));
        colorList.add(getColor(R.color.subtitleColor4));
        colorList.add(getColor(R.color.subtitleColor5));
        colorList.add(getColor(R.color.subtitleColor6));
        colorList.add(getColor(R.color.subtitleColor7));
        colorList.add(getColor(R.color.subtitleColor8));

        subtitleColorAdapter.SubtitleColorListener(color -> {
            binding.subtitleDisplay.setTextColor(color);
            Executors.newSingleThreadExecutor().execute(() -> settingsDao.updateSubtitleColor(color));
            getSubtitleColor(color);
            binding.colorRecycler.refreshDrawableState();
        });

        binding.colorRecycler.setAdapter(subtitleColorAdapter);
    }

    private void getSubtitleTextStyle(String style) {
        if (!style.isEmpty()) {
            binding.setTextStyle(style);

            switch (style) {
                case "normal":
                    binding.subtitleDisplay.setTypeface(Typeface.DEFAULT);
                    break;
                case "bold":
                    binding.subtitleDisplay.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                    break;
                case "italic":
                    binding.subtitleDisplay.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                    break;
            }
            Log.d(Constants.LOG, "SubTextStyle : " + style);
        } else {
            Log.e(Constants.LOG, "getSubtitleTextStyle is empty");
        }
    }

    private void setSubtitleTextStyle() {
        binding.textStyleLayout.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, binding.textStyleIcon);
            popupMenu.getMenu().add(TEXT_STYLES[0]);
            popupMenu.getMenu().add(TEXT_STYLES[1]);
            popupMenu.getMenu().add(TEXT_STYLES[2]);

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                String textStyle = (String) menuItem.getTitle();
                getSubtitleTextStyle(textStyle);
                Executors.newSingleThreadExecutor().execute(() -> settingsDao.updateSubTextStyle(textStyle));
                return true;
            });
        });
    }

    private void getSubtitleFont(String font) {
        if (!font.isEmpty()){
            Typeface typeface = Typeface.create(font,Typeface.NORMAL);
            binding.subtitleDisplay.setTypeface(typeface);
            binding.setTextFont(font);
            Log.d(Constants.LOG,"Font : " + font);
        }else {
            Log.e(Constants.LOG,"font is empty");
        }
    }

    private void setSubtitleFont() {
        binding.textFontLayout.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, binding.textFontIcon);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.font_menu,popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                String fontName = (String) menuItem.getTitle();
                getSubtitleFont(fontName);
                Executors.newSingleThreadExecutor().execute(() -> settingsDao.updateSubTextFont(fontName));
                return true;
            });
        });
    }
}