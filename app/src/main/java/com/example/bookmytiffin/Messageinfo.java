package com.example.bookmytiffin;

public class Messageinfo {
    private String orderid,customerid,msg,password,orderstatus,customerMobileNo;

    public Messageinfo()
    {

    }

    public Messageinfo(String customerid, String msg, String password, String orderstatus, String customerno) {
        this.customerid = customerid;
        this.msg = msg;
        this.password = password;
        this.orderstatus = orderstatus;
        this.customerMobileNo = customerno;
    }
    public String getOrderstatus() { return orderstatus; }

    public void setOrderstatus(String status) { this.orderstatus = status; }

    public String getCustomerMobileNo() { return customerMobileNo; }

    public void setOrderid(String orderid)
    {
        this.orderid = orderid;
    }

    public String getOrderid() {
        return orderid;
    }

    public String getCustomerid() {
        return customerid;
    }

    public String getMsg() {
        return msg;
    }

    public String getPassword() {
        return password;
    }
}
