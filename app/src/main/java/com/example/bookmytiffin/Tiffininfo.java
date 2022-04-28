package com.example.bookmytiffin;

public class Tiffininfo {
    private String ownerid,name,cuisine,openingtime,closingtime,orderprior,address,parentkey,homeimage;
    private float rating, distance,delivery,charge;
    private boolean takeaway,veg,nonveg;
    private int costfor2;
    double latitude,longitude;

    public Tiffininfo(String ownerid, String name, int costfor2, String cuisine, String openingtime, String closingtime, String orderprior,
                      String address, boolean veg, boolean nonveg, float rating, float delivery, float charge, boolean takeaway) {
        this.ownerid = ownerid;
        this.name = name;
        this.costfor2 = costfor2;
        this.cuisine = cuisine;
        this.openingtime = openingtime;
        this.closingtime = closingtime;
        this.orderprior = orderprior;
        this.address = address;
        this.veg = veg;
        this.nonveg = nonveg;
        this.rating = rating;
        this.delivery = delivery;
        this.charge = charge;
        this.takeaway = takeaway;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public void setOpeningtime(String openingtime) {
        this.openingtime = openingtime;
    }

    public void setClosingtime(String closingtime) {
        this.closingtime = closingtime;
    }

    public void setOrderprior(String orderprior) {
        this.orderprior = orderprior;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setDelivery(float delivery) {
        this.delivery = delivery;
    }


    public void setCharge(float charge) {
        this.charge = charge;
    }

    public void setTakeaway(boolean takeaway) {
        this.takeaway = takeaway;
    }

    public void setVeg(boolean veg) {
        this.veg = veg;
    }

    public void setNonveg(boolean nonveg) {
        this.nonveg = nonveg;
    }

    public void setCostfor2(int costfor2) {
        this.costfor2 = costfor2;
    }

    public void setHomeimage(String homeimage) {
        this.homeimage = homeimage;
    }

    public String getHomeimage() {
        return homeimage;
    }

    public int getCostfor2() { return costfor2; }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getCharge() {
        return charge;
    }

    public boolean isVeg() {
        return veg;
    }

    public boolean isNonveg() {
        return nonveg;
    }

    public Tiffininfo()
    {
    }

    public String getOwnerid() {
        return ownerid;
    }


    public String getName() {
        return name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getOpeningtime() {
        return openingtime;
    }

    public String getClosingtime() {
        return closingtime;
    }

    public String getOrderprior() {
        return orderprior;
    }

    public String getAddress() {
        return address;
    }

    public float getRating() {
        return rating;
    }

    public float getDelivery(){
        return delivery;
    }

    public boolean isTakeaway() {
        return takeaway;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getParentkey() { return parentkey; }

    public void setParentkey(String parentkey) { this.parentkey = parentkey; }

}
