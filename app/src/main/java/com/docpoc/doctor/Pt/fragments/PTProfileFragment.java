package com.docpoc.doctor.Pt.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Pt.EditPatientProfile;
import com.docpoc.doctor.Pt.PTDashboardActivity;
import com.docpoc.doctor.R;
import com.docpoc.doctor.adapter.DrSearchListViewAdapter;
import com.docpoc.doctor.classes.Internet;
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

public class PTProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    public String x;
    public RobottoTextView sp_time;

    public RobottoEditTextView etName, etAge, etGender, etAddress, etEmail, etContact, etCountry;
    public RoundedImageView imgUser;
    public RobottoTextView btnEditProfile;
    public ProgressDialog mprogressDialog;
    public ProgressBar pBar;
    public PTProfileFragment instance;
    public String patientID = "";
    public View v;


    /**
     * Called when the activity is first created.
     */

    public DrSearchListViewAdapter adapter;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
          /*  if (!app.mainUser.getUser_id().isEmpty() && shared != null) {

            }*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_pt_profile, container, false);
        instance = this;
        Fabric.with(getActivity(), new Crashlytics());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Utils.logUser();


        patientID = App.user.getUserID();
        PTDashboardActivity.setTitle("My Profile");

        getReference(v);
        setListener();
        getPTProfileAPI();


        return v;
    }

    public void getReference(View v) {
        etName = (RobottoEditTextView) v.findViewById(R.id.etName);
        etAge = (RobottoEditTextView) v.findViewById(R.id.etAge);
        etGender = (RobottoEditTextView) v.findViewById(R.id.etGender);
        etAddress = (RobottoEditTextView) v.findViewById(R.id.etAddress);
        etEmail = (RobottoEditTextView) v.findViewById(R.id.etEmail);
        etContact = (RobottoEditTextView) v.findViewById(R.id.etContact);
        etCountry = (RobottoEditTextView) v.findViewById(R.id.etCountry);
        btnEditProfile = (RobottoTextView) v.findViewById(R.id.btnEditProfile);
        imgUser = (RoundedImageView) v.findViewById(R.id.imgUser);


        pBar = (ProgressBar) v.findViewById(R.id.pBar);


    }

    public void setListener() {

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(instance.getActivity(), R.array.Time1,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        //  sp_time.setAdapter(staticAdapter);


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i7 = new Intent(instance.getActivity(), EditPatientProfile.class);
                i7.putExtra("x", x);
                i7.putExtra("patientID", patientID);
                startActivity(i7);
            }
        });

    }


    public void getPTProfileAPI() {

        if (!Internet.isAvailable(getActivity())) {
            Internet.showAlertDialog(getActivity(), "Error!", "No Internet Connection", false);

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

                return ServerAPI.generalAPI(getActivity(), urlStr);

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
                mprogressDialog = new ProgressDialog(getActivity());
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
                        etCountry.setText(country);
                        etEmail.setText(email);
                        etContact.setText(mobile);


                        //    tv_time.setText(current_zone_date_time);
                        AQuery aq = new AQuery(getActivity());
                        aq.id(imgUser).progress(pBar)
                                .image(imageURL.replaceAll(" ", "%20"), true, true, 0, R.drawable.dr_about_03, null, 0, 1.0f);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(getActivity(), "Error In API", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }

}