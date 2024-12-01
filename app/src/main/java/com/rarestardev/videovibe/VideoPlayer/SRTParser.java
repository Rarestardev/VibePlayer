package com.rarestardev.videovibe.VideoPlayer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SRTParser {

    public static class Subtitle{
        public long startTime;
        public long endTime;
        public String text;

        public Subtitle(long startTime, long endTime, String text) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.text = text;
        }
    }

    public List<Subtitle> parseSRT(String srtFilePath){

        List<Subtitle> subtitles = new ArrayList<>();

        try {

            BufferedReader reader = new BufferedReader(new FileReader(srtFilePath));
            String line;

            while ((line = reader.readLine()) != null){

                if (line.isEmpty()){
                    continue;
                }

                // read number srt
                int index = Integer.parseInt(line.trim());

                line = reader.readLine();

                String[] times = line.split("->");
                long startTime = parseSRTTime(times[0]);
                long endTime = parseSRTTime(times[1]);

                StringBuilder textBuilder = new StringBuilder();

                while ((line = reader.readLine()) != null && !line.isEmpty()){
                    textBuilder.append(line).append("\n");
                }

                Subtitle subtitle = new Subtitle(startTime,endTime,textBuilder.toString().trim());

                subtitles.add(subtitle);
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return subtitles;
    }

    private long parseSRTTime(String timeStr){
        String[] parts = timeStr.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        String[] secondAndMillis = parts[2].split(",");
        int seconds = Integer.parseInt(secondAndMillis[0]);
        int millis = Integer.parseInt(secondAndMillis[1]);
        return (hour * 3600L + minute * 60L + seconds) * 1000 + millis;
    }

}
