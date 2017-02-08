package com.docpoc.doctor;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.R;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.MyTagHandler;
import com.docpoc.doctor.webServices.Utils;

import io.fabric.sdk.android.Fabric;

public class AboutUs extends Activity implements OnClickListener {
    ImageView iv;
    public WebView webView;
    public RobottoTextView tvAboutus;
    public MediaController md;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_about_us);

        Utils.logUser();

        TextView title = (TextView) findViewById(R.id.tvHeader);
        title.setText("About Us");


        iv = (ImageView) findViewById(R.id.imgBack);
        tvAboutus = (RobottoTextView) findViewById(R.id.tvAboutus);
        //    tvAboutus.setText(Html.fromHtml(getString(R.string.title_activity_about_us)));
        iv.setOnClickListener(this);


        String html = "<b><font color='#000000'>Our Goal</font></b><br/><font color='#555555'>How would you feel knowing you have a doctor at your fingertips anywhere you go? Doctor Pocket provides you with the best medical professionals in the comfort of your own home or even on-the-go. We offer you a mobile clinic on your smartphone and tablet. Clinics are unappealing with their long waits, travel time, and inconvenient appointment schedule. Let alone increasing susceptibility to germs and infections. Why go through all this trouble? Our interactive platform allows you to chat with your doctors and attach high resolution images that you can share with them. Don&apos;t settle for less than Doctor Pocket, the platform that offers you the luxury to choose specialists based on your convenience.</font><br/><font color='#000000'><b><br/>Things we can help you with:</b><br/> <u>Cough, Colds, Alergies &amp; Sore Throats</u></font><br/><font color='#555555'> With symptoms like a runny nose, dry/moist coughs, and swollen eyes, Doctor Pocket can help find the root of the problem. Your doctor will ask for the patient\\&apos; history such as when they started noticing certain symptoms and their exacerbating and alleviating factors. He/she might also ask for a picture to further assess your situation. Attaching high resolution images gives valuable additional information from the diagnostic point of view. These pictures serve as a virtual experience of a doctor at your fingertips. Doctor Pocket provides you with all your answers at the comfort of your own home, without the exposure to germs and losing time at the doctor\\&apos;s clinic.\n" +
                "   </font><br/><br/><font color='#000000'><u>Trips &amp; Injuries</u></font><br/><font color='#555555'> Doctor Pocket offers medical advice in situations where local doctors cannot even speak your language. The doctors can also answer questions about the medical essentials to bring with you before you travel. The same doctor can assist you with any additional medical issues during travel that you may encounter. Doctor Pocket provides help with minor injuries such as sprains, bruises, and soreness. Severe injuries such as fractured bones and significant bodily trauma cannot be assessed; you must head directly to the emergency room. However, our doctors can provide a means for screening emergent cases that are not obvious to the neutral eye. Your doctor will advise you to go to the hospital if he/she has any doubt about the severity of your injury. Doctor Pocket can thereby save you a trip to the clinic through our communication platform that includes sharing high resolution images and by performing specific maneuvers requested by your doctor that may confirm a minor injury. Using Doctor Pocket, you can be reassured of the severity of your injury. The medical professionals on Doctor Pocket have your best interest in mind, how cool would it be to have a doctor you trust in your pocket?\n" +
                "   </font><br/><br/><font color='#000000'><u>Eye &amp; Skin Conditions</u></font><br/><font color='#555555'> Eye conditions can be very critical. Doctor Pocket offers assistance with minor eye complaints such as styes and pink/red eyes that can be handled with over the counter (OTC) products. In addition, if deemed necessary, your doctor will advise you which compresses would be helpful (hot or cold). The high resolution image sharing system at Doctor Pocket enables the doctor to see your eye as if he is actually present, in the comfort of your own home. Any severe condition that needs medical help such as sharp objects in your eye, you should directly seek urgent medical care.  Skin conditions such as rashes, burns, bites, and allergic reactions are just examples of how Doctor Pocket can help you. Simple procedures such as application of ice can help cool down a rash. The patients can also attach high resolution images of the rashes that enable the doctors to see more than in their own naked eye. Using Doctor Pocket saves the patient the burden of time that they would have to wait to see a specialist. Other things we give professional medical advice for are: <br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Bruises<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Headaches<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Diarrhea<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Vomiting <br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Urinary tract infections<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Sexually transmitted diseases<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Gynecologic conditions<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Body aches <br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Earaches<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Nasal congestion<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Fever<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Analyzing your blood tests<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Assessing medical imaging (X-ray, CT-scans, MRI, only those that have been verified by a certified radiologist)\n" +
                "   </font><br/><br/><font color='#000000'><u>Things We can NOT Help you With</u></font><br/><font color='#555555'> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Cancer<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Chronic conditions that require scheduled clinic visits, follow-ups, and physical exams<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Psychiatry and mental health<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Emergent cases, severe trauma<br/> \t&nbsp;&#8226;&nbsp;&nbsp;&nbsp; Our doctors hold the right to cancel your appointment if they identify your case as an emergency, in which case you will receive a 50% refund</font>\n";
        tvAboutus.setText(Html.fromHtml(html, null, new MyTagHandler()));
/*
        webView = (WebView) findViewById(R.id.videoView);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl("http://www.youtube.com/embed/Amn_QNxoCow?autoplay=1&vq=small");*/


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
