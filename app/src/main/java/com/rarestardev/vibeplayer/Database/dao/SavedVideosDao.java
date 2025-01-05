package com.rarestardev.vibeplayer.Database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rarestardev.vibeplayer.Model.VideoModel;

import java.util.List;

@Dao
public interface SavedVideosDao {

    @Insert
    void addedVideos(VideoModel videoModel);

    @Query("SELECT * FROM savedvideos")
    List<VideoModel> getAllSavedVideos();

    @Query("DELETE FROM savedvideos WHERE videoName = :videoName")
    void deleteByVideoName(String videoName);

}
