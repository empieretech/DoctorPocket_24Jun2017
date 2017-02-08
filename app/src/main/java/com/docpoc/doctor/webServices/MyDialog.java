package com.docpoc.doctor.webServices;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import com.docpoc.doctor.R;

public class MyDialog extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("your title");
        alertDialog.setMessage("your message");
        alertDialog.setIcon(R.drawable.ic_launcher);

        alertDialog.show();
    }
}