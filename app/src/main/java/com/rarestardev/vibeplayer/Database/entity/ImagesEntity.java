package com.rarestardev.vibeplayer.Database.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "saved_images")
public class ImagesEntity {

    @PrimaryKey (autoGenerate = true)
    private int id;

    private String image_name;
    private String image_path;

    public ImagesEntity(int id, String image_name, String image_path) {
        this.id = id;
        this.image_name = image_name;
        this.image_path = image_path;
    }

    @Ignore
    public ImagesEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
