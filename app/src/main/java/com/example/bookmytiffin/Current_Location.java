package com.example.bookmytiffin;

import android.app.Application;

public class Current_Location extends Application {
    private double curr_lat = 18.5073806,curr_long = 73.7871499;
    private String curr_address="Kothrud, Pune";
    private String type="address1";

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public double getCurr_lat() {
        return curr_lat;
    }

    public void setCurr_lat(double curr_lat) {
        this.curr_lat = curr_lat;
    }

    public double getCurr_long() {
        return curr_long;
    }

    public void setCurr_long(double curr_long) {
        this.curr_long = curr_long;
    }

    public String getCurr_address() {
        return curr_address;
    }

    public void setCurr_address(String curr_address) {
        this.curr_address = curr_address;
    }
}
