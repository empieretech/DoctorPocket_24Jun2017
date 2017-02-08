package com.docpoc.doctor.webServices;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.utils.MultipartUtility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sagar Sojitra on 3/8/2016.
 */
public class AsynchHttpRequest extends AsyncTask<Map<String, String>, Void, String> {

    public Fragment ft;
    public Constant.REQUESTS request;
    public Map<String, String> map;
    public AsynchTaskListner aListner;
    public Constant.POST_TYPE post_type;
    public String chatId = "";

    public AsynchHttpRequest(Fragment ft, Constant.REQUESTS request, Constant.POST_TYPE post_type, Map<String, String> map) {
        this.ft = ft;
        this.ct = ft.getActivity();
        this.request = request;
        this.map = map;
        this.aListner = (AsynchTaskListner) ft;
        this.post_type = post_type;
        this.map.put("unused", System.currentTimeMillis() + "");

        if (map.containsKey("chatId")) {
            chatId = map.get("chatId");
            map.remove("chatId");
        }
        if (Utils.isNetworkAvailable(ft.getActivity())) {

            if (!map.containsKey("show")) {
                Utils.showSimpleSpinProgressDialog(ft.getActivity(), "Please Wait..");
            }


            new MyRequest().execute(this.map);
        } else {
            aListner.onTaskCompleted(null, request);
            Utils.showToast("No Internet, Please try again later", ft.getActivity());
        }
    }

    public Context ct;

    public AsynchHttpRequest(Context ft, Constant.REQUESTS request, Constant.POST_TYPE post_type, Map<String, String> map) {
        this.ct = ft;
        this.request = request;
        this.map = map;
        this.aListner = (AsynchTaskListner) ft;
        this.post_type = post_type;
        this.map.put("unused", System.currentTimeMillis() + "");


        if (map.containsKey("chatId")) {
            chatId = map.get("chatId");
            map.remove("chatId");
        }


        if (Utils.isNetworkAvailable(ct)) {
            if (!map.containsKey("show")) {
                Utils.showSimpleSpinProgressDialog(ft, "Please Wait..");
            }
            new MyRequest().execute(this.map);
        } else {
            aListner.onTaskCompleted(null, request);
            Utils.showToast("No Internet, Please try again later", ct);
        }
    }

    @Override
    protected String doInBackground(Map<String, String>... params) {
        return null;
    }


    class MyRequest extends AsyncTask<Map<String, String>, Integer, String>

    {
        MyCustomMultiPartEntity reqEntity;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Map<String, String>... map) {
            System.out.println("::urls ::" + map[0].get("url"));

            String responseBody = "";
            try {
                switch (post_type) {
                    case GET:
                        String tempData = "";
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        String query = map[0].get("url");
                        System.out.println();
                        tempData += "\n\n\n\n URL : " + map[0].get("url");

                        map[0].remove("url");


                        List<String> values = new ArrayList<String>(map[0].values());


                        List<String> keys = new ArrayList<String>(map[0].keySet());

                        for (int i = 0; i < values.size(); i++) {
                            System.out.println();

                            System.out.println(keys.get(i) + "====>" + values.get(i));
                            query = query + keys.get(i) + "=" + values.get(i);
                            tempData += "\n" + keys.get(i) + "====>" + values.get(i);
                            if (i < values.size() - 1) {
                                query += "&";
                            }
                        }


                        System.out.println("URL" + "====>" + query);
                        HttpGet httpGet = new HttpGet(query);

                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        responseBody = EntityUtils.toString(httpEntity);

                        System.out.println("Responce" + "====>" + responseBody);
                        tempData += "\n" + "Responce" + "====>" + responseBody;
                        writeToSDFile(tempData);
                        return responseBody;

                    case POST:
                        tempData = "";
                        System.out.println("new Requested URL >> " + map[0].get("url"));
                        tempData += "\n\n\n\n URL : " + map[0].get("url");
                        HttpPost postRequest = new HttpPost(map[0].get("url"));
                        DefaultHttpClient httpclient = new DefaultHttpClient();
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                        for (String key : map[0].keySet()) {
                            System.out.println(key + "====>" + map[0].get(key));
                            tempData += "\n" + key + "====>" + map[0].get(key);
                            nameValuePairs.add(new BasicNameValuePair(key, map[0].get(key)));
                        }
                        postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        System.out.println("Final URI::" + postRequest.getEntity().toString());
                        HttpResponse response = httpclient.execute(postRequest);
                        httpEntity = response.getEntity();
                        responseBody = EntityUtils.toString(httpEntity);
                        tempData += "\n" + "Responce" + "====>" + responseBody;
                        writeToSDFile(tempData);
                        return responseBody;


                    case POST_WITH_IMAGE:


                        String charset = "UTF-8";
                        try {
                            MultipartUtility multipart = new MultipartUtility(map[0].get("url"), charset);
                            map[0].remove("url");
                            multipart.addFormField("type", "image");


                            for (String key : map[0].keySet()) {
                                System.out.println();
                                System.out.println(key + "====>" + map[0].get(key));


                                if (key.equals("image")) {

                                    String type = "image";
                                    File f = new File(map[0].get(key));

                                    multipart.addFilePart("image", f);
                                    multipart.addFormField("filename", f.getName());
                                } else {

                                    multipart.addFormField(key, map[0].get(key));
                                }
                            }

                            responseBody = multipart.finish();
                            Log.d("PostActivity", "SERVER REPLIED: " + responseBody);
                            return responseBody;
                        } catch (IOException e) {
                            Log.d("ExPostActivity", e.getMessage());
                        }
                }


            } catch (Exception e) {
                e.printStackTrace();
                aListner.onTaskCompleted(null, request);
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            if (!chatId.isEmpty()) {
                aListner.onProgressComplete(chatId, result, request);
            } else {
                aListner.onTaskCompleted(result, request);
            }
            super.onPostExecute(result);
        }
    }

    private void checkExternalMedia() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

    }

    /**
     * Method to write ascii text characters to file on SD card. Note that you must add a
     * WRITE_EXTERNAL_STORAGE permission to the manifest file or this method will throw
     * a FileNotFound Exception because you won't have write permission.
     */

    private void writeToSDFile(String data) {

       /* // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal
        File root = android.os.Environment.getExternalStorageDirectory();


        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
        String buffer = "";
        File dir = new File(root.getAbsolutePath() + "/fyndine");
        dir.mkdirs();
        File file = new File(dir, "fyndine_log.txt");
        if (file.exists()) {

            try {

                FileInputStream fIn = new FileInputStream(file);
                BufferedReader myReader = new BufferedReader(
                        new InputStreamReader(fIn));
                String aDataRow = "";
                String aBuffer = "";
                while ((aDataRow = myReader.readLine()) != null) {
                    aBuffer += aDataRow + "\n";
                }
                buffer = aBuffer.toString();
                buffer += data;
                myReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            if (buffer.isEmpty()) {
                pw.println(data);
            } else {
                pw.println(buffer);
            }
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

    /**
     * Method to read in a text file placed in the res/raw directory of the application. The
     * method reads in all lines of the file sequentially.
     */

   /* public void wrtieFileOnInternalStorage(Context mcoContext, String sBody) {
        File file = new File(mcoContext.getFilesDir(), "fyndine");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();

            try {
                File gpxfile = new File(file, "Fyndine_Logs.txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(sBody);
                writer.flush();
                writer.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File sdcard = new File(mcoContext.getExternalFilesDirs(), "fyndine");
            //   File sdcard = Environment.getExternalStorageDirectory();
            if (!file.exists())
                file.getParentFile().mkdirs();
//Get the text file
            File readFile = new File(sdcard, "Fyndine_Logs.txt");

//Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(readFile));

                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                sBody += text.toString();

                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                //You'll need to add proper error handling here
            }

            try {
                File gpxfile = new File(file, "Fyndine_Logs.txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(sBody);
                writer.flush();
                writer.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }*/
}
