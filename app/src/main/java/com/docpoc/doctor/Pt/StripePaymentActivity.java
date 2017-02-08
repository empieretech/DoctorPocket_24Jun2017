package com.docpoc.doctor.Pt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.R;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.Utils;

import io.fabric.sdk.android.Fabric;

public class StripePaymentActivity extends Activity implements AsynchTaskListner {


    public App app;
    public GlobalBeans.DoctorBean drBean;
    public WebView webView;
    public String patient_name = "", booking_date = "";
    public ProgressDialog pBar;
    // http://doctorpocketadmin.com/Stripe/?street=test street&city=test% city
    // &state=test state&zip_code=test code&email=test email&amount=200&booking_id=test id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.layout_stripe);
        Utils.logUser();



        app = App.getInstance();
        drBean = (GlobalBeans.DoctorBean) getIntent().getSerializableExtra("dr");
        patient_name = App.user.getUserName();
        booking_date = drBean.booking_date + " " + drBean.booking_time;
        pBar = new ProgressDialog(this);
        pBar.setMessage("Please wait loading");
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        String url = MyConstants.STRIPE_URL +"street=" + drBean.address + "&city=" + drBean.address
                + "&state=" + drBean.address + "&zip_code=M4N1K1&email=" + drBean.email +
                "&amount=" + drBean.fees + "&booking_id=" + drBean.row_id
                + "&doctor_name=" + drBean.fName + " " + drBean.lastName
                + "&patient_name=" + patient_name
                + "&booking_date=" + booking_date;

        Log.i("Url", url);
        webView.setWebViewClient(new AppWebViewClients(pBar));
        webView.loadUrl(url);

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

    public class AppWebViewClients extends WebViewClient {
        private ProgressDialog progressBar;

        public AppWebViewClients(ProgressDialog progressBar) {
            this.progressBar = progressBar;
            progressBar.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("index.php?success=true")) {

                if (app.discountArray.size() > 0) {

                    if (App.user.getUserID().equalsIgnoreCase(app.discountArray.get(0).getSignup_id())) {
                        new CallRequest(StripePaymentActivity.this).updateRefer(app.discountArray.get(0).id, "sign_up_used");
                    } else {
                        new CallRequest(StripePaymentActivity.this).updateRefer(app.discountArray.get(0).id, "refer_used");
                    }
                }
                Toast.makeText(StripePaymentActivity.this, "Your payment has been made successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StripePaymentActivity.this, PTDashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
            else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.dismiss();
        }
    }
}
