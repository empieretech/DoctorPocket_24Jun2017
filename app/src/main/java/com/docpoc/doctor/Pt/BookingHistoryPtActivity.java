package com.docpoc.doctor.Pt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.R;
import com.docpoc.doctor.adapter.AdapterBookingSearchPT;
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

import io.fabric.sdk.android.Fabric;

public class BookingHistoryPtActivity extends Activity  {
	ImageView iv;
	String x;
	
	ListView list;
	AdapterBookingSearchPT adapter;
	EditText editsearch;
	String[] name;
	String[] date;
	String[] time;
	String[] price;
	int[] image;
	ArrayList<GlobalBeans.DoctorBean> arraylist = new ArrayList<GlobalBeans.DoctorBean>();
	ProgressDialog mprogressDialog;
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_booking_history_pt);
		Utils.logUser();

		
		iv=(ImageView) findViewById(R.id.img_back);
		list = (ListView) findViewById(R.id.lv_drList);
		
		x = getIntent().getStringExtra("x");
		AngellinaTextView title = (AngellinaTextView) findViewById(R.id.txtheader);
		title.setText(x);

		AngellinaTextView text = (AngellinaTextView) findViewById(R.id.txtheader);





		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

		getBookingHistory_PT_API();

	}



	public  void  getBookingHistory_PT_API(){

		if(!Internet.isAvailable(BookingHistoryPtActivity.this)){
			Internet.showAlertDialog(BookingHistoryPtActivity.this,  "Error!", "No Internet Connection", false);

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
				return ServerAPI.generalAPI(BookingHistoryPtActivity.this, urlStr);

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
				mprogressDialog = new ProgressDialog(BookingHistoryPtActivity.this);
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

						adapter = new AdapterBookingSearchPT(this, R.layout.single_list_booking_history_pt,  arraylist, new InterfaceAcceptReject(){
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

					Toast.makeText(BookingHistoryPtActivity.this, msg, Toast.LENGTH_LONG).show();
				}


			} catch (JSONException e) {
				Log.e("login", e.toString());
				return;
			}

		}

	}




}
