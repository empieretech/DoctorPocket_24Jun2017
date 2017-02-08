package com.docpoc.doctor.gcm;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.WindowManager;

import com.docpoc.doctor.App;
import com.docpoc.doctor.ChatActivity;
import com.docpoc.doctor.Dr.DrDashboardActivity;
import com.docpoc.doctor.PreLogin;
import com.docpoc.doctor.Pt.Appointment;
import com.docpoc.doctor.R;
import com.docpoc.doctor.utils.ForegroundCheckTask;
import com.docpoc.doctor.webServices.MyConstants;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MyGcmListenerService extends GcmListenerService {

    static Context contextT;
    private static final String TAG = "GCMIntentService";
    public static int notificationCounter;
    public static final int NOTIFICATION_ID = 1;

    public MyGcmListenerService() {
        super();
    }

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public Context ct;

    public MyGcmListenerService(Context context) {
        this.contextT = context;
    }

    public static String bundle2string(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        this.ct = getApplicationContext();
        Log.i("", "Received message");

        handleData(ct, data);

    }

    public void handleData(final Context context, Bundle extra) {

        try {


            //   JSONObject json = new JSONObject(extra.getString("data"));

            //   String value = json.getString("value");

            String value = extra.getString("message");
            Log.i("GCM", "GCM messsssage  : " + value);
            if (value.contains("has just started!")) {
                // start a new ch
                    sendNotification(value);
                return;
            } else {
                SharedPreferences shared = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);

                if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {

                    if (value.contains("has answered")) {
                        // start a new ch
                        sendNotification(value);

                        return;
                    }
                    if (value.contains("Canceled the Payment")) {
                        // start a new ch
                        sendNotification(value);

                        return;
                    } else if (value.contains("appointment has been closed")) {
                        sendNotification(value);

                    } else if (value.contains("Submit the Payment")) {
                        sendNotification(value);


                    } else if (value.contains("Chat")) {
                        sendNotification(value);
                    } else {
                        sendNotification(value);
                    }


                } else {

                    // Patient

//                Dear vijayt, your appointment with Dr. Jeffery Khoury  has been Accepted.
// It will take place on 12-01-2016 15:40 PM-16:00 PM. The cost of the appointment will be $35,
// please pay now. Thank you!
                    if (value.contains("has answered")) {
                        // start a new ch
                        sendNotification(value);

                        return;
                    }
                    if (value.contains("take place on")) {
                        // start a new ch

                        String amountStr = value.substring(value.indexOf("will be ") + 8, value.indexOf(", please pay now"));

                        String dateTimeStr = value.substring(value.indexOf("place on ") + 9, value.indexOf(". The cost of the"));
                        String timeStr = dateTimeStr.substring(dateTimeStr.indexOf(" ") + 1, dateTimeStr.length());

                        JSONObject json = new JSONObject(extra.getString("data"));
                        String paypalBookingId = json.getString("booking_id");
                        String doctorId = json.getString("doctor_id");
                        String doctor_name = json.getString("doctor_name");
                        String patient_id = json.getString("patient_id");

                        boolean isAppRunning = false;
                        try {
                            isAppRunning = new ForegroundCheckTask().execute(context).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        paymentNotification(value, amountStr, dateTimeStr, timeStr, paypalBookingId, doctorId, patient_id, doctor_name);
                    /*if (isAppRunning) {
                        //startActivity(new Intent(context, MyDialog.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                          openPaymentAlert(context,value, amountStr, dateTimeStr, timeStr, paypalBookingId, doctorId, doctor_name);
                    } else {

                    }*/
                    } else if (value.contains("has been declined")) {
                        sendNotification(value);

                    } else if (value.contains("appointment has been closed")) {
                        sendNotification(value);
                        JSONObject json = new JSONObject(extra.getString("message"));
                        String feedbackBookingID = json.getString("booking_id");
                        String feedbackDoctorID = json.getString("doctor_id");


                    } else {
                        sendNotification(value);

                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void openAcceptRejectAlert(final String message, final String bookingId, final String docId) {


        Date d = new Date();
        CharSequence currentDate = DateFormat.format("yyyy-MM-dd", d.getTime());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ct);

        // set title
        alertDialogBuilder.setTitle("Dr. Pocket");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent acceptInent = new Intent(ct, DrDashboardActivity.class);

                        acceptInent.putExtra("booking_id", bookingId);
                        acceptInent.putExtra("docId", docId);
                        acceptInent.putExtra("is_accepted", true);

                        startActivity(acceptInent);

                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent acceptInent = new Intent(ct, DrDashboardActivity.class);

                        acceptInent.putExtra("booking_id", bookingId);
                        acceptInent.putExtra("docId", docId);
                        acceptInent.putExtra("is_accepted", true);

                        startActivity(acceptInent);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    public void openPaymentAlert(final Context context, final String message, final String amountStr
            , final String dateTimeStr, final String timeStr, final String paypalBookingId, final String doctorId,
                                 final String doctor_name) {

        Date d = new Date();
        CharSequence currentDate = DateFormat.format("yyyy-MM-dd", d.getTime());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getApplicationContext());

        // set title
        alertDialogBuilder.setTitle("Dr. Pocket");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        Intent acceptInent = new Intent(getApplicationContext(), Appointment.class);

     /*   drBean.dr_id =  b.getString("dr_id") ;
        drBean.row_id  = b.getString("booking_id");
        drBean.booking_date = b.getString("booking_date");
        drBean.booking_time = b.getString("booking_time");
        drBean.imageURL = b.getString("dr_imagUrl");
        drBean.fName = b.getString("Name");
        drBean.question = b.getString("Question");
        drBean.fees = b.getString("Fees");
*/
                        acceptInent.putExtra("booking_id", paypalBookingId);
                        acceptInent.putExtra("dr_id", doctorId);
                        acceptInent.putExtra("booking_date", dateTimeStr);
                        acceptInent.putExtra("booking_time", timeStr);
                        acceptInent.putExtra("dr_imagUrl", "");
                        acceptInent.putExtra("Name", doctor_name);
                        acceptInent.putExtra("Question", "");
                        acceptInent.putExtra("Fees", amountStr);

                        startActivity(acceptInent);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        // show it
        alertDialog.show();


    }


    public void showAlert(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ct);

        // set title
        alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PreLogin.class), 0);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Dr. Pocket")
                        .setSound(uri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private void sendNotificationWith(String msg) {
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PreLogin.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(getNotificationIcon(mBuilder))
                        .setContentTitle("Dr. Pocket")
                        .setSound(uri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void acceptRejectNotification(String message, String bookingId, String docId) {
        Intent acceptInent = new Intent(this, DrDashboardActivity.class);
        acceptInent.putExtra("booking_id", bookingId);
        acceptInent.putExtra("docId", docId);
        acceptInent.putExtra("is_accepted", true);

        PendingIntent pendingAccept = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), acceptInent, 0);


        Intent rejectIntent = new Intent(this, DrDashboardActivity.class);
        rejectIntent.putExtra("booking_id", bookingId);
        rejectIntent.putExtra("docId", docId);
        rejectIntent.putExtra("is_accepted", false);
        PendingIntent pendingReject = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), rejectIntent, 0);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), new Intent(), 0);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder n = new NotificationCompat.Builder(this)
                .setContentTitle("Dr. Pocket")
                .setContentText(message)
                .setSound(uri)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(0, "Accept", pendingAccept)
                .addAction(0, "Reject", pendingReject);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n.build());
    }

    public void paymentNotification(final String message, final String amountStr
            , final String dateTimeStr, final String timeStr, final String paypalBookingId, final String doctorId,
                                    final String patient_id, final String doctor_name) {


        Intent acceptInent = new Intent(this, Appointment.class);
        acceptInent.putExtra("booking_id", paypalBookingId);
        acceptInent.putExtra("dr_id", doctorId);
        acceptInent.putExtra("booking_date", dateTimeStr);
        acceptInent.putExtra("booking_time", timeStr);
        acceptInent.putExtra("dr_imagUrl", "");
        acceptInent.putExtra("Name", doctor_name);
        acceptInent.putExtra("Question", "");
        acceptInent.putExtra("Fees", amountStr);


        acceptInent.putExtra("booking_id", paypalBookingId);
        acceptInent.putExtra("dr_id", doctorId);
        acceptInent.putExtra("patient_id", patient_id);


        PendingIntent pendingAccept = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), acceptInent, 0);


        Intent rejectIntent = new Intent(this, DrDashboardActivity.class);

        PendingIntent pendingReject = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), rejectIntent, 0);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Constructs the Builder object.
        NotificationCompat.Builder n =
                new NotificationCompat.Builder(this)

                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Dr. Pocket")
                        .setSound(uri)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .addAction(R.drawable.ic_launcher, "PayNow", pendingAccept)
                        .addAction(R.drawable.ic_launcher, "Reject", pendingReject);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n.build());

    }

    public void acceptRejectNotification(final String message, final String amountStr
            , final String dateTimeStr, final String timeStr, final String paypalBookingId, final String doctorId,
                                         final String patient_id, final String doctor_name) {


        Intent acceptInent = new Intent(this, ChatActivity.class);
       /* acceptInent.putExtra("booking_id", paypalBookingId);
        acceptInent.putExtra("dr_id", doctorId);
        acceptInent.putExtra("booking_date", dateTimeStr);
        acceptInent.putExtra("booking_time", timeStr);
        acceptInent.putExtra("dr_imagUrl", "");
        acceptInent.putExtra("Name", doctor_name);
        acceptInent.putExtra("Question", "");
        acceptInent.putExtra("Fees", amountStr);*/


        acceptInent.putExtra("booking_id", paypalBookingId);
        acceptInent.putExtra("dr_id", doctorId);
        acceptInent.putExtra("patient_id", patient_id);


        PendingIntent pendingAccept = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), acceptInent, 0);


        Intent rejectIntent = new Intent(this, DrDashboardActivity.class);

        PendingIntent pendingReject = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), rejectIntent, 0);


        // Constructs the Builder object.
        NotificationCompat.Builder n =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Dr. Pocket")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .addAction(R.drawable.ic_launcher, "Accept", pendingAccept)
                        .addAction(R.drawable.ic_launcher, "Reject", pendingReject);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n.build());

    }
    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = 0x008000;
            notificationBuilder.setColor(color);
            return R.drawable.ic_launcher;

        } else {
            return R.drawable.ic_launcher;
        }
    }

}