package com.rarestardev.vibeplayer.TabFragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rarestardev.vibeplayer.Adapters.SubtitleColorAdapter;
import com.rarestardev.vibeplayer.Adapters.ViewModeAdapter;
import com.rarestardev.vibeplayer.Database.AppDatabase;
import com.rarestardev.vibeplayer.Database.DatabaseClient;
import com.rarestardev.vibeplayer.Database.dao.SettingsDao;
import com.rarestardev.vibeplayer.Database.entity.SettingsEntity;
import com.rarestardev.vibeplayer.Model.ViewModeLayoutModel;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.Utilities.ClearCache;
import com.rarestardev.vibeplayer.Utilities.Constants;
import com.rarestardev.vibeplayer.Views.settings.AppThemeSettingsActivity;
import com.rarestardev.vibeplayer.Views.settings.SubtitleSettingsActivity;
import com.rarestardev.vibeplayer.databinding.FragmentSettingsBinding;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String mParam1;
    String mParam2;


    private FragmentSettingsBinding binding;
    private final ArrayList<Integer> colorList = new ArrayList<>();
    private final List<ViewModeLayoutModel> modelList = new ArrayList<>();
    private SettingsDao settingsDao;
    private ViewModeAdapter adapter;
    SubtitleColorAdapter subtitleColorAdapter;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        subtitleColorAdapter = new SubtitleColorAdapter(colorList, getContext());
        setViewLayoutManager();

        binding.viewModeLayout.setOnClickListener(view -> {
            if (binding.viewModeRecycler.getVisibility() == View.GONE){
                binding.viewModeRecycler.setVisibility(View.VISIBLE);
                binding.viewModeIcon.setRotation(90f);
            }else {
                binding.viewModeRecycler.setVisibility(View.GONE);
                binding.viewModeIcon.setRotation(0);
            }
        });

        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();
        settingsDao = db.settingsDao();

        updateUi();

        binding.tvAppTheme.setOnClickListener(view ->
                getContext().startActivity(new Intent(getContext(), AppThemeSettingsActivity.class)));

        binding.tvVersionCode.setText(String.format("v %s", getVersionName()));

        binding.tvClearCache.setOnClickListener(view -> {
            ClearCache.clearAppCache(getContext());
            Toast.makeText(getContext(), "Cache cleaned", Toast.LENGTH_SHORT).show();
        });

        binding.tvInfoApp.setOnClickListener(view -> showDialogFeatureApp());

        return binding.getRoot();
    }

    private void updateUi() {
        getAllSettings(settingsEntities -> {
            Log.d(Constants.LOG, "Get all settings Success");
            getViewLayoutManager(settingsEntities.getLayoutMode());

            binding.tvSubtitleSetting.setOnClickListener(view -> {
                Intent subIntent = new Intent(getActivity(), SubtitleSettingsActivity.class);
                subIntent.putExtra("subColor", settingsEntities.getSubtitleColor());
                subIntent.putExtra("subTextSize", settingsEntities.getSubtitleTextSize());
                subIntent.putExtra("subTextStyle", settingsEntities.getSubTextStyle());
                subIntent.putExtra("subTextFont", settingsEntities.getSubTextFont());
                getActivity().startActivity(subIntent);
            });
        });
    }

    private void getAllSettings(Consumer<SettingsEntity> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            SettingsEntity settingsEntities = settingsDao.allSettings();
            getActivity().runOnUiThread(() -> callback.accept(settingsEntities));
        });
    }

    private void getViewLayoutManager(String layoutManager) {
        if (!layoutManager.isEmpty()) {
            adapter.isSelectedLayout(layoutManager);
            binding.viewModeRecycler.refreshDrawableState();
        } else {
            Log.e(Constants.LOG, "SettingFrag LayoutManger is empty : " + layoutManager);
        }
    }

    private void setViewLayoutManager() {
        binding.viewModeRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        modelList.add(new ViewModeLayoutModel(Constants.VIEW_MODE_LAYOUT[2], R.drawable.ic_detail_list));
        modelList.add(new ViewModeLayoutModel(Constants.VIEW_MODE_LAYOUT[1], R.drawable.icon_grid_view));
        modelList.add(new ViewModeLayoutModel(Constants.VIEW_MODE_LAYOUT[0], R.drawable.icon_list_view));

        adapter = new ViewModeAdapter(getContext(), modelList, mode -> {
            Executors.newSingleThreadExecutor().execute(() -> settingsDao.updateLayoutMode(mode));
            adapter.isSelectedLayout(mode);
            binding.viewModeRecycler.refreshDrawableState();
        });

        binding.viewModeRecycler.setAdapter(adapter);
    }

    private void showDialogFeatureApp() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.feature_dialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setCancelable(true);
        dialog.show();

        AppCompatTextView textView = dialog.findViewById(R.id.textView);
    }

    private String getFeature(){
        String content = "";
        try {
            InputStream inputStream = getActivity().getAssets().open("");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            content = new String(buffer, StandardCharsets.UTF_8);

        }catch (IOException e){
            e.printStackTrace();
        }

        return content;
    }

    private String getVersionName(){
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "v1.0";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }
}