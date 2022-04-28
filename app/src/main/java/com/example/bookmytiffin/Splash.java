package com.example.bookmytiffin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.Manifest;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.MapmyIndia;
import com.mmi.services.account.MapmyIndiaAccountManager;
import com.shakebugs.shake.Shake;

public class Splash extends AppCompatActivity{
    //private int SPLASH_TIME_OUT = 2000;
    //LocationManager locationManager;
    //private FusedLocationProviderClient fusedLocationClient;
    static Userinfo curruser;
    Address savedAddress;
    boolean userFetch =false, addressFetch =false;
    //int locationfetch=0;
    CountDownTimer timer;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shake.start(getApplication());
        setContentView(R.layout.activity_splash);

        MapmyIndiaAccountManager.getInstance().setRestAPIKey("k56nxrlg4h9uhav1fb3tnolemjzwqjon");
        MapmyIndiaAccountManager.getInstance().setMapSDKKey("n6aww7ddnxj47egx2jqojc3h7ed99ev8");
        MapmyIndiaAccountManager.getInstance().setAtlasClientId("33OkryzDZsKi9FIsOPofx2xDScxWXX0kWVFOHgbWnr9rmjE8GJyDedDVnXJME0kk_qAAXnXBUBFNFOvUdhtOrp6-887UnqucU09xkYb5S0ATFeVi3c9J5A==");
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret("lrFxI-iSEg9d2kgt-_58_LxogBsYGcTIzMjl5KPPejydgCo-8KVrg9hnPND8JOoBDex37UiuVBdObYIHJAk0lElmDYc7T_L6cYe43d_0TPH7cq96jBXkL8VeXsMTvD9E");
        MapmyIndiaAccountManager.getInstance().setAtlasGrantType("client_credentials");
        MapmyIndia.getInstance(this);

        firebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences userdatastore = getSharedPreferences("userdatastore", Context.MODE_PRIVATE);
        String username = userdatastore.getString("name","");
        String address = userdatastore.getString("address","");

        if(firebaseAuth.getCurrentUser() != null && username.equals("")) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    curruser = dataSnapshot.getValue(Userinfo.class);
                    if(curruser != null){
                        SharedPreferences.Editor editor = userdatastore.edit();
                        editor.putString("name",curruser.getName());
                        editor.putString("email",curruser.getEmail());
                        editor.putString("mobileno",curruser.getMobileno());
                        editor.putInt("rating_count",curruser.getRating_count());
                        editor.putInt("rating_sum",curruser.getRating_sum());
                        editor.putInt("verified",curruser.getVerified());
                        editor.apply();
                    }
                    userFetch = true;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else if(firebaseAuth.getCurrentUser() !=null){
            username = userdatastore.getString("name","");
            String email = userdatastore.getString("email","");
            String mobileno = userdatastore.getString("mobileno","");
            int rating_count = userdatastore.getInt("rating_count",0);
            int rating_sum = userdatastore.getInt("rating_sum",0);
            int verified = userdatastore.getInt("verified",0);
            curruser = new Userinfo(username,email,mobileno,rating_count,rating_sum,verified);
            userFetch =true;
        }

        if(firebaseAuth.getCurrentUser() != null && address.equals("")) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("address");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    savedAddress = dataSnapshot.child("address1").getValue(Address.class);
                    if(savedAddress != null){
                        SharedPreferences.Editor editor = userdatastore.edit();
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
                    }
                    addressFetch = true;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else if(firebaseAuth.getCurrentUser() != null)
        {
            address = userdatastore.getString("address","");
            String lati = userdatastore.getString("lat","");
            String longi = userdatastore.getString("long","");
            String type = userdatastore.getString("type","");
            savedAddress = new Address(address,Double.parseDouble(lati),Double.parseDouble(longi));

            Current_Location loc = (Current_Location) getApplication();
            loc.setCurr_lat(Double.parseDouble(lati));
            loc.setCurr_long(Double.parseDouble(longi));
            loc.setCurr_address(address);
            loc.setType(type);
            addressFetch = true;
        }


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                timer = new CountDownTimer(7000, 300) {

                    public void onTick(long millisUntilFinished) {
                        nextactivity();
                    }

                    public void onFinish() {
                        if (!userFetch || !addressFetch)
                            Toast.makeText(getApplicationContext(), "Unable to load your profile\nCheck your internet connection and open app again", Toast.LENGTH_LONG).show();
                        nextactivity();
                    }
                }.start();
            }
            else
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(Splash.this, SendOTP.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                },2000);
            }
        }

    }

    void nextactivity()
    {
        if(userFetch && addressFetch) {
            timer.cancel();
            if (curruser != null && savedAddress != null) {
                Intent i = new Intent(Splash.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
            else if(curruser == null){
                Intent i = new Intent(Splash.this, Userdetails.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
            else if(savedAddress == null){
                Intent i = new Intent(Splash.this, Addressbook.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("origin", "splash");
                startActivity(i);
                finish();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == 101 || requestCode == 102) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    recreate();
                }
            }
            else {
                boolean showRationale1 = shouldShowRequestPermissionRationale(permissions[0]);
                boolean showRationale2 = shouldShowRequestPermissionRationale(permissions[1]);
                if (!showRationale1 || !showRationale2) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Disabled")
                            .setMessage("We need your location to show food items around you. You have permanently disabled permissions for the app.\nEnable permissions from app setting. If permission is not granted the app will be closed.")
                            .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 111);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    System.exit(0);
                                }
                            }).create().show();

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Required")
                            .setMessage("We need your location to show food items around you. If permission is not granted the app will be closed.\nAre you sure you want to deny this permission?")
                            .setNegativeButton("RE-TRY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(Splash.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION}, 102);
                                }
                            })
                            .setPositiveButton("I'M SURE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    System.exit(0);
                                }
                            }).create().show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                recreate();
            }else{
                Toast.makeText(Splash.this,"Permission not granted. Hence closing app",Toast.LENGTH_LONG).show();
                System.exit(0);
            }
        }
    }

    /*
    @SuppressLint("MissingPermission")
    void getlastlocation()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            try {
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                Current_Location loc = (Current_Location) getApplication();
                                loc.setCurr_lat(location.getLatitude());
                                loc.setCurr_long(location.getLongitude());
                                loc.setCurr_address(addresses.get(0).getAddressLine(0));
                                locationfetch=1;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            getLocation();
                        }

                    }
                });

    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 5, (LocationListener)this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Current_Location loc = (Current_Location) getApplication();
            loc.setCurr_lat(location.getLatitude());
            loc.setCurr_long(location.getLongitude());
            loc.setCurr_address(addresses.get(0).getAddressLine(0));

            locationManager.removeUpdates((LocationListener)this);
            locationfetch=1;


        }
        catch (Exception e) {
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    */
}