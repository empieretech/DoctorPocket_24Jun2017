package com.docpoc.doctor.Dr;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.ImageLoader.Constant;

import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.classes.Zone;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundedImageView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;


public class EditDoctorProfile extends Activity implements OnClickListener, AsynchTaskListner {
    String x;
    ImageView iv;
    ImageView iv_back;

    RoundedImageView iv_drProfile;
    public Spinner sp_time;

    public EditText etDocName, etSpeciality, etAddress, etAbout, etFees, etMornStartHour, etMornStartMin, etMornStartAMPM, etMornEndHour, etMornEndMin, etMornEndAMPM, etEvnStartHour, etEvnStartMin, etEvnStartAMPM, etEvnEndHour, etEvnEndMin, etEvnEndAMPM;
    ArrayList<String> strZoneArray;
    RobottoTextView tvCurrentTime;
    ArrayList<Zone> zoneArray;
    public String current_zone_date_time, zone, phoneNumber;
    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_REQUEST = 2;
    String docId = "";
    public Uri selectedUri;
    Button btnUpdateProfile;
    ProgressDialog mprogressDialog;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_edit_doctor_profile);
        Utils.logUser();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        iv = (ImageView) findViewById(R.id.img_left_arrow);

        iv.setOnClickListener(this);

        x = getIntent().getStringExtra("x");
        docId = getIntent().getStringExtra("docId");
        AngellinaTextView title = (AngellinaTextView) findViewById(R.id.custom_text);
        title.setText(x);

        AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);


        getReference();


    }

    public void getReference() {


        etDocName = (EditText) findViewById(R.id.etDocName);
        etSpeciality = (EditText) findViewById(R.id.etSpeciality);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etAbout = (EditText) findViewById(R.id.etAbout);
        etFees = (EditText) findViewById(R.id.etFess);
        etMornStartHour = (EditText) findViewById(R.id.etMornStartHour);
        etMornStartMin = (EditText) findViewById(R.id.etMornStartMin);
        etMornStartAMPM = (EditText) findViewById(R.id.etMornStartAMPM);
        etMornEndHour = (EditText) findViewById(R.id.etMornEndHour);
        etMornEndMin = (EditText) findViewById(R.id.etMornEndMin);
        etMornEndAMPM = (EditText) findViewById(R.id.etMornEndAMPM);
        etEvnStartHour = (EditText) findViewById(R.id.etEvnStartHour);
        etEvnStartMin = (EditText) findViewById(R.id.etEvnStartMin);
        etEvnStartAMPM = (EditText) findViewById(R.id.etEvnStartAMPM);
        etEvnEndHour = (EditText) findViewById(R.id.etEvnEndHour);
        etEvnEndMin = (EditText) findViewById(R.id.etEvnEndMin);
        etEvnEndAMPM = (EditText) findViewById(R.id.etEvnEndAMPM);
        sp_time = (Spinner) findViewById(R.id.spZone);
        tvCurrentTime = (RobottoTextView) findViewById(R.id.tvCurrentTime);
        btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
        iv_drProfile = (RoundedImageView) findViewById(R.id.activity_drProfile_iv_drProfile);
        pBar = (ProgressBar) findViewById(R.id.pBar);
        iv_drProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(iv_drProfile.getWindowToken(), 0);
                selectImage();
            }
        });

        btnUpdateProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidated()) {

                    String fees = etFees.getText().toString();

                    fees.replaceAll("$", "");

                    new CallRequest(EditDoctorProfile.this).updateDoctorProfile(docId, selectedPath, etDocName.getText().toString().trim(),
                            etSpeciality.getText().toString().trim(), getMorningStartTime(), getMorningEndtime(), getEveningStartTime(), getEveningEndTime(),
                            fees, phoneNumber, etAddress.getText().toString().trim(), etAbout.getText().toString().trim(), zoneArray.get(sp_time.getSelectedItemPosition()).zone);
                }
            }
        });
        iv_back = (ImageView) findViewById(R.id.img_left_arrow);
        iv_back.setOnClickListener(this);


        getDrProfileAPI();

    }

    public String getMorningStartTime() {
        return etMornStartHour.getText().toString().trim() + ":" + etMornStartMin.getText().toString().trim() + " " + etMornStartAMPM.getText().toString().trim();
    }

    public String getMorningEndtime() {
        return etMornEndHour.getText().toString().trim() + ":" + etMornEndMin.getText().toString().trim() + " " + etMornEndAMPM.getText().toString().trim();
    }

    public String getEveningStartTime() {
        return etEvnStartHour.getText().toString().trim() + ":" + etEvnStartMin.getText().toString().trim() + " " + etEvnStartAMPM.getText().toString().trim();
    }

    public String getEveningEndTime() {
        return etEvnEndHour.getText().toString().trim() + ":" + etEvnEndMin.getText().toString().trim() + " " + etEvnEndAMPM.getText().toString().trim();
    }

    public boolean isValidated() {

        if (etDocName.getText().toString().isEmpty()) {
            Utils.showToast("Please enter Doctor Name ", EditDoctorProfile.this);
            return false;
        } else if (etAddress.getText().toString().isEmpty()) {
            Utils.showToast("Please enter valid address ", EditDoctorProfile.this);
            return false;

        } else if (etSpeciality.getText().toString().isEmpty()) {
            Utils.showToast("Please enter Speciality", EditDoctorProfile.this);
            return false;
        } else if (etAbout.getText().toString().isEmpty()) {
            Utils.showToast("Please enter about you ", EditDoctorProfile.this);
            return false;
        } else if (etFees.getText().toString().isEmpty()) {
            Utils.showToast("Please enter your fees detail ", EditDoctorProfile.this);
            return false;
        } else if (
                etMornStartHour.getText().toString().isEmpty() ||
                        etMornStartMin.getText().toString().isEmpty() ||
                        etMornStartAMPM.getText().toString().isEmpty() ||
                        etMornEndHour.getText().toString().isEmpty() ||
                        etMornEndMin.getText().toString().isEmpty() ||
                        etMornEndAMPM.getText().toString().isEmpty() ||
                        etEvnStartHour.getText().toString().isEmpty() ||
                        etEvnStartMin.getText().toString().isEmpty() ||
                        etEvnStartAMPM.getText().toString().isEmpty() ||
                        etEvnEndHour.getText().toString().isEmpty() ||
                        etEvnEndMin.getText().toString().isEmpty() ||
                        etEvnEndAMPM.getText().toString().isEmpty()) {
            Utils.showToast("Please enter your availability detail ", EditDoctorProfile.this);
            return false;
        } else {
            return true;
        }

    }


    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose Photo from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditDoctorProfile.this, R.style.MyDialogTheme);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utils.checkPermission(EditDoctorProfile.this);
                if (items[item].equals("Take Photo")) {

                    if (result) {
                        File f = new File(Environment.getExternalStorageDirectory() + "/dr_pocket/images/profile");
                        if (!f.exists()) {
                            f.mkdirs();
                        }
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "dr_pocket/images/img_" + System.currentTimeMillis() + ".jpg");
                        selectedUri = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedUri);

                        startActivityForResult(intent, CAMERA_REQUEST);
                    }
                } else if (items[item].equals("Choose Photo from Library")) {

                    if (result) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                     /*   intent.setType("image*//*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);//*/
                        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_PICTURE);

                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        finish();
    }


    public void getDrProfileAPI() {


        if (!Internet.isAvailable(EditDoctorProfile.this)) {
            Internet.showAlertDialog(EditDoctorProfile.this, "Error!", "No Internet Connection", false);

            return;
        }


        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {
                String urlStr = "getDoctorProfile.php?access_token=testermanishrahul234142test&user_id=" + App.user.getUserID();
                return ServerAPI.generalAPI(EditDoctorProfile.this, urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
                onProfileResult(result);
            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog(EditDoctorProfile.this);
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


    public void onProfileResult(JSONObject jsonResponse) {


        if (jsonResponse != null) {
            try {


                if (jsonResponse.getString("status").equals("1")) {

                    try {

                        String Fname = jsonResponse.getString("firstName");
                        String LName = jsonResponse.getString("lastName");
                        String email = jsonResponse.getString("userName");
                        String speciality = jsonResponse.getString("speciality");
                        String morStartTime = jsonResponse.getString("morning_appointment_start");
                        String mornEndTime = jsonResponse.getString("morning_appointment_end");
                        String eveStartTime = jsonResponse.getString("eve_appointment_start");
                        String eveEndTime = jsonResponse.getString("eve_appointment_end");

                        String fees = jsonResponse.getString("amount");
                        String address = jsonResponse.getString("address");

                        current_zone_date_time = jsonResponse.getString("current_zone_date_time");
                        zone = jsonResponse.getString("zone");
                        phoneNumber = jsonResponse.getString("phoneNumber");
                        String about = jsonResponse.getString("about");
                        String imageURL = jsonResponse.getString("image");

                        tvCurrentTime.setText(current_zone_date_time);
                        etDocName.setText(Fname + LName);
                        etSpeciality.setText(speciality);


                        String[] mornStartList = breakString(morStartTime);
                        etMornStartHour.setText(mornStartList[0]);
                        etMornStartMin.setText(mornStartList[1]);
                        etMornStartAMPM.setText(mornStartList[2]);


                        String[] mornEndList = breakString(mornEndTime);
                        etMornEndHour.setText(mornEndList[0]);
                        etMornEndMin.setText(mornEndList[1]);
                        etMornEndAMPM.setText(mornEndList[2]);


                        String[] evnStartList = breakString(eveStartTime);
                        etEvnStartHour.setText(evnStartList[0]);
                        etEvnStartMin.setText(evnStartList[1]);
                        etEvnStartAMPM.setText(evnStartList[2]);

                        String[] evnEndList = breakString(eveEndTime);

                        etEvnEndHour.setText(evnEndList[0]);
                        etEvnEndMin.setText(evnEndList[1]);
                        etEvnEndAMPM.setText(evnEndList[2]);

                        etFees.setText("$" + fees);
                        etAddress.setText(address);

                        etAbout.setText(about);



                        AQuery aq = new AQuery(this);
                        aq.id(iv_drProfile).progress(pBar)
                                .image(imageURL.replaceAll(" ", "%20"), true, true, 0, R.drawable.dr_about_03, null, 0, 1.0f);




                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(EditDoctorProfile.this, "Error In API", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

            new CallRequest(this).getZoneList();
        }

    }


    @Override
    public void onTaskCompleted(String result, Constant.REQUESTS request) {
        try {
            Utils.removeSimpleSpinProgressDialog();
            if (result != null && !result.isEmpty()) {
                Log.i("TAG", "TAG Result : " + result);
                //      {"document":{"response":{"status":1,"message":"Success."}}}
                switch (request) {
                    case getZone:
                        Utils.removeSimpleSpinProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(result);
                            int status = obj.getJSONObject("document").getJSONObject("response").getInt("status");
                            if (status > 0) {
                                strZoneArray = new ArrayList<>();
                                zoneArray = new ArrayList<>();
                                JSONArray jCountryArray = obj.getJSONObject("document").getJSONObject("response").getJSONArray("timezones");
                                int selectedZone = 0;
                                if (jCountryArray != null && jCountryArray.length() > 0) {
                                    for (int i = 0; i < jCountryArray.length(); i++) {
                                        JSONObject jZone = jCountryArray.getJSONObject(i);
                                        Zone c = new Zone();
                                        c.zone = jZone.getString("zone");
                                        c.offset_string = jZone.getString("offset_string");

                                        if (!zone.isEmpty()) {
                                            if (zone.equalsIgnoreCase(c.zone)) {
                                                selectedZone = i;
                                            }
                                        }
                                        strZoneArray.add(c.offset_string);
                                        zoneArray.add(c);
                                    }
                                    sp_time.setAdapter(new ArrayAdapter<String>(EditDoctorProfile.this, R.layout.custom_spinner_row, strZoneArray));
                                    sp_time.setSelection(selectedZone);
                                    sp_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                                            ((TextView) parent.getChildAt(0)).setTextSize(10);
                                            ((TextView) parent.getChildAt(0)).setGravity(View.FOCUS_LEFT);

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case updateDoctorProfile:
                        Utils.removeSimpleSpinProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(result);

                            int status = obj.getJSONObject("document").getJSONObject("response").getInt("status");
                            if (status > 0) {

                                Utils.showToast("Profile updated succesfully", this);
                                Intent i7 = new Intent(EditDoctorProfile.this, DoctorProfile.class);
                                i7.putExtra("x", x);
                                i7.putExtra("docId", docId);
                                i7.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i7.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i7);


                            }

                        } catch (JSONException e) {
                            Utils.removeSimpleSpinProgressDialog();
                            e.printStackTrace();
                        }
                        break;


                }

            } else {
                Utils.showToast("Please try again later", this);
                Utils.removeSimpleSpinProgressDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.removeSimpleSpinProgressDialog();
        }

    }

    @Override
    public void onProgressUpdate(String uniqueMessageId, int progres) {

    }

    @Override
    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS request) {

    }

    public String[] breakString(String data) {
        try {
            String from = data.substring(0, data.indexOf(":"));
            String to = data.substring(data.indexOf(":") + 1, data.indexOf(" "));
            String AMPM = data.substring(data.indexOf(" ") + 1, data.length());


            String[] newArray = {from, to, AMPM};
            return newArray;
        } catch (Exception e) {
            e.printStackTrace();
            String[] newArray = {"01", "12", "AM"};
            return newArray;
        }
    }

    Bitmap bitmap, bitmapCover;
    public String selectedPath = "";
    String strLength, TAG = "DOCTOR";


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            switch (requestCode) {
                case CAMERA_REQUEST:
                    if (requestCode == CAMERA_REQUEST) {
                        if (uri != null) {
                            System.out.println("going to collect Data of Camera");
                            selectedPath = uri.getPath();
                            iv_drProfile.setImageBitmap(BitmapFactory.decodeFile(selectedPath));

                        } else {
                            selectedPath = "";
                            Utils.showToast("Please Capture Image!!!", EditDoctorProfile.this);
                        }
                    }
                    break;
                case SELECT_PICTURE:
                    if (requestCode == SELECT_PICTURE) {
                        if (uri != null) {
                            System.out.println("going to collect Data of Select Picture");
                            selectedPath = getRealPathFromURI(uri);
                            System.out.print(selectedPath);
                            iv_drProfile.setImageBitmap(BitmapFactory.decodeFile(selectedPath));
                        } else {
                            selectedPath = "";
                            Utils.showToast("Please Select Image!!!", EditDoctorProfile.this);
                        }
                    }
                    break;
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
