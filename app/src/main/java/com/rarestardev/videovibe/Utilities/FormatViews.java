package com.rarestardev.videovibe.Utilities;

import java.util.Locale;

public class FormatViews {

    public static String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatDuration(int duration) {
        int h = duration / 3600;
        int m = (duration - h * 3600) / 60;
        int s = duration - (h * 3600 + m * 60);
        String durationValue;
        if (h == 0) {
            durationValue = String.format(Locale.getDefault(), "%1$02d:%2$02d", m, s);
        } else {
            durationValue = String.format(Locale.getDefault(), "%1$d:%2$02d:%3$02d", h, m, s);
        }
        return durationValue;
    }

}
