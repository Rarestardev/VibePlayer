package com.rarestardev.vibeplayer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.vibeplayer.Model.ViewModeLayoutModel;
import com.rarestardev.vibeplayer.R;

import java.util.List;

public class ViewModeAdapter extends RecyclerView.Adapter<ViewModeAdapter.ViewModeHolder> {

    private final Context context;
    private final List<ViewModeLayoutModel> viewModeList;
    private final ViewModeListener listener;
    private String isSelected;

    public ViewModeAdapter(Context context, List<ViewModeLayoutModel> viewModeList, ViewModeListener listener) {
        this.context = context;
        this.viewModeList = viewModeList;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void isSelectedLayout(String isSelected){
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewModeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_mode_item,viewGroup,false);
        return new ViewModeHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewModeHolder holder, int i) {
        ViewModeLayoutModel layoutModel = viewModeList.get(i);

        if (isSelected != null && isSelected.equals(layoutModel.getViewModeName())) {
            holder.modeLayout.setCardBackgroundColor(context.getColor(R.color.button_color));
        }else if (isSelected != null && !isSelected.equals(layoutModel.getViewModeName())){
            holder.modeLayout.setCardBackgroundColor(context.getColor(R.color.cardBgColor));
        }

        holder.tvModeName.setText(layoutModel.getViewModeName());
        holder.imageViewIcon.setImageDrawable(context.getDrawable(layoutModel.getViewModeIcon()));
        holder.modeLayout.setOnClickListener(view -> {
            listener.onClickLayoutMode(layoutModel.getViewModeName());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return viewModeList.size();
    }


    public interface ViewModeListener{
        void onClickLayoutMode(String mode);
    }

    public static class ViewModeHolder extends RecyclerView.ViewHolder{

        CardView modeLayout;
        AppCompatImageView imageViewIcon;
        AppCompatTextView tvModeName;

        public ViewModeHolder(@NonNull View itemView) {
            super(itemView);

            modeLayout = itemView.findViewById(R.id.modeLayout);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            tvModeName = itemView.findViewById(R.id.tvModeName);
        }
    }
}
