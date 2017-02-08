package com.docpoc.doctor.Dr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.ForgotPassword;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.R;
import com.docpoc.doctor.TermsConditionActivity;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;
import com.google.ads.conversiontracking.AdWordsConversionReporter;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class DrLogin extends Activity implements OnClickListener, AsynchTaskListner {
    RobottoTextView tv_forgot, tv_terms, tv_logIn;
    EditText et_email, et_pwd;
    ImageView iv_back;
    CheckBox checkB_terms;
    ProgressDialog mprogressDialog;
    public static DrLogin staticThis;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Login Activity";
    Context myContext = this;

    String regId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_dr_login);
        Utils.logUser();
        staticThis = this;
        App.user.setUser_Type(MyConstants.USER_DR);
        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);


        et_email = (EditText) findViewById(R.id.activity_dr_login_et_email);
        et_pwd = (EditText) findViewById(R.id.activity_dr_login_et_pwd);
        //	et_email.setText("oussama_has@hotmail.com");
        //	et_pwd.setText("Oussama1234");

        tv_forgot = (RobottoTextView) findViewById(R.id.activity_dr_login_tv_forgot);
        tv_terms = (RobottoTextView) findViewById(R.id.activity_dr_login_tv_Terms);
        tv_logIn = (RobottoTextView) findViewById(R.id.activity_dr_login_tv_logIn);

        checkB_terms = (CheckBox) findViewById(R.id.activity_dr_login_checkB_terms);

        iv_back = (ImageView) findViewById(R.id.img_left_arrow);


        iv_back.setOnClickListener(this);
        tv_forgot.setOnClickListener(this);
        tv_terms.setOnClickListener(this);
        tv_logIn.setOnClickListener(this);

        AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),
                "877929938", "OY--CObClmwQ0svQogM", "0.00", false);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.activity_dr_login_tv_logIn:

                loginAPI();
                break;

            case R.id.activity_dr_login_tv_forgot:
                ForgotPassword fP = new ForgotPassword();
                fP.displayLayout(DrLogin.this);
                break;

            case R.id.activity_dr_login_tv_Terms:
                Intent intent2 = new Intent(this, TermsConditionActivity.class);
                this.startActivity(intent2);
                break;

            case R.id.img_left_arrow:
                finish();

        }


    }


    public void loginAPI() {


        final String userID = et_email.getText().toString();
        final String pwd = et_pwd.getText().toString();


        if (userID.length() < 2 && pwd.length() < 1) {


            new AlertDialog.Builder(DrLogin.this)
                    .setTitle("Error!")
                    .setMessage("Emai id or Password can not be blank.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            return;
        }

        if (!checkB_terms.isChecked()) {


            new AlertDialog.Builder(DrLogin.this)
                    .setTitle("Error!")
                    .setMessage("Please check the Terms & Condition.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            return;
        }
        if (!Internet.isAvailable(DrLogin.this)) {
            Internet.showAlertDialog(DrLogin.this, "Error!", "No Internet Connection", false);

            return;
        }

        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {

                final SharedPreferences prefs = staticThis.getSharedPreferences(
                        DrDashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);

                String deviceID = "";
                if (prefs.contains(REG_ID))
                    deviceID = prefs.getString(REG_ID, "");
                String urlStr = "loginDoctor.php?access_token=testermanishrahul234142test" + "&userName=" + userID + "&password=" + pwd + "&deviceId=" + deviceID;

                return ServerAPI.Login(urlStr, DrLogin.this);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
                onLoginResult(result);
            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(DrLogin.this);
                mprogressDialog.setMessage("Login...");
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

    public void onLoginResult(JSONObject jsonResponse) {

        GlobalBeans.LoginResultBean loginResult = new GlobalBeans.LoginResultBean();
        if (jsonResponse != null) {
            try {

                loginResult.message = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {


                        String userID = jsonResponse.getJSONObject("data").getString("user_id");
                        String userName = jsonResponse.getJSONObject("data").getString("userName");
                        String image_url = jsonResponse.getJSONObject("data").getString("image_url");
                        String messageBadgeVal = jsonResponse.getJSONObject("data").getString("messageBadgeVal");
                        SharedPreferences sharedpreferences = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString(MyConstants.USER_TYPE, MyConstants.USER_DR);
                        editor.putString(MyConstants.USER_ID, userID);
                        editor.commit();

                        App.user.setUser_Type(MyConstants.USER_DR);
                        App.user.setMessageBadgeVal(messageBadgeVal);
                        App.user.setUserID(userID);
                        App.user.setUserEmail(userName);
                        App.user.setProfileUrl(MyConstants.BASE_URL + image_url);


                        Intent intent = new Intent(this, DrDashboardActivity.class);
                        this.startActivity(intent);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(DrLogin.this, loginResult.message, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }

    public static void storeRegistrationId(String regId) {

        final SharedPreferences prefs = staticThis.getSharedPreferences(
                DrDashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(staticThis);
        App.user.setUser_DeviceID(regId);

        Log.i(TAG, "Saving regId on app version " + appVersion + regId);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();


     //   new CallRequest(staticThis).updateDevice("p", App.user.getUserID(), regId);


    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity", "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {

    }

    @Override
    public void onProgressUpdate(String uniqueMessageId, int progres) {

    }

    @Override
    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS request) {

    }
}
        
            
            
		
	
	
	

