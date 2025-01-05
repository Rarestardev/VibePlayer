package com.rarestardev.vibeplayer.Utilities;

import com.rarestardev.vibeplayer.R;

public class Constants {

    public static final int[] TabsIcon = {R.drawable.ic_videos,R.drawable.ic_pictures,R.drawable.ic_bookmark,R.drawable.icon_settings};
    public static final String[] TabsTitle = {"Videos","Pictures","Favorite","Settings"};

    public static final String VIDEO_FOLDER_PATH_NAME = "folderPath";
    public static final String VIDEO_FOLDER_NAME = "folderName";

    public static final String[] VIEW_MODE_LAYOUT = {"LinearView","GridView","DetailView"};

    public static final String LOG = "MYAPP";

    private Constants() {
        throw new UnsupportedOperationException("Cannot instantiate Constants class");
    }
}
