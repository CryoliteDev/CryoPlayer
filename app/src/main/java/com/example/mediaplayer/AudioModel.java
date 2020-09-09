package com.example.mediaplayer;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Objects;

public class AudioModel {

    private Song song;

    //Sets the MEDIA_PATH file to the Music folder from the phone.
    File internalStorage = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC);
    String MEDIA_PATH = internalStorage.toString();

    private ArrayList<Song> songList = new ArrayList<>();

    //Constructor
    public AudioModel() {

    }

    /**
     * Reads all mp3 files from the folder
     * Saves the song details in array list
     * @return songs array list
     */
    public ArrayList<Song> getPlayList() {
        File directory = new File(MEDIA_PATH);

        if (Objects.requireNonNull(directory.listFiles(new FileExtensionFilter())).length > 0) {
            for (File file : Objects.requireNonNull(directory.listFiles(new FileExtensionFilter()))) {
                songList.add(new Song(file.getName(), file.getPath()));
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