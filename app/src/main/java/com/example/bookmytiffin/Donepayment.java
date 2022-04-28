package com.example.bookmytiffin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Donepayment extends AppCompatActivity {

    private long backPressedTime;

    LottieAnimationView lottieAnimationView;
    TextView orderidtv, vendornametv, vendornotv, amountpaidtv;
    FirebaseAuth firebaseAuth;
    Button homepage;
    String amount,orderid,vendorname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donepayment);

        homepage = findViewById(R.id.nexthomepage);

        Intent intent = getIntent();
        amount = intent.getStringExtra("amount");
        orderid = intent.getStringExtra("orderid");
        vendorname = intent.getStringExtra("vendorname");

        orderidtv = findViewById(R.id.orderid);
        vendornametv = findViewById(R.id.vendorname);
        vendornotv = findViewById(R.id.vendornumber);
        amountpaidtv = findViewById(R.id.amountpaid);

        orderidtv.setText("Order ID : \n " + orderid);
        vendornametv.setText(vendorname);
        amountpaidtv.setText("Amount : " +"\u20B9"+ amount);


        //Vendor Contact number
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(Uploadtiffin.currtif.getOwnerid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Userinfo vendorinfo = dataSnapshot.getValue(Userinfo.class);
                if(vendorinfo.getMobileno() != null)
                {
                    vendornotv.setText("Vendor no : " + vendorinfo.getMobileno());
                }
                else
                    vendornotv.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        lottieAnimationView = findViewById(R.id.successpayment);
        lottieAnimationView.playAnimation();

        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Donepayment.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("fromactivity","payment");
                startActivity(i);
                finish();
            }
        });

    }
    public void onBackPressed() {

        Intent setIntent = new Intent(Donepayment.this, MainActivity.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setIntent.putExtra("fromactivity","payment");
        startActivity(setIntent);
        finish();
    }


}