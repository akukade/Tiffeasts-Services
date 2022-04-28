package com.example.bookmytiffin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Orderotp extends AppCompatActivity {

    LottieAnimationView orderdeliveredAnimation;

    Button check,accept,reject;
    TextView orderstatus;
    EditText otp;
    int pos;
    Messageinfo msginfo;
    FirebaseAuth firebaseAuth;
    String notifykey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderotp);

        Intent intent = getIntent();
        pos = intent.getIntExtra("position",-1);

        msginfo = MessageFragment.msg_list.get(pos);
        orderdeliveredAnimation = findViewById(R.id.lottedeliver);
        accept = findViewById(R.id.acceptorder);
        reject = findViewById(R.id.rejectorder);
        check = findViewById(R.id.checkotp);
        orderstatus = findViewById(R.id.orderstatus);
        otp = findViewById(R.id.otptext);

        firebaseAuth = FirebaseAuth.getInstance();

        if(msginfo.getOrderstatus().equals("none"))
        {
            otp.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
            orderstatus.setText("Accept/Reject Order");
        }
        else if(msginfo.getOrderstatus().equals("accepted"))
        {
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            orderstatus.setText("Order Accepted");
        }
        else if(msginfo.getOrderstatus().equals("rejected"))
        {
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            otp.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
            orderstatus.setText("Order Rejected");

        }
        else if(msginfo.getOrderstatus().equals("delivered")) {
            otp.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            orderstatus.setText("Order Delivered");
            orderdeliveredAnimation.setVisibility(View.VISIBLE);
            orderdeliveredAnimation.playAnimation();
            orderdeliveredAnimation.loop(true);
        }

        accept.setOnClickListener(view -> {
            MessageFragment.msg_list.get(pos).setOrderstatus("accepted");
            orderstatus.setText("Order Accepted");
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            otp.setVisibility(View.VISIBLE);
            check.setVisibility(View.VISIBLE);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages").child(firebaseAuth.getUid()).child(msginfo.getOrderid());
            ref.child("orderstatus").setValue("accepted");

            ref = FirebaseDatabase.getInstance().getReference("orders").child(msginfo.getCustomerid()).child(msginfo.getOrderid());
            ref.child("orderstatus").setValue("accepted");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Tiffininfo tif = snapshot.getValue(Tiffininfo.class);
                    //Orderinfo orderinfo = snapshot.getValue(Orderinfo.class);

                    DatabaseReference userref = FirebaseDatabase.getInstance().getReference("users").child(msginfo.getCustomerid()).child("token");
                    userref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String customerToken = snapshot.getValue(String.class);

                            JSONObject notification = new JSONObject();
                            JSONObject notificationBody = new JSONObject();
                            try
                            {
                                notificationBody.put("title", "Order Accepted");
                                notificationBody.put("message", "Your Order is accepted by " + tif.getName());

                                notification.put("to", customerToken);
                                notification.put("data", notificationBody);
                            }
                            catch (JSONException e) {
                                System.out.println(e.toString());
                            }
                            sendNotification(notification);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    DatabaseReference managementref = FirebaseDatabase.getInstance().getReference("management").child("token");
                    managementref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            JSONObject notification = new JSONObject();
                            JSONObject notificationBody = new JSONObject();
                            try
                            {
                                notificationBody.put("title", "Order Accepted by " + tif.getName());
                                String message = "Order ID : " + msginfo.getOrderid();
                                message += "\nVendor ID :" + tif.getOwnerid();
                                message += "\nCustomer No : " + msginfo.getCustomerMobileNo();
                                notificationBody.put("message", message);

                                notification.put("to", snapshot.getValue(String.class));
                                notification.put("data", notificationBody);
                            }
                            catch (JSONException e) {
                                System.out.println(e.toString());
                            }
                            sendNotification(notification);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        });

        reject.setOnClickListener(view -> {
            MessageFragment.msg_list.get(pos).setOrderstatus("rejected");
            orderstatus.setText("Order Rejected");
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            otp.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages").child(firebaseAuth.getUid()).child(msginfo.getOrderid());
            ref.child("orderstatus").setValue("rejected");

            ref = FirebaseDatabase.getInstance().getReference("orders").child(msginfo.getCustomerid()).child(msginfo.getOrderid());
            ref.child("orderstatus").setValue("rejected");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Tiffininfo tif = snapshot.getValue(Tiffininfo.class);
                    Orderinfo orderinfo = snapshot.getValue(Orderinfo.class);

                    DatabaseReference userref = FirebaseDatabase.getInstance().getReference("users").child(msginfo.getCustomerid()).child("token");
                    userref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String customerToken = snapshot.getValue(String.class);

                            JSONObject notification = new JSONObject();
                            JSONObject notificationBody = new JSONObject();
                            try
                            {
                                notificationBody.put("title", "Sorry, Your order has been rejected by " + tif.getName());
                                notificationBody.put("message", "Your order amount of " + orderinfo.getAmount() +" Rs will be refunded shortly" + "\nGo and order something new!!");

                                notification.put("to", customerToken);
                                notification.put("data", notificationBody);
                            }
                            catch (JSONException e) {
                                System.out.println(e.toString());
                            }
                            sendNotification(notification);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    DatabaseReference managementref = FirebaseDatabase.getInstance().getReference("management").child("token");
                    managementref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            JSONObject notification = new JSONObject();
                            JSONObject notificationBody = new JSONObject();
                            try
                            {
                                notificationBody.put("title", "Order Rejected by " + tif.getName());
                                String message = "Order ID : " + msginfo.getOrderid();
                                message += "\nVendor ID : " + tif.getOwnerid();
                                message += "\nCustomer No : " + msginfo.getCustomerMobileNo();
                                message += "\nAmount : " + orderinfo.getAmount();
                                notificationBody.put("message", message);

                                notification.put("to", snapshot.getValue(String.class));
                                notification.put("data", notificationBody);
                            }
                            catch (JSONException e) {
                                System.out.println(e.toString());
                            }
                            sendNotification(notification);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

        check.setOnClickListener(view -> {
            String enteredotp = otp.getText().toString();
            if(msginfo.getPassword().equals(enteredotp))
            {
                otp.setVisibility(View.GONE);
                check.setVisibility(View.GONE);
                orderstatus.setText("Order Delivered");
                MessageFragment.msg_list.get(pos).setOrderstatus("delivered");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages").child(firebaseAuth.getUid()).child(msginfo.getOrderid());
                ref.child("orderstatus").setValue("delivered");

                ref = FirebaseDatabase.getInstance().getReference("orders").child(msginfo.getCustomerid()).child(msginfo.getOrderid());
                ref.child("orderstatus").setValue("delivered");

                orderdeliveredAnimation.setVisibility(View.VISIBLE);
                orderdeliveredAnimation.loop(true);
            }
            else
            {
                Toast.makeText(Orderotp.this,"Incorrect Password/nPlease Try again",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void sendNotification(JSONObject notification) {

        DatabaseReference notifyref = FirebaseDatabase.getInstance().getReference("notify").child("key");
        if(notifykey == null) {
            notifyref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    notifykey = snapshot.getValue(String.class);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                            response -> System.out.println(response.toString()),
                            error -> Toast.makeText(Orderotp.this, "Request error", Toast.LENGTH_LONG).show()){
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put("Authorization", "key="+notifykey);
                            params.put("Content-Type", "application/json");
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
        {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                    response -> System.out.println(response.toString()),
                    error -> Toast.makeText(Orderotp.this, "Request error", Toast.LENGTH_LONG).show()){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "key="+notifykey);
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
    }
}