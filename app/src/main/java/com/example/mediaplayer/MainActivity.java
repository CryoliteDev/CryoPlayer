package com.example.mediaplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    private ImageButton mPlay;
    private TextView mTitle;
    private TextView mLength;
    private TextView mCurrentDuration;

    private MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();
    private SeekBar mSeekBar;
    private int currentIndex = 0;
    private boolean isRepeatON = false;
    private boolean isShuffleON = false;

    private ArrayList<Song> songsList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize references to the Views
        mPlay = (ImageButton) findViewById(R.id.play);
        ImageButton mSkip = (ImageButton) findViewById(R.id.skip);
        ImageButton mPrevious = (ImageButton) findViewById(R.id.previous);
        Button mReplay = (Button) findViewById(R.id.replay);
        Button mShuffle = (Button) findViewById(R.id.shuffle);
        mTitle = (TextView) findViewById(R.id.title);
        mLength = (TextView) findViewById(R.id.length);
        mCurrentDuration = (TextView) findViewById(R.id.currentTime);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        ListView mListView = (ListView) findViewById(R.id.playList);

        mediaPlayer = new MediaPlayer();
        AudioModel audioModel = new AudioModel();

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
                        mPlay.setImageResource(R.mipmap.play_btn);
                    }
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        mPlay.setImageResource(R.mipmap.pause_btn);
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
                    mPlay.setImageResource(R.mipmap.pause_btn);
                    currentIndex = currentIndex + 1;
                } else {
                    // plays the first song, if no nxt song in list
                    playSong(0);
                    mPlay.setImageResource(R.mipmap.pause_btn);
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
                    mPlay.setImageResource(R.mipmap.pause_btn);
                    currentIndex = currentIndex - 1;
                } else {
                    //plays last song in the list, if first song is playing
                    playSong(songsList.size() - 1);
                    mPlay.setImageResource(R.mipmap.pause_btn);
                    currentIndex = songsList.size() - 1;
                }
            }
        });

        /**
         * Replay Button
         * Replay's the current song when it finishes playing
         */
        mReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRepeatON) {
                    isRepeatON = false;
                    Toast.makeText(getApplicationContext(),"Repeat is OFF", Toast.LENGTH_SHORT).show();

                    /*
                     * TODO:
                     *  change img to repeat OFF
                     */
                } else {
                    isRepeatON = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    isShuffleON = false;

                    /*
                     * TODO:
                     *  change img to selected/ON
                     *  set shuffle img to NOT selected
                     */
                }
            }
        });

        /**
         * Shuffle Button
         * Plays a random Song from songlist when the
         * current song finishes playing.
         */
        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShuffleON) {
                    isShuffleON = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();

                    /*
                     * TODO:
                     *  change img to shuffle OFF
                     */
                } else {
                    isShuffleON = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    isRepeatON = false;
                    /*
                     * TODO:
                     *  set img to shuffle ON
                     *  set img to repeat OFF
                     */
                }
            }
        });


        /**
         * Playlist adapter to display the
         * list of songs
         */
        PlayListAdapter adapter = new PlayListAdapter(this,songsList,currentIndex);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                currentIndex = position;
                playSong(currentIndex);
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
            mediaPlayer.setDataSource(songsList.get(songIndex).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            String title = songsList.get(songIndex).getTitle();
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
            currentIndex = Objects.requireNonNull(data.getExtras()).getInt("songIndex");
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
     * @param milliseconds - given time to convert
     * @return Hours:Minutes:Seconds
     */
    public String milliSecondToTimer(long milliseconds) {
        String finalString = "";
        String secondString;

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
     * @param currentDuration - Current duration of the song.
     * @param totalDuration - the total length of the song.
     * @return percentage of progressBar
     */
    public int progressPercentage(long currentDuration, long totalDuration) {
        double percentage;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        //calculate percentage
        percentage = (((double)currentSeconds)/totalSeconds) * 100;

        //return percentage
        return (int) percentage;
    }

    /**
     * change progress to timer
     * @param progress - The current progress of the song.
     * @param totalDuration - the total length of the song.
     * @return duration in milliseconds
     */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress)/100) * totalDuration);

        //return duration in milliseconds
        return currentDuration * 1000;
    }



    //Implementation Methods

    /**
     * When songs finishes playing
     * @param mediaPlayer - the media player Object
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        //check if repeat is ON or not
        if (isRepeatON) {
            playSong(currentIndex);
        } else if (isShuffleON) {
            Random randomNumber = new Random();
            currentIndex = randomNumber.nextInt(songsList.size());
            playSong(currentIndex);
        } else {
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
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    /**
     * wen user starts moving the seek bar
     * @param seekBar -
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        myHandler.removeCallbacks(UpdateSongTime);
    }

    /**
     * wen user stops moving the seek barr
     * @param seekBar -
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