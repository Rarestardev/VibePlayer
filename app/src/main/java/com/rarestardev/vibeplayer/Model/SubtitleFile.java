package com.rarestardev.vibeplayer.Model;

public class SubtitleFile {

    private String subName;
    private String subPath;

    public SubtitleFile(String subName, String subPath) {
        this.subName = subName;
        this.subPath = subPath;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }
}
