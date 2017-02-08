package com.docpoc.doctor.Pt;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundedImageView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class DocProfileForUser extends Activity implements OnClickListener, AsynchTaskListner {
    public RoundedImageView popup;
    private EditText et;
    Spinner sp1;
    Button btn_send;
    String x, selectedTime;
    TextView tv_drName, tvDoctorCurrentAvail,txt_aboutme, tv_specility, tv_address, tv_startTime, tv_endTime, tv_aboutMe, tv_send;
    EditText et_date, tv_question;
    RoundedImageView img_drProfile;
    public String dispTime;
    TextView tv_Sdate,tv_Stime,fee;
    GlobalBeans.DoctorBean drBean;
    ProgressDialog mprogressDialog;
    final Calendar myCalendar = Calendar.getInstance();


    private Calendar calendar;
    private int year, month, day;
    public ProgressBar pBar;
    public ArrayList<String> patient_morning_timeslot = new ArrayList<>(),
            doctor_morning_timeslot = new ArrayList<>(), doctor_evening_timeslot = new ArrayList<>(), patient_evening_timeslot = new ArrayList<>(),
            booking_mornig_timeslot = new ArrayList<>(), booking_evening_timeslot = new ArrayList<>(), patientArray = new ArrayList<>(),
            doctorArray = new ArrayList<>(), bookingArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_doc_profile_for_user);
        Utils.logUser();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        pBar = (ProgressBar) findViewById(R.id.pBar);

        ImageView iv = (ImageView) findViewById(R.id.imgBack);
        popup=(RoundedImageView) findViewById(R.id.popup);
        popup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateTime();
            }
        });

        x = getIntent().getStringExtra("x");
        Bundle b = getIntent().getExtras();

        drBean = (GlobalBeans.DoctorBean) b.getSerializable("dr");


        TextView title = (TextView) findViewById(R.id.tvHeader);
        title.setText("");
        tv_drName = (TextView) findViewById(R.id.dr_name);
        tv_specility = (TextView) findViewById(R.id.genral);
        tv_address = (TextView) findViewById(R.id.place);
        tv_startTime = (TextView) findViewById(R.id.tv_startTime);
        tv_endTime = (TextView) findViewById(R.id.tv_endTime);
        txt_aboutme=(TextView)findViewById(R.id.txt_aboutme);
        fee = (TextView) findViewById(R.id.fee);

        img_drProfile = (RoundedImageView) findViewById(R.id.img_drProfile);



        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);


        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.imgBack:
                        finish();
                        break;
                }
            }
        });

        // ib = (ImageView) findViewById(R.id.img_cal);
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        et = (EditText) findViewById(R.id.edt_date);
        //  ib.setOnClickListener(this);


        new CallRequest(this).getDoctorProfile(drBean.dr_id);
    }

    private void selectDateTime() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DocProfileForUser.this);
        final AlertDialog selectImageDialog = dialogBuilder.create();
        LayoutInflater inflater = DocProfileForUser.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_appointment, null);
         tv_Sdate=(TextView)view.findViewById(R.id.edt_date);
        tv_Stime=(TextView)view.findViewById(R.id.edt_time);
        btn_send=(Button)view.findViewById(R.id.btn_send);

        selectImageDialog.setView(view);
        selectImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        selectImageDialog.show();
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
                new DatePickerDialog(DocProfileForUser.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tv_Stime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(DocProfileForUser.this,
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
        drBean.booking_time = ctime;
        drBean.booking_date = ct;

        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DocProfileForUser.this,StripePaymentActivity.class);
                intent.putExtra("dr", drBean);


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




    /*  public void initView() {

          img_drProfile = (RoundedImageView) findViewById(R.id.img_drProfile);
        //  tvDoctorCurrentAvail = (RobottoTextView) findViewById(R.id.tvDoctorCurrentAvail);
  // = (RobottoTextView) findViewById(R.id.doc_name);
       //   tv_specility = (RobottoTextView) findViewById(R.id.speciality);
       //  tv_address = (RobottoTextView) findViewById(R.id.place);
        //  tv_startTime = (RobottoTextView) findViewById(R.id.tv_startTime);
         // tv_endTime = (RobottoTextView) findViewById(R.id.tv_endTime);
         // tv_aboutMe = (RobottoTextView) findViewById(R.id.txt_aboutme);
         // tv_question = (EditText) findViewById(R.id.et_question);
          tv_send = (RobottoTextView) findViewById(R.id.tv_send);

          et_date = (EditText) findViewById(R.id.edt_date);

          sp1 = (Spinner) findViewById(R.id.spinner1);
          setValues();
      }
  */
    public void setValues() {
        tv_drName.setText(drBean.fName);
        tv_specility.setText("Speciality: " + drBean.speciality);
        tv_address.setText(drBean.address);
        tv_startTime.setText(drBean.morning_appointment_start + " To " + drBean.morning_appointment_end);
        tv_endTime.setText(drBean.eve_appointment_start + " To " + drBean.eve_appointment_end);
        fee.setText("Consulation Fee $"+drBean.fees);
        txt_aboutme.setText(drBean.fName +" "+ "is a" +" "+ drBean.speciality +" "+ "in the " +" "+ drBean.address +" "+ "."+" "+"He studied from University of South Carolina School of Medicine and has been in practice for 32 years.");

       /* tvDoctorCurrentAvail.setText(drBean.current_zone_date_time);
        tv_aboutMe.setText(drBean.about);
        et_date.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                showDialog(0);
            }
        });*/
        AQuery aq = new AQuery(this);
        aq.id(img_drProfile).progress(pBar)
                .image(drBean.imageURL.replaceAll(" ", "%20"), true, true, 0, R.drawable.aala1, null, 0, 1.0f);


      /*  tv_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                notificatioAPI();

            }
        });*/
    }


    @Override
    public void onClick(View v) {
        showDialog(0);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        Log.i("TAG", "TAG : Morning Appoinment  On Create Dialog: ");
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    String selectedDate;
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            if (view.isShown()) {


                Log.i("TAG", "TAG : Morning Appoinment  from listner : ");
                //   et.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                Date date = new Date(selectedYear - 1900, selectedMonth, selectedDay);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                selectedDate = df.format(date);


                et.setText(selectedDate);

                Utils.showSimpleSpinProgressDialog(DocProfileForUser.this, "Please Wait..");
                getMorningAppointments();


            }

        }
    };


    public void getMorningAppointments() {

        Log.i("TAG", "TAG : Morning Appoinment  : ");

        String patient_zone = App.user.getCurrent_zone_date_time();
        String doctor_timezone = drBean.timeZone;
        String appointment_start = convertTimeToServiceFormat(drBean.morning_appointment_start);
        String appointment_end = convertTimeToServiceFormat(drBean.morning_appointment_end);
        String appoinment_date = selectedDate;


        new CallRequest(DocProfileForUser.this).getMorningSLots(patient_zone, doctor_timezone, appoinment_date, appointment_start, appointment_end);
    }


    public void getEveningAppointments() {
        Log.i("TAG", "TAG : Evening Appoinment  : ");
        String patient_zone = App.user.getCurrent_zone_date_time();
        String doctor_timezone = drBean.timeZone;
        String appointment_start = convertTimeToServiceFormat(drBean.eve_appointment_start);


        String appointment_end = convertTimeToServiceFormat(drBean.eve_appointment_end);
        String appoinment_date = selectedDate;
        new CallRequest(DocProfileForUser.this).getEveningSlots(patient_zone, doctor_timezone, appoinment_date, appointment_start, appointment_end);
    }


    public String convertTimeToServiceFormat(String slot) {

        if (slot.contains("am") || slot.contains("pm") || slot.contains("AM") || slot.contains("PM")) {
            return slot;
        }
        Log.i("DATE", "DAte for convert :" + slot);
        String outPutFormat = "HH:mm:ss";
        String serviceFormat = "hh:mm aa";

        SimpleDateFormat sdf = new SimpleDateFormat(serviceFormat);
        try {
            Date d = sdf.parse(slot);

            //  d.setTime(d.getTime() - (330 * 60 * 1000));

            return DateFormat.format(outPutFormat, d).toString();
        } catch (ParseException ex) {
            ex.printStackTrace();
            return "";
        }


    }


    public void notificatioAPI() {

        String bookingDate = et.getText().toString();
        if (bookingDate.length() < 2) {
            new AlertDialog.Builder(DocProfileForUser.this)
                    .setTitle("Error")
                    .setMessage("Date field can't be blank.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            return;


        }

        if (!Internet.isAvailable(DocProfileForUser.this)) {
            Internet.showAlertDialog(DocProfileForUser.this, "Error!", "No Internet Connection", false);

            return;
        }

        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {

                if (patientArray != null && patientArray.size() != 0) {

                    String bookingDate = et.getText().toString(); //"2015-06-16 5:30pm";
                    int pos = sp1.getSelectedItemPosition();
                    dispTime = patientArray.get(pos);

                    String bookingTime = convertTimeToServiceFormat(patientArray.get(pos));
                    // String bookingTime = dispTime;
                    String urlStr = "send_Appoinments.php?patient_user_id=" + App.user.getUserID() + "&doctor_id=" + drBean.dr_id + "&appointment_date_time=" + bookingDate + " " + bookingTime + "&appointment_question=" + tv_question.getText().toString();
                    // 308&appointment_date_time=2016-05-02 (null)=

                    Log.i("Doctor pocket : ", "URL : " + urlStr);
                    return ServerAPI.sendAppontAPI(DocProfileForUser.this, urlStr);
                } else {
                    Utils.showToast("Please select time to appointment", DocProfileForUser.this);
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
                onGetResult(result);
            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(DocProfileForUser.this);
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

                if (jsonResponse.getString("response").equals("y")) {

                    try {

                        drBean.row_id = jsonResponse.getString("booking_id");
                        drBean.booking_time = dispTime;
                        drBean.booking_date = selectedDate;
                        Intent intent = new Intent(DocProfileForUser.this, Appointment.class);
                        intent.putExtra("dr", drBean);
                        startActivity(intent);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(DocProfileForUser.this, msg, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }

    public void onProfileResult(JSONObject jsonResponse) {


    }


    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {
        Utils.removeSimpleSpinProgressDialog();
        try {
            if (result != null && !result.isEmpty()) {
                Log.i("TAG", "TAG Result : " + result);
                //      {"document":{"response":{"status":1,"message":"Success."}}}
                switch (request) {
                    case GET_MORNING_SLOT:

                        JSONObject mainObj = new JSONObject(result);

                        JSONObject document = mainObj.getJSONObject("document");

                        JSONObject response = document.getJSONObject("response");
                        doctorArray.clear();
                        patientArray.clear();
                        bookingArray.clear();
                        if (response.getString("status").equals("1")) {
                            JSONArray dataArray = response.getJSONArray("data");

                            if (dataArray.length() > 0) {
                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject obj = dataArray.getJSONObject(i);

                                    String p_slot = obj.getString("patient_timeslot");
                                    String p_time = p_slot.substring(p_slot.indexOf(" ") + 1, p_slot.length());
                                    p_time = convertTimeToDisplayFormat(p_time);
                                    patient_morning_timeslot.add(p_time);


                                    String b_slot = obj.getString("booking_timeslot");
                                    String b_time = b_slot.substring(b_slot.indexOf(" ") + 1, b_slot.length());
                                    b_time = convertTimeToDisplayFormat(b_time);
                                    booking_mornig_timeslot.add(b_time);

                                    String d_slot = obj.getString("doctor_timeslot");
                                    String d_time = d_slot.substring(d_slot.indexOf(" ") + 1, d_slot.length());
                                    d_time = convertTimeToDisplayFormat(d_time);
                                    doctor_morning_timeslot.add(d_time);


                                }


                                for (int j = 0; j < booking_mornig_timeslot.size(); j++) {
                                    if (j < booking_mornig_timeslot.size() - 1) {
                                        patientArray.add(patient_morning_timeslot.get(j));
                                        bookingArray.add(booking_mornig_timeslot.get(j));
                                        doctorArray.add(doctor_morning_timeslot.get(j));
                                    }

                                }


                            } else {
                                Utils.removeSimpleSpinProgressDialog();
                                Toast.makeText(DocProfileForUser.this, "Sorry There is no profile information available", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Utils.removeSimpleSpinProgressDialog();
                            Toast.makeText(DocProfileForUser.this, "Sorry, Please try again later", Toast.LENGTH_SHORT).show();
                        }

                        getEveningAppointments();
                        break;


                    case GET_EVENING_SLOT:

                        mainObj = new JSONObject(result);

                        document = mainObj.getJSONObject("document");

                        response = document.getJSONObject("response");


                        if (response.getString("status").equals("1")) {
                            JSONArray dataArray = response.getJSONArray("data");

                            if (dataArray.length() > 0) {
                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject obj = dataArray.getJSONObject(i);

                                    String p_slot = obj.getString("patient_timeslot");
                                    String p_time = p_slot.substring(p_slot.indexOf(" ") + 1, p_slot.length());
                                    p_time = convertTimeToDisplayFormat(p_time);
                                    patient_evening_timeslot.add(p_time);


                                    String b_slot = obj.getString("booking_timeslot");
                                    String b_time = b_slot.substring(b_slot.indexOf(" ") + 1, b_slot.length());
                                    b_time = convertTimeToDisplayFormat(b_time);
                                    booking_evening_timeslot.add(b_time);

                                    String d_slot = obj.getString("doctor_timeslot");
                                    String d_time = d_slot.substring(d_slot.indexOf(" ") + 1, d_slot.length());
                                    d_time = convertTimeToDisplayFormat(d_time);
                                    doctor_evening_timeslot.add(d_time);

                                }


                                for (int j = 0; j < booking_evening_timeslot.size(); j++) {
                                    if (j < booking_evening_timeslot.size() - 1) {
                                        patientArray.add(patient_evening_timeslot.get(j));
                                        bookingArray.add(booking_evening_timeslot.get(j));
                                        doctorArray.add(doctor_evening_timeslot.get(j));

                                    }

                                }


                            } else {
                                Utils.removeSimpleSpinProgressDialog();
                                Toast.makeText(DocProfileForUser.this, "Sorry There is no profile information available", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Utils.removeSimpleSpinProgressDialog();
                            Toast.makeText(DocProfileForUser.this, "Sorry, Please try again later", Toast.LENGTH_SHORT).show();
                        }


                        ArrayAdapter<String> staticAdapter = new ArrayAdapter<String>(DocProfileForUser.this,
                                android.R.layout.simple_spinner_item, patientArray);
                        // Specify the layout to use when the list of choices appears

                        staticAdapter.setDropDownViewResource(R.layout.simple_dropdown_file);

                        // Apply the adapter to the spinner
                        sp1.setOnItemSelectedListener(OnCatSpinnerCL);
                        sp1.setAdapter(staticAdapter);

                        Utils.removeSimpleSpinProgressDialog();

                        break;

                    case getDoctorProfile:
                        mainObj = new JSONObject(result);

                        document = mainObj.getJSONObject("document");

                        JSONObject jsonResponse = document.getJSONObject("response");

                        if (jsonResponse != null) {
                            try {


                                if (jsonResponse.getString("status").equals("1")) {

                                    try {

                                        drBean.fName = jsonResponse.getString("firstName");
                                        drBean.lastName = jsonResponse.getString("lastName");
                                        drBean.email = jsonResponse.getString("userName");
                                        drBean.speciality = jsonResponse.getString("speciality");
                                        drBean.morning_appointment_start = jsonResponse.getString("morning_appointment_start");
                                        drBean.morning_appointment_end = jsonResponse.getString("morning_appointment_end");
                                        drBean.eve_appointment_start = jsonResponse.getString("eve_appointment_start");
                                        drBean.eve_appointment_end = jsonResponse.getString("eve_appointment_end");

                                        drBean.fees = jsonResponse.getString("amount");
                                        Log.i("", "Fees of Doctor : " + drBean.fees);
                                        drBean.address = jsonResponse.getString("address");

                                        drBean.timeZone = jsonResponse.getString("zone");
                                        drBean.phoneNumber = jsonResponse.getString("phoneNumber");
                                        drBean.about = jsonResponse.getString("about");
                                        drBean.imageURL = jsonResponse.getString("image");
                                        drBean.current_zone_date_time = jsonResponse.getString("current_zone_date_time");


                                        setValues();
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                } else {

                                    Toast.makeText(DocProfileForUser.this, "Error In API", Toast.LENGTH_LONG).show();
                                }


                            } catch (JSONException e) {
                                Log.e("login", e.toString());
                            }

                        }
                        break;
                }
            }
        } catch (JSONException e) {
            Utils.removeSimpleSpinProgressDialog();
            Toast.makeText(DocProfileForUser.this, "Sorry, Please try again later", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    @Override
    public void onProgressUpdate(String uniqueMessageId, int progres) {

    }

    @Override
    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS request) {

    }


    public String convertTimeToDisplayFormat(String slot) {
        String serviceFormat = "HH:mm:ss";
        String outPutFormat = "hh:mm aa";

        SimpleDateFormat sdf = new SimpleDateFormat(serviceFormat);
        try {
            Date d = sdf.parse(slot);

            return DateFormat.format(outPutFormat, d).toString();
        } catch (ParseException ex) {
            return "";
        }


    }

    private AdapterView.OnItemSelectedListener OnCatSpinnerCL = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#505050"));
            ((TextView) parent.getChildAt(0)).setTextSize(8);


        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}


		
	

