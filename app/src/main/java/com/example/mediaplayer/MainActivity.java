package com.example.mediaplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    private Button mPlay;
    private Button mSkip;
    private Button mPrevious;
    private TextView mTitle;
    private TextView mLength;
    private TextView mCurrentDuration;

    private MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();
    private AudioModel audioModel;
    private SeekBar mSeekBar;
    private int currentIndex = 0;

    private static final String TAG = "MainActivity";

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize references to the Views
        mPlay = (Button) findViewById(R.id.play);
        mSkip = (Button) findViewById(R.id.skip);
        mPrevious = (Button) findViewById(R.id.previous);
        mTitle = (TextView) findViewById(R.id.title);
        mLength = (TextView) findViewById(R.id.length);
        mCurrentDuration = (TextView) findViewById(R.id.currentTime);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);

        mediaPlayer = new MediaPlayer();
        audioModel = new AudioModel();

        mSeekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

        songsList = audioModel.getPlayList();
        playSong(0);

        /**
         * Play/Pause Button
         * plays song
         */
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                        mPlay.setText("Play");
                        // change the pause btn to play btn
                    }
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        mPlay.setText("Pause");

                        // change the play btn to pause btn
                    }
                }
            }
        });

        /**
         * Skip Button
         * Plays the next song
         */
        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //plays nxt song in list, if there is one
                if (currentIndex < (songsList.size() - 1)) {
                    playSong(currentIndex + 1);
                    mPlay.setText("Pause");
                    currentIndex = currentIndex + 1;
                } else {
                    // plays the first song, if no nxt song in list
                    playSong(0);
                    mPlay.setText("Pause");
                    currentIndex = 0;
                }
            }
        });

        /**
         * Previous Button
         * Plays previous song
         */
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //play's previous song
                if (currentIndex > 0) {
                    playSong(currentIndex - 1);
                    mPlay.setText("Pause");
                    currentIndex = currentIndex - 1;
                } else {
                    //plays last song in the list, if first song is playin
                    playSong(songsList.size() - 1);
                    mPlay.setText("Pause");
                    currentIndex = songsList.size() - 1;
                }
            }
        });

    }

    /**
     * Plays a song
     * @param songIndex - index of the current song
     */
    private void playSong(int songIndex) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songsList.get(songIndex).get("path"));
            mediaPlayer.prepare();
            mediaPlayer.start();

            String title = songsList.get(songIndex).get("title");
            mTitle.setText(title);

            mSeekBar.setProgress(0);
            mSeekBar.setMax(100);
            updateProgressBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentIndex);
        }

    }

    /**
     * Update timer on seekbar
     */
    private void updateProgressBar() {
        myHandler.postDelayed(UpdateSongTime, 100);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            long totalLength = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();


            mLength.setText(milliSecondToTimer(totalLength));
            mCurrentDuration.setText(milliSecondToTimer(currentDuration));


            int progress = (int) progressPercentage(currentDuration, totalLength);
            mSeekBar.setProgress(progress);
            myHandler.postDelayed(this, 100);
        }
    };

    /**
     * convert milliseconds to to Hours:Minutes:Seconds
     * @param milliseconds
     * @return Hours:Minutes:Seconds
     */
    public String milliSecondToTimer(long milliseconds) {
        String finalString = "";
        String secondString = "";



        //convert total duration into time
        int hours = (int) (milliseconds / (1000*60*60));
        int minutes = (int) (milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);

        if(hours > 0) {
            finalString = hours + ":";
        }

        //if it is single digit
        if(seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }

        finalString = finalString + minutes + ":" + secondString;

        return finalString;
    }

    /**
     * Progress Percentage
     * @param currentDuration
     * @param totalDuration
     * @return percentage of progressBar
     */
    public int progressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        //calculate percentage
        percentage = (((double)currentSeconds)/totalSeconds) * 100;

        //return percentage
        return percentage.intValue();
    }

    /**
     * change progress to timer
     * @param progress
     * @param totalDuration
     * @return duration in milliseconds
     */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress)/100) * totalDuration);

        //return duration in milliseconds
        return currentDuration * 1000;
    }


    //Implementation Methods

    /**
     * When songs finishes playing
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //play nxt song
        if (currentIndex < (songsList.size() -1)) {
            playSong(currentIndex + 1);
            currentIndex = currentIndex + 1;
        } else {
            // first song
            playSong(0);
            currentIndex = 0;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    /**
     * wen user starts moving the seek bar
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        myHandler.removeCallbacks(UpdateSongTime);
    }

    /**
     * wen user stops moving the seek barr
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        myHandler.removeCallbacks(UpdateSongTime);
        int totalDuration = mediaPlayer.getDuration();
        int currentDuration = progressToTimer(seekBar.getProgress(), totalDuration);

        mediaPlayer.seekTo(currentDuration);

        updateProgressBar();
    }
}
