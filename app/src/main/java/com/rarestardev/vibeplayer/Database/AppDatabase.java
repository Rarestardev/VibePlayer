package com.rarestardev.vibeplayer.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rarestardev.vibeplayer.Database.dao.SettingsDao;
import com.rarestardev.vibeplayer.Database.entity.ImagesEntity;
import com.rarestardev.vibeplayer.Database.entity.SettingsEntity;
import com.rarestardev.vibeplayer.Model.VideoModel;
import com.rarestardev.vibeplayer.Database.dao.SavedImagesDao;
import com.rarestardev.vibeplayer.Database.dao.SavedVideosDao;

@Database(entities = {SettingsEntity.class, VideoModel.class, ImagesEntity.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SettingsDao settingsDao();
    public abstract SavedVideosDao savedVideosDao();
    public abstract SavedImagesDao savedImagesDao();
}
