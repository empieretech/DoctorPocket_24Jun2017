package com.docpoc.doctor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.R;
import com.docpoc.doctor.webServices.Utils;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

public class AudioPlayer extends Activity implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {
    private static final String TAG = "AudioPlayer";

    public static final String AUDIO_FILE_NAME = "audioFileName";


    private MediaController mediaController;

    private Button btn;
    /**
     * help to toggle between play and pause.
     */
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    /**
     * remain false till media is not completed, inside OnCompletionListener make it true.
     */
    private boolean intialStage = true;

    private Handler handler = new Handler();
    public String url;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.audio_player);
        Utils.logUser();


        url = this.getIntent().getStringExtra("url");

        ((TextView) findViewById(R.id.now_playing_text)).setText("Dr. Pocket");


        try {

            mediaPlayer = MediaPlayer.create(this, Uri.parse(getIntent().getStringExtra("url")));
            mediaController = new MediaController(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setDataSource(this, Uri.parse(getIntent().getStringExtra("url")));


            mediaPlayer.prepare();

        } catch (Exception e) {
            Log.e(TAG, "Could not open file " + url + " for playback.", e);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (null != mediaPlayer) {
                mediaController.hide();
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        mediaController.show();
        return false;
    }

    //--MediaPlayerControl methods----------------------------------------------------
    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e){
            e.printStackTrace();
            return  0;
        }
    }

    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
    //--------------------------------------------------------------------------------

    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared");
        mediaPlayer.start();
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.main_audio_view));

        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }

}