package com.example.bookmytiffin;

public class Userinfo {
    private String name,email,mobileno,token;
    private int rating_count,rating_sum,verified;

    Userinfo() { }

    Userinfo(String name, String email, String mobileno) {
        this.name = name;
        this.email = email;
        this.mobileno = mobileno;
        rating_count = 0;
        rating_sum = 0;
        verified = 0;
    }

    Userinfo(String name, String email, String mobileno, int rating_count, int rating_sum, int verified) {
        this.name = name;
        this.email = email;
        this.mobileno = mobileno;
        this.rating_count = rating_count;
        this.rating_sum = rating_sum;
        this.verified = verified;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getRating_count() {
        return rating_count;
    }

    public void setRating_count(int rating_count) {
        this.rating_count = rating_count;
    }

    public int getRating_sum() {
        return rating_sum;
    }

    public void setRating_sum(int rating_sum) {
        this.rating_sum = rating_sum;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileno() {
        return mobileno;
    }

}
