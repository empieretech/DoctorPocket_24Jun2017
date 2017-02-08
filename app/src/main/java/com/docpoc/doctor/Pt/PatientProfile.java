package com.docpoc.doctor.Pt;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Messages;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoEditTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundedImageView;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;


public class PatientProfile extends Activity implements OnClickListener {
    String x;
    RobottoTextView sp_time;
    ImageView iv_back;
    RobottoEditTextView etName, etAge,etGender,etAddress,etEmail,etContact,etCountry;
    RoundedImageView imgUser;
    RobottoTextView btnEditProfile;
    ProgressDialog mprogressDialog;
    ProgressBar pBar;

    String patientID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_patient_profile);
        Utils.logUser();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        x = getIntent().getStringExtra("x");
        patientID = getIntent().getStringExtra("patientID");


    /*    RobottoTextView text = (RobottoTextView) findViewById(R.id.custom_text);
        text.setText("My Profile");
     */   TextView title = (TextView) findViewById(R.id.tvHeader);
        title.setText("My Profile");


        getReference();
        setListener();

        getPTProfileAPI();

    }

    public void getReference() {
        etName = (RobottoEditTextView)  findViewById(R.id.etName);
        etAge = (RobottoEditTextView)  findViewById(R.id.etAge);
        etGender = (RobottoEditTextView)  findViewById(R.id.etGender);
        etAddress = (RobottoEditTextView)  findViewById(R.id.etAddress);
        etEmail = (RobottoEditTextView)  findViewById(R.id.etEmail);
        etContact = (RobottoEditTextView)  findViewById(R.id.etContact);
        etCountry = (RobottoEditTextView)  findViewById(R.id.etCountry);
        btnEditProfile = (RobottoTextView)  findViewById(R.id.btnEditProfile);
        imgUser = (RoundedImageView) findViewById(R.id.imgUser);

        iv_back = (ImageView) findViewById(R.id.imgBack);

        pBar = (ProgressBar) findViewById(R.id.pBar);
        iv_back.setOnClickListener(this);



    }



    public void setListener() {

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.Time1,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        //  sp_time.setAdapter(staticAdapter);

        iv_back.setOnClickListener(this);



        btnEditProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i7 = new Intent(PatientProfile.this, EditPatientProfile.class);
                i7.putExtra("x", x);
                i7.putExtra("patientID", patientID);
                startActivity(i7);
            }
        });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.imgBack:
                finish();

        }
    }


    public void getPTProfileAPI() {

        if (!Internet.isAvailable(PatientProfile.this)) {
            Internet.showAlertDialog(PatientProfile.this, "Error!", "No Internet Connection", false);

            return;
        }


        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {

                String urlStr = "";
                try {
                    if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR))
                        urlStr = "getProfile.php?access_token=testermanishrahul234142test&user_id=" + patientID;

                    else
                        urlStr = "getProfile.php?access_token=testermanishrahul234142test&user_id=" + App.user.getUserID();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                return ServerAPI.generalAPI(PatientProfile.this, urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
                onProfileResult(result);
            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(PatientProfile.this);
                mprogressDialog.setMessage("Please wait...");
                mprogressDialog.show();
            }

            @Override
            public void onTaskCancelled(int taskId) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
            }
        });
    }


    public void onProfileResult(JSONObject jsonResponse) {


        if (jsonResponse != null) {
            try {


                if (jsonResponse.getString("status").equals("1")) {
                    Log.i("TAG", "TAG RESULT : " + jsonResponse.toString());
                    try {

                        String name = jsonResponse.getString("name");
                        String email = jsonResponse.getString("userName");
                        String age = jsonResponse.getString("DOB");
                        String gender = jsonResponse.getString("gender");
                        String address = jsonResponse.getString("address");
                        String country = jsonResponse.getString("country");
                        String state = jsonResponse.getString("state");

                        String mobile = jsonResponse.getString("mobile");
                        String current_zone_date_time = jsonResponse.getString("current_zone_date_time");
                        String zone = jsonResponse.getString("zone");
                        String message = jsonResponse.getString("message");
                        String imageURL = jsonResponse.getString("image");

/*
                        List<String> array = Arrays.asList(getResources().getStringArray(R.array.Time1));

                        for (int i = 0; i < array.size(); i++) {
                            if (array.get(i).equalsIgnoreCase(current_zone_date_time)) {
                                sp_time.setSelection(i);
                            }
                        }*/

                      //  sp_time.setText(zone);

                        etName.setText(name);
                        etAge.setText(age);
                        etGender.setText(gender);
                        etAddress.setText(address);
                        etEmail.setText(email);
                        etContact.setText(mobile);
                        etCountry.setText(country);


                        //    tv_time.setText(current_zone_date_time);
                        AQuery aq = new AQuery(this);
                        aq.id(imgUser).progress(pBar)
                                .image(imageURL.replaceAll(" ", "%20"), true, true, 0, R.drawable.dr_about_03, null, 0, 1.0f);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(PatientProfile.this, "Error In API", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


}

