package com.docpoc.doctor.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.docpoc.doctor.App;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.InterfaceAcceptReject;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BookingHistoryAdapter extends ArrayAdapter<GlobalBeans.DrBookingHistoryBean> {

    Context context;
    ProgressDialog mprogressDialog;
    InterfaceAcceptReject callBack;
    public AQuery aqList;

    public BookingHistoryAdapter(Context context, int resourceId, List<GlobalBeans.DrBookingHistoryBean> items, InterfaceAcceptReject listener) {
        super(context, resourceId, items);
        this.context = context;
        this.callBack = listener;
        this.aqList = new AQuery(context);
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageperson;
        RobottoTextView name;
        RobottoTextView price;
        RobottoTextView date;
        RobottoTextView time;
        RobottoTextView status;
        LinearLayout ll;
        ProgressBar pBar;
        Button btn_accept;
        Button btn_reject;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final GlobalBeans.DrBookingHistoryBean bean = (GlobalBeans.DrBookingHistoryBean) getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_list_booking_history, null);

            holder = new ViewHolder();

            holder.name = (RobottoTextView) convertView.findViewById(R.id.txtname);
            holder.price = (RobottoTextView) convertView.findViewById(R.id.txtprice);
            holder.date = (RobottoTextView) convertView.findViewById(R.id.txtdate);
            holder.time = (RobottoTextView) convertView.findViewById(R.id.txttime);
            holder.status = (RobottoTextView) convertView.findViewById(R.id.txtStatus);

            holder.pBar = (ProgressBar) convertView.findViewById(R.id.pBar);
            holder.imageperson = (ImageView) convertView.findViewById(R.id.activity_drProfile_iv_drProfile);


            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        holder.name.setText(bean.fName);
        holder.price.setText("$ " + bean.amount);
        holder.date.setText(bean.booking_date);
        holder.time.setText(bean.booking_time);
        //	holder.imageperson.setImageResource(rowItem.getImageperson());
        AQuery aq = aqList.recycle(convertView);
        aq.id(holder.imageperson).progress(holder.pBar)
                .image(bean.imageURL.replaceAll(" ", "%20"), true, true, 0, R.drawable.aala1, null, 0, 1.0f);


        if (bean.amount_status.equalsIgnoreCase("1")) {
            holder.status.setText(Html.fromHtml("Status : <font color=\"#00ff00\">Paid</font>"));
        } else {
            holder.status.setText(Html.fromHtml("Status : <font color=\"#ff0000\">UnPaid</font>"));
        }

        return convertView;
    }


    public void onAccept_Reject_ClickAPI(final GlobalBeans.DrBookingHistoryBean bean, final String serviceName) {


        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {

                String urlStr = "";
                if (serviceName.equalsIgnoreCase("Accept"))
                    urlStr = "accept_reject.php?access_token=testermanishrahul234142test&booking_id=" + bean.row_id + "&status=accept&doctor_id=" + App.user.getUserID();
                else
                    urlStr = "accept_reject.php?access_token=testermanishrahul234142test&booking_id=" + bean.row_id + "&status=reject&doctor_id=" + App.user.getUserID();

                return ServerAPI.CancelAPI(urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }

                onGetResult_Cancel(result, serviceName);


            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(context);
                mprogressDialog.setMessage("Please wait...");
                mprogressDialog.setCancelable(false);
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

    public void onGetResult_Cancel(JSONObject jsonResponse, String ServiceName) {

        String msg = "";
        if (jsonResponse != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {

                        jsonResponse.getString("status");

                        if (ServiceName.equalsIgnoreCase("Accept")) {
                            new AlertDialog.Builder(context)
                                    .setTitle("")
                                    .setMessage("Booking is accepted.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();


                                        }
                                    })
                                    .show();
                            if (callBack != null) {

                                callBack.acceptReject();
                                callBack = null;

                            }


                        } else {

                            new AlertDialog.Builder(context)
                                    .setTitle("")
                                    .setMessage("Booking is rejected.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();


                                        }
                                    })
                                    .show();
                            if (callBack != null) {

                                callBack.acceptReject();
                                callBack = null;

                            }

                        }


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


}
