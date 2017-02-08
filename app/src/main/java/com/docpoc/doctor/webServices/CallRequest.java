package com.docpoc.doctor.webServices;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.docpoc.doctor.ImageLoader.Constant;
import com.docpoc.doctor.App;

import java.util.HashMap;
import java.util.Map;

;

public class CallRequest {


    // public static String url = "http://starlineinfotech.net/fyndine/index.php?";
    //  public static String url ="http://mexaninfotech.com/fyndine/webservices/index.php?";
    public static String access_token = "testermanishrahul234142test";
    //public static String url = "http://192.168.88.1/grocmart/webservices/serviceCall.php?";
    public App app;
    public Context ct;
    public Fragment ft;


    public CallRequest(Fragment ft) {
        app = App.getInstance();
        this.ft = ft;
    }

    public CallRequest(Context ct) {
        this.ct = ct;
        app = App.getInstance();
    }

    public CallRequest() {

    }


    public void updatePatientProfile(String userId, String imagePath, String mobile, String address
            , String country, String state, String name, String gender, String timeZone) {

        //  updateProfile.php?access_token=testermanishrahul234142test&user_id=1&DOB=06-12-1981&
        // image=$_FILE&mobile=98765432159&address=address&country=India&state=UP&name=sanjiv&gender=male&zone=Asia/Kolkata
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "updateProfile.php?");
        map.put("access_token", access_token);
        map.put("user_id", userId.replace(" ", ""));
        if (!imagePath.isEmpty()) {
            map.put("image", imagePath);
        }
        map.put("image", imagePath.replace(" ", ""));
        map.put("mobile", mobile.replace(" ", ""));
        map.put("address", address);
        map.put("country", country);
        map.put("state", state);
        map.put("name", name);
        map.put("gender", gender);
        map.put("patient_zone", timeZone);

        Utils.printMap(map);

        if (imagePath.isEmpty()) {
            new AsynchHttpRequest(ct, Constant.REQUESTS.updatePatientProfile, Constant.POST_TYPE.POST, map);
        } else {
            new AsynchHttpRequest(ct, Constant.REQUESTS.updatePatientProfile, Constant.POST_TYPE.POST_WITH_IMAGE, map);
        }
    }

    public void updateDoctorProfile(String userId, String imagePath, String firstName, String speciality,
                                    String morning_appointment_start, String morning_appointment_end,
                                    String eve_appointment_start, String eve_appointment_end, String amount,
                                    String mobile, String address, String about, String timeZone) {

        // stringWithFormat:@"%@updateDoctorProfile.php?
        // access_token=testermanishrahul234142test&user_id=%@
        // &firstName=%@&lastName=&speciality=%@&morning_appointment_start=%@&morning_appointment_end=%@&eve_appointment_start=%
        // @&eve_appointment_end=%@&amount=%@&address=%@&phoneNumber=&about=%@&doctor_zone=%@"
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "updateDoctorProfile.php?");
        map.put("access_token", access_token);
        map.put("user_id", userId.replace(" ", ""));
        map.put("firstName", firstName);
        map.put("lastName", "");
        map.put("speciality", speciality);
        map.put("morning_appointment_start", morning_appointment_start);
        map.put("morning_appointment_end", morning_appointment_end);
        map.put("eve_appointment_start", eve_appointment_start);
        map.put("eve_appointment_end", eve_appointment_end);
        map.put("amount", amount.replace("$", ""));
        map.put("address", address);
        map.put("about", about);
        map.put("doctor_zone", timeZone);
        map.put("phoneNumber", mobile);
        if (!imagePath.isEmpty()) {
            map.put("image", imagePath);
        }
        map.put("image", imagePath.replace(" ", ""));


        Utils.printMap(map);

        if (imagePath.isEmpty()) {
            new AsynchHttpRequest(ct, Constant.REQUESTS.updateDoctorProfile, Constant.POST_TYPE.POST, map);
        } else {
            new AsynchHttpRequest(ct, Constant.REQUESTS.updateDoctorProfile, Constant.POST_TYPE.POST_WITH_IMAGE, map);
        }
    }


    public void acceptRejectBooking(String bookingID, String docId, String status) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "accept_reject.php?");
        map.put("access_token", access_token);
        map.put("booking_id", bookingID.replace(" ", ""));
        map.put("status", status);
        map.put("doctor_id", docId);
        map.put("user_id", status);

        new AsynchHttpRequest(ct, Constant.REQUESTS.acceptReject, Constant.POST_TYPE.POST, map);

    }


    public void createBooking(String bookingID,
                              String booking_date,
                              String booking_time,

                              String question,
                              String booking_amount,

                              String docId, String userId) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "createBooking.php?");
        map.put("access_token", access_token);
        map.put("booking_date", booking_date);
        map.put("booking_time", booking_time);
        map.put("amount_status", "1");
        map.put("question", question);
        map.put("booking_id", bookingID.replace(" ", ""));
        map.put("booking_amount", booking_amount);


        map.put("doctor_id", docId);
        map.put("user_id", userId);

        new AsynchHttpRequest(ct, Constant.REQUESTS.createBooking, Constant.POST_TYPE.POST, map);

    }

    public void checkTimeOver(String bookingID, String docId, String user_id) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "check_time_over.php?");
        map.put("access_token", access_token);
        map.put("doctor_id", docId.replace(" ", ""));

        map.put("bookingId", bookingID.replace(" ", ""));
        map.put("user_id", user_id.replace(" ", ""));

        new AsynchHttpRequest(ct, Constant.REQUESTS.checkTimeOver, Constant.POST_TYPE.POST, map);

    }

    public void updateDevice(String user_type, String id, String deviceId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "updateDeviceId.php?");

        map.put("user_type", user_type);

        map.put("id", id);
        map.put("device_type", "android");
        map.put("deviceid", deviceId);

        new AsynchHttpRequest(ct, Constant.REQUESTS.updateDevice, Constant.POST_TYPE.GET, map);

    }

    public void setOnOffStatus(String user_type, String id, String status) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "status.php?");
        map.put("action", "set");
        map.put("user_type", user_type.replace(" ", ""));

        map.put("id", id);
        map.put("status", status.replace(" ", ""));
        map.put("show", "");

        new AsynchHttpRequest(ct, Constant.REQUESTS.setOnOffStatus, Constant.POST_TYPE.POST, map);

    }


    public void getOnOffStatus(String user_type, String id, String booking_id) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "status.php?");
        map.put("action", "get");
        map.put("user_type", user_type.replace(" ", ""));
        map.put("id", id.replace(" ", ""));
        map.put("booking_id", booking_id);
        map.put("show", "");

        new AsynchHttpRequest(ct, Constant.REQUESTS.getOnOffStatus, Constant.POST_TYPE.POST, map);

    }

    public void cancelChat(String doctor_id, String booking_id) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "cancel_chat.php?");
        map.put("action", "set");
        map.put("doctor_id", doctor_id.replace(" ", ""));
        map.put("booking_id", booking_id.replace(" ", ""));

        new AsynchHttpRequest(ct, Constant.REQUESTS.cancelChat, Constant.POST_TYPE.POST, map);

    }

    public void updateBadge(String type, String id) {

        try {

            Map<String, String> map = new HashMap<String, String>();
            map.put("access_token", access_token);
            if (type.equalsIgnoreCase("d")) {
                map.put("url", MyConstants.BASE_URL + "updateDoctorBadge.php?");
                map.put("doctor_id", id.replace(" ", ""));
            } else {
                map.put("url", MyConstants.BASE_URL + "updatePatientBadge.php?");
                map.put("user_id", id.replace(" ", ""));
            }

            new AsynchHttpRequest(ct, Constant.REQUESTS.updateBadge, Constant.POST_TYPE.GET, map);
        } catch (NullPointerException e){
            e.printStackTrace();
        }


    }


    public void geChatHistory(String booking_id) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "getChats.php?");


        map.put("booking_id", booking_id.replace(" ", ""));

        new AsynchHttpRequest(ct, Constant.REQUESTS.chatHistory, Constant.POST_TYPE.GET, map);

    }

    public void getDiscount(String user_id) {
        //  http://admindoctorpocket.in/web_serv/getDiscounts.php?user_id=96

        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "getDiscounts.php?");

        map.put("user_id", user_id);

        new AsynchHttpRequest(ct, Constant.REQUESTS.getDiscounts, Constant.POST_TYPE.GET, map);

    }


    public void updateRefer(String refer_id, String type) {
        //         http://admindoctorpocket.in/web_serv/updateRefer.php?refer_id=id&sign_up_used='Y';

        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "updateRefer.php?");

        map.put("refer_id", refer_id.replace(" ", ""));
        map.put(type.replace(" ", ""), "Y");

        new AsynchHttpRequest(ct, Constant.REQUESTS.updateRefer, Constant.POST_TYPE.POST, map);

    }

    public void getCountry() {
        //   http://admindoctorpocket.in/web_serv/get_countries.php
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "get_countries.php?");

        new AsynchHttpRequest(ct, Constant.REQUESTS.getCountry, Constant.POST_TYPE.GET, map);

    }

    public void getTimeZone(String country, String state) {
        //http://admindoctorpocket.in/web_serv/getTimeZone.php?country=Canada&state=Torranto
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "getTimeZone.php?");

        map.put("country", country.replace(" ", "%20"));
        map.put("state", state.replace(" ", "%20"));
        new AsynchHttpRequest(ct, Constant.REQUESTS.getZone, Constant.POST_TYPE.GET, map);
    }

    public void getReferCode(String user_id) {
        //   http://admindoctorpocket.in/web_serv/get_refferalCode.php?user_id=96
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "get_refferalCode.php?");

        map.put("user_id", user_id.replace(" ", ""));

        new AsynchHttpRequest(ct, Constant.REQUESTS.get_refferalCode, Constant.POST_TYPE.POST, map);

    }


    public void sendNotification(String message, String sender_id, String reciever_id, String user_type, String is_live) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "notification.php?");

        map.put("reciever_id", reciever_id.replace(" ", ""));
        map.put("sender_id", sender_id.replace(" ", ""));
        map.put("user_type", user_type.replace(" ", ""));
        map.put("is_live", is_live.replace(" ", ""));
        map.put("message", message);
        map.put("show", "");
        new AsynchHttpRequest(ct, Constant.REQUESTS.cancelChat, Constant.POST_TYPE.POST, map);

    }


    public void checkCancelStatus(String bookingID, String type, String user_id) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "cancel_chat.php?");
        map.put("access_token", access_token);
        map.put("user_type", type);
        map.put("action", "get");
        map.put("booking_id", bookingID);
        map.put("doctor_id", user_id);

        new AsynchHttpRequest(ct, Constant.REQUESTS.checkCancelStatus, Constant.POST_TYPE.POST, map);

    }

    public void checkCancelStatus(Fragment ft, String bookingID, String type, String user_id) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "cancel_chat.php?");
        map.put("access_token", access_token);
        map.put("user_type", type);
        map.put("action", "get");
        map.put("booking_id", bookingID);
        map.put("doctor_id", user_id);

        new AsynchHttpRequest(ft, Constant.REQUESTS.checkCancelStatus, Constant.POST_TYPE.POST, map);

    }


    public void uploadChatImage(String imagePath, String chatId) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.CHAT_UPLOAD_URL + "uploadsFiles.php?");

        map.put("type", "image");

        map.put("filename", imagePath);
        map.put("chatId", chatId);

        new AsynchHttpRequest(ct, Constant.REQUESTS.uploadChatImage, Constant.POST_TYPE.POST_WITH_IMAGE, map);

    }


    public void uploadChatVideo(String imagePath, String chatId) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.CHAT_UPLOAD_URL + "uploadsFiles.php?");
        map.put("type", "video");
        map.put("filename", imagePath);
        map.put("chatId", chatId);

        new AsynchHttpRequest(ct, Constant.REQUESTS.uploadChatVideo, Constant.POST_TYPE.POST_WITH_IMAGE, map);

    }

    public void uploadChatAudio(String imagePath, String chatId) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.CHAT_UPLOAD_URL + "uploadsFiles.php?");
        map.put("type", "audio");
        map.put("filename", imagePath);
        map.put("chatId", chatId);

        new AsynchHttpRequest(ct, Constant.REQUESTS.uploadChatAudio, Constant.POST_TYPE.POST_WITH_IMAGE, map);

    }

    public void getDoctorProfile(String user_id) {

        //    String urlStr = "getDoctorProfile.php?access_token=testermanishrahul234142test&user_id=" + drBean.dr_id;
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "getDoctorProfile.php?");
        map.put("access_token", access_token);

        map.put("user_id", user_id);

        new AsynchHttpRequest(ct, Constant.REQUESTS.getDoctorProfile, Constant.POST_TYPE.GET, map);

    }

    public void getZoneList() {

        //    String urlStr = "getDoctorProfile.php?access_token=testermanishrahul234142test&user_id=" + drBean.dr_id;
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "zoneList.php?");
        map.put("access_token", access_token);


        new AsynchHttpRequest(ct, Constant.REQUESTS.getZone, Constant.POST_TYPE.GET, map);

    }


    public void getMorningSLots(String patient_zone, String doctor_zone, String appointment_date
            , String appointment_start, String appointment_end) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "timeSlot.php?");
        map.put("access_token", access_token);
        map.put("patient_zone", patient_zone);

        map.put("doctor_zone", doctor_zone);
        map.put("appointment_date", appointment_date);
        map.put("appointment_start", appointment_start);
        map.put("appointment_end", appointment_end);

        new AsynchHttpRequest(ct, Constant.REQUESTS.GET_MORNING_SLOT, Constant.POST_TYPE.POST, map);

    }


    public void getEveningSlots(String patient_zone, String doctor_zone, String appointment_date
            , String appointment_start, String appointment_end) {

        //  accept_reject.php?doctor_id=%@&booking_id=%@&status=accept
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", MyConstants.BASE_URL + "timeSlot.php?");
        map.put("access_token", access_token);
        map.put("patient_zone", patient_zone);

        map.put("doctor_zone", doctor_zone);
        map.put("appointment_date", appointment_date);
        map.put("appointment_start", appointment_start);
        map.put("appointment_end", appointment_end);

        new AsynchHttpRequest(ct, Constant.REQUESTS.GET_EVENING_SLOT, Constant.POST_TYPE.POST, map);

    }

}

//

