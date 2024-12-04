package com.rarestardev.videovibe.Views;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import com.rarestardev.videovibe.R;
import com.rarestardev.videovibe.Utilities.Constants;
import com.rarestardev.videovibe.Utilities.SecurePreferences;
import com.rarestardev.videovibe.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SecurePreferences securePreferences;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        binding.backActivity.setOnClickListener(view -> finish());

        sharedPreferences = getSharedPreferences(Constants.PREFS_NUMBER, MODE_PRIVATE);
        securePreferences = new SecurePreferences(this);

        ViewLayoutManager();
        ViewOrderHandle();
        setSubtitleTextSize();
        SetThemeStateManager();

    }

    private void SetThemeStateManager() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_THEME, MODE_PRIVATE);
        int theme = prefs.getInt(Constants.KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (theme == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.rbLightMode.setChecked(true);
        } else if (theme == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.rbNightMode.setChecked(true);
        } else {
            binding.rbSystemDefault.setChecked(true);
        }

        binding.radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = prefs.edit();
            if (checkedId == binding.rbLightMode.getId()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putInt(Constants.KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO);
            }else if (checkedId == binding.rbNightMode.getId()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putInt(Constants.KEY_THEME, AppCompatDelegate.MODE_NIGHT_YES);
            }else if (checkedId == binding.rbSystemDefault.getId()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                editor.putInt(Constants.KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            editor.apply();
        });
    }

    private void setSubtitleTextSize() {
        int textSize = sharedPreferences.getInt(Constants.KEY_SELECTED_NUMBER, 0);

        binding.textSizeNumberPicker.setMinValue(0);
        binding.textSizeNumberPicker.setMaxValue((50 - 15) / 5);
        String[] displayedValues = new String[(50 - 15) / 5 + 1];
        for (int i = 0; i <= (50 - 15) / 5; i++) {
            displayedValues[i] = String.valueOf(15 + (i * 5));
        }
        binding.textSizeNumberPicker.setDisplayedValues(displayedValues);

        binding.textSizeNumberPicker.setValue(textSize);

        binding.textSizeNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            int selectedNumber = 15 + (newVal * 5);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constants.KEY_SELECTED_NUMBER, selectedNumber);
            editor.apply();
        });
    }

    private void ViewLayoutManager() {
        final String linear = "Linear";
        final String grid = "Grid";
        String state = securePreferences.getSecureString(Constants.PREF_LAYOUT_MANAGER, Constants.PREF_LAYOUT_MANAGER_KEY);
        if (state.equals(linear)) {

            binding.cardViewList.setCardBackgroundColor(getColor(R.color.button_color));
            binding.cardViewGrid.setCardBackgroundColor(getColor(R.color.cardBgColor));

            binding.cardViewGrid.setOnClickListener(view -> {
                securePreferences.saveSecureString(Constants.PREF_LAYOUT_MANAGER, Constants.PREF_LAYOUT_MANAGER_KEY, grid);
                binding.cardViewList.setCardBackgroundColor(getColor(R.color.cardBgColor));
                binding.cardViewGrid.setCardBackgroundColor(getColor(R.color.button_color));
            });


        } else if ((state.equals(grid))) {
            binding.cardViewList.setCardBackgroundColor(getColor(R.color.cardBgColor));
            binding.cardViewGrid.setCardBackgroundColor(getColor(R.color.button_color));

            binding.cardViewList.setOnClickListener(view -> {
                securePreferences.saveSecureString(Constants.PREF_LAYOUT_MANAGER, Constants.PREF_LAYOUT_MANAGER_KEY, linear);
                binding.cardViewList.setCardBackgroundColor(getColor(R.color.button_color));
                binding.cardViewGrid.setCardBackgroundColor(getColor(R.color.cardBgColor));
            });
        }
    }

    private void ViewOrderHandle() {
        final String desc = "DESC";
        final String asc = "ASC";
        String state = securePreferences.getSecureString(Constants.PREF_ORDER_VIEW, Constants.PREF_ORDER_VIEW_KEY);

        if (state.equals(desc)) {

            binding.btnNewest.setBackgroundColor(getColor(R.color.button_color));
            binding.btnOldest.setBackgroundColor(getColor(R.color.cardBgColor));

            binding.btnOldest.setOnClickListener(view -> {
                securePreferences.saveSecureString(Constants.PREF_ORDER_VIEW, Constants.PREF_ORDER_VIEW_KEY, asc);
                binding.btnNewest.setBackgroundColor(getColor(R.color.cardBgColor));
                binding.btnOldest.setBackgroundColor(getColor(R.color.button_color));
            });

        } else if (state.equals(asc)) {
            binding.btnNewest.setBackgroundColor(getColor(R.color.cardBgColor));
            binding.btnOldest.setBackgroundColor(getColor(R.color.button_color));

            binding.btnNewest.setOnClickListener(view -> {
                securePreferences.saveSecureString(Constants.PREF_ORDER_VIEW, Constants.PREF_ORDER_VIEW_KEY, desc);
                binding.btnNewest.setBackgroundColor(getColor(R.color.button_color));
                binding.btnOldest.setBackgroundColor(getColor(R.color.cardBgColor));
            });
        }
    }
}