package com.docpoc.doctor.Pt.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.ChatActivityNew;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.Messages;
import com.docpoc.doctor.Pt.PTDashboardActivity;
import com.docpoc.doctor.R;
import com.docpoc.doctor.adapter.AdapterBookingSearchPT;
import com.docpoc.doctor.adapter.MessageListAdapter;
import com.docpoc.doctor.classes.InterfaceAcceptReject;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoEditTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundedImageView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class PtMessagesFragment extends Fragment implements AsynchTaskListner{
    // TODO: Rename parameter arguments, choose names that match


    public ProgressBar pBar;
    public PtMessagesFragment instance;
    public String patientID = "";
    public View v;

    public ArrayList<GlobalBeans.DoctorBean> arraylist = new ArrayList<GlobalBeans.DoctorBean>();

    public ImageView iv;
    public String recvImageUrl;

    public String booking_id = "", chat_user_name = "", pt_id = "", dr_id = "";
    public ListView listView;

    public List<GlobalBeans.MessageBean> rowItems;
    public ProgressDialog mprogressDialog;


  
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
          /*  if (!app.mainUser.getUser_id().isEmpty() && shared != null) {

            }*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_messages, container, false);
        instance = this;
        Fabric.with(getActivity(), new Crashlytics());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Utils.logUser();


        listView = (ListView) v.findViewById(R.id.list1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                GlobalBeans.MessageBean bean = (GlobalBeans.MessageBean) rowItems.get(position);
                chat_user_name = bean.fName;
                String date_time = bean.booking_date_time;

                String url = bean.image_url;
                dr_id = bean.dr_id;
                pt_id = bean.pt_id;
                booking_id = bean.booking_id;

                String user_id = App.user.getUserID();
                recvImageUrl = bean.image_url;
                new CallRequest(getActivity()).checkCancelStatus(PtMessagesFragment.this,booking_id, "doctor", dr_id);

/*
                if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)) {
                    String user_id = App.user.getUserID();
                    recvImageUrl = bean.image_url;
                    new CallRequest(Messages.this).checkCancelStatus(booking_id, "patient", user_id);

                } else {
                    String user_id = App.user.getUserID();
                    recvImageUrl = bean.image_url;
                    new CallRequest(Messages.this).checkCancelStatus(booking_id, "doctor", dr_id);

                }*/

            }
        });

        getMessagesAPI();
        return v;
    }

    public void getMessagesAPI() {

        if (!Internet.isAvailable(getActivity())) {
            Internet.showAlertDialog(getActivity(), "Error!", "No Internet Connection", false);

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

                    return ServerAPI.generalAPI(getActivity(), urlStr);
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

                onGetResult_PT(result);
                /*if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR))
                    onGetResult_DR(result);

                else
                    onGetResult_PT(result);*/
            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(getActivity());
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

                        MessageListAdapter adapter = new MessageListAdapter(getActivity(),
                                R.layout.single_list_message, rowItems, false);
                        listView.setAdapter(adapter);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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


                                Utils.showToast("Please try again later", getActivity());
                            } else {


                                Intent in = new Intent( getActivity(), ChatActivityNew.class);


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
                Utils.showToast("Please try again later",  getActivity());
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