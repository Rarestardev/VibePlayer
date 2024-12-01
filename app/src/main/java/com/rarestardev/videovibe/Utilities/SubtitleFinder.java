package com.rarestardev.videovibe.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.rarestardev.videovibe.Model.SubtitleFile;
import com.rarestardev.videovibe.Model.SubtitleFolder;
import com.rarestardev.videovibe.Model.VideoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SubtitleFinder {

    public static List<SubtitleFolder> getSubtitleFolders(Context context) {
        List<SubtitleFolder> subtitleFolders = new ArrayList<>();
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.Files.FileColumns.PARENT, MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {"application/x-subrip"};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            HashSet<String> folderPaths = new HashSet<>();
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                File file = new File(filePath);
                String parentFolder = file.getParentFile().getAbsolutePath();
                folderPaths.add(parentFolder);
            }
            cursor.close();
            for (String folderPath : folderPaths) {
                File folder = new File(folderPath);
                subtitleFolders.add(new SubtitleFolder(folder.getName(), folder.getPath()));
            }
        }
        return subtitleFolders;
    }

    public static List<SubtitleFile> getSubtitleFiles(String folderName, Context context) {
        List<SubtitleFile> subtitleFiles = new ArrayList<>();
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? AND " + MediaStore.Files.FileColumns.DATA + " LIKE ?";
        String[] selectionArgs = {"application/x-subrip", "%" + folderName + "%"};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                File file = new File(filePath);
                if (file.getParentFile().getName().equals(folderName)) {
                    subtitleFiles.add(new SubtitleFile(file.getName(),file.getPath()));
                }
            }
            cursor.close();
        }
        return subtitleFiles;
    }

}
