package com.docpoc.doctor.Pt.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Interface.ClickBook;
import com.docpoc.doctor.Pt.DocProfileForUser;
import com.docpoc.doctor.Pt.StripePaymentActivity;
import com.docpoc.doctor.R;
import com.docpoc.doctor.adapter.DrSearchListViewAdapter;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class DrListFragment extends Fragment implements ClickBook {
    // TODO: Rename parameter arguments, choose names that match


    public  ImageView imgBack;
    public  String x;
    Button btn_send;
    final Calendar myCalendar = Calendar.getInstance();


    private Calendar calendar;
    private int year, month, day;


    TextView tv_Sdate,tv_Stime;
    public  ProgressDialog mprogressDialog;

    public EditText editsearch;
    public static Context staticInstance;

    public static DrListFragment staticFragment;
    public Context instance;


    public ListView listView;
    // List<RowItem> rowItems;
    public View v;
    public ArrayList<GlobalBeans.DoctorBean> arraylist = new ArrayList<GlobalBeans.DoctorBean>();

    /**
     * Called when the activity is first created.
     */
    public  DrSearchListViewAdapter adapter;

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
        // Inflate the layout for this fragment

        instance = getActivity();
        staticInstance = getActivity();
        staticFragment = this;
        v = inflater.inflate(R.layout.activity_dr_list, container, false);
        Fabric.with(getActivity(), new Crashlytics());

        Utils.logUser();

         calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);



/*

        imgBack = (ImageView) v.findViewById(R.id.img_back);
        imgBack.setOnClickListener();
*/


        AngellinaTextView text = (AngellinaTextView) v.findViewById(R.id.txtheader);



      /*  arraylist = new ArrayList<GlobalBeans.DoctorBean>();
        for (int i = 0; i < titles.length; i++) {
            RowItem item = new RowItem(images[i], titles[i], speciality[i],availability[i],availability1[i] );
            arraylist.add(item);
        }*/

        listView = (ListView) v.findViewById(R.id.list1);
        adapter = new DrSearchListViewAdapter(this,  R.layout.single_list_dr_search, arraylist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalBeans.DoctorBean bean = (GlobalBeans.DoctorBean) arraylist.get(position);


                Bundle b = new Bundle();
                b.putSerializable("dr", bean);

                Intent newActivity = new Intent(getActivity(), DocProfileForUser.class);
                newActivity.putExtra("x", "x");
                newActivity.putExtras(b);
                startActivity(newActivity);
            }
        });
        editsearch = (EditText) v.findViewById(R.id.edit_search);
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);


        // Capture Text in EditText
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

        return v;
    }
    @Override
    public void setOnBookClick(GlobalBeans.DoctorBean bean) {
        selectDateTime(bean);

    }
    public void selectDateTime(final GlobalBeans.DoctorBean bean) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(staticInstance);
        final AlertDialog selectDateTime = dialogBuilder.create();
        LayoutInflater inflater =  (LayoutInflater) instance
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_appointment, null);
        tv_Sdate=(TextView)view.findViewById(R.id.edt_date);
        tv_Stime=(TextView)view.findViewById(R.id.edt_time);
        btn_send=(Button)view.findViewById(R.id.btn_send);

        selectDateTime.setView(view);
        selectDateTime.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        selectDateTime.show();
        java.text.DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ct = (String) android.text.format.DateFormat.format("yyyy-MM-dd", new Date());
        String ctime = java.text.DateFormat.getTimeInstance().format(new Date());

        tv_Stime.setText(ctime);


        tv_Sdate.setText(ct);


        tv_Sdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDays();
                    }

                };
                new DatePickerDialog(staticInstance, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tv_Stime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(staticInstance,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                updateTime(hourOfDay, minute, c.get(Calendar.SECOND));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        bean.booking_time = ctime;
        bean.booking_date = ct;

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(staticInstance, StripePaymentActivity.class);
                intent.putExtra("dr", bean);


                startActivity(intent);
            }
        });



    }
    private void updateDays() {

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tv_Sdate.setText(sdf.format(myCalendar.getTime()));
    }
    private void updateTime(int hours, int mins, int sec) {

        String h = hours + "";
        String m = mins + "";
        String s = sec + "";
        if (hours < 10) {
            h = "0" + String.valueOf(hours);
        }
        if (sec < 10) {
            s = "0" + String.valueOf(sec);
        }

        if (mins < 10) {
            m = "0" + String.valueOf(mins);
        }
        // Append in a StringBuilder
        String aTime = new StringBuilder().append(h).append(':')
                .append(m).append(":").append(s).toString();

        tv_Stime.setText(aTime);
    }




    public void getDoctorListAPI() {

        if (!Internet.isAvailable(getActivity())) {
            Internet.showAlertDialog(getActivity(), "Error!", "No Internet Connection", false);

            return;
        }

        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {
                String urlStr = "doctorList.php?access_token=testermanishrahul234142test&user_id=" + App.user.getUserID();
                return ServerAPI.generalAPI(getActivity(), urlStr);

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

    public void onGetResult(JSONObject jsonResponse) {

        String msg = "";
        if (jsonResponse != null) {
            try {

                msg = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {


                        JSONArray dataArr = jsonResponse.getJSONArray("data");

                        for (int i = 0; i < dataArr.length(); i++) {

                            JSONObject jsonObj = dataArr.getJSONObject(i);
                            GlobalBeans.DoctorBean bean = new GlobalBeans.DoctorBean();

                            bean.pt_id = jsonObj.getString("id");
                            bean.fName = jsonObj.getString("firstName");
                            bean.lastName = jsonObj.getString("lastName");

                            bean.imageURL = jsonObj.getString("image");
                            bean.speciality = jsonObj.getString("speciality");
                            bean.morning_appointment_start = jsonObj.getString("morning_appointment_start");
                            bean.morning_appointment_end = jsonObj.getString("morning_appointment_end");
                            bean.eve_appointment_start = jsonObj.getString("eve_appointment_start");
                            bean.eve_appointment_end = jsonObj.getString("eve_appointment_end");
                            bean.fees = jsonObj.getString("amount");
                            bean.address = jsonObj.getString("address");
                            bean.about = jsonObj.getString("about");
                            bean.phoneNumber = jsonObj.getString("phoneNumber");
                            bean.dr_id = jsonObj.getString("doctor_id");

                            arraylist.add(bean);

                        }
                        adapter = new DrSearchListViewAdapter(this, R.layout.single_list_dr_search, arraylist);
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


}