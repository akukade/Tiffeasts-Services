package com.tiffeasts.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import androidx.annotation.NonNull;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {

        super.onNewToken(s);
        System.out.println("Token :" +  s);

    }

}