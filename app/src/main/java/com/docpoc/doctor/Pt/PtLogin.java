package com.docpoc.doctor.Pt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Dr.DrDashboardActivity;
import com.docpoc.doctor.ForgotPassword;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.PreLogin;
import com.docpoc.doctor.R;
import com.docpoc.doctor.TermsConditionActivity;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.gcm.MyGcmRegistrationService;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoEditTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class PtLogin extends Activity implements OnClickListener, AsynchTaskListner {

    public RobottoTextView tv_logIn, tv_forget, tv_signUp, tv_terms, tvDoctorSignUp;
    public RobottoEditTextView et_email, et_password;
    public ProgressDialog mprogressDialog;
    public LoginButton loginButton;
    public CallbackManager callbackManager;
    public GoogleCloudMessaging gcm;
    public Context context = this;
    public static PtLogin staticThis;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    public static final String TAG = "Login Activity";
    public String regId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_pt_login);
        staticThis = this;
        Utils.logUser();

        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);

        App.user.setUser_Type(MyConstants.USER_PT);
        et_email = (RobottoEditTextView) findViewById(R.id.activity_pt_login_et_emailId);
        et_password = (RobottoEditTextView) findViewById(R.id.activity_pt_login_et_pwd);

        et_email.setText("sagar.jeff@gmail.com");
        et_password.setText("1234");

        tv_logIn = (RobottoTextView) findViewById(R.id.activity_pt_login_tv_logIN);
        tv_forget = (RobottoTextView) findViewById(R.id.activity_pt_login_tv_forget);
        tv_signUp = (RobottoTextView) findViewById(R.id.activity_pt_login_tv_SignUp);
        tv_signUp.setText(Html.fromHtml("Don't have an account? <b><u>Sign Up</u></b>"));
        tvDoctorSignUp = (RobottoTextView) findViewById(R.id.tvDoctorSignUp);
        tvDoctorSignUp.setText(Html.fromHtml("Doctor? <b><u>Click here</b>"));
        tv_terms = (RobottoTextView) findViewById(R.id.activity_pt_login_tv_terms);
        tv_terms.setText(Html.fromHtml("By signing in you agree to our <br> <b><u>Terms & Conditions</u></b>"));


        loginButton = (LoginButton) findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();
        setListener();


        AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),
                "877929938", "OY--CObClmwQ0svQogM", "0.00", false);


        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }

        registerGCM();
        setUpHomePAge();


    }

    public void setUpHomePAge() {

        final SharedPreferences prefs = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);
        String userTYPE = prefs.getString(MyConstants.USER_TYPE, "");


        if (!(userTYPE.isEmpty())) {
            String userID = prefs.getString(MyConstants.USER_ID, "");
            String userName = prefs.getString(MyConstants.USER_EMAIL, "");
            String name = prefs.getString(MyConstants.PT_NAME, "");
            String PT_ZONE = prefs.getString(MyConstants.PT_ZONE, "");
            String PROFILE_PIC = prefs.getString(MyConstants.PROFILE_PIC, "");
            if (userTYPE.equalsIgnoreCase(MyConstants.USER_PT)) {

                App.user.setUser_Type(MyConstants.USER_PT);
                App.user.setCurrent_zone_date_time(PT_ZONE);
                App.user.setProfileUrl(PROFILE_PIC);
                App.user.setUserID(userID);
                App.user.setUserEmail(userName);
                App.user.setUserName(userName);
                App.user.setName(name);
                Intent intent = new Intent(this, PTDashboardActivity.class);
                this.startActivity(intent);
            } else {
                App.user.setUser_Type(MyConstants.USER_DR);
                Intent intent = new Intent(this, DrDashboardActivity.class);
                this.startActivity(intent);

            }


        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.activity_pt_login_tv_logIN:
                //Intent intent = new Intent(this, PtDashboard_OldActivity.class);
                //	this.startActivity(intent);

                loginAPI();
                break;

            case R.id.activity_pt_login_tv_forget:
                ForgotPassword fP = new ForgotPassword();
                fP.displayLayout(PtLogin.this);
                break;

            case R.id.activity_pt_login_tv_SignUp:
                Intent intent3 = new Intent(this, PtSignUp.class);
                this.startActivity(intent3);
                break;

            case R.id.activity_pt_login_tv_terms:
                Intent intent4 = new Intent(this, TermsConditionActivity.class);
                this.startActivity(intent4);
                break;

            case R.id.img_left_arrow:
                finish();
                break;

        }

        // TODO Auto-generated method stub

    }

    public String registerGCM() {
        if (!Internet.isAvailable(PtLogin.this)) {
            Internet.showAlertDialog(PtLogin.this, "Error!", "No Internet Connection", false);

            return null;
        }

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerDevice();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
        } else {
            //	Toast.makeText(getApplicationContext(),"RegId already available. RegId: " + regId,
            //			Toast.LENGTH_LONG).show();
        }
        return regId;
    }

    @SuppressLint("NewApi")
    private String getRegistrationId(Context context) {

        final SharedPreferences prefs = getSharedPreferences(
                DrDashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    public void registerDevice() {
        if (Utils.isNetworkAvailable(PtLogin.this)) {
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.

                //     Utils.showSimpleSpinProgressDialog(instance, "Please wait...");
                Intent intent = new Intent(this, MyGcmRegistrationService.class);
                startService(intent);
            }
        }
    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("GCM ::", "GCM This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private void setListener() {

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email","user_birthday","user_location","user_location"));
//        @[@"public_profile", @"email", @"user_friends",@"user_birthday",@"user_location",@"user_hometown"];
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

			/*
                new AlertDialog.Builder(PtLogin.this)
						.setTitle("FB")
						.setMessage("Success")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.show();     */

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("HomeActivity", response.toString());

                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    String id = object.getString("id");
                                    String gender = object.getString("gender");


                                    getPTProfileAPI(id, name, email);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "timezone,locale,id,first_name,last_name,name,email,birthday,picture.type(large),location,hometown");
                //parameters:@{@"fields": @"timezone,locale,id,first_name,last_name,name,email,birthday,picture.type(large),location,hometown"}]
                request.setParameters(parameters);
                request.executeAsync();
            }


            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

                new AlertDialog.Builder(PtLogin.this)
                        .setTitle("FB")
                        .setMessage("Error")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });

        tv_logIn.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        tv_signUp.setOnClickListener(this);
        tv_terms.setOnClickListener(this);


    }

    public void loginAPI() {


        final String userID = et_email.getText().toString();
        final String pwd = et_password.getText().toString();

        if (userID.length() < 2 && pwd.length() < 1) {


            new AlertDialog.Builder(PtLogin.this)
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


        if (!Internet.isAvailable(PtLogin.this)) {
            Internet.showAlertDialog(PtLogin.this, "Error!", "No Internet Connection", false);

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
                String urlStr = "login.php?access_token=testermanishrahul234142test" + "&device=android&userName=" + userID + "&password=" + pwd + "&deviceId=" + deviceID;
                Log.i("URL", "URL FIRED : " + urlStr);
                return ServerAPI.Login(urlStr, PtLogin.this);

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
                mprogressDialog = new ProgressDialog(PtLogin.this);
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
                        //   appdelegate.appdelegateMessageBadgeValue = jsonData[@"document"][@"response"][@"data"][@"messageBadgeVal"];

                        String userID = jsonResponse.getJSONObject("data").getString("user_id");
                        String userName = jsonResponse.getJSONObject("data").getString("userName");
                        String current_zone_date_time = jsonResponse.getJSONObject("data").getString("zone");
                        String image_url = jsonResponse.getJSONObject("data").getString("image_url");
                        String messageBadgeVal = jsonResponse.getJSONObject("data").getString("messageBadgeVal");
                        String name = jsonResponse.getJSONObject("data").getString("name");

                        SharedPreferences sharedpreferences = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString(MyConstants.USER_TYPE, MyConstants.USER_PT);
                        editor.putString(MyConstants.USER_EMAIL, userName);
                        editor.putString(MyConstants.USER_ID, userID);
                        editor.putString(MyConstants.PT_ZONE, current_zone_date_time);
                        editor.putString(MyConstants.PT_NAME, name);
                        editor.putString(MyConstants.PROFILE_PIC, MyConstants.BASE_URL + image_url);
                        editor.commit();
                        App.user.setProfileUrl(MyConstants.BASE_URL + image_url.replaceAll("\\/", "/"));

                        App.user.setUser_Type(MyConstants.USER_PT);
                        App.user.setUserID(userID);
                        App.user.setUserID(userID);
                        App.user.setMessageBadgeVal(messageBadgeVal);
                        App.user.setUserEmail(userName);
                        App.user.setUserName(userName);
                        App.user.setName(name);
                        App.user.setCurrent_zone_date_time(current_zone_date_time);

                        Intent intent = new Intent(this, PTDashboardActivity.class);
                        this.startActivity(intent);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(PtLogin.this, loginResult.message, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


    public void getPTProfileAPI(final String FB_id, final String Name, final String email) {


        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {

                String urlStr = "";

                urlStr = "getProfile.php?access_token=testermanishrahul234142test&user_id=" + FB_id;

                return ServerAPI.generalAPI(PtLogin.this, urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
                onProfileResult(result, FB_id, Name, email);
            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(PtLogin.this);
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


    public void onProfileResult(JSONObject jsonResponse, String fb_id, String name, String email) {


        if (jsonResponse != null) {
            try {

                Log.i("responce", jsonResponse.toString());
                if (jsonResponse.getString("status").equals("1")) {

                    try {

                        App.user.setUser_Type(MyConstants.USER_PT);
                        String userID = jsonResponse.getString("user_id");
                        String userName = jsonResponse.getString("userName");
                        String current_zone_date_time = jsonResponse.getString("zone");
                        String image_url = jsonResponse.getString("image_url");
                        String messageBadgeVal = jsonResponse.getString("messageBadgeVal");
                        name = jsonResponse.getString("name");

                        SharedPreferences sharedpreferences = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString(MyConstants.USER_TYPE, MyConstants.USER_PT);
                        editor.putString(MyConstants.USER_EMAIL, userName);
                        editor.putString(MyConstants.USER_ID, userID);
                        editor.putString(MyConstants.PT_ZONE, current_zone_date_time);
                        editor.putString(MyConstants.PT_NAME, name);
                        editor.putString(MyConstants.PROFILE_PIC, MyConstants.BASE_URL + image_url);
                        editor.commit();
                        App.user.setProfileUrl(MyConstants.BASE_URL + image_url.replaceAll("\\/", "/"));


                        App.user.setUserID(userID);
                        App.user.setUserID(userID);
                        App.user.setMessageBadgeVal(messageBadgeVal);
                        App.user.setUserEmail(userName);
                        App.user.setUserName(userName);
                        App.user.setName(name);
                        App.user.setCurrent_zone_date_time(current_zone_date_time);

                        Intent intent = new Intent(this, PTDashboardActivity.class);
                        this.startActivity(intent);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Intent i = new Intent(PtLogin.this, PtSignUp.class);
                    i.putExtra("FB_id", fb_id);
                    i.putExtra("Name", name);
                    i.putExtra("Email", email);
                    startActivity(i);

                    Toast.makeText(PtLogin.this, "Sign Up", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
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


        //      new CallRequest(staticThis).updateDevice("p", App.user.getUserID(), regId);


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
