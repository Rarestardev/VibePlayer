package com.rarestardev.vibeplayer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.vibeplayer.Listener.ColorClickListener;
import com.rarestardev.vibeplayer.R;

import java.util.ArrayList;

public class SubtitleColorAdapter extends RecyclerView.Adapter<SubtitleColorAdapter.SubtitleColorViewHolder> {

    private final ArrayList<Integer> colorsList;
    private ColorClickListener listener;
    private final Context context;
    private int recentColor;

    public SubtitleColorAdapter(ArrayList<Integer> colorsList,Context context) {
        this.colorsList = colorsList;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void SubtitleColorListener(ColorClickListener listener){
        this.listener = listener;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getSelectedColor(int color){
        recentColor = color;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubtitleColorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.color_state_item, viewGroup, false);
        return new SubtitleColorViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull SubtitleColorViewHolder subtitleColorViewHolder, int i) {

        if (recentColor == colorsList.get(i)){
            subtitleColorViewHolder.selectedColor.setVisibility(View.VISIBLE);
        }else {
            subtitleColorViewHolder.selectedColor.setVisibility(View.GONE);
        }


        subtitleColorViewHolder.colorItem.setCardBackgroundColor(colorsList.get(i));
        subtitleColorViewHolder.colorItem.setOnClickListener(view -> listener.onColorStateChange(colorsList.get(i)));

    }

    @Override
    public int getItemCount() {
        return colorsList.size();
    }


    public static class SubtitleColorViewHolder extends RecyclerView.ViewHolder {

        CardView colorItem;
        RelativeLayout selectedColor;

        public SubtitleColorViewHolder(@NonNull View itemView) {
            super(itemView);

            colorItem = itemView.findViewById(R.id.colorItem);
            selectedColor = itemView.findViewById(R.id.selectedColor);
        }
    }

}
