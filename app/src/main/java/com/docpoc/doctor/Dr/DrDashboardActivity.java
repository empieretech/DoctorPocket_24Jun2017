package com.docpoc.doctor.Dr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.AboutUs;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Help;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.Messages;
import com.docpoc.doctor.Pt.PtLogin;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class DrDashboardActivity extends Activity implements OnClickListener, AsynchTaskListner {
    ImageView img1, img2, img3, img4, img5, img6, img7, img8, logout;

    String x;

    ProgressDialog mprogressDialog;
    public TextView tvBadge;
    public int badgeCounter;
    public static DrDashboardActivity staticThis;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Login Activity";
    Context myContext=this;

    String regId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_dr_dashboard);
        Utils.logUser();
        staticThis = this;

        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);


        img1 = (ImageView) findViewById(R.id.imageView2);
        img2 = (ImageView) findViewById(R.id.imageView3);
        img3 = (ImageView) findViewById(R.id.imageView4);
        img4 = (ImageView) findViewById(R.id.imageView5);
        img5 = (ImageView) findViewById(R.id.image_DrProfile);
        img6 = (ImageView) findViewById(R.id.image_searchDr);
        img7 = (ImageView) findViewById(R.id.image_message);
        img8 = (ImageView) findViewById(R.id.image_help);

        logout = (ImageView) findViewById(R.id.img_logout);
        tvBadge = (TextView) findViewById(R.id.tvBadge);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);
        img7.setOnClickListener(this);
        img8.setOnClickListener(this);

        logout.setOnClickListener(this);

        getBookingHistory_DR_API();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub


        switch (v.getId()) {
            case R.id.imageView2:
                Intent i = new Intent(DrDashboardActivity.this, AboutUs.class);
                startActivity(i);

                break;

            case R.id.imageView3:
                Intent sharingIntent = new Intent(
                        android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check this out, I just saved time from visiting the doctor by using Doctor Pocket https://play.google.com/store/apps/details?id=com.docpoc.doctor";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "Share Doctor Pocket ");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;

            case R.id.imageView4:
                Intent i3 = new Intent(DrDashboardActivity.this, AboutUs.class);
                startActivity(i3);

                break;

            case R.id.imageView5:
                //	Intent i4 = new Intent(DrDashboardActivity.this, BookingHistorySearchDoc.class);
                Intent i4 = new Intent(DrDashboardActivity.this, BookingHistory.class);
                startActivity(i4);

                break;

            case R.id.image_DrProfile:

                Intent intent = new Intent(DrDashboardActivity.this, DoctorProfile.class);
                intent.putExtra("x", "Doctor Profile");
                startActivity(intent);

                break;

            case R.id.image_searchDr:
                Intent i6 = new Intent(DrDashboardActivity.this, BookingHistorySearchDoc.class);
                i6.putExtra("x", "Doctor List");
                startActivity(i6);

                break;

            case R.id.image_message:
                Intent i7 = new Intent(DrDashboardActivity.this, Messages.class);
                startActivity(i7);


                break;


            case R.id.image_help:
                Intent i8 = new Intent(DrDashboardActivity.this, Help.class);
                startActivity(i8);


                break;
            case R.id.img_logout:

                final SharedPreferences prefs = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);
                String userTYPE = prefs.getString(MyConstants.USER_TYPE, "");
                String userID = prefs.getString(MyConstants.USER_ID, "");


                if (userTYPE.equalsIgnoreCase(MyConstants.USER_PT)) {
                    intent = new Intent(this, PtLogin.class);
                    this.startActivity(intent);
                } else {
                    intent = new Intent(this, DrLogin.class);
                    this.startActivity(intent);
                }


                SharedPreferences sharedpreferences = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(MyConstants.USER_TYPE, "");
                editor.putString(MyConstants.USER_ID, "");
                editor.commit();

                finish();
                break;


        }

    }

    public void getBookingHistory_DR_API() {

        if (!Internet.isAvailable(DrDashboardActivity.this)) {
            Internet.showAlertDialog(DrDashboardActivity.this, "Error!", "No Internet Connection", false);

            return;
        }

        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {

                String urlStr = "";
        /*		if(App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)){
                    urlStr = "historyDoctorBooking.php?access_token=testermanishrahul234142test&doctor_id=" + App.user.getUserID();
				}else{
					urlStr = "historyBooking.php?access_token=testermanishrahul234142test&user_id=" + App.user.getUserID();
				}
*/
                urlStr = "historyDoctorBooking.php?access_token=testermanishrahul234142test&doctor_id=" + App.user.getUserID();
                return ServerAPI.generalAPI(DrDashboardActivity.this, urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }

                onGetResult_Pt(result);


            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(DrDashboardActivity.this);
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

    public void onGetResult_Pt(JSONObject jsonResponse) {

        String msg = "";
        if (jsonResponse != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {


                        JSONArray dataArr = jsonResponse.getJSONArray("data");


                        for (int i = 0; i < dataArr.length(); i++) {

                            JSONObject jsonObj = dataArr.getJSONObject(i);
                            GlobalBeans.DoctorBean bean = new GlobalBeans.DoctorBean();

                            bean.status = jsonObj.getString("status");
                            bean.amount_status = jsonObj.getString("amount_status");
                            if (bean.amount_status.equalsIgnoreCase("0") && bean.status.equalsIgnoreCase("1")) {
                                badgeCounter++;
                            }
                        }

                        if (badgeCounter > 0) {
                            tvBadge.setVisibility(View.VISIBLE);
                            tvBadge.setText(badgeCounter + "");
                        }



                        new CallRequest(this).updateBadge("d", App.user.getUserID());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                } else {

                    Toast.makeText(DrDashboardActivity.this, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
                return;
            }

        }

    }


    public static void storeRegistrationId( String regId) {

        final SharedPreferences prefs = staticThis.getSharedPreferences(
                DrDashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(staticThis);
        App.user.setUser_DeviceID(regId);

        Log.i(TAG, "Saving regId on app version " + appVersion + regId);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();


        new CallRequest(staticThis).updateDevice("d",App.user.getUserID(),regId);




    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager() .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity","I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }




    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {
        Utils.removeSimpleSpinProgressDialog();
        try {
            if (result != null && !result.isEmpty()) {
                Log.i("TAG", "TAG Result : " + result);
                //      {"document":{"response":{"status":1,"message":"Success."}}}
                switch (request) {


                    case acceptReject:
                        Utils.removeSimpleSpinProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(result);


                            Utils.showToast(obj.getString("message"), this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;


                }

            } else {
                Utils.showToast("Please try again later", this);
            }
        } catch (Exception e) {
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
