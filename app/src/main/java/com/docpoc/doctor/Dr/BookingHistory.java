package com.docpoc.doctor.Dr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.R;
import com.docpoc.doctor.adapter.BookingHistoryAdapter;
import com.docpoc.doctor.classes.InterfaceAcceptReject;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class BookingHistory extends Activity implements OnClickListener,
        OnItemClickListener {
    ImageView iv;
    ProgressDialog mprogressDialog;
    BookingHistoryAdapter adapter;




    ListView listView;
    List<GlobalBeans.DrBookingHistoryBean> rowItems;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_booking_history);
        Utils.logUser();
        iv = (ImageView) findViewById(R.id.img_left_arrow);

        iv.setOnClickListener(this);


        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);
        text.setText("Booking History");


        rowItems = new ArrayList<GlobalBeans.DrBookingHistoryBean>();

        listView = (ListView) findViewById(R.id.list_booking);

        listView.setOnItemClickListener(this);


        getBookingHistoryAPI_DR();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + rowItems.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_left_arrow:
                finish();
                break;
        }
        // TODO Auto-generated method stub

    }


    public void getBookingHistoryAPI_DR() {


        if (!Internet.isAvailable(BookingHistory.this)) {
            Internet.showAlertDialog(BookingHistory.this, "Error!", "No Internet Connection", false);

            return;
        }


        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {
                String urlStr = "";
                urlStr = "historyDoctorBooking.php?access_token=testermanishrahul234142test&doctor_id=" + App.user.getUserID();


                return ServerAPI.generalAPI(BookingHistory.this, urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }

                onGetResult_Dr(result);

            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(BookingHistory.this);
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

    public void onGetResult_Dr(JSONObject jsonResponse) {

        String msg = "";
        if (jsonResponse != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {


                        JSONArray dataArr = jsonResponse.getJSONArray("data");

                        rowItems = new ArrayList<GlobalBeans.DrBookingHistoryBean>();
                        for (int i = 0; i < dataArr.length(); i++) {

                            JSONObject jsonObj = dataArr.getJSONObject(i);
                            GlobalBeans.DrBookingHistoryBean bean = new GlobalBeans.DrBookingHistoryBean();


                            bean.row_id = jsonObj.getString("id");
                            bean.pt_id = jsonObj.getString("user_id");
                            bean.fName = jsonObj.getString("firstName");
                            bean.imageURL = jsonObj.getString("image");
                            bean.booking_date = jsonObj.getString("booking_date");
                            bean.booking_time = jsonObj.getString("booking_time");
                            bean.amount_status = jsonObj.getString("amount_status");
                            bean.amount = jsonObj.getString("amount");
                            bean.question = jsonObj.getString("question");
                            bean.status = jsonObj.getString("status");
                            rowItems.add(bean);


                        }

                        adapter = new BookingHistoryAdapter(this,
                                R.layout.single_list_booking_history, rowItems, new InterfaceAcceptReject() {
                            @Override
                            public void acceptReject() {
                                getBookingHistoryAPI_DR();

                            }
                        });
                        listView.setAdapter(adapter);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(BookingHistory.this, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


    /*

    public  void onGetResult_PT(JSONObject jsonResponse){

        String msg = "";
        if (jsonResponse  != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {


                        JSONArray dataArr = jsonResponse.getJSONArray("data");

                        for (int i=0; i< dataArr.length(); i++){

                            JSONObject jsonObj = dataArr.getJSONObject(i);
                            GlobalBeans.DrBookingHistoryBean bean = new  GlobalBeans.DrBookingHistoryBean();


                            bean.dr_id= jsonObj.getString("id");
                            bean.pt_id= jsonObj.getString("user_id");
                            bean.fName= jsonObj.getString("firstName");
                            bean.imageURL = jsonObj.getString("image");
                            bean.booking_date = jsonObj.getString("booking_date");
                            bean.booking_time = jsonObj.getString("booking_time");
                            bean.amount_status = jsonObj.getString("amount_status");
                            bean.amount = jsonObj.getString("amount");
                            bean.question = jsonObj.getString("question");
                            bean.status = jsonObj.getString("status");
                            rowItems.add(bean);


                        }

                        BookingHistoryAdapter adapter = new BookingHistoryAdapter(this,
                                R.layout.single_list_booking_history, rowItems, new InterfaceAcceptReject(){
                            @Override
                            public void acceptReject() {


                            }
                        });
                        listView.setAdapter(adapter);



                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(BookingHistory.this, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


*/
}
