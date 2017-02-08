package com.docpoc.doctor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.R;
import com.docpoc.doctor.webServices.Utils;

import io.fabric.sdk.android.Fabric;

public class VideoPlayerController extends Activity {

    public ProgressDialog progDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.video_layout);

        Utils.logUser();



        VideoView videoView = (VideoView) findViewById(R.id.VideoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
// Set video link (mp4 format )
        Uri video = Uri.parse(getIntent().getStringExtra("url"));
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);

        videoView.requestFocus();
        videoView.start();
        progDialog = ProgressDialog.show(this, "Please wait ...", "Retrieving data ...", true);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub


                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        if (percent == 100) {
                            progDialog.dismiss();
                        } else {
                            if (!progDialog.isShowing()) {
                                progDialog.show();
                            }
                        }
                    }
                });
            }
        });


    }
}