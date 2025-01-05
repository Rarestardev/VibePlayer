package com.rarestardev.vibeplayer.Database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rarestardev.vibeplayer.Database.entity.SettingsEntity;

@Dao
public interface SettingsDao {

    /**
     * this method for init default settings for start app
     *
     * @param settings SettingsEntity class for database dao
     */

    @Insert
    void initDefaultSettings(SettingsEntity settings);

    @Query("SELECT * FROM settings")
    SettingsEntity allSettings();

    @Query("SELECT layoutMode FROM settings LIMIT 1")
    String getLayoutMode();

    @Query("SELECT subTextFont FROM settings LIMIT 1")
    String getSubFont();

    @Query("SELECT subTextStyle FROM settings LIMIT 1")
    String getSubStyle();

    @Query("SELECT subtitleTextSize FROM settings LIMIT 1")
    int getSubtitleTextSize();

    @Query("SELECT appTheme FROM settings LIMIT 1")
    int getCurrentAppTheme();

    @Query("SELECT subtitleColor FROM settings LIMIT 1")
    int getSubtitleColor();

    @Query("UPDATE settings SET layoutMode = :layoutMode")
    void updateLayoutMode(String layoutMode);

    @Query("UPDATE settings SET subtitleColor = :subColor")
    void updateSubtitleColor(int subColor);

    @Query("UPDATE settings SET subtitleTextSize = :subTextSize")
    void updateSubtitleTextSize(int subTextSize);

    @Query("UPDATE settings SET appTheme = :theme")
    void updateAppTheme(int theme);

    @Query("UPDATE settings SET subTextStyle = :subTextStyle")
    void updateSubTextStyle(String subTextStyle);

    @Query("UPDATE settings SET subTextFont = :subTextFont")
    void updateSubTextFont(String subTextFont);
}
