package com.example.mediaplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

public class RawMediaPlayer {
    private Context mContext = null;
    private int mPosition = 0;
    private ArrayList<Integer> mRawList = null;

    public static RawMediaPlayer create(Context context, ArrayList<Integer> rawList) {
        return new RawMediaPlayer(context, rawList);
    }

    private RawMediaPlayer(Context context, ArrayList<Integer> rawList) {

        mContext = context;
        mRawList = rawList;

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                mPosition++;

                if (mRawList.size() <= mPosition)
                    return;

                loadRawFile(mediaPlayer, mRawList.get(mPosition));
            }
        });

        loadRawFile(mediaPlayer, mRawList.get(0));
    }


    private void loadRawFile(MediaPlayer mediaPlayer, int resId) {
        try {
            AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(resId);

            if (afd == null)
                return;

            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();

            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Class from Stackoverflow
 * https://stackoverflow.com/a/29906447
 */
