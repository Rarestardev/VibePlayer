package com.rarestardev.vibeplayer.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.rarestardev.vibeplayer.Model.VideoFolderModel;
import com.rarestardev.vibeplayer.Model.VideoModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class VideoFoldersRetriever {

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(".mp4", ".avi", ".mov", ".mkv", ".flv", ".webm", ".wmv");

    public static ArrayList<VideoFolderModel> getVideoFolders(Context context) {
        ArrayList<VideoFolderModel> videoFolders = new ArrayList<>();
        HashMap<String, Integer> folderVideoCountMap = new HashMap<>();
        HashSet<String> folderPaths = new HashSet<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
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

    public static void getVideoFoldersAsync(Context context, VideoFoldersCallback callback) {
        new AsyncTask<Void, Void, ArrayList<VideoFolderModel>>() {
            @Override
            protected ArrayList<VideoFolderModel> doInBackground(Void... voids) {
                ArrayList<VideoFolderModel> videoFolders = new ArrayList<>();
                HashMap<String, Integer> folderVideoCountMap = new HashMap<>();
                HashSet<String> folderPaths = new HashSet<>();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.Video.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
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

            @Override
            protected void onPostExecute(ArrayList<VideoFolderModel> videoFolders) {
                if (callback != null) {
                    callback.onVideoFoldersFound(videoFolders);
                }
            }
        }.execute();
    }

    public interface VideoFoldersCallback {
        void onVideoFoldersFound(ArrayList<VideoFolderModel> videoFolders);
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
                        videos.add(new VideoModel(file.getName(), path, duration, size));
                    }
                }
            }
        }
        return videos;
    }

    public static void getVideosFromFolderAsync(String folderPath, VideosCallback callback) {
        new AsyncTask<Void, Void, ArrayList<VideoModel>>() {
            @Override
            protected ArrayList<VideoModel> doInBackground(Void... voids) {
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
                                videos.add(new VideoModel(file.getName(), path, duration, size));
                            }
                        }
                    }
                }
                return videos;
            }

            @Override
            protected void onPostExecute(ArrayList<VideoModel> videos) {
                if (callback != null) {
                    callback.onVideosFound(videos);
                }
            }
        }.execute();
    }

    public interface VideosCallback {
        void onVideosFound(ArrayList<VideoModel> videos);
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
