package com.docpoc.doctor.webServices;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.docpoc.doctor.classes.ChatMessage;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadVideoFromUrl extends AsyncTask<String, String, String> {

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    public String url;
    public ImageView iv;
    public ProgressBar pBar;
    public Context ct;
    public String fileName;
    public ChatMessage cBean;

    public DownloadVideoFromUrl(Context ct, ChatMessage cBean, String url, ImageView iv, ProgressBar pBar) {
        this.url = url;
        this.iv = iv;
        this.pBar = pBar;
        this.ct = ct;
        this.cBean = cBean;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * Downloading file in background thread
     */
    long total = 0;
    int lenghtOfFile = 0;
    String ext;

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            String tempUrl = f_url[0];
            URL url = new URL(tempUrl);
            URLConnection conection = url.openConnection();
            conection.connect();

            ext = tempUrl.substring(tempUrl.length() - 3, tempUrl.length());

            lenghtOfFile = conection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = new FileOutputStream("/sdcard/dr_pocket/images/" + cBean.chatID + "." + ext);

            byte data[] = new byte[1024];


            while ((count = input.read(data)) != -1) {
                total += count;

                ((Activity) ct).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pBar.setProgress((int) ((total * 100) / lenghtOfFile));
                    }
                });
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {


    }


    @Override
    protected void onPostExecute(String file_url) {

        String imagePath = Environment.getExternalStorageDirectory().toString() + "/dr_pocket/images/" + cBean.chatID + "." + ext;

        iv.setImageDrawable(Drawable.createFromPath(imagePath));

    }

}
