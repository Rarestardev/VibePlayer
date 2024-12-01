package com.rarestardev.videovibe.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.rarestardev.videovibe.Enum.OrderStateEnum;
import com.rarestardev.videovibe.Enum.SharedPrefEnum;
import com.rarestardev.videovibe.Enum.SharedPrefKeyEnum;
import com.rarestardev.videovibe.Enum.ViewStateEnum;
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


        ViewStateHandle();

        ViewOrderHandle();

    }

    private void ViewStateHandle() {
        String state = securePreferences.getSecureString(SharedPrefEnum.LayoutView.name(), SharedPrefKeyEnum.ViewState.name());
        if (state.equals(ViewStateEnum.List.name())) {

            binding.cardViewList.setCardBackgroundColor(mContext.getColor(R.color.button_color));
            binding.cardViewGrid.setCardBackgroundColor(mContext.getColor(R.color.cardBgColor));

            binding.cardViewGrid.setOnClickListener(view -> {
                securePreferences.saveSecureString(SharedPrefEnum.LayoutView.name(), SharedPrefKeyEnum.ViewState.name(), ViewStateEnum.Grid.name());
                binding.cardViewList.setCardBackgroundColor(mContext.getColor(R.color.cardBgColor));
                binding.cardViewGrid.setCardBackgroundColor(mContext.getColor(R.color.button_color));
                this.dismiss();
            });


        } else if ((state.equals(ViewStateEnum.Grid.name()))) {

            binding.cardViewList.setCardBackgroundColor(mContext.getColor(R.color.cardBgColor));
            binding.cardViewGrid.setCardBackgroundColor(mContext.getColor(R.color.button_color));

            binding.cardViewList.setOnClickListener(view -> {
                securePreferences.saveSecureString(SharedPrefEnum.LayoutView.name(), SharedPrefKeyEnum.ViewState.name(), ViewStateEnum.List.name());
                binding.cardViewList.setCardBackgroundColor(mContext.getColor(R.color.button_color));
                binding.cardViewGrid.setCardBackgroundColor(mContext.getColor(R.color.cardBgColor));
                this.dismiss();
            });
        }
    }

    private void ViewOrderHandle() {
        String state = securePreferences.getSecureString(SharedPrefEnum.Order.name(), SharedPrefKeyEnum.BasedState.name());

        if (state.equals(OrderStateEnum.Newest.name())) {

            binding.btnNewest.setBackgroundColor(mContext.getColor(R.color.button_color));
            binding.btnOldest.setBackgroundColor(mContext.getColor(R.color.cardBgColor));

            binding.btnOldest.setOnClickListener(view -> {
                securePreferences.saveSecureString(SharedPrefEnum.Order.name(), SharedPrefKeyEnum.BasedState.name(), OrderStateEnum.Oldest.name());
                binding.btnNewest.setBackgroundColor(mContext.getColor(R.color.cardBgColor));
                binding.btnOldest.setBackgroundColor(mContext.getColor(R.color.button_color));
                this.dismiss();
            });

        } else if (state.equals(OrderStateEnum.Oldest.name())) {
            binding.btnNewest.setBackgroundColor(mContext.getColor(R.color.cardBgColor));
            binding.btnOldest.setBackgroundColor(mContext.getColor(R.color.button_color));

            binding.btnNewest.setOnClickListener(view -> {
                securePreferences.saveSecureString(SharedPrefEnum.Order.name(), SharedPrefKeyEnum.BasedState.name(), OrderStateEnum.Newest.name());
                binding.btnNewest.setBackgroundColor(mContext.getColor(R.color.button_color));
                binding.btnOldest.setBackgroundColor(mContext.getColor(R.color.cardBgColor));
                this.dismiss();

            });
        }
    }
}
