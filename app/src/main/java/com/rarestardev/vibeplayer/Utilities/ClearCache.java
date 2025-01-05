package com.rarestardev.vibeplayer.Utilities;

import android.content.Context;

import java.io.File;

public class ClearCache {

    public static void clearAppCache(Context context) {

        try {

            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean deleteDir(File cacheDir) {
        if (cacheDir != null && cacheDir.isDirectory()) {
            String[] children = cacheDir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(cacheDir, child));
                if (!success) {
                    return false;
                }
            }
            return cacheDir.delete();
        } else if (cacheDir != null && cacheDir.isFile()) {
            return cacheDir.delete();
        } else {
            return false;
        }
    }

}
