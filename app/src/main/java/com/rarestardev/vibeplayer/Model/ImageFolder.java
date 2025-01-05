package com.rarestardev.vibeplayer.Model;

public class ImageFolder {

    private String folderName;
    private String path;
    private int imageCount;

    public ImageFolder(String folderName, String path, int imageCount) {
        this.folderName = folderName;
        this.path = path;
        this.imageCount = imageCount;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }
}
