package com.docpoc.doctor.Dr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundedImageView;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class DoctorProfile extends Activity implements OnClickListener {
    String x;
    ImageView iv;
    ImageView iv_back;
    RoundedImageView iv_drProfile;

    RobottoTextView tv_ptName, tv_specialiy,
            tv_address,
            tv_morStrtTime, tv_morEndTime,
            tv_eveStartTime,
            tv_eveEndTime, tv_fees, tv_aboutME, tv_timeZone, tv_mb;
    ProgressDialog mprogressDialog;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_doctor_profile);

        Utils.logUser();

        iv = (ImageView) findViewById(R.id.img_left_arrow);

        iv.setOnClickListener(this);

        x = getIntent().getStringExtra("x");
        AngellinaTextView title = (AngellinaTextView) findViewById(R.id.custom_text);
        title.setText(x);

        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);


        getReference();
        setListener();

        getDrProfileAPI();

    }

    public void getReference() {

        //	sp_time=(Spinner) findViewById(R.id.activity_pt_profile_spinner_time);
        tv_ptName = (RobottoTextView) findViewById(R.id.activity_drProfile_tv_name);
        tv_specialiy = (RobottoTextView) findViewById(R.id.activity_drProfile_tv_speciality);
        tv_address = (RobottoTextView) findViewById(R.id.activity_drProfile_tv_address);
        tv_morStrtTime = (RobottoTextView) findViewById(R.id.activity_drProfile_tv_mornigStartTime);

        tv_morEndTime = (RobottoTextView) findViewById(R.id.activity_drProfile_tv_mornigEndTime);
        //	tv_eveStartTime = (RobottoTextView) findViewById(R.id.activity_drProfile_tv_eveStatrTime);
        //	tv_eveEndTime = (RobottoTextView)findViewById(R.id.activity_drProfile_tv_eveEndTime);
        tv_fees = (RobottoTextView) findViewById(R.id.activity_drProfile_tv_fees);

        tv_aboutME = (RobottoTextView) findViewById(R.id.activity_drProfile_tv_aboutMe);
        tv_timeZone = (RobottoTextView) findViewById(R.id.activity_drProfile_tv_timeZone);
        pBar = (ProgressBar) findViewById(R.id.pBar);

        iv_drProfile = (RoundedImageView) findViewById(R.id.activity_drProfile_iv_drProfile);

        iv_drProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i7 = new Intent(DoctorProfile.this, EditDoctorProfile.class);
                i7.putExtra("x", x);
                i7.putExtra("docId", App.user.getUserID());
                i7.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i7.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i7);
            }
        });

        iv_back = (ImageView) findViewById(R.id.img_left_arrow);

    }


    public void setListener() {

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.Time1,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        //	sp_time.setAdapter(staticAdapter);

        iv_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        finish();
    }


    public void getDrProfileAPI() {


        if (!Internet.isAvailable(DoctorProfile.this)) {
            Internet.showAlertDialog(DoctorProfile.this, "Error!", "No Internet Connection", false);

            return;
        }


        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {
                String urlStr = "getDoctorProfile.php?access_token=testermanishrahul234142test&user_id=" + App.user.getUserID();
                return ServerAPI.generalAPI(DoctorProfile.this, urlStr);

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
                mprogressDialog = new ProgressDialog(DoctorProfile.this);
                mprogressDialog.setMessage("Please wait...");
                mprogressDialog.setCancelable(false);
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

                    try {

                        String Fname = jsonResponse.getString("firstName");
                        String LName = jsonResponse.getString("lastName");
                        String email = jsonResponse.getString("userName");
                        String speciality = jsonResponse.getString("speciality");
                        String morStartTime = jsonResponse.getString("morning_appointment_start");
                        String mornEndTime = jsonResponse.getString("morning_appointment_end");
                        String eveStartTime = jsonResponse.getString("eve_appointment_start");
                        String eveEndTime = jsonResponse.getString("eve_appointment_end");

                        String fees = jsonResponse.getString("amount");
                        String address = jsonResponse.getString("address");

                        String timeZone = jsonResponse.getString("current_zone_date_time");
                        String mb = jsonResponse.getString("phoneNumber");
                        String about = jsonResponse.getString("about");
                        String imageURL = jsonResponse.getString("image");
                        String zone = jsonResponse.getString("zone");


                        tv_ptName.setText(Fname + LName);
                        tv_specialiy.setText(speciality);
                        tv_morStrtTime.setText(morStartTime + " To " + mornEndTime);
                        tv_morEndTime.setText(eveStartTime + " To " + eveEndTime);
                        /*tv_eveStartTime.setText(eveStartTime);
						tv_eveEndTime.setText(eveEndTime);*/
                        tv_fees.setText("$" + fees);
                        tv_address.setText(address);
                        tv_timeZone.setText(zone);

                        tv_aboutME.setText(about);


                        AQuery aq = new AQuery(this);
                        aq.id(iv_drProfile).progress(pBar)
                                .image(imageURL.replaceAll(" ", "%20"), true, true, 0, R.drawable.dr_about_03, null, 0, 1.0f);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(DoctorProfile.this, "Error In API", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


}
