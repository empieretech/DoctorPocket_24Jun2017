package com.docpoc.doctor.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIdListenerService extends InstanceIDListenerService {
    public void onTokenRefresh() {
        Intent intent = new Intent(this, MyGcmRegistrationService.class);
        startService(intent);
    }
}