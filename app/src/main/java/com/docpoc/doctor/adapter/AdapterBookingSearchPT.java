package com.docpoc.doctor.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Pt.Appointment;
import com.docpoc.doctor.Pt.DocProfileForUser;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.InterfaceAcceptReject;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterBookingSearchPT extends ArrayAdapter<GlobalBeans.DoctorBean> {
    ProgressDialog mprogressDialog;
    // Declare Variables
    Context mContext;

    LayoutInflater inflater;
    private List<GlobalBeans.DoctorBean> rowItemlist = new ArrayList<GlobalBeans.DoctorBean>();
    private ArrayList<GlobalBeans.DoctorBean> arraylist;
    InterfaceAcceptReject callBack;
    public AQuery aqList;
    public AdapterBookingSearchPT(Context context, int resourceId, List<GlobalBeans.DoctorBean> rowItemlist, InterfaceAcceptReject listener) {

        super(context, resourceId, rowItemlist);

        mContext = context;
        this.rowItemlist = rowItemlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<GlobalBeans.DoctorBean>();
        this.arraylist.addAll(rowItemlist);
        callBack = listener;
        this.aqList = new AQuery(context);
    }

    public class ViewHolder {
        RobottoTextView name;
        RobottoTextView speciality;
        RobottoTextView date_time;
        RobottoTextView price, tvPaid;
        ImageView image;
        ProgressBar pBar;
        Button btn_payNow, btn_cancel;
        LinearLayout ll;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        final GlobalBeans.DoctorBean bean = (GlobalBeans.DoctorBean) getItem(position);

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(com.docpoc.doctor.R.layout.single_list_booking_history_pt, null);
            holder.ll = (LinearLayout) view.findViewById(com.docpoc.doctor.R.id.row_item_ll_payNow_cancel);

            holder.name = (RobottoTextView) view.findViewById(R.id.txtname);
            holder.speciality = (RobottoTextView) view.findViewById(R.id.rowitem_bookingHistory_pt_tv_speciality);
            holder.date_time = (RobottoTextView) view.findViewById(R.id.rowitem_bookingHistory_pt_tv_date_time);
            holder.price = (RobottoTextView) view.findViewById(R.id.txtprice);
            holder.image = (ImageView) view.findViewById(R.id.activity_drProfile_iv_drProfile);
            holder.btn_payNow = (Button) view.findViewById(R.id.row_item_btn_PayNow);
            holder.btn_cancel = (Button) view.findViewById(R.id.row_item_btn_Cancle);
            holder.tvPaid = (RobottoTextView) view.findViewById(R.id.tvPaid);
            holder.pBar = (ProgressBar) view.findViewById(R.id.pBar);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(bean.fName);
        holder.speciality.setText(bean.speciality);
        holder.date_time.setText(bean.booking_date + " " + bean.booking_time);
        holder.price.setText("$ " + bean.fees);



        AQuery aq = aqList.recycle(view);
        aq.id(holder.image).progress(   holder.pBar)
                .image(bean.imageURL.replaceAll(" ","%20"), true, true, 0, R.drawable.aala1, null, 0, 1.0f);


        if (bean.amount_status.equalsIgnoreCase("0")) {
            holder.ll.setVisibility(View.VISIBLE);
            holder.tvPaid.setVisibility(View.GONE);
        } else {
            holder.ll.setVisibility(View.GONE);
            if (bean.status.equalsIgnoreCase("0")) {
                holder.tvPaid.setText(Html.fromHtml("  Status : <font color=\"#d44937\">UnPaid</font>"));
            } else {
                holder.tvPaid.setText(Html.fromHtml("  Status : <font color=\"#2c9b2c\">Paid</font>"));
            }
            holder.tvPaid.setVisibility(View.VISIBLE);
        }


        holder.btn_payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //go to Appointment page
            /*    Bundle b = new Bundle();
                b.putString("dr_id", bean.dr_id);
                b.putString("booking_id", bean.row_id);
                b.putString("booking_date", bean.booking_date);
                b.putString("booking_time", bean.booking_time);
                b.putString("dr_imagUrl", bean.imageURL);
                b.putString("Name", bean.fName + bean.lastName);
                b.putString("Question", bean.question);
                b.putString("Fees", bean.fees);

*/
                Intent intent = new Intent(mContext, Appointment.class);
                intent.putExtra("dr",bean);
                mContext.startActivity(intent);
            }
        });

        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClickAPI(bean);

            }
        });

        // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                GlobalBeans.DoctorBean bean = (GlobalBeans.DoctorBean) arraylist.get(position);
                String name = bean.fName;
                String specility = bean.speciality;
                String address = bean.address;
                String mor_strtTime = bean.morning_appointment_start;
                String mor_endTime = bean.morning_appointment_end;
                String ev_startTime = bean.eve_appointment_start;
                String ev_endTime = bean.eve_appointment_end;
                String aboutMe = bean.about;
                String url = bean.imageURL;
                String dr_id = bean.dr_id;

                Log.i("IMAGE URL : ", bean.imageURL);

                Bundle b = new Bundle();
                b.putString("name", name);
                b.putString("specility", specility);
                b.putString("address", address);
                b.putString("mor_strtTime", mor_strtTime);
                b.putString("mor_endTime", mor_endTime);
                b.putString("ev_startTime", ev_startTime);
                b.putString("ev_endTime", ev_endTime);
                b.putString("aboutMe", aboutMe);
                b.putString("url", url);
                b.putString("dr_id", dr_id);


                if (bean.amount_status.equalsIgnoreCase("1")) {

                } else {
                    Intent newActivity = new Intent(mContext, DocProfileForUser.class);
                    newActivity.putExtra("x", "Doctor Profile");
                    newActivity.putExtra("dr", bean);
                    mContext.startActivity(newActivity);
                }


            }
        });

        return view;
    }


    public void onCancelClickAPI(final GlobalBeans.DoctorBean bean) {


        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {

                String urlStr = "";
                urlStr = "createBooking.php?access_token=testermanishrahul234142test&doctor_id=" + bean.dr_id.toString() + "&user_id=" + App.user.getUserID() + "&booking_date=" + bean.booking_date + "&booking_time=" + bean.booking_time + "&amount_status=0&question=" + bean.question + "&booking_id=" + bean.row_id;
                return ServerAPI.CancelAPI(urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }


                onGetResult_Cancel(result);


            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(mContext);
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

    public void onGetResult_Cancel(JSONObject jsonResponse) {


        String msg = "";
        if (jsonResponse != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {

                        jsonResponse.getString("status");

                        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                .setTitle("")
                                .setMessage("Booking is canceled.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();

                        if (callBack != null) {

                            callBack.acceptReject();
                            callBack = null;

                        }


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


}