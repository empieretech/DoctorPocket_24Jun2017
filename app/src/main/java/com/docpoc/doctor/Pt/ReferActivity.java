package com.docpoc.doctor.Pt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.R;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

import static android.R.attr.label;

public class ReferActivity extends Activity implements View.OnClickListener, AsynchTaskListner {


    public ImageView iv_back;
     public String referCode;
    public RobottoTextView tvCopy,tvReferLink,tvTotalEarn,tvTotalRefer,tvHowWorks,tvCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_refer);
        Utils.logUser();


      /*  RobottoTextView text = (RobottoTextView) findViewById(R.id.custom_text);
        text.setText("Refer & Earn");
     */   TextView title = (TextView) findViewById(R.id.tvHeader);
        title.setText("Refer & Earn");
        iv_back = (ImageView) findViewById(R.id.imgBack);


        tvReferLink = (RobottoTextView) findViewById(R.id.tvReferLink);
        tvCopy = (RobottoTextView) findViewById(R.id.tvCopy);

        tvCode = (RobottoTextView) findViewById(R.id.tvCode);
        tvTotalEarn = (RobottoTextView) findViewById(R.id.tvTotalEarn);
        tvTotalRefer = (RobottoTextView) findViewById(R.id.tvTotalRefer);
        tvHowWorks = (RobottoTextView) findViewById(R.id.tvHowWorks);



        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(ReferActivity.this, getLink());
            }
        });

        tvHowWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReferralPopup();
            }
        });

        tvReferLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Doctor Pocket Referral System");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getLink());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

            }
        });



        iv_back.setOnClickListener(this);
        Utils.showSimpleSpinProgressDialog(this, "Please Wait..");
        new CallRequest(this).getReferCode(App.user.getUserID());
    }

    public void openReferralPopup() {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.popup_for_refer);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout relClose = (RelativeLayout) dialog.findViewById(R.id.relClose);
        ImageView imgClose = (ImageView) dialog.findViewById(R.id.imgClose);

        relClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("Share Via", text);
                clipboard.setPrimaryClip(clip);
            }
            Utils.showToast("Content Copied", this);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.imgBack:
                finish();

        }

    }

    public String getLink() {
        return "Save $20 for your first consult and  $15 for each referral, Using the Code : '" + referCode
                + "' Enjoy! Register Using the Link:  http://admindoctorpocket.in/ref/?map=" + referCode;


    }

    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {
        Utils.removeSimpleSpinProgressDialog();;
        try {
            if (result != null && !result.isEmpty()) {

                switch (request) {

                    case get_refferalCode:

                        try {
                            JSONObject jObj = new JSONObject(result);
                            if (jObj.getBoolean("success")) {
                                referCode = jObj.getString("refer_code");
                                tvCode.setText(referCode);
                                tvTotalRefer.setText(jObj.getString("no_of_refer"));
                                tvTotalEarn.setText("$" + jObj.getString("total_earn"));
                            }

                        } catch (JSONException e) {
                            Utils.hideProgressDialog();
                            e.printStackTrace();
                        }

                        break;


                }

            } else {
                Utils.removeSimpleSpinProgressDialog();

                Utils.showToast("Please try again later", this);
            }
        } catch (Exception e) {
            Utils.removeSimpleSpinProgressDialog();

            e.printStackTrace();
        }

    }

    @Override
    public void onProgressUpdate(String uniqueMessageId, int progres) {

    }

    @Override
    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS request) {

    }
}
