package com.docpoc.doctor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.R;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.webServices.Utils;

import io.fabric.sdk.android.Fabric;

public class Help extends Activity implements OnClickListener {
    ImageView iv;
    public WebView webView;

    public MediaController md;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_how_it_wors);
        Utils.logUser();


        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);
        text.setText("Help");

        iv = (ImageView) findViewById(R.id.img_left_arrow);

        iv.setOnClickListener(this);


        webView = (WebView) findViewById(R.id.videoView);

        WebView wv;

        webView.loadUrl("file:///android_asset/faq.html");


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.img_left_arrow:
                finish();
                break;
        }


    }
}
