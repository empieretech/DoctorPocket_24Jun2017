package com.docpoc.doctor.classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Sagar Sojitra on 10/27/2016.
 */

public class Country implements Serializable {
    public ArrayList<State> stateArray = new ArrayList<>();
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSortname() {
        return sortname;
    }

    public void setSortname(String sortname) {
        this.sortname = sortname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String sortname;
    public String name;
}
