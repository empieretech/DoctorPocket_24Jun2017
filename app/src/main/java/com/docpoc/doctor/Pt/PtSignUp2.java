package com.docpoc.doctor.Pt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.Dr.DrDashboardActivity;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Country;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.classes.State;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoTextView;
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
import java.util.Calendar;

import io.fabric.sdk.android.Fabric;

public class PtSignUp2 extends Activity implements AsynchTaskListner {
    Spinner spinner_country, spinner_state;
    RobottoTextView tvDone,tvEditPhoto;
    Context context;

    Button btn_DOB;
    ProgressDialog mprogressDialog;

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    String DOBStr = "", country = "", ZONE_NAME = "";

    String fb_id = "";
    public PtSignUp2 staticThis;

    public ArrayList<Country> countryArray;
    public static final String REG_ID = "regId";

    public String deviceID = "";
    public ImageView imgProfile;
    public String user_name, user_email, user_password, user_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_pt_sign_up2);
        Utils.logUser();
        staticThis = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            user_name = b.getString("name");
            user_email = b.getString("email");
            user_password = b.getString("pass");
            user_contact = b.getString("number");
            if(b.containsKey("fb_id"))
               fb_id = b.getString("fb_id");
        }


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        getReference();
        setListener();


    }


    public void getReference() {


        tvDone = (RobottoTextView) findViewById(R.id.tvDone);
        tvEditPhoto = (RobottoTextView) findViewById(R.id.tvEditPhoto);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        spinner_country = (Spinner) findViewById(R.id.activity_pt_signUp_spinnerCountry);
        spinner_state = (Spinner) findViewById(R.id.spState);
        btn_DOB = (Button) findViewById(R.id.activity_pt_signUp_btn_BOD);


        new CallRequest(this).getCountry();

    }

    public void setListener() {


        tvDone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                signUPAPI();
            }
        });


    }


    public void onClickSetDOB(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            setDate(arg1, arg2 + 1, arg3);
        }
    };

    private void setDate(int year, int month, int day) {
        StringBuilder date = new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year);

        DOBStr = date.toString();
        btn_DOB.setText(DOBStr);


    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public void signUPAPI() {
        try {



            final String state = spinner_state.getSelectedItem().toString();



            if (!Internet.isAvailable(PtSignUp2.this)) {
                Internet.showAlertDialog(PtSignUp2.this, "Error!", "No Internet Connection", false);

                return;
            }


            if (state.equalsIgnoreCase("State")) {

                Utils.showToast("Please select Province/State", PtSignUp2.this);
                return;
            }

            BaseTask.run(new BaseTask.TaskListener() {
                @Override
                public JSONObject onTaskRunning(int taskId, Object data) {

                    final SharedPreferences prefs = staticThis.getSharedPreferences(
                            DrDashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);

                    if (prefs.contains(REG_ID))
                        deviceID = prefs.getString(REG_ID, "");


                    String urlStr = "signup.php?access_token=" + "testermanishrahul234142test" + "&userName=" +
                            user_email + "&DOB=" + DOBStr + "&password="
                            + user_password + "&mobile=" + user_contact + "&address=" +
                            "&country=" + spinner_country.getSelectedItem().toString().replace(" ", "%20") + "&state=" +
                            spinner_state.getSelectedItem().toString().replace(" ", "%20") + "&deviceId=" + deviceID
                            + "&name=" + user_name + "&zone=" + ZONE_NAME + "&facebookid=" + fb_id;
                    return ServerAPI.Login(urlStr, PtSignUp2.this);

                    //return updateHistory();
                }

                @Override
                public void onTaskResult(int taskId, JSONObject result) {
                    if (mprogressDialog != null) {
                        mprogressDialog.dismiss();
                        mprogressDialog = null;
                    }
                    onSignUPResult(result);
                }

                @Override
                public void onTaskProgress(int taskId, Object... values) {
                }

                @Override
                public void onTaskPrepare(int taskId, Object data) {
                    mprogressDialog = new ProgressDialog(PtSignUp2.this);
                    mprogressDialog.setMessage("Please wait...");
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void onSignUPResult(JSONObject jsonResponse) {

        GlobalBeans.LoginResultBean loginResult = new GlobalBeans.LoginResultBean();
        if (jsonResponse != null) {
            try {

                loginResult.message = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {

                        jsonResponse.getString("status").equals("1");

                       /* new AlertDialog.Builder(PtSignUp.this)
                                .setTitle("Sign Up!")
                                .setMessage("A link was sent to your email. Please click the link to activate your account.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        PtSignUp.this.finish();
                                    }
                                })
                                .show();*/
                        App.user.setUser_Type(MyConstants.USER_PT);
                        Utils.showToast("Your account is ready to use", this);
                        Intent intent = new Intent(this, PtLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.startActivity(intent);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(PtSignUp2.this, loginResult.message, Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(PtSignUp2.this)
                            .setTitle("Sign Up!")
                            .setMessage("Email Id already exists.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {
        Log.i("", "RESULT : " + result);
        Utils.removeSimpleSpinProgressDialog();
        if (result != null && !result.isEmpty()) {
            try {

                switch (request) {
                    case getCountry:
                        JSONArray jArray = new JSONArray(result);
                        if (jArray != null && jArray.length() > 0) {
                            countryArray = new ArrayList<>();

                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject jCountry = jArray.getJSONObject(i);

                                Country country = new Country();
                                country.setName(jCountry.getString("name"));

                                if (jCountry.has("states")) {
                                    JSONArray jStateArray = jCountry.getJSONArray("states");

                                    if (jStateArray != null && jStateArray.length() > 0) {
                                        State s = new State();
                                        s.setName("State");
                                        country.stateArray.add(s);
                                        for (int j = 0; j < jStateArray.length(); j++) {

                                            State st = new State();
                                            st.name = jStateArray.getJSONObject(j).getString("name");

                                            country.stateArray.add(st);
                                        }
                                    }

                                }

                                countryArray.add(country);


                            }

                            if (countryArray.size() > 0) {
                                ArrayList<String> tempArray = new ArrayList<>();
                                for (Country c : countryArray) {
                                    tempArray.add(c.name);
                                }

                                ArrayAdapter countryAdapter = new ArrayAdapter(this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        tempArray);

                                spinner_country.setAdapter(countryAdapter);


                                spinner_country.setOnItemSelectedListener(new OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        ((TextView) spinner_country.getSelectedView()).setTextColor(getResources().getColor(R.color.grey_color));
                                        ((TextView) spinner_country.getSelectedView()).setTextSize(12);

                                        setStateSpinner(position);

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        } else {
                            Utils.showToast("Please try again later", this);
                        }
                        break;

                    case getZone:

                        JSONObject obj = new JSONObject(result);
                        if (obj.getBoolean("success")) {
                            ZONE_NAME = obj.getString("zone");
                        } else {
                            Utils.showToast(obj.getString("message"), this);
                        }
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Utils.showToast("Please try again later", this);
        }
    }

    @Override
    public void onProgressUpdate(String uniqueMessageId, int progres) {

    }

    @Override
    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS request) {

    }

    public void setStateSpinner(int pos) {
        ArrayList<String> stateArray = new ArrayList<>();
        for (State s : countryArray.get(pos).stateArray) {
            stateArray.add(s.name);
        }

        ArrayAdapter stateAdapter = new ArrayAdapter(PtSignUp2.this,
                android.R.layout.simple_spinner_dropdown_item,
                stateArray);

        spinner_state.setAdapter(stateAdapter);

        spinner_state.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) spinner_state.getSelectedView()).setTextColor(getResources().getColor(R.color.grey_color));
                ((TextView) spinner_state.getSelectedView()).setTextSize(12);
                if (position > 0)
                    new CallRequest(PtSignUp2.this).getTimeZone(spinner_country.getSelectedItem().toString(), spinner_state.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}

	


	