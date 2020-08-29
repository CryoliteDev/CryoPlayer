package com.example.mediaplayer;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class AudioModel {

    File internalStorage = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS);
    String MEDIA_PATH = internalStorage.toString();

    private ArrayList<HashMap<String, String>> songList = new ArrayList<HashMap<String, String>>();

    //Constructor
    public AudioModel() {

    }

    /**
     * Reads all mp3 files from the raw folder
     * Saves the song details in array list
     * @return songs array list
     */
    public ArrayList<HashMap<String,String>> getPlayList(){

        File directory = new File(MEDIA_PATH);

        if (directory.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : directory.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("title", file.getName());
                song.put("path", file.getPath());
                songList.add(song);
            }
        }


        return songList;
    }

    /**
     * Filter out the .mp3 files
     */
    static class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File file, String ext) {
            return (ext.endsWith(".mp3") || ext.endsWith(".MP3"));
        }
    }
}
