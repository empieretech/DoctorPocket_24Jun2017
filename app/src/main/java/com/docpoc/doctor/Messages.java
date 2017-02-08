package com.docpoc.doctor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.adapter.MessageListAdapter;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class Messages extends Activity implements OnClickListener,
        OnItemClickListener, AsynchTaskListner {

    ImageView iv;
    public String recvImageUrl;




    ListView listView;

    List<GlobalBeans.MessageBean> rowItems;
    ProgressDialog mprogressDialog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_messages);
        Utils.logUser();


        iv = (ImageView) findViewById(R.id.img_left_arrow);

        iv.setOnClickListener(this);


        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);
        text.setText("Messages");

        listView = (ListView) findViewById(R.id.list1);

        listView.setOnItemClickListener(this);

        getMessagesAPI();
    }

    public String booking_id = "", chat_user_name = "", pt_id = "", dr_id = "";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
      /*  Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + rowItems.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();*/


        GlobalBeans.MessageBean bean = (GlobalBeans.MessageBean) rowItems.get(position);
        chat_user_name = bean.fName;
        String date_time = bean.booking_date_time;

        String url = bean.image_url;
        dr_id = bean.dr_id;
        pt_id = bean.pt_id;
        booking_id = bean.booking_id;


        SharedPreferences shared = getSharedPreferences(MyConstants.PREF, Context.MODE_PRIVATE);

        if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
            String user_id = App.user.getUserID();
            recvImageUrl = bean.image_url;
            new CallRequest(Messages.this).checkCancelStatus(booking_id, "patient", user_id);

        } else {
            String user_id = App.user.getUserID();
            recvImageUrl = bean.image_url;
            new CallRequest(Messages.this).checkCancelStatus(booking_id, "doctor", dr_id);

        }


      /*  Intent newActivity = new Intent(Messages.this, Appointment.class);
        newActivity.putExtra("x", "Doctor Profile");
        newActivity.putExtras(b);
        startActivity(newActivity);*/


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.img_left_arrow:
                finish();
                break;
        }

    }


    public void getMessagesAPI() {

        if (!Internet.isAvailable(Messages.this)) {
            Internet.showAlertDialog(Messages.this, "Error!", "No Internet Connection", false);

            return;
        }

        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {
                String urlStr = "";
                try {
                    if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR))
                        urlStr = "messageDoctorGet.php?access_token=testermanishrahul234142test&doctor_id=" + App.user.getUserID();
                    else
                        urlStr = "messageGet.php?access_token=testermanishrahul234142test&patient_id=" + App.user.getUserID();

                    return ServerAPI.generalAPI(Messages.this, urlStr);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return null;
                }



                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
                if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR))
                    onGetResult_DR(result);

                else
                    onGetResult_PT(result);
            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(Messages.this);
                mprogressDialog.setMessage("Please wait...");
                mprogressDialog.setCancelable(false);
                mprogressDialog.show();
            }

            @Override
            public void onTaskCancelled(int taskId) {
            }
        });
    }

    public void onGetResult_PT(JSONObject jsonResponse) {

        String msg = "";
        if (jsonResponse != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {


                        JSONArray dataArr = jsonResponse.getJSONArray("data");

                        rowItems = new ArrayList<GlobalBeans.MessageBean>();
                        for (int i = 0; i < dataArr.length(); i++) {

                            JSONObject jsonObj = dataArr.getJSONObject(i);
                            GlobalBeans.MessageBean bean = new GlobalBeans.MessageBean();

                            bean.dr_id = jsonObj.getString("doctor_id");

                            bean.fName = jsonObj.getString("firstName");
                            bean.image_url = jsonObj.getString("doctor_image");
                            bean.booking_id = jsonObj.getString("booking_id");
                            bean.booking_date_time = jsonObj.getString("patient_booking_date_time");

                            rowItems.add(bean);


                        }
                        boolean isDoctor = false;
                        if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                            isDoctor = true;
                        }
                        MessageListAdapter adapter = new MessageListAdapter(this,
                                R.layout.single_list_message, rowItems, isDoctor);
                        listView.setAdapter(adapter);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(Messages.this, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


    public void onGetResult_DR(JSONObject jsonResponse) {

        String msg = "";
        if (jsonResponse != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {


                        JSONArray dataArr = jsonResponse.getJSONArray("data");

                        rowItems = new ArrayList<GlobalBeans.MessageBean>();
                        for (int i = 0; i < dataArr.length(); i++) {

                            JSONObject jsonObj = dataArr.getJSONObject(i);
                            GlobalBeans.MessageBean bean = new GlobalBeans.MessageBean();

                            bean.pt_id = jsonObj.getString("patient_id");
                            bean.fName = jsonObj.getString("firstName");
                            bean.image_url = jsonObj.getString("patient_image");
                            bean.booking_id = jsonObj.getString("booking_id");
                            bean.booking_date_time = jsonObj.getString("doctor_booking_date_time");

                            rowItems.add(bean);


                        }
                        MessageListAdapter adapter = new MessageListAdapter(this,
                                R.layout.single_list_message, rowItems, true);
                        listView.setAdapter(adapter);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(Messages.this, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }

    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {
        try {
            if (result != null && !result.isEmpty()) {
                Log.i("TAG", "TAG Result : " + result);
                //      {"document":{"response":{"status":1,"message":"Success."}}}
                switch (request) {


                    case checkCancelStatus:
                        Utils.removeSimpleSpinProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(result);

                            boolean success = obj.getBoolean("success");

                            if (!success) {


                                Utils.showToast("Please try again later", this);
                            } else {


                                Intent in = new Intent(Messages.this, ChatActivityNew.class);


                                in.putExtra("uniqueChatId", booking_id);
                                in.putExtra("dr_id", dr_id);
                                in.putExtra("recvImageUrl", recvImageUrl);
                                in.putExtra("pt_id", pt_id);
                                in.putExtra("fName", rowItems.get(0).fName);
                                startActivity(in);
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
    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS request) {

    }

}
