package com.example.bookmytiffin;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import com.shakebugs.shake.Shake;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.example.bookmytiffin.Splash.curruser;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";

    FirebaseAuth fAuth;
    Timer timer;
    TextView verify;
    EditText  codeEnter;
    RadioGroup radiogrp;
    RadioButton terms;
    EditText phoneNumber;
    Button nextBtn;
    ProgressBar progressBar, verifyprogress;
    TextView state;
    String verificationId,mobile_no;
    PhoneAuthProvider.ForceResendingToken token;
    //LottieAnimationView lottieAnimationView;
    RelativeLayout relativeLayout;
    Boolean verificationInProgress = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shake.start(getApplication());
        setContentView(R.layout.activity_register);

        //lottieAnimationView = findViewById(R.id.wel);

        fAuth = FirebaseAuth.getInstance();

        verify = findViewById(R.id.verifytext);
        verifyprogress = findViewById(R.id.verifyprogressbar);
        terms = findViewById(R.id.termsandcond);
        phoneNumber = findViewById(R.id.phone);
        codeEnter = findViewById(R.id.codeEnter);
        progressBar = findViewById(R.id.progressBar);
        nextBtn = findViewById(R.id.nextBtn);
        state = findViewById(R.id.state);

        relativeLayout = findViewById(R.id.innerRel);


        //lottieAnimationView.setVisibility(View.GONE);
        //on clicking the next button, an OTP will be sent to the user's phone

        nextBtn.setOnClickListener(v -> {
            if(!verificationInProgress){
                if(!phoneNumber.getText().toString().isEmpty() && phoneNumber.getText().toString().length() == 10) {
                    String phoneNum = "+91" + phoneNumber.getText().toString();
                    Log.d(TAG, "onClick: Phone no: " + phoneNum);

                    progressBar.setVisibility(View.VISIBLE);
                    state.setText("Sending OTP");
                    state.setVisibility(View.VISIBLE);

                    requestOTP(phoneNum);

                }
                else {
                    phoneNumber.setError("Phone Number is not Valid");
                }
            } else {

                //extract otp entered by user
                String userOTP = codeEnter.getText().toString();
                if(userOTP.length() == 6) {

                    //create credentials for user
                    verifyprogress.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, userOTP);
                    verifyAuth(credential);

                } else {
                    codeEnter.setError("Valid OTP is required");
                    verifyprogress.setVisibility(View.INVISIBLE);
                    verify.setVisibility(View.INVISIBLE);
                }

            }

        });
    }



    private void verifyAuth(PhoneAuthCredential credential) {

        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    mobile_no = phoneNumber.getText().toString();
                    String userid = FirebaseAuth.getInstance().getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userid);



                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null)
                            {
                                curruser = dataSnapshot.getValue(Userinfo.class);
                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Register.this, instanceIdResult -> {
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
                                editor.apply();

                                Intent i = new Intent(Register.this,MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                Intent i = new Intent(Register.this, Userdetails.class);
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
                    verifyprogress.setVisibility(View.INVISIBLE);
                    verify.setVisibility(View.INVISIBLE);
                    Toast.makeText(Register.this, "Authentication failed./nPlease try again", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                //whenever OTP is sent, this method is called
                //s contains verification id
                //forceResendingToken is required when user does not receive OTP and requests server to resend the OTP

                verificationId = s;
                token = forceResendingToken;

                //when user receives otp, the user should be able to type it into the editText
                //so, hide the progressbar and make visible editText to type OTP

                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                codeEnter.setVisibility(View.VISIBLE);

                //change the name of button from "next" to "verify" and make it un-clickable
                nextBtn.setText("Verify");

                //nextBtn.setEnabled(false);

                verificationInProgress = true;

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                //this method is called when the OTP is not entered within the given time frame(in this case, 60sec)


            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //method called automatically when user is verified

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(Register.this, "Cannot verify account" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
