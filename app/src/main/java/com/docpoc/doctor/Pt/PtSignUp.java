package com.docpoc.doctor.Pt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Dr.DrDashboardActivity;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Country;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.classes.State;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoEditTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import io.fabric.sdk.android.Fabric;

public class PtSignUp extends Activity {

    RobottoTextView tvNext, tvSkipSignUp;

    RobottoEditTextView et_Name, et_pass, et_contact, et_emai;

    ProgressDialog mprogressDialog;

    String fb_id = "";
    public PtSignUp staticThis;
    String name, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_pt_sign_up);
        Utils.logUser();
        staticThis = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Bundle b = getIntent().getExtras();
        if (b != null) {

            fb_id = b.getString("FB_id");
            name = b.getString("Name");
            email = b.getString("Email");

        }

        et_Name = (RobottoEditTextView) findViewById(R.id.activity_pt_signUp_et_name);
        et_pass = (RobottoEditTextView) findViewById(R.id.activity_pt_signUp_et_pass);


        et_contact = (RobottoEditTextView) findViewById(R.id.activity_pt_signUp_et_contact);
        et_emai = (RobottoEditTextView) findViewById(R.id.activity_pt_signUp_et_email);

        tvNext = (RobottoTextView) findViewById(R.id.tvNext);
        tvSkipSignUp = (RobottoTextView) findViewById(R.id.tvSkipSignUp);


        et_Name.setText(name);
        et_emai.setText(email);
        tvNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                goToNext();
            }
        });

        tvSkipSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public void goToNext() {
        try {

            final String user_name = et_Name.getText().toString();
            final String userEmail = et_emai.getText().toString();
            final String pwd = et_pass.getText().toString();


            final String mobStr = et_contact.getText().toString();


            if ((!isValidEmail(userEmail))) {

                new AlertDialog.Builder(PtSignUp.this)
                        .setTitle("Error!")
                        .setMessage("Email id is invalid")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return;
            }

            if ((pwd.length() < 1) || (mobStr.length() < 1)) {


                new AlertDialog.Builder(PtSignUp.this)
                        .setTitle("Error!")
                        .setMessage("Please fill all details.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return;
            }


            Intent intent = new Intent(PtSignUp.this, PtSignUp2.class);
            intent.putExtra("name", user_name);
            intent.putExtra("email", userEmail);
            intent.putExtra("pass", pwd);
            intent.putExtra("number", mobStr);
            intent.putExtra("fb_id", fb_id);
            startActivity(intent);


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


}

	


	