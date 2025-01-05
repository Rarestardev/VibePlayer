package com.rarestardev.vibeplayer.Database;

import android.content.Context;

import androidx.room.Room;


// Singleton
public class DatabaseClient {

    private static DatabaseClient instance;
    private final AppDatabase appDatabase;
    private static final String DB_NAME = "RareStarApp";

    public DatabaseClient(Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
