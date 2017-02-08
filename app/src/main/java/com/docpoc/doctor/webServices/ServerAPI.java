package com.docpoc.doctor.webServices;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ServerAPI {

    public static JSONObject Login(String urlStr, Activity act) {
        Utils.showSimpleSpinProgressDialog(act, "Please wait..");
        String strUrl = null;
        try {
            urlStr = MyConstants.BASE_URL + urlStr+ "&unused=" + System.currentTimeMillis();


            JSONParser jparser = new JSONParser();
            JSONObject json = jparser.getJSONFromUrl(urlStr);

            JSONObject jsonResponse = null;


            //	Log.d("SERVER API ", "Login: " + json.toString());
            //	response = json.getString("response");

            jsonResponse = json.getJSONObject("document").getJSONObject("response");
            Log.d("SERVER API ", "Login API: " + jsonResponse.toString());
            Utils.removeSimpleSpinProgressDialog();
            return jsonResponse;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Utils.removeSimpleSpinProgressDialog();
        return null;

    }


    public static JSONObject getAllPlans(Activity act) {
        Utils.showSimpleSpinProgressDialog(act, "Please wait..");
        try {
            String urlStr = MyConstants.BASE_URL + MyConstants.URL_GET_ALLPLAN;

            JSONParser jparser = new JSONParser();
            JSONObject json = jparser.getJSONFromUrl(URLEncoder.encode(urlStr, "UTF-8"));
            JSONObject jsonResponse = null;
            String response = "";


            jsonResponse = json.getJSONObject("document").getJSONObject("response");
            //	Log.d("SERVER API ", "Login: " + json.toString());
            //	response = json.getString("response");
            Log.d("SERVER API ", "Login API: " + jsonResponse.toString());
            Utils.removeSimpleSpinProgressDialog();
            return jsonResponse;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }
        Utils.removeSimpleSpinProgressDialog();
        return null;

    }


    public static JSONObject signUP(Activity act, String url) {
        Utils.showSimpleSpinProgressDialog(act, "Please wait..");
        try {
            String urlStr = MyConstants.BASE_URL + URLEncoder.encode(url, "UTF-8");

            Log.i("TAG", "TAG : URL " + urlStr);

            JSONParser jparser = new JSONParser();
            JSONObject json = jparser.getJSONFromUrl(url);
            Log.d("SERVER API ", "Sign Up: " + json.toString());
            //response = json.getString("response");
            Utils.removeSimpleSpinProgressDialog();
            return json;
        } catch (UnsupportedEncodingException e) {
            Utils.removeSimpleSpinProgressDialog();
            e.printStackTrace();
            return null;
        }
    }


    public static JSONObject updateProfile(Activity act, String url) {
        Utils.showSimpleSpinProgressDialog(act, "Please wait..");
        try {
            String urlStr = MyConstants.BASE_URL + URLEncoder.encode(url, "UTF-8");

            Log.i("TAG", "TAG : URL " + urlStr);

            JSONParser jparser = new JSONParser();
            JSONObject json = jparser.getJSONFromUrl(url);

            Log.d("SERVER API ", "Sign Up: " + json.toString());
            //response = json.getString("response");
            Utils.removeSimpleSpinProgressDialog();
            return json;
        } catch (UnsupportedEncodingException e) {
            Utils.removeSimpleSpinProgressDialog();
            e.printStackTrace();
            return null;
        }

    }

    public static JSONObject generalAPI(Activity act, String url) {
        JSONObject jsonResponse = null;
        try {
            Utils.showSimpleSpinProgressDialog(act, "Please wait..");
            String urlStr = MyConstants.BASE_URL +url;

            Log.i("TAG", "TAG : URL " + urlStr);

            JSONParser jparser = new JSONParser();
            JSONObject json = jparser.getJSONFromUrl(urlStr);

            String response = "";


            jsonResponse = json.getJSONObject("document").getJSONObject("response");
            Log.d("SERVER API ", "General API: " + jsonResponse.toString());
            //	response = json.getString("response");


        }catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Utils.removeSimpleSpinProgressDialog();
        return jsonResponse;

    }

    public static JSONObject ForgotAPI(Context act, String url) {
        JSONObject jsonResponse = null;
        String response = "";
        try {
            Utils.showSimpleSpinProgressDialog(act, "Please wait..");
            String urlStr = MyConstants.BASE_URL + URLEncoder.encode(url, "UTF-8");

            Log.i("TAG", "TAG : URL " + urlStr);

            JSONParser jparser = new JSONParser();
            JSONObject json = jparser.getJSONFromUrl(url);


            jsonResponse = json.getJSONObject("document").getJSONObject("response");
            //	Log.d("SERVER API ", "General API: " + json.toString());
            response = json.getString("document");


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Utils.removeSimpleSpinProgressDialog();
        return jsonResponse;

    }

    public static JSONObject sendAppontAPI(Activity act, String url) {
        Utils.showSimpleSpinProgressDialog(act, "Please wait..");
        try {
            String urlStr = MyConstants.BASE_URL + URLEncoder.encode(url, "UTF-8");

            Log.i("TAG", "TAG : URL " + urlStr);

            JSONParser jparser = new JSONParser();
            JSONObject json = jparser.getJSONFromUrl(url);


            Log.d("SERVER API ", "General API: Responce :  " + json.toString());
            Utils.removeSimpleSpinProgressDialog();
            return json;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        Utils.removeSimpleSpinProgressDialog();
        return null;

    }

    public static JSONObject CancelAPI(String url) {
        try {
            String urlStr = MyConstants.BASE_URL + URLEncoder.encode(url, "UTF-8");

            Log.i("TAG", "TAG : URL " + urlStr);

            JSONParser jparser = new JSONParser();
            JSONObject json = jparser.getJSONFromUrl(url);
            JSONObject jsonResponse = null;
            String response = "";


            jsonResponse = json.getJSONObject("document").getJSONObject("response");
            Log.d("SERVER API ", "General API: " + json.toString());
            //	response = json.getString("response");

            Utils.removeSimpleSpinProgressDialog();
            return jsonResponse;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Utils.removeSimpleSpinProgressDialog();
        return null;

    }


}