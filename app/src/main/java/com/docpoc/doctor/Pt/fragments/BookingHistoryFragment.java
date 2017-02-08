package com.docpoc.doctor.Pt.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.docpoc.doctor.Pt.BookingHistoryPtActivity;
import com.docpoc.doctor.Pt.PTDashboardActivity;
import com.docpoc.doctor.R;
import com.docpoc.doctor.adapter.AdapterBookingSearchPT;
import com.docpoc.doctor.adapter.MessageListAdapter;
import com.docpoc.doctor.classes.InterfaceAcceptReject;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.RobottoEditTextView;
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

public class BookingHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match



    String x;
    BookingHistoryFragment instance;
    ListView list;
    AdapterBookingSearchPT adapter;
    EditText editsearch;
    String[] name;
    String[] date;
    String[] time;
    String[] price;
    int[] image;
    RobottoEditTextView etName;
    ArrayList<GlobalBeans.DoctorBean> arraylist = new ArrayList<GlobalBeans.DoctorBean>();
    ProgressDialog mprogressDialog;
    public View v;



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

        v = inflater.inflate(R.layout.activity_booking_history_pt, container, false);
        instance = this;
        etName=(RobottoEditTextView)v.findViewById(R.id.etName);
        Fabric.with(getActivity(), new Crashlytics());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Utils.logUser();
        list = (ListView) v.findViewById(R.id.lv_drList);
        PTDashboardActivity.setTitle("Booking History");

        getBookingHistory_PT_API();
        return v;
    }

    public  void  getBookingHistory_PT_API(){

        if(!Internet.isAvailable(getActivity())){
            Internet.showAlertDialog(getActivity(),  "Error!", "No Internet Connection", false);

            return;
        }

        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {

                String urlStr = "";
		/*		if(App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR)){
					urlStr = "historyDoctorBooking.php?access_token=testermanishrahul234142test&doctor_id=" + App.user.getUserID();
				}else{
					urlStr = "historyBooking.php?access_token=testermanishrahul234142test&user_id=" + App.user.getUserID();
				}
*/
                urlStr = "historyBooking.php?access_token=testermanishrahul234142test&user_id=" + App.user.getUserID();
                return ServerAPI.generalAPI(getActivity(), urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
                onGetResult_Pt(result);



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
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
            }
        });
    }

    public  void onGetResult_Pt(JSONObject jsonResponse){

        String msg = "";
        if (jsonResponse  != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {


                        JSONArray dataArr = jsonResponse.getJSONArray("data");
                        arraylist = new ArrayList<GlobalBeans.DoctorBean>();

                        for (int i=0; i< dataArr.length(); i++){

                            JSONObject jsonObj = dataArr.getJSONObject(i);
                            GlobalBeans.DoctorBean bean = new  GlobalBeans.DoctorBean();


                            bean.fName = jsonObj.getString("firstName");
                            bean.lastName= jsonObj.getString("lastName");
                            bean.status= jsonObj.getString("status");
                            bean.amount_status = jsonObj.getString("amount_status");
                            bean.speciality= jsonObj.getString("speciality");
                            bean.imageURL= jsonObj.getString("image");
                            bean.booking_date = jsonObj.getString("booking_date");
                            bean.booking_time= jsonObj.getString("booking_time");

                            bean.morning_appointment_start = jsonObj.getString("morning_appointment_start");
                            bean.morning_appointment_end = jsonObj.getString("morning_appointment_end");

                            bean.eve_appointment_start= jsonObj.getString("eve_appointment_start");
                            bean.eve_appointment_end = jsonObj.getString("eve_appointment_end");

                            bean.fees = jsonObj.getString("amount");
                            bean.dr_id = jsonObj.getString("doctor_id");
                            bean.row_id = jsonObj.getString("id");
                            bean.question = jsonObj.getString("question");




                            arraylist.add(bean);


                        }

                        adapter = new AdapterBookingSearchPT(getActivity(), R.layout.single_list_booking_history_pt,  arraylist, new InterfaceAcceptReject(){
                            @Override
                            public void acceptReject() {
                                getBookingHistory_PT_API();
                            }
                        });

                        list.setAdapter(adapter);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                } else {

                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
                return;
            }

        }

    }

}