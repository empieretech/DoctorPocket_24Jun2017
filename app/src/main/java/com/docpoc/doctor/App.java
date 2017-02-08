package com.docpoc.doctor;

import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.classes.ChatMessage;
import com.docpoc.doctor.classes.Discount;
import com.docpoc.doctor.classes.User;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;

import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;

/**
 * Created by Sagar Sojitra on 5/16/2016.
 */
public class App extends MultiDexApplication {

    public static App getInstance() {
        return instance;
    }

    public static App instance;
    public SharedPreferences sharedPref;
    public String recentChatId = "";
    public String messageBadgeVal = "";
    public static User user = null;


    public ArrayList<Discount> discountArray = new ArrayList<>();
    public ArrayList<ChatMessage> chatArray = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Fabric.with(this, new Crashlytics());
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);

            instance = this;
            user = new User();
            sharedPref = getSharedPreferences(App.class.getSimpleName(), 0);
            MultiDex.install(this);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
        AppEventsLogger.deactivateApp(this);
    }

    public App() {
        setInstance(this);
    }


    public static void setInstance(App instance) {
        App.instance = instance;
    }
}
