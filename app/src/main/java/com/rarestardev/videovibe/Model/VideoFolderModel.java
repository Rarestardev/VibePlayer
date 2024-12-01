package com.rarestardev.videovibe.Model;

public class VideoFolderModel {

    private String folderName;
    private String videoPath;
    private int videoCount;

    public VideoFolderModel(String folderName, String videoPath, int videoCount) {
        this.folderName = folderName;
        this.videoPath = videoPath;
        this.videoCount = videoCount;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }
}
