package com.docpoc.doctor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.R;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.webServices.Utils;

import io.fabric.sdk.android.Fabric;

public class TermsConditionActivity extends Activity implements View.OnClickListener {

    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_terms_condition);

        Utils.logUser();


        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);
        text.setText("Terms & Conditions");



        iv_back=(ImageView) findViewById(R.id.img_left_arrow);
        iv_back.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.img_left_arrow:
                finish();

        }

    }
}
