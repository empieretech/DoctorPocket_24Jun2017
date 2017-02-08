package com.docpoc.doctor.classes;

/**
 * Created by Sagar Sojitra on 10-19-16.
 */
public class Discount {
    public String
            id,
            refer_by,
            refer_discount,
            signup_discount,
            signup_id,
            refer_used,
            sign_up_used;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefer_by() {
        return refer_by;
    }

    public void setRefer_by(String refer_by) {
        this.refer_by = refer_by;
    }

    public String getRefer_discount() {
        return refer_discount;
    }

    public void setRefer_discount(String refer_discount) {
        this.refer_discount = refer_discount;
    }

    public String getSignup_discount() {
        return signup_discount;
    }

    public void setSignup_discount(String signup_discount) {
        this.signup_discount = signup_discount;
    }

    public String getSignup_id() {
        return signup_id;
    }

    public void setSignup_id(String signup_id) {
        this.signup_id = signup_id;
    }

    public String getRefer_used() {
        return refer_used;
    }

    public void setRefer_used(String refer_used) {
        this.refer_used = refer_used;
    }

    public String getSign_up_used() {
        return sign_up_used;
    }

    public void setSign_up_used(String sign_up_used) {
        this.sign_up_used = sign_up_used;
    }
}
