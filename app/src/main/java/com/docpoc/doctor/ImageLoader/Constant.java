package com.docpoc.doctor.ImageLoader;

public class Constant {

    public static final String AD_IMAGE_PREFIX = "http://fyndine.com/public/ads/";
    public static final String TREND_IMAGE_PREFIX = "http://fyndine.com/public/trending/";
    public static final String REST_IMAGE_PREFIX = "http://fyndine.com/public/images/restaurants/";
    public static final String OFFER_IMAGE_PRIFIX = "http://fyndine.com/public/images/offers/";


    public static final String SOAP_ACTION = "SOAP_ACTION", METHOD_NAME = "METHOD_NAME";
//	public static final int pink = Color.rgb(179, 62, 93);

    public static final String IMAGE_PREFIX = "";

    public static final String PLAY_STORE_LINK = "Click Here to download and install " + /*add the application name here*/ " android Application "
            + "https://play.google.com/store/apps/details?id="; //// add the package name at the end
    public static final String[] NO_RECORDS = new String[]{"No Recent Chat Found"};


    public static final String NO_INTERNET = "Internet not available",
            GCM_ID = "gcm_id";

    public static final String KEY_USERNAME = "username", KEY_EMAIL = "email", KEY_DEVICEID = "deviceid",
            PREFRENCE = "f2f";


    public enum POST_TYPE {
        GET, POST, POST_WITH_IMAGE;
    }


    public enum TABS {
        DR_LIST, HISTORY,  PROFILE, MESSAGES, ;

    }    public enum REQUESTS {
        getCountry,getZone,getDoctorProfile,updateDevice,
        updateBadge,get_refferalCode,getDiscounts,updateRefer,
        chatHistory,cancelChat,setOnOffStatus,getOnOffStatus,updateDoctorProfile,
        updatePatientProfile,checkCancelStatus,createBooking,GET_MORNING_SLOT, GET_EVENING_SLOT,uploadChatImage,uploadChatVideo, uploadChatAudio, checkTimeOver, acceptReject}

    /**
     * This is for PUSH Notification
     **/

    public static final String SENDER_ID = "";
    public static final String DISPLAY_ACTION = "com.empiere.fyndine";

    public static final int TWITTER_LOGIN_REQUEST_CODE = 1;

}