package com.example.bookmytiffin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

import static com.example.bookmytiffin.Splash.curruser;

public class VerifyOTP extends AppCompatActivity {

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private String verificationId;
    private String mobile_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        mobile_no = getIntent().getStringExtra("mobile");
        TextView textMobile = findViewById(R.id.textMobile);

        textMobile.setText(String.format(
                "+91-%s",mobile_no)
        );

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        setupOTPInputs();

        final ProgressBar progressBar = findViewById(R.id.progessBar);
        final Button buttonVerify = findViewById(R.id.buttonVerify);

        verificationId = getIntent().getStringExtra("verificationId");

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(inputCode1.getText().toString().trim().isEmpty()
                        || inputCode2.getText().toString().trim().isEmpty()
                        || inputCode3.getText().toString().trim().isEmpty()
                        || inputCode4.getText().toString().trim().isEmpty()
                        || inputCode5.getText().toString().trim().isEmpty()
                        || inputCode6.getText().toString().trim().isEmpty()) {

                    Toast.makeText(VerifyOTP.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                String code = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString() ;

                if(verificationId != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    buttonVerify.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );

                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    buttonVerify.setVisibility(View.VISIBLE);
                                    if(task.isSuccessful()) {

                                        Toast.makeText(VerifyOTP.this,"Authentication Successful. Logging you in",Toast.LENGTH_LONG).show();
                                        String userid = FirebaseAuth.getInstance().getUid();
                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userid);

                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.getValue() != null)
                                                {
                                                    curruser = dataSnapshot.getValue(Userinfo.class);
                                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(VerifyOTP.this, instanceIdResult -> {
                                                        String newToken = instanceIdResult.getToken();
                                                        userRef.child("token").setValue(newToken);
                                                    });

                                                    SharedPreferences userdatastore = getSharedPreferences("userdatastore", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = userdatastore.edit();
                                                    editor.putString("name",curruser.getName());
                                                    editor.putString("email",curruser.getEmail());
                                                    editor.putString("mobileno",curruser.getMobileno());
                                                    editor.putInt("rating_sum",curruser.getRating_sum());
                                                    editor.putInt("rating_count",curruser.getRating_count());
                                                    editor.putInt("verified",curruser.getVerified());

                                                    Address savedAddress = dataSnapshot.child("address1").getValue(Address.class);
                                                    if(savedAddress != null){
                                                        editor.putString("address",savedAddress.address);
                                                        editor.putString("lat",savedAddress.latitude.toString());
                                                        editor.putString("long",savedAddress.longitude.toString());
                                                        editor.putString("type","address1");
                                                        editor.apply();
                                                        Current_Location loc = (Current_Location) getApplication();
                                                        loc.setCurr_lat(savedAddress.latitude);
                                                        loc.setCurr_long(savedAddress.longitude);
                                                        loc.setCurr_address(savedAddress.address);
                                                        loc.setType("address1");

                                                        Intent i = new Intent(VerifyOTP.this,MainActivity.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(i);
                                                        finish();
                                                    }else{
                                                        editor.apply();
                                                        Intent i = new Intent(VerifyOTP.this,Addressbook.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        i.putExtra("origin","verifyotp");
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                }
                                                else
                                                {
                                                    Intent i = new Intent(VerifyOTP.this, Userdetails.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    i.putExtra("number",mobile_no);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    } else {
                                        Toast.makeText(VerifyOTP.this, "Invalid OTP", Toast.LENGTH_SHORT).show();

                                    }

                                }

                            });
                }

            }
        });

        findViewById(R.id.textResentOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        VerifyOTP.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = newVerificationId;
                                Toast.makeText(VerifyOTP.this, "OTP Sent", Toast.LENGTH_SHORT).show();

                            }
                        }
                );
            }
        });

    }

    private void setupOTPInputs() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}