package com.docpoc.doctor.classes;

import java.io.Serializable;

/**
 * Created by Sagar Sojitra on 10/27/2016.
 */

public class State implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String

            id,
            name,
            country_id;
}
