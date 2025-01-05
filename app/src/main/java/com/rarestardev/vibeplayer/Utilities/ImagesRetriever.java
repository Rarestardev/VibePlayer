package com.rarestardev.vibeplayer.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.rarestardev.vibeplayer.Model.ImageFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagesRetriever {

    public static List<ImageFolder> getImageFolders(Context context) {
        List<ImageFolder> folders = new ArrayList<>();
        Map<String, Integer> folderImageCount = new HashMap<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String folderPath = imagePath.substring(0, imagePath.lastIndexOf("/"));
                if (folderImageCount.containsKey(folderPath)) {
                    folderImageCount.compute(folderPath, (k, count) -> count + 1);
                } else {
                    folderImageCount.put(folderPath, 1);
                }
            }
            cursor.close();
        }
        for (Map.Entry<String, Integer> entry : folderImageCount.entrySet()) {
            String folderPath = entry.getKey();
            String folderName = new File(folderPath).getName();
            folders.add(new ImageFolder(folderName, folderPath, entry.getValue()));
        }
        return folders;
    }

    public static List<String> getImageInFolder(Context context, String folder) {
        List<String> imagePaths = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%" + folder + "%"};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String currentFolderName = new File(path).getParentFile().getName();

                if (currentFolderName.equals(folder)) {
                    imagePaths.add(path);
                }
            }
            cursor.close();
        }
        return imagePaths;
    }

    public static List<String> getImagesFromFolder(Context context,String folderName) {
        List<String> imagePaths = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%" + folderName + "%"};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String currentFolderName = new File(path).getParentFile().getName();

                if (currentFolderName.equals(folderName)) {
                    imagePaths.add(path);
                    Log.i(Constants.LOG,path);
                }
            }
            cursor.close();
        }
        return imagePaths;
    }

}
