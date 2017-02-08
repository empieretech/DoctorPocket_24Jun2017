package com.docpoc.doctor.Pt;


import android.app.Activity;
import android.app.ProgressDialog;

import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.App;
import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.Pt.fragments.PTProfileFragment;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Zone;
import com.docpoc.doctor.utils.AngellinaTextView;
import com.docpoc.doctor.utils.RobottoEditTextView;
import com.docpoc.doctor.utils.RobottoTextView;
import com.docpoc.doctor.utils.RoundedImageView;
import com.docpoc.doctor.webServices.AsynchTaskListner;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.CallRequest;
import com.docpoc.doctor.webServices.MyConstants;
import com.docpoc.doctor.webServices.ServerAPI;
import com.docpoc.doctor.webServices.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;


public class EditPatientProfile extends FragmentActivity implements OnClickListener, AsynchTaskListner {
    String x;
    Spinner sp_time;
    ImageView iv_back;
    ImageView editPro1, editPro2, editPro3, editPro4, editPro5, editPro6, editPro7;
    RoundedImageView iv_ptProfile;
    EditText et_gender, et_address;
    EditText et_ptName, et_expe, et_email, et_mobNo, et_time;
    public RobottoEditTextView tvPatientName, tvPatientAge, tvPatientGender, tvPatientAddress, tvPatientEmail, tvPatientPhone, tvPatientCountry;
    Button btnUpdateProfile;
    ProgressDialog mprogressDialog;
    public Geocoder g;
    List<Address> addressList, displayArray;
    ArrayList<String> strZoneArray;
    ArrayList<Zone> zoneArray;
    public String current_zone_date_time, zone;
    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_REQUEST = 2;
    String patientID = "";
    public Uri selectedUri;
    ProgressBar pBar;
    public Button btn_save,btn_change_photo,btn_discard;
    public RoundedImageView imgUser;

    @Override
    protected void onCreate(Bundle saveState) {
        super.onCreate(saveState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_edit_patient_profile);
        Utils.logUser();


        x = getIntent().getStringExtra("x");
        patientID = getIntent().getStringExtra("patientID");

     /*   AngellinaTextView text = (AngellinaTextView) findViewById(R.id.custom_text);
        text.setText("Patient Profile");
*/
        TextView tvHeader = (TextView) findViewById(R.id.tvHeader);
        ImageView iv = (ImageView) findViewById(R.id.imgBack);
        tvHeader.setText("Patient Profile");
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


        getReference();
        setListener();

        getPTProfileAPI();


    }

    public void getReference() {

        //  sp_time = (Spinner) findViewById(R.id.activity_pt_profile_spinner_time);
        tvPatientName = (RobottoEditTextView) findViewById(R.id.tvPatientName);
        tvPatientAge = (RobottoEditTextView) findViewById(R.id.tvPatientAge);

        tvPatientGender = (RobottoEditTextView) findViewById(R.id.tvPatientGender);

        tvPatientAddress = (RobottoEditTextView) findViewById(R.id.tvPatientAddress);
        tvPatientEmail = (RobottoEditTextView) findViewById(R.id.tvPatientEmail);
        tvPatientPhone = (RobottoEditTextView) findViewById(R.id.tvPatientPhone);
        tvPatientCountry = (RobottoEditTextView) findViewById(R.id.tvPatientCountry);


        iv_back = (ImageView) findViewById(R.id.img_left_arrow);
        pBar = (ProgressBar) findViewById(R.id.pBar);
        btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);

        btn_save=(Button)findViewById(R.id.btn_save);
        btn_discard=(Button)findViewById(R.id.btn_discard);

        btn_change_photo=(Button)findViewById(R.id.btn_change_photo);
        imgUser=(RoundedImageView)findViewById(R.id.imgUser);

        btn_change_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        editPro1 = (ImageView) findViewById(R.id.editPro1);
        editPro2 = (ImageView) findViewById(R.id.editPro2);
        editPro3 = (ImageView) findViewById(R.id.editPro3);
        editPro4 = (ImageView) findViewById(R.id.editPro4);
        editPro5 = (ImageView) findViewById(R.id.editPro5);
        editPro6 = (ImageView) findViewById(R.id.editPro6);
        editPro7 = (ImageView) findViewById(R.id.editPro7);

        editPro1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               tvPatientName.setEnabled(true);


            }
        });
        editPro2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPatientAge.setEnabled(true);
            }
        });
        editPro3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPatientGender.setEnabled(true);
            }
        });
        editPro4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPatientAddress.setEnabled(true);
            }
        });
        editPro5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPatientEmail.setEnabled(true);
            }
        });
        editPro6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPatientPhone.setEnabled(true);
            }
        });
        editPro7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPatientCountry.setEnabled(true);
            }
        });
        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidated()) {
                    new CallRequest(EditPatientProfile.this).updatePatientProfile(patientID, selectedPath, tvPatientPhone.getText().toString(),
                            tvPatientAddress.getText().toString(), "", "", tvPatientName.getText().toString(),
                            tvPatientGender.getText().toString(), "FGH");
                }
            }
        });

        btn_discard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editPatient();
            }
        });


    }

    private void editPatient() {
        Intent i=new Intent(EditPatientProfile.this,PatientProfile.class);
        i.putExtra("x", x);
        i.putExtra("patientID", patientID);

        startActivity(i);

    }


    public void setListener() {

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.Time1,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        //   sp_time.setAdapter(staticAdapter);

        //  iv_back.setOnClickListener(this);

       /* iv_ptProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(iv_ptProfile.getWindowToken(), 0);
                selectImage();
            }
        });*/

       /* btnUpdateProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidated()) {
                    new CallRequest(EditPatientProfile.this).updatePatientProfile(patientID, selectedPath, et_mobNo.getText().toString(),
                            et_address.getText().toString(), "", "", et_ptName.getText().toString(),
                            et_gender.getText().toString(), "FGH");
                }
            }
        });*/

    }
  /*  public void switchFragment(Fragment fragment) {
       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.tabcontent, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }*/

    public boolean isValidated() {

        if (tvPatientPhone.getText().toString().isEmpty() || tvPatientPhone.getText().toString().trim().length() != 10) {
            Utils.showToast("Please enter valid mobile no ", EditPatientProfile.this);
            return false;
        } else if (tvPatientAddress.getText().toString().isEmpty()) {
            Utils.showToast("Please enter valid address no ", EditPatientProfile.this);
            return false;

        } else if (tvPatientName.getText().toString().isEmpty()) {
            Utils.showToast("Please enter Name ", EditPatientProfile.this);
            return false;
        } else if (tvPatientGender.getText().toString().isEmpty()) {
            Utils.showToast("Please enter Gender ", EditPatientProfile.this);
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        finish();
    }


    public void getPTProfileAPI() {

        if (!Internet.isAvailable(EditPatientProfile.this)) {
            Internet.showAlertDialog(EditPatientProfile.this, "Error!", "No Internet Connection", false);

            return;
        }


        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {

                String urlStr = "";

                if (App.user.getUser_Type().equalsIgnoreCase(MyConstants.USER_DR))
                    urlStr = "getProfile.php?access_token=testermanishrahul234142test&user_id=" + patientID;

                else
                    urlStr = "getProfile.php?access_token=testermanishrahul234142test&user_id=" + App.user.getUserID();


                return ServerAPI.generalAPI(EditPatientProfile.this, urlStr);

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
                mprogressDialog = new ProgressDialog(EditPatientProfile.this);
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
    }


    public void onProfileResult(JSONObject jsonResponse) {


        if (jsonResponse != null) {
            try {


                if (jsonResponse.getString("status").equals("1")) {
                    Log.i("TAG", "TAG RESULT : " + jsonResponse.toString());
                    try {

                        String name = jsonResponse.getString("name");
                        String email = jsonResponse.getString("userName");
                        String age = jsonResponse.getString("DOB");
                        String gender = jsonResponse.getString("gender");
                        String address = jsonResponse.getString("address");
                        String country = jsonResponse.getString("country");
                        String state = jsonResponse.getString("state");

                        String mobile = jsonResponse.getString("mobile");
                        current_zone_date_time = jsonResponse.getString("current_zone_date_time");
                        zone = jsonResponse.getString("zone");
                        String message = jsonResponse.getString("message");
                        String imageURL = jsonResponse.getString("image");


                        tvPatientName.setText(name);
                        tvPatientAge.setText(age);
                        tvPatientGender.setText(gender);
                        tvPatientAddress.setText(address);
                        tvPatientEmail.setText(email);
                        tvPatientPhone.setText(mobile);


                        //    et_time.setText(current_zone_date_time);

                        //    tv_time.setText(current_zone_date_time);
                        AQuery aq = new AQuery(this);
                        aq.id(iv_ptProfile).progress(pBar)
                                .image(imageURL.replaceAll(" ", "%20"), true, true, 0, R.drawable.dr_about_03, null, 0, 1.0f);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(EditPatientProfile.this, "Error In API", Toast.LENGTH_LONG).show();
                }

                new CallRequest(this).getZoneList();

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
                                    ArrayAdapter<String> staticAdapter = new ArrayAdapter<String>(EditPatientProfile.this,
                                            android.R.layout.simple_spinner_item, strZoneArray);
                                    // Specify the layout to use when the list of choices appears

                                    staticAdapter.setDropDownViewResource(R.layout.simple_dropdown_file);
                                    sp_time.setAdapter(staticAdapter);
                                    sp_time.setSelection(selectedZone);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case updatePatientProfile:
                        Utils.removeSimpleSpinProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(result);

                            int status = obj.getJSONObject("document").getJSONObject("response").getInt("status");
                            if (status > 0) {

                                Utils.showToast("Profile updated succesfully", this);
                                Intent i7 = new Intent(EditPatientProfile.this, PatientProfile.class);
                                i7.putExtra("x", x);
                                i7.putExtra("patientID", patientID);
                                i7.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i7.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i7);


                            }

                        } catch (JSONException e) {
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

    Bitmap bitmap, bitmapCover;
    public String selectedPath = "";
    String strLength, TAG = "DOCTOR";

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose Photo from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPatientProfile.this, R.style.MyDialogTheme);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utils.checkPermission(EditPatientProfile.this);
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
                            imgUser.setImageBitmap(BitmapFactory.decodeFile(selectedPath));

                        } else {
                            selectedPath = "";
                            Utils.showToast("Please Capture Image!!!", EditPatientProfile.this);
                        }
                    }
                    break;
                case SELECT_PICTURE:
                    if (requestCode == SELECT_PICTURE) {
                        if (uri != null) {
                            System.out.println("going to collect Data of Select Picture");
                            selectedPath = getRealPathFromURI(uri);
                            System.out.print(selectedPath);
                            imgUser.setImageBitmap(BitmapFactory.decodeFile(selectedPath));
                        } else {
                            selectedPath = "";
                            Utils.showToast("Please Select Image!!!", EditPatientProfile.this);
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

