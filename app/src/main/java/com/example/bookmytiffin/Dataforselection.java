package com.example.bookmytiffin;

public class Dataforselection {
    private String item,price;
    private int counter;

    public Dataforselection()
    {

    }

    public Dataforselection(String item, String price, int counter) {
        this.item = item;
        this.price = price;
        this.counter = counter;
    }

    public int inccount()
    {
        counter++;
        return counter;
    }

    public int deccount()
    {
        counter--;
        return counter;
    }

    public String getItem() {
        return item;
    }

    public String getPrice() {
        return price;
    }

    public int getCounter() {
        return counter;
    }

}
