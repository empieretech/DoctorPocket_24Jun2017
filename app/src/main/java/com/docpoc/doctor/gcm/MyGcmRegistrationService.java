package com.docpoc.doctor.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


import com.docpoc.doctor.App;
import com.docpoc.doctor.Dr.DrDashboardActivity;
import com.docpoc.doctor.Dr.DrLogin;
import com.docpoc.doctor.PreLogin;
import com.docpoc.doctor.Pt.PTDashboardActivity;
import com.docpoc.doctor.Pt.PtLogin;
import com.docpoc.doctor.webServices.MyConstants;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class MyGcmRegistrationService extends IntentService {
    private static final String TAG = "MyRegistrationService";
    private static final String GCM_SENDER_ID = "436674102927";
    private static final String[] TOPICS = {"global"};

    public MyGcmRegistrationService() {
        super(TAG);
    }
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(GCM_SENDER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                subscribeTopics(token);



                final SharedPreferences prefs = getSharedPreferences(
                        DrDashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                int appVersion = getAppVersion(getApplicationContext());
                App.user.setUser_DeviceID(token);

                Log.i(TAG, "Saving regId on app version " +token);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(REG_ID, token);
                editor.putInt(APP_VERSION, appVersion);
                editor.commit();


                if (App.user.getUser_Type() == null) {

                    PtLogin.storeRegistrationId(token);
                } else {
                    if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {

                        if(App.user.getUserID().equalsIgnoreCase("")) {
                            DrLogin.storeRegistrationId(token);
                        } else {
                            DrDashboardActivity.storeRegistrationId(token);
                        }
                    } else {
                        if(App.user.getUserID().equalsIgnoreCase("")) {
                            PtLogin.storeRegistrationId(token);
                        } else {
                            PTDashboardActivity.storeRegistrationId(token);
                        }

                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
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


}