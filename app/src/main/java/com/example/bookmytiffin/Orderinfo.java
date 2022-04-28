package com.example.bookmytiffin;

public class Orderinfo {
    private float amount;
    private String password,orderstatus;
    private float orderrating;

    Orderinfo()
    {

    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderrating(float orderrating)
    {
        this.orderrating = orderrating;
    }

    public float getAmount() {
        return amount;
    }

    public String getPassword() { return password; }

    public float getOrderrating() {
        return orderrating;
    }

    public Orderinfo(float amount, float orderrating, String password,String orderstatus) {
        this.amount = amount;
        this.orderrating = orderrating;
        this.password = password;
        this.orderstatus = orderstatus;
    }
}
