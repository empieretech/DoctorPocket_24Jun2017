package com.docpoc.doctor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.R;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.MyTagHandler;
import com.docpoc.doctor.webServices.Utils;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import io.fabric.sdk.android.Fabric;

public class HowItWorks extends YouTubeBaseActivity implements OnClickListener,
        YouTubePlayer.OnInitializedListener {


    public static final String API_KEY = "AIzaSyC-oN88-H3I7XI4j2cLkoTrpbQtVl1d994";
    private static final int RECOVERY_DIALOG_REQUEST = 55;
   // public static final String YOUTUBE_VIDEO_CODE = "Amn_QNxoCow";
    public static final String YOUTUBE_VIDEO_CODE = "wpyn4NPv208";
    public RobottoTextView tvHowWorks;

    ImageView iv;
    public WebView webView;


    ImageView buttonPlay;
    private YouTubePlayerView youTubeView;
    public YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_how_it_woks);
        Utils.logUser();


      /*  RobottoTextView text = (RobottoTextView) findViewById(R.id.custom_text);
        text.setText("How it Works");
      */  TextView title = (TextView) findViewById(R.id.tvHeader);
        title.setText("How it Works");
        ImageView iv = (ImageView) findViewById(R.id.imgBack);
        iv.setOnClickListener(this);



        //  iv = (ImageView) findViewById(R.id.img_left_arrow);
        tvHowWorks = (RobottoTextView) findViewById(R.id.tvHowWorks);


        String html = "<br/><font color='#000'><u>How it all works?</u></font><font color='#444444'>\n" +
                "    <ol>\n" +
                "        <li>Begin by editing and personalizing your profile, choose your respected <font color='#000'><u>time zone</u></font>, and add a profile picture.\"\n" +
                "        <li>To book an appointment you must search for for the doctor of your choice under “Search Doctor”.\"\n" +
                "        <li>On the doctor profile's screen you can pick the date and appointment time you desire.\"\n" +
                "        <li>The rate for the virtual consontult is <font color='#000'>selected by each physican for\n" +
                "            20 minutes of their time</font>.\n" +
                "        <li>Once you pick the time of your appointment, you must include a short message so that\n" +
                "            the doctor is briefed for your appointment (this is located under the date and time).\"\n" +
                "        <li>Once the doctor confirms the appointment and accepts the booking,the payment must be made (through <b>PayPal</b> or <b>Stripe</b>)\"\n" +
                "        <li>To pay for an appointment click on your calendar icon (Booking History) that is located to the left of the messaging icon at the bottom of your screen.\"\n" +
                "        <li>When your appointment begins click on the mail icon, select your doctor of choice and begin instant messaging.\"\n" +
                "        <li>For high-risk situations, your physician will advise you to seek urgent medical care at your local hospital and <b>50% of the original payment will be returned*</b>.\"\n" +
                "        <li>10.\tFor any further questions, click the help icon (Icon with the “?”).\"\n" +
                "    </ol>\n" +
                "</font><br/><br/><b><u><font color='#000'>How to prepare for your appoinment?</font></u></b><br/>\n" +
                "<font color='#444444'>Expecting beforehand what the doctor will ask you during the appointment will\n" +
                "    save you time. The following section about pain will give you a brief overview of what questions\n" +
                "    to anticipate. Please prepare the following answers ahead of time in order to optimize the\n" +
                "    quality of your appointment with your doctor.\"\n" +
                "    <br/>It is often difficult to describe pain, because everyone reacts so differently to it. For\n" +
                "    example, a person suffering with intense pain may cope with it better than someone with a lower\n" +
                "    tolerance level for discomfort. It is very important to describe the type of pain to your\n" +
                "    physician as clearly as possible. In order to do that, you must share where it hurts and\n" +
                "    correctly describe the intensity, duration and severity of your pain.\"\n" +
                "    <br/>The main types of information that are useful for a doctor are: </font>\n<br/>" +
                "<ul>\n" +
                "    <li>&nbsp; <font color='#000'>How and when the pain started.</font><font color='#444444'> Give\n" +
                "        Details on how long the pain has persisted, what caused it (following what kind of event)\n" +
                "        and how it started (gradually or suddenly). </font></li>\n" +
                "    <li>&nbsp; <font color='#000'>The location of the pain.</font><font color='#444444'> Show the spot\n" +
                "        (by attaching an image) where it hurts or areas where the pain travels.</font></li>\n" +
                "    <li>&nbsp; <font color='#000'>Pain characteristics.</font><font color='#444444'> Descirbe the\n" +
                "        duration, frequency, intensity (mild, moderate, intense, severe, etc.) and quality of the\n" +
                "        pain (continuous, intermittent, throbbing, etc.). Describing pain is not easy. This is why a\n" +
                "        pain rating scale is a useful evaluation technique.</font></li>\n" +
                "    <li>&nbsp; <font color='#000'>Associated symptoms.</font><font color='#444444'> Tell your doctor\n" +
                "        whether other symptoms (sluggishness, fatigue, fever, etc.) are present.</font></li>\n" +
                "    <li>&nbsp; <font color='#000'>Pain response to activities</font><font color='#444444'> Describe\n" +
                "        activities that increase the pain and also those that relieve it.</font></li>\n" +
                "    <li>&nbsp; <font color='#000'>What improves or worsens the pain.</font><font color='#444444'>\n" +
                "        Describe situations that make your pain better or worse. These can include changes in\n" +
                "        weather conditions, living or working environment, lifestyle, etc.).</font></li>\n" +
                "</ul><br/><br/>\n" +
                "<font color='#000'>* Your best interest is on our mind, however only 50% of the fees will be\n" +
                "    refunded since our doctors run a strict schedule in order to provide their full services.\n" +
                "    Services that are explained in full detail located in \"About Us\".</font></font>";

        tvHowWorks.setText(Html.fromHtml(html, null, new MyTagHandler()));
        tvHowWorks.setTextSize(10);
        iv.setOnClickListener(this);
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);





        // Initializing video player with developer key
        youTubeView.initialize(API_KEY, this);


    }



    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "Error", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        final YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            player.loadVideo(YOUTUBE_VIDEO_CODE);

            // Hiding player controls
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);

            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(String s) {

                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {

                }

                @Override
                public void onVideoEnded() {
                    player.loadVideo(YOUTUBE_VIDEO_CODE);
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
        }


    }
}
