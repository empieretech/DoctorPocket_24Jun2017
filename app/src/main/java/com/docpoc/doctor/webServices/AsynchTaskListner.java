package com.docpoc.doctor.webServices;


import com.docpoc.doctor.ImageLoader.Constant;

/**
 * Created by Sagar Sojitra on 3/4/2016.
 */
public interface AsynchTaskListner {

    public void onTaskCompleted(String result, Constant.REQUESTS request);

    public void onProgressUpdate(String uniqueMessageId, int progres);

    public void onProgressComplete(String uniqueMessageId, String result, Constant.REQUESTS request);
}
