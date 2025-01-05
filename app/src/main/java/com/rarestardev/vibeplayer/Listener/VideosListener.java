package com.rarestardev.vibeplayer.Listener;

import android.view.View;

import com.rarestardev.vibeplayer.Model.VideoModel;

public interface VideosListener {

    void onClickVideos(VideoModel videoModel);

    void onLongClickVideos(VideoModel videoModel, View anchorView);
}
