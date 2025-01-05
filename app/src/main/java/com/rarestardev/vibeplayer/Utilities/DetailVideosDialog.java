package com.rarestardev.vibeplayer.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.rarestardev.vibeplayer.Model.VideoModel;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.databinding.DetailVideosDialogBinding;

import java.io.File;

public class DetailVideosDialog {

    private final VideoModel videoModel;
    private final Context context;

    public DetailVideosDialog(VideoModel videoModel, Context context) {
        this.videoModel = videoModel;
        this.context = context;
    }

    public void showDetailDialog() {
        Dialog dialog = new Dialog(context);
        DetailVideosDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context)
                , R.layout.detail_videos_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setCancelable(true);
        dialog.show();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.fromFile(new File(videoModel.getVideoPath())));

        dialogBinding.setVideoName("Name =  " + videoModel.getVideoName());
        dialogBinding.setVideoSize("Size =  " + FileFormater.formatFileSize(videoModel.getSize()));
        dialogBinding.setVideoDuration("Duration =  " + FileFormater.formatTime(videoModel.getDuration()));
        dialogBinding.setVideoFormat("Format =  " + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE));

        String resolution = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) + " x " +
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

        dialogBinding.setVideoResolution("Resolution =  " + resolution);

        dialogBinding.setVideoPath("Path =  " + videoModel.getVideoPath());

        dialogBinding.closestDialog.setOnClickListener(view -> dialog.dismiss());
    }
}
