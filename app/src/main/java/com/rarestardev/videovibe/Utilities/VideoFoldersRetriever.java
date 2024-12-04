package com.rarestardev.videovibe.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.rarestardev.videovibe.Model.VideoFolderModel;
import com.rarestardev.videovibe.Model.VideoModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class VideoFoldersRetriever {

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(".mp4", ".avi", ".mov", ".mkv", ".flv");

    public static ArrayList<VideoFolderModel> getVideoFolders(Context context, String order) {
        ArrayList<VideoFolderModel> videoFolders = new ArrayList<>();
        HashMap<String, Integer> folderVideoCountMap = new HashMap<>();
        HashSet<String> folderPaths = new HashSet<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, order);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                String folderPath = path.substring(0, path.lastIndexOf("/"));
                folderPaths.add(folderPath);
                folderVideoCountMap.put(folderPath, folderVideoCountMap.getOrDefault(folderPath, 0) + 1);
            }
            cursor.close();
        }
        for (String folderPath : folderPaths) {
            String folderName = folderPath.substring(folderPath.lastIndexOf("/") + 1);
            int videoCount = folderVideoCountMap.get(folderPath);
            videoFolders.add(new VideoFolderModel(folderName, folderPath, videoCount));
        }
        return videoFolders;
    }

    public static ArrayList<VideoModel> getVideosFromFolder(String folderPath) {
        ArrayList<VideoModel> videos = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isSupportedFormat(file.getName())) {
                        long size = file.length();
                        long duration = getVideoDuration(file);
                        String path = file.getPath();
                        videos.add(new VideoModel(file.getName(), path, file, duration, size));
                    }
                }
            }
        }
        return videos;
    }

    private static boolean isSupportedFormat(String fileName) {
        for (String format : SUPPORTED_FORMATS) {
            if (fileName.toLowerCase().endsWith(format)) {
                return true;
            }
        }
        return false;
    }

    private static long getVideoDuration(File videoFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoFile.getAbsolutePath());
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return time != null ? Long.parseLong(time) : 0;
    }
}
