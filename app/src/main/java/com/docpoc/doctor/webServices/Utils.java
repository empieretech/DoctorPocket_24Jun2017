package com.docpoc.doctor.webServices;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.docpoc.doctor.R;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Utils {

    /* for GCM */
    static final String EXTRA_MESSAGE = "message";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
    public static SimpleDateFormat dayOFWeekFormat = new SimpleDateFormat("EEEE");
    public static ProgressDialog pDialog;
    static float density = 1;
    private static ProgressDialog mProgressDialog;
    public static Dialog dialog;

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display.getWidth();
    }

    public static int validateDate(int date) {
        return (date < 10) ? date : Integer.parseInt("0" + date);
    }

    public static void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods


        Crashlytics.setUserIdentifier("Doctor Pocket");
        Crashlytics.setUserEmail("sojitrasagar108@gmail.com");
        Crashlytics.setUserName("Sagar Sojitra");
    }

    public static void printMap(Map<String, String> map) {

        for (String key : map.keySet()) {
            System.out.println(key + "====>" + map.get(key));
        }

    }

    public static String getTodayDay() {

        return dayOFWeekFormat.format(new Date());
    }

    public static String intentToString(Intent intent) {
        if (intent == null) {
            return null;
        }

        return intent.toString() + " " + bundleToString(intent.getExtras());
    }

    public static String bundleToString(Bundle bundle) {
        StringBuilder out = new StringBuilder("Bundle[");

        if (bundle == null) {
            out.append("null");
        } else {
            boolean first = true;
            for (String key : bundle.keySet()) {
                if (!first) {
                    out.append(", ");
                }

                out.append(key).append('=');

                Object value = bundle.get(key);

                if (value instanceof int[]) {
                    out.append(Arrays.toString((int[]) value));
                } else if (value instanceof byte[]) {
                    out.append(Arrays.toString((byte[]) value));
                } else if (value instanceof boolean[]) {
                    out.append(Arrays.toString((boolean[]) value));
                } else if (value instanceof short[]) {
                    out.append(Arrays.toString((short[]) value));
                } else if (value instanceof long[]) {
                    out.append(Arrays.toString((long[]) value));
                } else if (value instanceof float[]) {
                    out.append(Arrays.toString((float[]) value));
                } else if (value instanceof double[]) {
                    out.append(Arrays.toString((double[]) value));
                } else if (value instanceof String[]) {
                    out.append(Arrays.toString((String[]) value));
                } else if (value instanceof CharSequence[]) {
                    out.append(Arrays.toString((CharSequence[]) value));
                } else if (value instanceof Parcelable[]) {
                    out.append(Arrays.toString((Parcelable[]) value));
                } else if (value instanceof Bundle) {
                    out.append(bundleToString((Bundle) value));
                } else {
                    out.append(value);
                }

                first = false;
            }
        }

        out.append("]");
        return out.toString();
    }

    public static String getValidateDate() {

        final Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);


        return Utils.validateDate(mDay) + "-" + Utils.validateDate(mMonth + 1) + "-" + mYear;
    }

    public static boolean checkValidation(EditText et) {
        if (et.getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getValue(EditText et) {
        return et.getText().toString();
    }

    public static boolean isEmpty(EditText et) {
        return et.getText().toString().equalsIgnoreCase("");
    }

    public static void showProgressDialog(Context context, String msg) {
        try {

            if (dialog == null) {
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.custom_progress_dialog);


            }

            TextView txtView = (TextView) dialog.findViewById(R.id.textView);
            txtView.setText(msg);

            if (!dialog.isShowing())
                dialog.show();

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgressDialog(Context context) {
        showProgressDialog(context, "Please Wait..");
    }

    public static void hideProgressDialog() {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUpperCase(String name) {
        StringBuilder sb = new StringBuilder(name); // one StringBuilder object
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString(); // one String object
    }


    public static void addValueToEditor(SharedPreferences.Editor et, Object o, Activity act) {
        Class c;
        try {
            c = Class.forName(o.getClass().getCanonicalName(), false, o.getClass().getClassLoader());

            Field[] fields = c.getFields();
            for (Field f : fields) {
                f.setAccessible(true);
                if (f.getType() == String.class) {
                    et.putString(f.getName(), f.get(o).toString());

                } else if (f.getType() == Integer.class) {
                    et.putInt(f.getName(), Integer.parseInt(f.get(o).toString()));

                } else if (f.getType() == Boolean.class) {
                    et.putBoolean(f.getName(), Boolean.valueOf(f.get(o).toString()));
                }
            }

            et.commit();

            showToast("prefrence saved", act);
        } catch (Exception e) {
            e.printStackTrace();


        }

    }

    public static void showSimpleSpinProgressDialog(Context context, String message) {
        showProgressDialog(context, message);
/*
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        } else {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }*/
    }

    public static void removeSimpleSpinProgressDialog() {
        hideProgressDialog();
       /* if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }*/
    }


    public static String getTodayDate() {
        final Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);


        return Utils.validateDate(mDay) + "-" + Utils.validateDate(mMonth + 1) + "-" + mYear;

    }


    public static float getDisplayMetricsDensity(Context context) {
        density = context.getResources().getDisplayMetrics().density;

        return density;
    }

    public static int getPixel(Context context, int p) {
        if (density != 1) {
            return (int) (p * density + 0.5);
        }
        return p;
    }

    public static Animation FadeAnimation(float nFromFade, float nToFade) {
        Animation fadeAnimation = new AlphaAnimation(nToFade, nToFade);

        return fadeAnimation;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();
        return uuid;
    }

    public static Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f);

        return inFromRight;
    }

    public static Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f);

        return inFromLeft;
    }

    public static Animation inFromBottomAnimation() {
        Animation inFromBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT,
                0.0f);

        return inFromBottom;
    }

    public static Animation outToLeftAnimation() {
        Animation outToLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return outToLeft;
    }

    public static Animation outToRightAnimation() {
        Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

        return outToRight;
    }

    public static Animation outToBottomAnimation() {
        Animation outToBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                +1.0f);

        return outToBottom;
    }


    public static boolean isNetworkAvailable(Context activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            showToast("Please connect to Internet", activity);
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean eMailValidation(String emailstring) {
        if (null == emailstring || emailstring.length() == 0) {
            return false;
        }
        Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher emailMatcher = emailPattern.matcher(emailstring);
        return emailMatcher.matches();
    }

    // public static String convert24HoursTo12HoursFormat(String twelveHourTime)
    // throws ParseException {
    // return outputformatter.format(inputformatter.parse(twelveHourTime));
    // }

    public static void showToast(String msg, Context ctx) {
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
        // toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    /**
     * You can integrate interstitials in any placement in your app, but for
     * testing purposes we present integration tied to a button click
     */
   /* public static Typeface setNormalFontFace(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/DroidSans.ttf");
        return font;
    }

    public static Typeface setBoldFontFace(Context context) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/DroidSans-Bold.ttf");
        return font;
    }*/
    public static View setTabCaption() {

        return null;
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int RECORD_AUDDIO = 555;


    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public static boolean checkAudioPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.RECORD_AUDIO)) {

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("MicroPhone usage Permission NNecessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDDIO);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {

                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDDIO);

                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }



    public static String manageDecimal(String temp) {
        String displayRiskDecimal = null, before = null, after = null;
        if (temp.contains(".")) {
            int in = temp.indexOf(".") + 1;
            before = temp.substring(0, in);
            after = temp.substring(in, temp.length());
            if (after.length() == 1) {
                displayRiskDecimal = before + after + "0";
            } else {
                displayRiskDecimal = before + after;
            }
            return displayRiskDecimal;
        } else {
            displayRiskDecimal = temp + ".00";
            return displayRiskDecimal;
        }
    }

    public static String manageDecimalPointForGetLines(String temp) {
        String displayRiskDecimal = null, before = null, after = null;
        int in = temp.indexOf(".") + 1;
        before = temp.substring(0, in);
        after = temp.substring(in, temp.length());
        if (after.equals("00")) {
            displayRiskDecimal = before.substring(0, before.length() - 1);
        } else if (after.length() == 2) {
            if (after.startsWith("0", 1)) {
                displayRiskDecimal = after.substring(0, after.length() - 1);
                displayRiskDecimal = before + displayRiskDecimal;
            }
        }
        return displayRiskDecimal;
    }

    public static String manageSign(String temp) {
        String displayRiskDecimal = null;
        if (temp.contains("-") || temp.contains("+")) {
            displayRiskDecimal = temp;
        } else {
            displayRiskDecimal = "+" + temp;
        }
        return displayRiskDecimal;
    }

    public static SpannableStringBuilder displayNameValue(String battingTeamName, String displayBaseValue) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString WordtoSpan = new SpannableString(displayBaseValue);
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#1298DE")), 0, displayBaseValue.length(), 0);
        builder.append(battingTeamName);
        builder.append(WordtoSpan);
        return builder;
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

  /*  public static String removeSpecialChar(String value) {
        try {
            if (value.contains("("))
                value = value.replaceAll("(", "");
            if (value.contains(")"))
                value = value.replaceAll(")", "");
            if (value.contains("-"))
                value = value.replaceAll("-", "");
            if (value.contains(" "))
                value = value.replaceAll(" ", "");
            if (value.contains("."))
                value = value.replaceAll(".", "");
            if (value.contains(":"))
                value = value.replaceAll(":", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }*/

    public static String getUrlVideoRTSP(String urlYoutube, String video_id) {
        try {
            String gdy = "http://gdata.youtube.com/feeds/api/videos/";
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String id = video_id;
            URL url = new URL(gdy + id);

            System.out.println("Gdata Url :::" + url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Document doc = documentBuilder.parse(connection.getInputStream());
            Element el = doc.getDocumentElement();
            NodeList list = el.getElementsByTagName("media:content");// /media:content
            String cursor = urlYoutube;
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node != null) {
                    NamedNodeMap nodeMap = node.getAttributes();
                    HashMap<String, String> maps = new HashMap<String, String>();
                    for (int j = 0; j < nodeMap.getLength(); j++) {
                        Attr att = (Attr) nodeMap.item(j);
                        maps.put(att.getName(), att.getValue());
                    }
                    if (maps.containsKey("yt:format")) {
                        String f = maps.get("yt:format");
                        if (maps.containsKey("url")) {
                            cursor = maps.get("url");
                        }
                        if (f.equals("1"))
                            return cursor;
                    }
                }
            }
            return cursor;
        } catch (Exception ex) {
            Log.e("Get ======>>", ex.toString());
        }
        return urlYoutube;

    }

    public static void hideKeyboard(Activity context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static String getcurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("KK:mm a");
        return dateFormat.format(new Date());
    }

    public static String findDeviceID(Context cnt) {
        return Secure.getString(cnt.getContentResolver(), Secure.ANDROID_ID);
    }
}
