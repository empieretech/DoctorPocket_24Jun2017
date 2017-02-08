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
import android.text.Html;
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
import android.widget.Spinner;
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

public class PtSignUpNew extends Activity implements AsynchTaskListner {

    public Spinner spinner_country, spinner_state;
    public RobottoTextView tv_signUP, tv_alredayHaveAC;
    public EditText et_Name, et_pass, et_confirm_Pass, et_address, et_contact, et_emai;
    public Button btn_DOB;
    public ProgressDialog mprogressDialog;

    private Calendar calendar;
    private int year, month, day;
    public String DOBStr = "", ZONE_NAME = "";

    public String fb_id = "";
    public PtSignUpNew staticThis;
    public String name, email;
    public ArrayList<Country> countryArray;
    public static final String REG_ID = "regId";

    public String deviceID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_pt_sign_new);
        Utils.logUser();
        staticThis = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Bundle b = getIntent().getExtras();
        if (b != null) {

            fb_id = b.getString("FB_id");
            name = b.getString("Name");
            email = b.getString("Email");

        }


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        getReference();
        setListener();


    }


    public void getReference() {

        et_Name = (EditText) findViewById(R.id.activity_pt_signUp_et_name);
        et_pass = (EditText) findViewById(R.id.activity_pt_signUp_et_pass);

        et_confirm_Pass = (EditText) findViewById(R.id.etConfirmPass);
        et_address = (EditText) findViewById(R.id.etAddress);

        et_contact = (EditText) findViewById(R.id.activity_pt_signUp_et_contact);
        et_emai = (EditText) findViewById(R.id.activity_pt_signUp_et_email);

        tv_signUP = (RobottoTextView) findViewById(R.id.tvSignUp);
        tv_alredayHaveAC = (RobottoTextView) findViewById(R.id.tvSkipSignUp);

        tv_alredayHaveAC.setText(Html.fromHtml("Already have an account? <b><u>LOG IN</u></b>"));


        spinner_country = (Spinner) findViewById(R.id.activity_pt_signUp_spinnerCountry);
        spinner_state = (Spinner) findViewById(R.id.spState);
        btn_DOB = (Button) findViewById(R.id.activity_pt_signUp_btn_BOD);

        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/angelina.ttf");
        new CallRequest(this).getCountry();

    }

    public void setListener() {

        et_Name.setText(name);
        et_emai.setText(email);
        tv_signUP.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                signUPAPI();
            }
        });

        tv_alredayHaveAC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

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

            final String user_name = et_Name.getText().toString();
            final String userEmail = et_emai.getText().toString();
            final String pwd = et_pass.getText().toString();
            String confirmPWD = et_confirm_Pass.toString();

            //	final String DOBstr = et_BOD.getText().toString();
            final String mobStr = et_contact.getText().toString();
            final String addressStr = et_address.getText().toString();
            String countryStr = spinner_country.getSelectedItem().toString();
            //	final String stateStr  = "";


            final String zoneStr = spinner_state.getSelectedItem().toString();


            if ((!isValidEmail(userEmail))) {

                new AlertDialog.Builder(PtSignUpNew.this)
                        .setTitle("Error!")
                        .setMessage("Email id is invalid")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return;
            }

            if ((pwd.length() < 1) || (confirmPWD.length() < 1) || (mobStr.length() < 1) || (DOBStr.length() < 2)) {


                new AlertDialog.Builder(PtSignUpNew.this)
                        .setTitle("Error!")
                        .setMessage("Please fill all details.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return;
            }


            if (confirmPWD.equals(pwd)) {


                new AlertDialog.Builder(PtSignUpNew.this)
                        .setTitle("Error!")
                        .setMessage("Password & Confirm Password does not match.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return;
            }

            if (!Internet.isAvailable(PtSignUpNew.this)) {
                Internet.showAlertDialog(PtSignUpNew.this, "Error!", "No Internet Connection", false);

                return;
            }


            if (zoneStr.equalsIgnoreCase("State")) {

                Utils.showToast("Please select Province/State", PtSignUpNew.this);
                return;
            }

            BaseTask.run(new BaseTask.TaskListener() {
                @Override
                public JSONObject onTaskRunning(int taskId, Object data) {

                    final SharedPreferences prefs = staticThis.getSharedPreferences(
                            DrDashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);

                    if (prefs.contains(REG_ID))
                        deviceID = prefs.getString(REG_ID, "");


                    String urlStr = "signup.php?access_token=" + "testermanishrahul234142test" + "&userName=" + userEmail + "&DOB=" + DOBStr + "&password=" + pwd + "&mobile=" + mobStr + "&address=" +
                            "&country=" + spinner_country.getSelectedItem().toString().replace(" ", "%20") + "&state=" +
                            spinner_state.getSelectedItem().toString().replace(" ", "%20") + "&deviceId=" + deviceID
                            + "&name=" + user_name + "&zone=" + ZONE_NAME + "&facebookid=" + fb_id;
                    return ServerAPI.Login(urlStr, PtSignUpNew.this);

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
                    mprogressDialog = new ProgressDialog(PtSignUpNew.this);
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

                       /* new AlertDialog.Builder(PtSignUpNew.this)
                                .setTitle("Sign Up!")
                                .setMessage("A link was sent to your email. Please click the link to activate your account.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        PtSignUpNew.this.finish();
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

                    Toast.makeText(PtSignUpNew.this, loginResult.message, Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(PtSignUpNew.this)
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


                                spinner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

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

        ArrayAdapter stateAdapter = new ArrayAdapter(PtSignUpNew.this,
                android.R.layout.simple_spinner_dropdown_item,
                stateArray);

        spinner_state.setAdapter(stateAdapter);

        spinner_state.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    new CallRequest(PtSignUpNew.this).getTimeZone(spinner_country.getSelectedItem().toString(), spinner_state.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}

	


	