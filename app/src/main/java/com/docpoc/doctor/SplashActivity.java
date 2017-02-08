package com.docpoc.doctor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.Pt.PTDashboardActivity;
import com.docpoc.doctor.Dr.DrDashboardActivity;

import com.docpoc.doctor.Pt.PtLogin;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.Utils;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity {


	// Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_splash);
		Utils.logUser();




		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent i = new Intent(SplashActivity.this, PtLogin.class);
				startActivity(i);

			}
		}, SPLASH_TIME_OUT);
	}





}