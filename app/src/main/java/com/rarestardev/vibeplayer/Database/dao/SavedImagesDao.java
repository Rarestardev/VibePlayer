package com.rarestardev.vibeplayer.Database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rarestardev.vibeplayer.Database.entity.ImagesEntity;

import java.util.List;

@Dao
public interface SavedImagesDao {

    @Insert
    void insertImages(ImagesEntity images);

    @Query("DELETE FROM saved_images WHERE image_name = :image_name")
    void deleteImages(String image_name);

    @Query("SELECT image_path FROM saved_images")
    List<String> getAllImages();

    @Query("SELECT COUNT(*) FROM saved_images WHERE image_path = :image_path")
    int isImageExists(String image_path);

}
