package com.docpoc.doctor.Dr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Pt.PatientProfile;
import com.docpoc.doctor.R;
import com.docpoc.doctor.adapter.BookingHistorySearchDocAdapter;
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
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class BookingHistorySearchDoc extends Activity implements OnClickListener,
        OnItemClickListener {
	ImageView iv;
    ProgressDialog mprogressDialog;
    EditText editsearch;
    BookingHistorySearchDocAdapter adapter;





    ListView listView;
    List<GlobalBeans.DrBookingHistoryBean> rowItems;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_booking_history_search_doc);

        Utils.logUser();
        iv=(ImageView) findViewById(R.id.imgBack);
        editsearch = (EditText) findViewById(R.id.edit_search);
        
        iv.setOnClickListener(this);


        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.txtheader);


 
        rowItems = new ArrayList<GlobalBeans.DrBookingHistoryBean>();

 
        listView = (ListView) findViewById(R.id.list_booking);
        adapter = new BookingHistorySearchDocAdapter(this, R.layout.single_book_history_search, rowItems);
        listView.setOnItemClickListener(this);

        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });


        getDoctorListAPI();
    }
 
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {


        GlobalBeans.DrBookingHistoryBean bean = (GlobalBeans.DrBookingHistoryBean) rowItems.get(position);

        Intent intent = new Intent(BookingHistorySearchDoc.this, PatientProfile.class);
        intent.putExtra("x", "Patient Profile");
        intent.putExtra("patientID", bean.pt_id);
        startActivity(intent);
    }

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.imgBack:
			finish();
					break;
			}
		// TODO Auto-generated method stub
		
	}


    public  void  getDoctorListAPI(){

        if(!Internet.isAvailable(BookingHistorySearchDoc.this)){
            Internet.showAlertDialog(BookingHistorySearchDoc.this,  "Error!", "No Internet Connection", false);

            return;
        }


    BaseTask.run(new BaseTask.TaskListener() {
        @Override
        public JSONObject onTaskRunning(int taskId, Object data) {
            String urlStr = "historyDoctorBooking.php?access_token=testermanishrahul234142test&doctor_id=" + App.user.getUserID();
            return ServerAPI.generalAPI(BookingHistorySearchDoc.this, urlStr);

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
            mprogressDialog = new ProgressDialog( BookingHistorySearchDoc.this);
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

    public  void onGetResult(JSONObject jsonResponse){

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

                        adapter = new BookingHistorySearchDocAdapter(this, R.layout.single_book_history_search, rowItems);
                        listView.setAdapter(adapter);



                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(BookingHistorySearchDoc.this, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }




}

