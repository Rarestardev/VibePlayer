package com.rarestardev.vibeplayer.Views;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.rarestardev.vibeplayer.TabFragments.SettingsFragment;
import com.rarestardev.vibeplayer.TabFragments.VideosFragment;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SettingsDao;
import com.rarestardev.vibeplayer.Database.entity.SettingsEntity;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.TabFragments.BookmarkFragment;
import com.rarestardev.vibeplayer.TabFragments.PicturesFragment;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.databinding.ActivityMainBinding;

import java.util.concurrent.Executors;

/**
 * setup layouts with tab layout and fragments for
 * show video,image,favorite and setting app.
 * initialize first data for setup first settings.
 *
 * @author Rarestar
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SettingsDao settingsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        settingsDao = db.settingsDao();

        checkAndInitializeSettings();

        doInitialize();
        SetupTabViewPagers();
    }

    private void doInitialize() {
        binding.imageViewSearch.setOnClickListener(view -> {
            Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(searchIntent);
        });
    }

    private void checkAndInitializeSettings() {
        Executors.newSingleThreadExecutor().execute(() -> {
            SettingsEntity settingsEntities = settingsDao.allSettings();
            if (settingsEntities == null) {
                SettingsEntity settings = new SettingsEntity();
                settings.setLayoutMode(Constants.VIEW_MODE_LAYOUT[2]);
                settings.setSubtitleColor(getColor(R.color.subtitleColor4)); // default subtitle text color
                settings.setSubtitleTextSize(15); // default text size
                settings.setSubTextStyle("normal"); // default font style
                settings.setSubTextFont("sans-serif"); // default font text
                settings.setAppTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM); // default theme
                settingsDao.initDefaultSettings(settings);
            }
        });
    }

    /**
     * this method set tabs with fragment , setup title & icons tab.
     * set default tab HomeFragments
     */
    private void SetupTabViewPagers() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(Constants.TabsIcon[0]).setText(Constants.TabsTitle[0]));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(Constants.TabsIcon[1]).setText(Constants.TabsTitle[1]));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(Constants.TabsIcon[2]).setText(Constants.TabsTitle[2]));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(Constants.TabsIcon[3]).setText(Constants.TabsTitle[3]));

        // Default Page
        replaceFragment(new VideosFragment());
        Drawable icon = binding.tabLayout.getTabAt(0).getIcon();
        if (icon != null) {
            icon.setTint(getColor(R.color.button_color));
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new VideosFragment();
                        break;
                    case 1:
                        selectedFragment = new PicturesFragment();
                        break;
                    case 2:
                        selectedFragment = new BookmarkFragment();
                        break;
                    case 3:
                        selectedFragment = new SettingsFragment();
                        break;
                }
                if (selectedFragment != null) {
                    replaceFragment(selectedFragment);
                }

                Drawable icon = tab.getIcon();
                if (icon != null) {
                    icon.setTint(getColor(R.color.button_color));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Drawable icon = tab.getIcon();
                if (icon != null) {
                    icon.setTint(getColor(R.color.button_color));
                }
            }
        });
    }


    // Opens Fragments
    // Default : HomeFragment()
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_layout, fragment);
        transaction.commit();
    }
}