package com.docpoc.doctor.Pt;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.AboutUs;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Dr.DrDashboardActivity;
import com.docpoc.doctor.Help;
import com.docpoc.doctor.HowItWorks;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.Interface.ClickBook;
import com.docpoc.doctor.Messages;
import com.docpoc.doctor.Pt.fragments.BookingHistoryFragment;
import com.docpoc.doctor.Pt.fragments.DrListFragment;
import com.docpoc.doctor.Pt.fragments.PTProfileFragment;
import com.docpoc.doctor.Pt.fragments.PtMessagesFragment;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Discount;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundedImageView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.JsonParserUniversal;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class PTDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AsynchTaskListner,ClickBook {



    String x;
   ProgressDialog mprogressDialog;
    public TextView tvBadge;
    public int badgeCounter;
    public DrawerLayout drawer;
    public TextView tvAboutUs, tvUser, tvReferEarn, tvHowItWorks;
    public JsonParserUniversal jParser;
    public App app;
    public RoundedImageView imgUser;
    public static RobottoTextView tvTitle;
    public PTDashboardActivity context;
    public static PTDashboardActivity staticThis;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Login Activity";
    Context myContext = this;
    public AppEventsLogger fbAppLogger;
    private FragmentTabHost mTabHost;
    String regId = "";
    public Button btn_my_profile;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_patientgdrawer);

        fbAppLogger = AppEventsLogger.newLogger(this);
        fbAppLogger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, 1);
        Utils.logUser();

        staticThis = this;
        app = App.getInstance();


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);//first get parent toolbar of current action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        try {
            context = this;

            mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
            mTabHost.setup(context, getSupportFragmentManager(), android.R.id.tabcontent);
            mTabHost.getTabWidget().setDividerDrawable(null);

            mTabHost.addTab(mTabHost.newTabSpec("Home")
                    .setIndicator(getTabIndicator(mTabHost.getContext(),
                            R.drawable.tab_home_selector)), DrListFragment.class, null);

            mTabHost.addTab(mTabHost.newTabSpec(Constant.TABS.PROFILE.toString())
                    .setIndicator(getTabIndicator(mTabHost.getContext(),
                            R.drawable.tab_profile_selector)), PTProfileFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec(Constant.TABS.HISTORY.toString())
                    .setIndicator(getTabIndicator(mTabHost.getContext(),
                            R.drawable.tab_history_selector)), BookingHistoryFragment.class, null);

            mTabHost.addTab(mTabHost.newTabSpec(Constant.TABS.MESSAGES.toString())
                    .setIndicator(getTabIndicator(mTabHost.getContext(),
                            R.drawable.tab_messages_selector)), PtMessagesFragment.class, null);


        } catch (Exception e) {
            e.printStackTrace();
        }


        tvTitle = (RobottoTextView) toolbar.findViewById(R.id.custom_text);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);

       // LinearLayout linBottom = (LinearLayout) headerLayout.findViewById(R.id.linBottom);
       // setLayoutParam(linBottom);
        jParser = new JsonParserUniversal();
        tvAboutUs = (TextView) headerLayout.findViewById(R.id.tvAboutUs);
        tvReferEarn = (TextView) headerLayout.findViewById(R.id.tvReferEarn);
       // tvTalkDoctor = (TextView) headerLayout.findViewById(R.id.tvTalkToDoctor);
       // tvLogout = (TextView) headerLayout.findViewById(R.id.tvLogout);
        tvHowItWorks = (TextView) headerLayout.findViewById(R.id.tvHowItWorks);
        btn_my_profile=(Button)headerLayout.findViewById(R.id.btn_my_profile);
        tvUser = (TextView) headerLayout.findViewById(R.id.tvUser);
        imgUser = (RoundedImageView) headerLayout.findViewById(R.id.imgUser);
       //  = (ImageView) headerLayout.findViewById(R.id.);


        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();


     /*   RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins((width / 8) - (tvTitle.getMeasuredHeight() / 8) - 50, 0, 0, 0);
        tvTitle.setLayoutParams(params);
*/
        //     iv = (ImageView) findViewById(R.id.img_logout1);

        //  imgMenu = (ImageView) findViewById(R.id.imgMenu);
     //   imgDashTalk = (ImageView) findViewById(R.id.imgDashTalk);
//        iv.setOnClickListener(this);
        tvBadge = (TextView) findViewById(R.id.tvBadge);

        tvUser.setText(App.user.getName());
      //  AQuery aq = new AQuery(this);
       /* aq.id(imgUser).progress(pBar)
                .image(App.user.pho.replaceAll(" ", "%20"), true, true, 0, R.drawable.dr_about_03, null, 0, 1.0f);
*/


        try {
            AQuery aq = new AQuery(this);
            aq.id(imgUser).progress(null)
                    .image(App.user.getProfileUrl().replaceAll(" ", "%20"), true, true, 0, R.drawable.about, null, 0, 1.0f);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        btn_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PTDashboardActivity.this, PatientProfile.class);
                intent.putExtra("x", "Patient Profile");
                intent.putExtra("patientID", App.user.getUserID());

                startActivity(intent);
            }
        });
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PTDashboardActivity.this, PatientProfile.class);
                intent.putExtra("x", "Patient Profile");
                intent.putExtra("patientID", App.user.getUserID());

                startActivity(intent);
            }
        });

        Log.i("DASHBAOARD", "IMAGE FOR PATIENT :" + App.user.getProfileUrl());
        badgeCounter = 0;
        getBookingHistory_PT_API();



      /*  imgDashTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent i6 = new Intent(PTDashboardActivity.this, DrListActivity.class);
                i6.putExtra("x", "Doctor List");
                startActivity(i6);
            }
        });*/

        tvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent i = new Intent(PTDashboardActivity.this, AboutUs.class);
                startActivity(i);
            }
        });
     /*   tvTalkDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent i6 = new Intent(PTDashboardActivity.this, DrListActivity.class);
                i6.putExtra("x", "Doctor List");
                startActivity(i6);
            }
        });*/

       tvHowItWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// change to refer
                drawer.closeDrawer(GravityCompat.START);
                Intent i6 = new Intent(PTDashboardActivity.this, HowItWorks.class);
                i6.putExtra("x", "Doctor List");
                startActivity(i6);
            }
        });
        tvReferEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// change to refer
                drawer.closeDrawer(GravityCompat.START);
                Intent i6 = new Intent(PTDashboardActivity.this, ReferActivity.class);
                i6.putExtra("x", "Doctor List");
                startActivity(i6);
            }
        });


      /*  tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                final SharedPreferences prefs = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);
                String userTYPE = prefs.getString(MyConstants.USER_TYPE, "");
                String userID = prefs.getString(MyConstants.USER_ID, "");


                if (userTYPE.equalsIgnoreCase(MyConstants.USER_PT)) {
                    Intent intent = new Intent(PTDashboardActivity.this, PtLogin.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PTDashboardActivity.this, DrLogin.class);
                    startActivity(intent);
                }


                SharedPreferences sharedpreferences = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(MyConstants.USER_TYPE, "");
                editor.putString(MyConstants.USER_ID, "");
                editor.commit();

                finish();
            }
        });*/
        app.discountArray.clear();
    }

    public static void setTitle(){

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private View getTabIndicator(Context context, int icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        ImageView v = (ImageView) view.findViewById(R.id.tab);
        v.setBackgroundResource(icon);
        v.setScaleType(ImageView.ScaleType.FIT_XY);
            return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub


        switch (v.getId()) {


            case R.id.img_share:
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

/*
            case R.id.img_search_doc:
                Intent i6 = new Intent(PTDashboardActivity.this, DrListActivity.class);
                i6.putExtra("x", "Doctor List");
                startActivity(i6);
                break;*/


            case R.id.img_PtProfile:

                Intent intent = new Intent(PTDashboardActivity.this, PatientProfile.class);
                intent.putExtra("x", "Patient Profile");
                intent.putExtra("patientID", App.user.getUserID());

                startActivity(intent);

                break;


            case R.id.img_appointment:
                Intent i4 = new Intent(PTDashboardActivity.this, BookingHistoryPtActivity.class);
                i4.putExtra("x", "Booking History");
                startActivity(i4);

                break;
            case R.id.img_message:
                Intent i7 = new Intent(PTDashboardActivity.this, Messages.class);
                startActivity(i7);

                break;


            case R.id.img_help:
                Intent i8 = new Intent(PTDashboardActivity.this, Help.class);
                startActivity(i8);

                break;


        }

    }

    public void getBookingHistory_PT_API() {

        if (!Internet.isAvailable(PTDashboardActivity.this)) {
            Internet.showAlertDialog(PTDashboardActivity.this, "Error!", "No Internet Connection", false);

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
                urlStr = "historyBooking.php?access_token=testermanishrahul234142test&user_id=" + App.user.getUserID();
                return ServerAPI.generalAPI(PTDashboardActivity.this, urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
               /* if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }*/
                onGetResult_Pt(result);


            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
               /* mprogressDialog = new ProgressDialog(PTDashboardActivity.this);
                mprogressDialog.setMessage("Please wait...");
                mprogressDialog.setCancelable(false);
                mprogressDialog.show();*/
            }

            @Override
            public void onTaskCancelled(int taskId) {
              /*  if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }*/
            }
        });
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


        new CallRequest(staticThis).updateDevice("p", App.user.getUserID(), regId);


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


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                } else {

                    // Toast.makeText(PTDashboardActivity.this, msg, Toast.LENGTH_LONG).show();
                }

                new CallRequest(this).updateBadge("p", App.user.getUserID());
            } catch (JSONException e) {
                Log.e("login", e.toString());
                return;
            }

        }

    }

    public static void setTitle(String title){
        tvTitle.setText(title);
    }
    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {
        Utils.removeSimpleSpinProgressDialog();

        try {
            if (result != null && !result.isEmpty()) {

                switch (request) {

                    case getDiscounts:

                        try {
                            JSONObject jObj = new JSONObject(result);
                            if (jObj.getBoolean("success")) {
                                JSONArray jArray = jObj.getJSONArray("data");
                                app.discountArray.clear();
                                if (jArray != null && jArray.length() > 0) {
                                    for (int i = 0; i < jArray.length(); i++) {
                                        app.discountArray.add((Discount) jParser.parseJson(jArray.getJSONObject(i), new Discount()));
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            Utils.removeSimpleSpinProgressDialog();
                            e.printStackTrace();
                        }

                        break;

                    case updateBadge:

                        new CallRequest(this).getDiscount(App.user.getUserID());

                        break;


                }

            } else {
                Utils.hideProgressDialog();

                Utils.showToast("Please try again later", this);
            }
        } catch (Exception e) {
            Utils.hideProgressDialog();

            e.printStackTrace();
        }


    }

    @Override
    public void onProgressUpdate(String uniqueMessageId, int progres) {
        Utils.hideProgressDialog();
    }

    @Override
    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS request) {
        Utils.hideProgressDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void setOnBookClick(GlobalBeans.DoctorBean bean) {

    }
}
