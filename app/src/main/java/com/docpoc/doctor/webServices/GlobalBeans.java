package com.docpoc.doctor.webServices;

import java.io.Serializable;

/**
 * Created by Badruduja on 3/9/16.
 */
public class GlobalBeans implements Serializable {

    public static class LoginResultBean {
        public int status;            //RESPONSE_STATUS_CODES
        public String username;        //REPEATED_USERNAME
        public String api_key;        //API_KEY_YOU_NEED_THIS_FOR_SESSION
        public String customer_id;    //CUSTOMER_ID
        public String session_name;    //SESSION_COOKIE_NAME
        public String session_key;    //SESSION_KEY
        public String message;        //TEXT_DESCRIBING_THE_STATUS
        public String type;
        public String device;
        public String deviceId;
        public String function;
        public String password;


    }

    public static class AllPlansBean {

        public String id;
        public String name;
        public String amount;
        public String no_of_doughts;
        public String createdAt;
        public String desc;
        public String status;
    }

    public static class BookingHisrorySearchDocBean {

        public String id;
        public String firstName;
        public String lastName;
        public String imageURL;
        public String speciality;
        public String morning_appointment_start;
        public String morning_appointment_end;
        public String eve_appointment_start;
        public String eve_appointment_end;
        public String fees;
        public String address;
        public String about;
        public String phoneNumber;
        public String doctor_id;
        public String message;

    }

    public static class DrBookingHistoryBean {

        public String dr_id;
        public String pt_id;
        public String row_id;
        public String fName;
        public String lastName;
        public String imageURL;
        public String booking_date;
        public String booking_time;
        public String amount_status;
        public String amount;
        public String question;
        public String status;
    }

    public static class DoctorBean implements Serializable {

        public String dr_id;
        public String pt_id;
        public String row_id;
        public String fName, email;
        public String lastName;
        public String imageURL;
        public String morning_appointment_start;
        public String morning_appointment_end;
        public String eve_appointment_start;
        public String eve_appointment_end;
        public String fees;
        public String address;
        public String about, current_zone_date_time;
        public String phoneNumber;
        public String speciality;
        public String booking_date;
        public String booking_time;
        public String amount_status;
        public String booking_id;
        public String status, timeZone;
        public String question;
    }

    public static class MessageBean {

        public String dr_id = "";
        public String pt_id = "";
        public String fName;
        public String image_url;
        public String booking_id;
        public String booking_date_time;

    }

}
