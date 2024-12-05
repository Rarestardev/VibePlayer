package com.rarestardev.videovibe.Views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.PopupMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.rarestardev.videovibe.Adapters.SubtitleColorAdapter;
import com.rarestardev.videovibe.R;
import com.rarestardev.videovibe.Utilities.Constants;
import com.rarestardev.videovibe.Utilities.SecurePreferences;
import com.rarestardev.videovibe.databinding.ActivitySettingsBinding;

import java.util.ArrayList;

/**
 *
 * @author Rarestardev
 */
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SecurePreferences securePreferences;
    private SharedPreferences sharedPreferences;
    private final ArrayList<Integer> colorList = new ArrayList<>();

    private int textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        binding.backActivity.setOnClickListener(view -> finish());

        sharedPreferences = getSharedPreferences(Constants.PREFS_NUMBER, MODE_PRIVATE);
        securePreferences = new SecurePreferences(this);

        setColorOnSubtitle();
        ViewLayoutManager();
        ViewOrderHandle();
        setSubtitleTextSize();
        SetThemeStateManager();


        // this is demo description for my app.
        final String appDesc = "This powerful video player allows you to play all your video files with high quality. Key features include:\n" +
                "\n" +
                " . Play and pause videos\n" +
                "\n" +
                " . Fast forward and rewind videos\n" +
                "\n" +
                " . Adjust sound and screen size\n" +
                "\n" +
                " . Support for various video formats\n" +
                "\n" +
                " . Subtitle support";


        binding.faq.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Description");
            builder.setMessage(appDesc);
            builder.setCancelable(false);
            builder.setPositiveButton("Thanks!", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.show();
        });

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
            if (checkedId == binding.rbLightMode.getId()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putInt(Constants.KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == binding.rbNightMode.getId()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putInt(Constants.KEY_THEME, AppCompatDelegate.MODE_NIGHT_YES);
            } else if (checkedId == binding.rbSystemDefault.getId()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                editor.putInt(Constants.KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            editor.apply();
        });
    }

    private void setSubtitleTextSize() {
        textSize = sharedPreferences.getInt(Constants.KEY_SELECTED_NUMBER, 0);
        if (textSize != 0) {
            binding.textSizeNumberPicker.setText(String.valueOf(textSize));
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
                textSize = Integer.parseInt(text);
                binding.textSizeNumberPicker.setText(text);
                binding.subtitleDisplay.setTextSize(textSize);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constants.KEY_SELECTED_NUMBER, textSize);
                editor.apply();
                return true;
            });
        });
    }

    private void setColorOnSubtitle() {
        int subtitleColor = securePreferences.getSecureInt(Constants.PREFS_COLOR, Constants.PREFS_COLOR_KEY);
        binding.subtitleDisplay.setTextColor(subtitleColor);

        binding.colorRecycler.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));

        colorList.add(getColor(R.color.subtitleColor1));
        colorList.add(getColor(R.color.subtitleColor2));
        colorList.add(getColor(R.color.subtitleColor3));
        colorList.add(getColor(R.color.subtitleColor4));
        colorList.add(getColor(R.color.subtitleColor5));
        colorList.add(getColor(R.color.subtitleColor6));
        colorList.add(getColor(R.color.subtitleColor7));
        colorList.add(getColor(R.color.subtitleColor8));

        SubtitleColorAdapter adapter = new SubtitleColorAdapter(colorList, color -> {
            binding.subtitleDisplay.setTextColor(color);
            securePreferences.saveSecureInt(Constants.PREFS_COLOR, Constants.PREFS_COLOR_KEY, color);
        }, this);

        binding.colorRecycler.setAdapter(adapter);
        binding.colorRecycler.setHasFixedSize(true);
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