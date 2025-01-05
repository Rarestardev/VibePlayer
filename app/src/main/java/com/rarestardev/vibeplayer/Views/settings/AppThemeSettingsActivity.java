package com.rarestardev.vibeplayer.Views.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;

import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.dao.SettingsDao;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.databinding.ActivityAppThemeSettingsBinding;

import java.util.concurrent.Executors;

public class AppThemeSettingsActivity extends AppCompatActivity {

    private SettingsDao settingsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAppThemeSettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_app_theme_settings);

        binding.backActivity.setOnClickListener(view -> finish());

        AppDatabase db = DatabaseClient.getInstance(this).getAppDatabase();
        settingsDao = db.settingsDao();

        getCurrentTheme(integer -> {
            if (integer == AppCompatDelegate.MODE_NIGHT_NO) {
                binding.rbLightMode.setChecked(true);
            } else if (integer == AppCompatDelegate.MODE_NIGHT_YES) {
                binding.rbNightMode.setChecked(true);
            } else {
                binding.rbSystemDefault.setChecked(true);
            }
        });

        binding.radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rbLightMode.getId()) {
                applyTheme(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == binding.rbNightMode.getId()) {
                applyTheme(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (checkedId == binding.rbSystemDefault.getId()) {
                applyTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });
    }

    private void getCurrentTheme(Consumer<Integer> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            int theme = settingsDao.getCurrentAppTheme();
            runOnUiThread(() -> callback.accept(theme));
        });
    }

    private void applyTheme(int newTheme){
        AppCompatDelegate.setDefaultNightMode(newTheme);
        Executors.newSingleThreadExecutor().execute(() -> settingsDao.updateAppTheme(newTheme));
    }
}