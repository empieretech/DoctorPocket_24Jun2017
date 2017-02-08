package com.docpoc.doctor.Pt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.fabric.sdk.android.Fabric;


public class Appointment extends Activity implements AsynchTaskListner {
    ProgressDialog mprogressDialog;
    GlobalBeans.DoctorBean drBean;

    public String TAG = "Dr. Pocket Payment";
    // private static final String TAG = "paymentdemoblog";
    /**
     * - Set to PaymentActivity.ENVIRONMENT_PRODUCTION to move real money.
     * <p/>
     * - Set to PaymentActivity.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * <p/>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    // private static final String CONFIG_ENVIRONMENT =
    // PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

    // note that these credentials will differ between live & sandbox
    // environments.
    String sandBox = "AXkBxqsh-aV0PW0wvYBszG92RjzQGOGkYnERWMGZvGsAmbW-xrGilsPVkTSDD_RWZf6tyrikOX6l0W03";
    private static final  String live = "AXORmeUOWK9QW0hw68V6iO7kZMKYpasXqZiG6PaiwMrDJTs9oSc13g5uyRdoNi7mDl77OAtI4FGbhZwx";


    private static final String CONFIG_CLIENT_ID = "AXkBxqsh-aV0PW0wvYBszG92RjzQGOGkYnERWMGZvGsAmbW-xrGilsPVkTSDD_RWZf6tyrikOX6l0W03";


    // note that these credentials will differ between live & sandbox environments.


    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    public String planName;
    public String countryCode, companyId, PaymentID, SubscriptionID, planAmount;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(live)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Dr. Pocket")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    public RobottoTextView tvPlanName;
    PayPalPayment thingToBuy;

    Button btn_payment_Now;
    public LinearLayout linDiscount;
    ImageView iv_drImage,iv_back;
    public ProgressBar pBar;
    RobottoTextView tv_date, tv_time, tv_amount, tvDisAmount, tvDisType;
    public App app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_appointment);
        Utils.logUser();


        app = App.getInstance();
        drBean = (GlobalBeans.DoctorBean) getIntent().getSerializableExtra("dr");

        pBar = (ProgressBar)  findViewById(R.id.pBar);

     /* //  Bundle b = getIntent().getExtras();

        drBean.dr_id = b.getString("dr_id");
        drBean.row_id = b.getString("booking_id");
        drBean.booking_date = b.getString("booking_date");
        drBean.booking_time = b.getString("booking_time");
        drBean.imageURL = b.getString("dr_imagUrl");
        drBean.fName = b.getString("Name");
        drBean.question = b.getString("Question");
        drBean.fees = b.getString("Fees");*/

        initView();

        // PAyPalSetUp();


    }

    public void initView() {

        btn_payment_Now = (Button) findViewById(R.id.activity_appointment_btn_payNow);

        iv_drImage = (ImageView) findViewById(R.id.imageView1);


        tv_date = (RobottoTextView) findViewById(R.id.editText1);
        tv_time = (RobottoTextView) findViewById(R.id.tv_time);
        tv_amount = (RobottoTextView) findViewById(R.id.textView3);
        linDiscount = (LinearLayout) findViewById(R.id.linDiscount);
        tvDisAmount = (RobottoTextView) findViewById(R.id.tvDiscountAmount);
        tvDisType = (RobottoTextView) findViewById(R.id.tvDiscountType);
        iv_back = (ImageView) findViewById(R.id.img_left_arrow);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);


        AQuery aq = new AQuery(this);
        aq.id(iv_drImage).progress(pBar)
                .image(drBean.imageURL.replaceAll(" ","%20"), true, true, 0, R.drawable.dr_about_03, null, 0, 1.0f);

        Intent intent = new Intent(Appointment.this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        setListener();
    }

    public void setListener() {
        tv_amount.setText("$ " + drBean.fees);
        btn_payment_Now.setText("Pay Now  $" + drBean.fees);
        if (app.discountArray.size() > 0) {
            int finalAmount = 0;
            linDiscount.setVisibility(View.VISIBLE);
            if (App.user.getUserID().equalsIgnoreCase(app.discountArray.get(0).getSignup_id())) {
                tvDisType.setText("Welcome Discount");
                tvDisAmount.setText("$ " + app.discountArray.get(0).getSignup_discount());

                try {


                    finalAmount = Integer.parseInt(drBean.fees) - Integer.parseInt(app.discountArray.get(0).getSignup_discount());
                    Log.i("","Fees of Doctor final welcome : " + finalAmount);
                    if (finalAmount < 1) {
                        finalAmount = Integer.parseInt(drBean.fees);
                        linDiscount.setVisibility(View.GONE);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                /// discount to use : signup_discount
            } else {
                /// discount to use :refer_discount
                tvDisType.setText("Referral Discount");
                tvDisAmount.setText("$ " + app.discountArray.get(0).getRefer_discount());

                try {
                    finalAmount = Integer.parseInt(drBean.fees) - Integer.parseInt(app.discountArray.get(0).getRefer_discount());
                    Log.i("","Fees of Doctor final refer : " + finalAmount);
                    if (finalAmount < 1) {
                        finalAmount = Integer.parseInt(drBean.fees);
                        linDiscount.setVisibility(View.GONE);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            btn_payment_Now.setText("Pay Now  $" + finalAmount);
            drBean.fees = finalAmount + "";


        }
        tv_date.setText(drBean.booking_date);
        tv_time.setText(drBean.booking_time);


        //  PaymentID = "dc" + System.currentTimeMillis();
        btn_payment_Now.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {

                                                   openPaymentAlert();

                                               }
                                           }

        );
    }

    public void openPaymentAlert() {
        CharSequence colors[] = new CharSequence[]{"PayPal", "Stripe"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Payment Method");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (which) {
                    case 0:

                        PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

                        Intent intent = new Intent(Appointment.this, PaymentActivity.class);

                        // send the same configuration for restart resiliency
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                        break;
                    case 1:

                        Intent stripeIntent = new Intent(Appointment.this, StripePaymentActivity.class);
                        stripeIntent.putExtra("dr", drBean);
                        startActivity(stripeIntent);

                        break;
                }
            }
        });
        builder.show();
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS));
        return new PayPalOAuthScopes(scopes);
    }

    protected void displayResultText(String result) {
       /* ((RobottoTextView) findViewById(R.id.txtResult)).setText("Result : " + result);
        Toast.makeText(
                getApplicationContext(),
                result, Toast.LENGTH_LONG)
                .show();*/
    }


    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items =
                {
                        new PayPalItem(drBean.fName + " Consulting charge", 1, new BigDecimal(drBean.fees), "USD",
                                PaymentID),

                };
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        Log.i("Payment", "Payment subtotal : " + subtotal.toString());
        BigDecimal shipping = new BigDecimal(0);
        Log.i("Payment", "Payment shipping : " + shipping.toString());
        BigDecimal tax = new BigDecimal(0);
        Log.i("Payment", "Payment tax : " + tax.toString());
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        Log.i("Payment", "Payment : " + drBean.fees.toString());
        Log.i("Payment", "Payment : " + amount.toString());
        PayPalPayment payment = new PayPalPayment(amount, "USD", drBean.fName + " Consulting  Charge", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);


        return payment;
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {


                       /* {
                            "response": {
                            "state": "approved",
                                    "id": "PAY-18X32451H0459092JKO7KFUI",
                                    "create_time": "2014-07-18T18:46:55Z",
                                    "intent": "sale"
                        },
                            "client": {
                            "platform": "Android",
                                    "paypal_sdk_version": "2.14.2",
                                    "product_name": "PayPal-Android-SDK",
                                    "environment": "mock"
                        },
                            "response_type": "payment"
                        }
                        06-02 05:21:30.830 com.markteq.wms I: {
                            "short_description": "sample item",
                                    "amount": "0.01",
                                    "intent": "sale",
                                    "currency_code": "USD"
                        }*/

                        JSONObject jObj = confirm.toJSONObject();
                        String payId = jObj.getJSONObject("response").getString("id");
                        Log.i(TAG, "Resutl1 : Confirm " + confirm.toJSONObject().toString(4));
                        Log.i(TAG, "Resutl1 : Payment " + confirm.getPayment().toJSONObject().toString(4));


                        //    new CallRequests(this).getPaymentSuccess(payId, PaymentID, SubscriptionID, companyId, "PayPal");


                        new CallRequest(this).createBooking(drBean.row_id, drBean.booking_date, drBean.booking_time, drBean.question,
                                drBean.fees, drBean.dr_id, App.user.getUserID());
                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);


                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Future Payment code received from PayPal");

                    } catch (JSONException e) {


                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {


                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }


    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        bookAppointmentAPI();

    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Application Correlation ID from the SDK
        String correlationId = PayPalConfiguration.getApplicationCorrelationId(this);

        Log.i("FuturePaymentExample", "Application Correlation ID: " + correlationId);

        // TODO: Send correlationId and transaction details to your server for
        // processing with
        // PayPal...
        Toast.makeText(getApplicationContext(), "App Correlation ID received from SDK", Toast.LENGTH_LONG).show();
    }


    public void bookAppointmentAPI() {

        if (!Internet.isAvailable(Appointment.this)) {
            Internet.showAlertDialog(Appointment.this, "Error!", "No Internet Connection", false);

            return;
        }

        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {
                String bookingDate = "2015-06-16";
                String urlStr = "createBooking.php?access_token=testermanishrahul234142test&doctor_id=" + drBean.dr_id + "&user_id=" + App.user.getUserID() + "&booking_date=" + drBean.booking_date + "&booking_time=" + drBean.booking_time + "&amount_status=1&question=" + drBean.question;
                return ServerAPI.generalAPI(Appointment.this, urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
                onGetResult(result);
            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(Appointment.this);
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

    public void onGetResult(JSONObject jsonResponse) {

        String msg = "";
        if (jsonResponse != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {

                        String booking_id = jsonResponse.getString("booking_id");

                        Toast.makeText(Appointment.this, msg, Toast.LENGTH_LONG).show();


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(Appointment.this, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


    public static class AcceptListner extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent b) {
            Log.d("Here", "I am here");

            String dr_id = b.getStringExtra("dr_id");
            String booking_id = b.getStringExtra("booking_id");
            String booking_date = b.getStringExtra("booking_date");
            String booking_time = b.getStringExtra("booking_time");
            String imageURL = b.getStringExtra("dr_imagUrl");
            String fName = b.getStringExtra("Name");
            String question = b.getStringExtra("Question");
            String fees = b.getStringExtra("Fees");

            Intent appoinmentIntent = new Intent(context, Appointment.class);

            appoinmentIntent.putExtra("booking_id", booking_id);
            appoinmentIntent.putExtra("dr_id", dr_id);
            appoinmentIntent.putExtra("booking_date", booking_date);
            appoinmentIntent.putExtra("booking_time", booking_time);
            appoinmentIntent.putExtra("dr_imagUrl", imageURL);
            appoinmentIntent.putExtra("Name", fName);
            appoinmentIntent.putExtra("Question", question);
            appoinmentIntent.putExtra("Fees", fees);

            context.startActivity(appoinmentIntent);
        }
    }

    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {
        try {
            if (result != null && !result.isEmpty()) {
                Log.i("TAG", "TAG Result : " + result);
                //      {"document":{"response":{"status":1,"message":"Success."}}}
                switch (request) {


                    case createBooking:
                        Utils.removeSimpleSpinProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(result);
                            JSONObject docObj = obj.getJSONObject("document");
                            JSONObject resObj = docObj.getJSONObject("response");

                            int status = resObj.getInt("status");

                            if (app.discountArray.size() > 0) {

                                if (App.user.getUserID().equalsIgnoreCase(app.discountArray.get(0).getSignup_id())) {
                                    new CallRequest(this).updateRefer(app.discountArray.get(0).id, "sign_up_used");
                                } else {
                                    new CallRequest(this).updateRefer(app.discountArray.get(0).id, "refer_used");
                                }
                            }

                            if (status == 1) {
                                Toast.makeText(Appointment.this, "Your payment has been made successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, PTDashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }

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
    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS
            request) {

    }
}
