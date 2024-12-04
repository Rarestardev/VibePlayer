package com.rarestardev.videovibe.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.rarestardev.videovibe.R;
import com.rarestardev.videovibe.databinding.ChangeStateRecyclerDialogLayoutBinding;

public class ViewStateCustomDialog extends Dialog {

    private final Context mContext;
    private final ChangeStateRecyclerDialogLayoutBinding binding;
    private final SecurePreferences securePreferences;

    public ViewStateCustomDialog(@NonNull Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ChangeStateRecyclerDialogLayoutBinding.inflate(inflater);
        setContentView(binding.getRoot());
        mContext = context;
        securePreferences = new SecurePreferences(context);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ViewLayoutManager();

        ViewOrderHandle();

    }

    private void ViewLayoutManager() {
        final String linear = "Linear";
        final String grid = "Grid";
        String state = securePreferences.getSecureString(Constants.PREF_LAYOUT_MANAGER, Constants.PREF_LAYOUT_MANAGER_KEY);
        if (state.equals(linear)) {

            binding.cardViewList.setCardBackgroundColor(mContext.getColor(R.color.button_color));
            binding.cardViewGrid.setCardBackgroundColor(mContext.getColor(R.color.cardBgColor));

            binding.cardViewGrid.setOnClickListener(view -> {
                securePreferences.saveSecureString(Constants.PREF_LAYOUT_MANAGER, Constants.PREF_LAYOUT_MANAGER_KEY, grid);
                binding.cardViewList.setCardBackgroundColor(mContext.getColor(R.color.cardBgColor));
                binding.cardViewGrid.setCardBackgroundColor(mContext.getColor(R.color.button_color));
                this.dismiss();
            });


        } else if ((state.equals(grid))) {
            binding.cardViewList.setCardBackgroundColor(mContext.getColor(R.color.cardBgColor));
            binding.cardViewGrid.setCardBackgroundColor(mContext.getColor(R.color.button_color));

            binding.cardViewList.setOnClickListener(view -> {
                securePreferences.saveSecureString(Constants.PREF_LAYOUT_MANAGER, Constants.PREF_LAYOUT_MANAGER_KEY, linear);
                binding.cardViewList.setCardBackgroundColor(mContext.getColor(R.color.button_color));
                binding.cardViewGrid.setCardBackgroundColor(mContext.getColor(R.color.cardBgColor));
                this.dismiss();
            });
        }
    }

    private void ViewOrderHandle() {
        final String desc = "DESC";
        final String asc = "ASC";
        String state = securePreferences.getSecureString(Constants.PREF_ORDER_VIEW, Constants.PREF_ORDER_VIEW_KEY);

        if (state.equals(desc)) {

            binding.btnNewest.setBackgroundColor(mContext.getColor(R.color.button_color));
            binding.btnOldest.setBackgroundColor(mContext.getColor(R.color.cardBgColor));

            binding.btnOldest.setOnClickListener(view -> {
                securePreferences.saveSecureString(Constants.PREF_ORDER_VIEW, Constants.PREF_ORDER_VIEW_KEY, asc);
                binding.btnNewest.setBackgroundColor(mContext.getColor(R.color.cardBgColor));
                binding.btnOldest.setBackgroundColor(mContext.getColor(R.color.button_color));
                this.dismiss();
            });

        } else if (state.equals(asc)) {
            binding.btnNewest.setBackgroundColor(mContext.getColor(R.color.cardBgColor));
            binding.btnOldest.setBackgroundColor(mContext.getColor(R.color.button_color));

            binding.btnNewest.setOnClickListener(view -> {
                securePreferences.saveSecureString(Constants.PREF_ORDER_VIEW, Constants.PREF_ORDER_VIEW_KEY,desc);
                binding.btnNewest.setBackgroundColor(mContext.getColor(R.color.button_color));
                binding.btnOldest.setBackgroundColor(mContext.getColor(R.color.cardBgColor));
                this.dismiss();
            });
        }
    }

    public void onDismissDialogListener(DialogInterface.OnDismissListener onClickListener){
        setOnDismissListener(onClickListener);
    }
}
