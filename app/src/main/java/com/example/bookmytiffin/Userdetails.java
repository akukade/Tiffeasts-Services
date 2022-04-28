package com.example.bookmytiffin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.protobuf.Api;
import com.shakebugs.shake.Shake;

import java.util.Timer;
import java.util.regex.Pattern;

import soup.neumorphism.NeumorphButton;

import static com.example.bookmytiffin.Splash.curruser;

public class Userdetails extends AppCompatActivity {

    EditText firstname ,email, autodetect;
    Button saveBtn;
    TextView privacyterms;

    private final int REQUEST_CHECK_CODE = 8989;


    RadioGroup radioGroup;

    LottieAnimationView lottieAnimationView2;
    RelativeLayout linearLayout;
    TextView textView1,textView2;
    Button button;
    TextView termstext;

    String userID,mobileno;

    DatabaseReference myRef;
    FirebaseAuth firebaseAuth;
    Userinfo newuser;

     private LocationSettingsRequest.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shake.start(getApplication());
        setContentView(R.layout.activity_userdetails);


        autodetect = findViewById(R.id.autodetect1);


        autodetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocationRequest request =  LocationRequest.create();
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                request.setInterval(5000);
                request.setFastestInterval(1000);

                builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(request);


                Task<LocationSettingsResponse> result =
                        LocationServices.getSettingsClient(Userdetails.this).checkLocationSettings(builder.build());

                result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            task.getResult(ApiException.class);
                        } catch (ApiException e) {
                            switch ( e.getStatusCode())
                            {
                                case LocationSettingsStatusCodes
                                        .RESOLUTION_REQUIRED:

                                    try {
                                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                        resolvableApiException.startResolutionForResult(Userdetails.this, REQUEST_CHECK_CODE );
                                    } catch (IntentSender.SendIntentException sendIntentException) {
                                        sendIntentException.printStackTrace();
                                    }catch (ClassCastException ex){

                                    }break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                {
                                    break;
                                }

                            }
                        }

                    }
                });

            }
        });

        privacyterms = findViewById(R.id.privacyterms);

        String termstext = "By continuing, I confirm that I have read and agree to the Terms and Conditions and Privacy Policy";

        SpannableString ss = new SpannableString(termstext);

        ForegroundColorSpan fcblue = new ForegroundColorSpan(Color.BLUE);
        ForegroundColorSpan fcblue1 = new ForegroundColorSpan(Color.BLUE);

        ss.setSpan(fcblue,58,79, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(fcblue1,83,98, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //ss.setSpan(fcblue,83,98, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacyterms.setText(ss);


        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                //Toast.makeText(Userdetails.this, "Hello", Toast.LENGTH_SHORT).show();
                //Intent i = new Intent(Userdetails.this, Privacy.class);
                //startActivity(i);
                gotoUrl("https://www.tiffeasts.com/privacy");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
            }
        };

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                //Intent i = new Intent(Userdetails.this, Terms.class);
                //startActivity(i);

                gotoUrl("https://www.tiffeasts.com/terms-of-services");

                //Toast.makeText(Userdetails.this, "Hello", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
            }
        };

        ss.setSpan(clickableSpan1, 58, 79, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, 83, 98, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyterms.setText(ss);
        privacyterms.setMovementMethod(LinkMovementMethod.getInstance());



        Intent i = getIntent();
        mobileno = i.getStringExtra("number");

        privacyterms.setText(ss);

        firstname = findViewById(R.id.firstName);
        email = findViewById(R.id.emailAddress);
        saveBtn = findViewById(R.id.saveBtn);
        lottieAnimationView2 = findViewById(R.id.success);
        linearLayout = findViewById(R.id.innerLin);
        textView1 = findViewById(R.id.textView3);
        textView2 = findViewById(R.id.detailtext);
        radioGroup = findViewById(R.id.radioterms);

        lottieAnimationView2.setVisibility(View.GONE);
        saveBtn.setVisibility(View.INVISIBLE);


        firebaseAuth = FirebaseAuth.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();

        myRef = FirebaseDatabase.getInstance().getReference("users");


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                saveBtn.setVisibility(View.VISIBLE);
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!firstname.getText().toString().isEmpty() && !email.getText().toString().isEmpty()) {
                    //retrieve details from the edit text fields

                    String name= firstname.getText().toString();

                    String no = mobileno;

                    String useremail = email.getText().toString();

                    if(!useremail.contains("@") || !useremail.contains(".") || useremail.length()<5)
                    {
                        Toast.makeText(getApplicationContext(),"Please enter valid Email-Id",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    linearLayout.setVisibility(View.GONE);
                    textView1.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);

                    firstname.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    saveBtn.setVisibility(View.GONE);

                    lottieAnimationView2.setVisibility(View.VISIBLE);
                    lottieAnimationView2.playAnimation();

                    newuser = new Userinfo(name,useremail,no);
                    myRef.child(userID).setValue(newuser);
                    myRef.child(userID).child("accepted").setValue(1);
                    Splash.curruser = newuser;

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Userdetails.this, instanceIdResult -> {
                        String newToken = instanceIdResult.getToken();
                        myRef.child(userID).child("token").setValue(newToken);
                    });

                    SharedPreferences userdatastore = getSharedPreferences("userdatastore", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = userdatastore.edit();
                    editor.putString("name",newuser.getName());
                    editor.putString("email",newuser.getEmail());
                    editor.putString("mobileno",newuser.getMobileno());
                    editor.putInt("rating_sum",newuser.getRating_sum());
                    editor.putInt("rating_count",newuser.getRating_count());
                    editor.putInt("verified",newuser.getVerified());
                    editor.apply();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(Userdetails.this, Addressbook.class);
                            i.putExtra("origin", "details");
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                    },2000);
                }
                else {
                    Toast.makeText(Userdetails.this, "Please complete all the fields", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }


}