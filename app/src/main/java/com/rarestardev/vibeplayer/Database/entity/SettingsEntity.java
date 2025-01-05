package com.rarestardev.vibeplayer.Database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Settings")
public class SettingsEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String layoutMode;

    private int subtitleColor;

    private int subtitleTextSize;

    private int appTheme;

    private String subTextStyle;

    private String subTextFont;

    public SettingsEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLayoutMode() {
        return layoutMode;
    }

    public void setLayoutMode(String layoutMode) {
        this.layoutMode = layoutMode;
    }

    public int getSubtitleColor() {
        return subtitleColor;
    }

    public void setSubtitleColor(int subtitleColor) {
        this.subtitleColor = subtitleColor;
    }

    public int getSubtitleTextSize() {
        return subtitleTextSize;
    }

    public void setSubtitleTextSize(int subtitleTextSize) {
        this.subtitleTextSize = subtitleTextSize;
    }

    public int getAppTheme() {
        return appTheme;
    }

    public void setAppTheme(int appTheme) {
        this.appTheme = appTheme;
    }

    public String getSubTextStyle() {
        return subTextStyle;
    }

    public void setSubTextStyle(String subTextStyle) {
        this.subTextStyle = subTextStyle;
    }

    public String getSubTextFont() {
        return subTextFont;
    }

    public void setSubTextFont(String subTextFont) {
        this.subTextFont = subTextFont;
    }
}
