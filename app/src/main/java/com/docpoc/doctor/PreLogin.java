package com.docpoc.doctor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.Dr.DrDashboardActivity;
import com.docpoc.doctor.Dr.DrLogin;
import com.docpoc.doctor.Pt.PTDashboardActivity;
import com.docpoc.doctor.Pt.PtLogin;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.gcm.MyGcmRegistrationService;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import io.fabric.sdk.android.Fabric;

public class PreLogin extends Activity implements OnClickListener {
     Button btn, btn1;
     
     boolean doubleBackToExitPressedOnce = false;


	GoogleCloudMessaging gcm;
	Context context=this;
	public static PreLogin staticThis;
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	static final String TAG = "Login Activity";
	Context myContext=this;

	String regId="";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_pre_login);
		Utils.logUser();
		staticThis = this;

		setUpHomePAge();
		AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);


		btn=(Button) findViewById(R.id.btn1);
		btn1=(Button) findViewById(R.id.btn2);
		
		
		btn.setOnClickListener(this);
		btn1.setOnClickListener(this);


			if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
				if (!Settings.canDrawOverlays(this)) {
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
					startActivityForResult(intent, 0);
				}
			}



		registerGCM();

		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()) {
        case R.id.btn1:
          //Play voicefile


          Intent i= new Intent(PreLogin.this, DrLogin.class );
          startActivity(i);
          break;
        case R.id.btn2:
          //Stop MediaPlayer
        	 Intent i1= new Intent(PreLogin.this, PtLogin.class);
             startActivity(i1);
          break;
          
      
		
	}
	}
		
		@Override
		public void onBackPressed() {
			if (doubleBackToExitPressedOnce) {
				super.onBackPressed();
				return;
			}

			this.doubleBackToExitPressedOnce = true;
			Toast.makeText(this, "Please click Twice to exit",
					Toast.LENGTH_SHORT).show();

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					doubleBackToExitPressedOnce = false;

				}
			}, 2000);
}




	@Override
	protected void onActivityResult(
			final int requestCode,
			final int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) {
			if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
				if (!Settings.canDrawOverlays(this)) {
					// ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
				}
				else {
					Log.d("ScreenOverLay", "Screen overlay detected");
					finish();
					Intent mStartActivity = new Intent(this, PreLogin.class);
					int mPendingIntentId = 123456;
					PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
					AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
					System.exit(0);
				}
			}
		}
	}

	public String registerGCM() {
		if(!Internet.isAvailable(PreLogin.this)){
			Internet.showAlertDialog(PreLogin.this,  "Error!", "No Internet Connection", false);

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
		if (Utils.isNetworkAvailable(PreLogin.this)) {
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






	public  void setUpHomePAge(){

		final SharedPreferences prefs = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);
		String userTYPE = prefs.getString(MyConstants.USER_TYPE, "");



		if (!(userTYPE.isEmpty())){
			String userID = prefs.getString(MyConstants.USER_ID, "");
			String userName = prefs.getString(MyConstants.USER_EMAIL, "");
			String name = prefs.getString(MyConstants.PT_NAME, "");
			String PT_ZONE = prefs.getString(MyConstants.PT_ZONE, "");
			String PROFILE_PIC = prefs.getString(MyConstants.PROFILE_PIC, "");
			if(userTYPE.equalsIgnoreCase(MyConstants.USER_PT)){

				App.user.setUser_Type(MyConstants.USER_PT);
				App.user.setCurrent_zone_date_time(PT_ZONE);
				App.user.setProfileUrl(PROFILE_PIC);
				App.user.setUserID(userID);
				App.user.setUserEmail(userName);
				App.user.setUserName(userName);
				App.user.setName(name);
				Intent intent = new Intent(this, PTDashboardActivity.class);
				this.startActivity(intent);
			}
			else{
				App.user.setUser_Type(MyConstants.USER_DR);
				Intent intent = new Intent(this, DrDashboardActivity.class);
				this.startActivity(intent);

			}



		}
	}



}
