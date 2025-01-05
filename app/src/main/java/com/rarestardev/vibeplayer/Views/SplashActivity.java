package com.rarestardev.vibeplayer.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

import com.rarestardev.vibeplayer.Database.dao.SettingsDao;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.R;

import java.util.concurrent.Executors;

/**
 * This activity for custom splash.
 * Just for more beauty.
 *
 * @author Rarestardev
 */

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler();
    private static final int STORAGE_PERMISSION_CODE = 5016;
    private SettingsDao settingsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppDatabase db = DatabaseClient.getInstance(this).getAppDatabase();
        settingsDao = db.settingsDao();

        getCurrentTheme(integer -> {
            if (integer == AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (integer == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });

        CheckPermission();
    }

    private void getCurrentTheme(Consumer<Integer> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            int theme = settingsDao.getCurrentAppTheme();
            runOnUiThread(() -> callback.accept(theme));
        });
    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // android 13 above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {

                // request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO},
                        STORAGE_PERMISSION_CODE);
            } else {
                StartApp();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            } else {
                StartApp();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Main", "Permission Granted");
                StartApp();
            } else {
                Log.e("Main", "Permission Denied");
                finish();
            }

        }
    }

    private void StartApp() {
        // 3000 == 3 second
        final int DurationPerSecond = 3000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                handler.removeCallbacks(this);
            }
        }, DurationPerSecond);
    }
}