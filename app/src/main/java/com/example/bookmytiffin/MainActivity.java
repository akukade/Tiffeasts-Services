package com.example.bookmytiffin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.shakebugs.shake.Shake;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity  {
    private long backPressedTime;
    private FirebaseAnalytics firebaseAnalytics;


    //private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String fromactivity = intent.getStringExtra("fromactivity");

        if(fromactivity == null){
            fromactivity = "";
        }

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        Shake.getReportConfiguration().setInvokeShakeOnShakeDeviceEvent(true);
        Shake.start(getApplication());
        setContentView(R.layout.activity_main);

        checkConnection();

        //FrameLayout mMainFrame = (FrameLayout) findViewById(R.id.fragment_container);
        BottomNavigationView mMainNav = (BottomNavigationView) findViewById(R.id.nav_view);

        if(Splash.curruser.getVerified() == 0)
        {
            mMainNav.getMenu().removeItem(R.id.nav_message);
            mMainNav.getMenu().removeItem(R.id.nav_tiffin);
        }

        if(fromactivity.equals("payment")){
            HomeFragment ofrag = new HomeFragment();
            Bundle oarg = new Bundle();
            oarg.putString("selected", "myorder");
            ofrag.setArguments(oarg);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ofrag).commit();
        }else{
            HomeFragment mainhfrag = new HomeFragment();
            Bundle mainharg = new Bundle();
            mainharg.putString("selected", "home");
            mainhfrag.setArguments(mainharg);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainhfrag).commit();
        }

        mMainNav.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.nav_home:
                    HomeFragment hfrag = new HomeFragment();
                    Bundle harg = new Bundle();
                    harg.putString("selected", "home");
                    hfrag.setArguments(harg);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, hfrag).commit();
                    break;

                case R.id.nav_message:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new MessageFragment()).commit();
                    break;

                case R.id.nav_orders:
                    HomeFragment ofrag = new HomeFragment();
                    Bundle oarg = new Bundle();
                    oarg.putString("selected", "myorder");
                    ofrag.setArguments(oarg);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ofrag).commit();
                    break;

                case R.id.nav_tiffin:
                    HomeFragment tfrag = new HomeFragment();
                    Bundle targ = new Bundle();
                    targ.putString("selected", "mytiffin");
                    tfrag.setArguments(targ);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tfrag).commit();
                    break;

                case R.id.nav_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    break;
            }
            return true;
        });



    }


    @Override
    public void onBackPressed() {


        if(backPressedTime + 2000 > System.currentTimeMillis()){
            //backToast.cancel();
            super.onBackPressed();
            return;
        }
        else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            //backToast.show();
        }

        backPressedTime = System.currentTimeMillis();

    }

    public void checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        }

        if(activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()){
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.network_alert);
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.nonetwork);

            //lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();

            dialog.setCanceledOnTouchOutside(false);

            Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

            Button tryagain = dialog.findViewById(R.id.tryagain);

            tryagain.setOnClickListener(v -> recreate());
            dialog.show();
        }

    }


}