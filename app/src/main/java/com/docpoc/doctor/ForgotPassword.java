package com.docpoc.doctor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.docpoc.doctor.R;
import com.docpoc.doctor.classes.Internet;
import com.docpoc.doctor.webServices.BaseTask;
import com.docpoc.doctor.webServices.GlobalBeans;
import com.docpoc.doctor.webServices.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Badruduja on 3/22/16.
 */
public class ForgotPassword {

      static Dialog popupDialog;

    ProgressDialog mprogressDialog;
    static EditText et_email;
    Context mContext;

    public  void  displayLayout(Context context){

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_forgot_password,null);

        et_email  = (EditText) view.findViewById(R.id.layout_forgotPassword_et_emaiID);
        Button btn_send = (Button) view.findViewById(R.id.layout_forgotPassword_Btn_send);
        Button btn_cancel = (Button) view.findViewById(R.id.layout_forgotPassword_Btn_cancel);



        popupDialog = new Dialog(context);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popupDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //popupDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        popupDialog.setContentView(view);
        popupDialog.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams wmlp = popupDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP|Gravity.LEFT;
        wmlp.x+= 0;
        wmlp.y+= 0;
        wmlp.height= LinearLayout.LayoutParams.MATCH_PARENT;
        wmlp.width= LinearLayout.LayoutParams.MATCH_PARENT;

        popupDialog.getWindow().setDimAmount(0.6f);
        popupDialog.getWindow().setAttributes(wmlp);
        popupDialog.getWindow().setAttributes(wmlp);
   //     popupDialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        popupDialog.show();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                forgotAPI();
                popupDialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public void  forgotAPI() {


        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(et_email.getWindowToken(), 0);

        final String emailId =  et_email.getText().toString();


        if (!isValidEmail(emailId)  ){


            new AlertDialog.Builder(this.mContext)
                    .setTitle("Error!")
                    .setMessage("Emai id invalid.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            return;
        }

        if(!Internet.isAvailable(mContext)){
            Internet.showAlertDialog(mContext, "Error!", "No Internet Connection", false);

            return;
        }


        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public JSONObject onTaskRunning(int taskId, Object data) {
                String urlStr = "forget_pass.php?access_token=testermanishrahul234142test&userName=" + emailId;
                return ServerAPI.ForgotAPI(  mContext, urlStr);

                //return updateHistory();
            }

            @Override
            public void onTaskResult(int taskId, JSONObject result) {
                if (mprogressDialog != null) {
                    mprogressDialog.dismiss();
                    mprogressDialog = null;
                }
                onForgotResult(result);
            }

            @Override
            public void onTaskProgress(int taskId, Object... values) {
            }

            @Override
            public void onTaskPrepare(int taskId, Object data) {
                mprogressDialog = new ProgressDialog( mContext);
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

    public  void onForgotResult(JSONObject jsonResponse){

        GlobalBeans.LoginResultBean loginResult = new GlobalBeans.LoginResultBean();
        if (jsonResponse  != null) {
            try {

                loginResult.message = jsonResponse.getString("message");

                if (jsonResponse.getString("status").equals("1")) {

                    try {

                        loginResult.message = jsonResponse.getString("message");

                   //     AppManager.user.setUserID(jsonResponse.getString("user_id"));
                   //     AppManager.user.setUserName(jsonResponse.getString("user_name"));
                    //    AppManager.user.setUserEmail(jsonResponse.getString("email"));
                        //	DataHolder.getDataHolder().user.setMobileNo(jsonResponse.getString("mobile_no"));
                        //	DataHolder.getDataHolder().user.setRefferalCode(jsonResponse.getString("referal_code"));

                        Toast.makeText(mContext, loginResult.message, Toast.LENGTH_LONG).show();


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(mContext, loginResult.message, Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Log.e("login", e.toString());
            }

        }

    }


}
