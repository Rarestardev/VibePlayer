package com.rarestardev.vibeplayer.Model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "SavedVideos")
public class VideoModel {

    @PrimaryKey (autoGenerate = true)
    private int id;

    private String videoName;

    private String videoPath;
    private long duration;
    private long size;

    public VideoModel(String videoName, String videoPath, long duration, long size) {
        this.videoName = videoName;
        this.videoPath = videoPath;
        this.duration = duration;
        this.size = size;
    }

    @Ignore
    public VideoModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
