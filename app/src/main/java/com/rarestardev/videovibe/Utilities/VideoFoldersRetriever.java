package com.rarestardev.videovibe.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.rarestardev.videovibe.Enum.OrderStateEnum;
import com.rarestardev.videovibe.Enum.SharedPrefEnum;
import com.rarestardev.videovibe.Enum.SharedPrefKeyEnum;
import com.rarestardev.videovibe.Model.VideoFolderModel;
import com.rarestardev.videovibe.Model.VideoModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class VideoFoldersRetriever {

    public static ArrayList<VideoFolderModel> getVideoFolders(Context context) {
        SecurePreferences securePreferences = new SecurePreferences(context);
        String state = securePreferences.getSecureString(SharedPrefEnum.Order.name(), SharedPrefKeyEnum.BasedState.name());
        String order = "";
        if (state.equals(OrderStateEnum.Newest.name())){
            order = " DESC";
        } else if (state.equals(OrderStateEnum.Oldest.name())) {
            order = " ASC";
        }
        ArrayList<VideoFolderModel> videoFolders = new ArrayList<>();
        HashMap<String, Integer> folderVideoCountMap = new HashMap<>();
        HashSet<String> folderPaths = new HashSet<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media.DATA};
        String sortByOrder = MediaStore.Files.FileColumns.DATE_TAKEN + order;
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, sortByOrder);
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

        if (folder.exists() && folder.isDirectory()){
            File[] files = folder.listFiles();

            if (files != null){
                for (File file : files){
                    if (file.isFile() && (file.getName().endsWith(".mp4")) || (file.getName().endsWith(".avi")) || (file.getName().endsWith(".mov"))){
                        long size = file.length();
                        long duration = getVideoDuration(file);
                        String path = file.getPath();
                        videos.add(new VideoModel(file.getName(),path,file,duration,size));
                    }
                }
            }
        }
        return videos;
    }

    private static long getVideoDuration(File videoFile){
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
