package com.example.bookmytiffin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class Faq extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Versions> versionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);


        recyclerView = findViewById(R.id.faqrecycler);

        initData();
        setRecyclerView();
    }

    private void setRecyclerView() {
        VersionAdapter versionAdapter = new VersionAdapter(versionsList);
        recyclerView.setAdapter(versionAdapter);
        recyclerView.setHasFixedSize(true);


    }

    private void initData() {

        versionsList = new ArrayList<>();
        versionsList.add(new Versions("How can I start selling on Tiffeasts ?", "1) Click on “Join Us” on the homepage and accept the conditions.\n\n2) We will verify your request and after successful verification, you can begin selling!"));
        versionsList.add(new Versions("How do I upload my Food?", "1) Once verified, In the bottom right corner, you will see a “+” floating button. Click on it.\n\n2) Select the category you want to upload your food into, that is Tiffin Service or Set Menu.\n\n3) Add all the required details that will be asked.\n\n4) Click on “Upload” and your tiffin will be visible to the customers for ordering."));
        versionsList.add(new Versions("Which category does my food fall under? Tiffin Service or Set Menu?","1) Tiffin Service includes chefs/ agencies/home cooks, which provide day to day meals of  breakfast, lunch and dinner services.\n\n2)  Set Menu Service includes those who are preparing specific dishes, cakes, bakery products, masalas, etc. Each vendor can be a part of either of these categories, or both."));
        versionsList.add(new Versions("How do I check the orders that I have received?", "1) You will have to check the “My Messages” tab\n\n2) Everytime someone orders food, you will see it here and also receive a text message with the order details"));
        versionsList.add(new Versions("Do I have to pay any fees to register on Tiffeasts?","No! Registering on Tiffeasts is absolutely free."));
        versionsList.add(new Versions("Does Tiffeasts provide delivery?","No! As of now, we are not providing delivery. Each vendor/home cook has made their own arrangements for delivery or pick-up of food. Please contact the food vendor you have placed an order to/ planning to place an order to, and enquire about their delivery system."));
        versionsList.add(new Versions("Which personal details of mine will be visible to other customers and/or vendors?", "Customers will only see the food item/tiffin that you wish to sell, your name and the average rating of your food.\n"));
        versionsList.add(new Versions("Can I be both, a vendor and a buyer from the same account, or will I have to register different accounts?", "You can be both from the same account. Once you register and login to the app, it is a one stop solution for all your needs, be it selling, buying or both. You can manage everything together in a single account."));
    }

}