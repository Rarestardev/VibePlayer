package com.rarestardev.videovibe.Model;

import java.io.File;

public class VideoModel {

    private String videoName;
    private String videoPath;
    private File videoFile;
    private long duration;
    private long size;

    public VideoModel(String videoName, String videoPath, File videoFile, long duration, long size) {
        this.videoName = videoName;
        this.videoPath = videoPath;
        this.videoFile = videoFile;
        this.duration = duration;
        this.size = size;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }
}
