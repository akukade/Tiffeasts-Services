package com.example.bookmytiffin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapmyindia.sdk.plugins.places.placepicker.PlacePicker;
import com.mapmyindia.sdk.plugins.places.placepicker.model.PlacePickerOptions;
import com.mmi.services.api.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Addressbook extends AppCompatActivity implements LocationListener {

    String origin;
    LocationManager locationManager;
    DatabaseReference addressreference;
    boolean edit = false, userlivelocationfetch = false, maplocationfetch = false, addressfetchfromfirebase = false;
    int editpos;
    FirebaseAuth firebaseAuth;
    String extraaddress;
    TextView addresstext1, addresstext2, addaddress;
    ImageView addaddressimage, more1, more2;
    int countaddress = 0;
    Button nextpage;
    EditText yourlocation;
    private final int REQUEST_CHECK_CODE = 8989;
    private String provider = LocationManager.NETWORK_PROVIDER;
    String liveaddress = "Kothrud, Pune", mapaddress;
    Double livelat=18.5073806, livelong=73.7871499, maplat, maplong;
    ArrayList<com.example.bookmytiffin.Address> address_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressbook);

        origin = getIntent().getStringExtra("origin");

        nextpage = findViewById(R.id.nextaddressbutton);

        if(origin.equals("profile")){
            nextpage.setVisibility(View.GONE);
        }

        more1 = findViewById(R.id.more);
        more2 = findViewById(R.id.more2);

        addresstext1 = findViewById(R.id.addresstext1);

        addresstext2 = findViewById(R.id.addresstext2);

        addaddress = findViewById(R.id.addaddress);

        addaddressimage = findViewById(R.id.addaddressimage);

        DisplaySavedAddress();

        if(origin.equals("profile")) {
            firebaseAuth = FirebaseAuth.getInstance();
            addressreference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("address");
            address_list = new ArrayList<>();

            addressreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        address_list.add(postSnapshot.getValue(com.example.bookmytiffin.Address.class));
                    }
                    countaddress = address_list.size();
                    addressfetchfromfirebase = true;
                    DisplaySavedAddress();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Addressbook.this, "Something went wrong, can't fetch your saved address. Please load page again", Toast.LENGTH_SHORT).show();
                }
            });
        }

        LocationRequest request =  LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);
        request.setFastestInterval(1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(Addressbook.this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    System.out.println("Response"+response.toString());
                    Toast.makeText(Addressbook.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
                    provider = LocationManager.GPS_PROVIDER;
                    getLocation();

                } catch (ApiException e) {
                    switch ( e.getStatusCode())
                    {
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Addressbook.this, REQUEST_CHECK_CODE );
                            }
                            catch (IntentSender.SendIntentException sendIntentException) {
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


        addaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(origin.equals("profile") && !addressfetchfromfirebase){
                    Toast.makeText(Addressbook.this,"Fetching your saved address. Please wait and try again",Toast.LENGTH_SHORT).show();
                    return;
                }
                bottomsheet();
            }
        });


        addaddressimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(origin.equals("profile") && !addressfetchfromfirebase){
                    Toast.makeText(Addressbook.this,"Fetching your saved address. Please wait and try again",Toast.LENGTH_SHORT).show();
                    return;
                }
                bottomsheet();
            }
        });


        more1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit = true;
                editpos = 0;
                bottomsheet();
            }
        });

        more2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit = true;
                editpos = 1;
                bottomsheet();
            }
        });


        nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Addressbook.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });

        addresstext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(address_list.size() < 1)
                    return;
                ChangeAddress(0);
            }
        });

        addresstext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(address_list.size() < 2)
                    return;
                ChangeAddress(1);
            }
        });
    }

    private void ChangeAddress(int pos){
        com.example.bookmytiffin.Address savedAddress = address_list.get(pos);
        Current_Location loc = (Current_Location) getApplication();
        loc.setCurr_lat(savedAddress.latitude);
        loc.setCurr_long(savedAddress.longitude);
        loc.setCurr_address(savedAddress.address);
        if(pos == 0){
            loc.setType("address1");
            addresstext1.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.bright_sky_color));
            addresstext2.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.grey));
        }
        else if(pos == 1){
            loc.setType("address2");
            addresstext1.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.grey));
            addresstext2.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.bright_sky_color));
        }
        SharedPreferences userdatastore = getSharedPreferences("userdatastore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userdatastore.edit();
        editor.putString("address",savedAddress.address);
        editor.putString("lat",savedAddress.latitude.toString());
        editor.putString("long",savedAddress.longitude.toString());
        if(pos == 0)
            editor.putString("type","address1");
        else if(pos == 1)
            editor.putString("type","address2");

        editor.apply();
    }

    private void DisplaySavedAddress(){

        if(countaddress == 0){
            more1.setVisibility(View.GONE);
            more2.setVisibility(View.GONE);
            addresstext1.setVisibility(View.GONE);
            addresstext2.setVisibility(View.GONE);
        }
        else if(countaddress == 1){
            more2.setVisibility(View.GONE);
            addresstext2.setVisibility(View.GONE);
            addresstext1.setVisibility(View.VISIBLE);
            more1.setVisibility(View.VISIBLE);
            addresstext1.setText(address_list.get(0).address);
        }
        else if(countaddress == 2)
        {
            addaddressimage.setVisibility(View.GONE);
            addaddress.setVisibility(View.GONE);

            addresstext1.setVisibility(View.VISIBLE);
            addresstext2.setVisibility(View.VISIBLE);
            more1.setVisibility(View.VISIBLE);
            more2.setVisibility(View.VISIBLE);
            addresstext1.setText(address_list.get(0).address);
            addresstext2.setText(address_list.get(1).address);
        }

        if(countaddress > 0) {
            nextpage.setEnabled(true);
            nextpage.setClickable(true);
        }
        else{
            nextpage.setClickable(false);
            nextpage.setEnabled(false);
        }

        Current_Location loc = (Current_Location) getApplication();

        if(loc.getType().equals("address1")){
            addresstext1.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.bright_sky_color));
            addresstext2.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.grey));
        }
        else if(loc.getType().equals("address2")){
            addresstext1.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.grey));
            addresstext2.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.bright_sky_color));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == REQUEST_CHECK_CODE) {

             if(resultCode == RESULT_OK)
             {
                 Toast.makeText(this, "GPS Enabled by user", Toast.LENGTH_SHORT).show();
                 provider = LocationManager.GPS_PROVIDER;
                 getLocation();
             }
             else if (resultCode == RESULT_CANCELED){
                 provider = LocationManager.NETWORK_PROVIDER;
                 Toast.makeText(this, "GPS Cancelled by user", Toast.LENGTH_SHORT).show();
             }
         }
         else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {

            Place place = PlacePicker.getPlace(data);
            if(place!= null || place.getFormattedAddress()!= null || place.getLat()!= null || place.getLng()!= null){
                mapaddress = place.getFormattedAddress();

                maplat = Double.parseDouble(place.getLat());

                maplong = Double.parseDouble(place.getLng());

                yourlocation.setText(mapaddress);
                maplocationfetch = true;
            }else {
                Toast.makeText(this, "Something went wrong, not able to fetch your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(provider, 0, 0, (LocationListener)this);
            if(provider.equals(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener)this);
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            liveaddress = addresses.get(0).getAddressLine(0);
            livelat = location.getLatitude();
            livelong = location.getLongitude();

            locationManager.removeUpdates((LocationListener)this);
            userlivelocationfetch=true;
            Toast.makeText(this,"User live location fetched",Toast.LENGTH_LONG).show();
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


    public void bottomsheet() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Addressbook.this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_address);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        yourlocation = bottomSheetDialog.findViewById(R.id.yourlocation1);

        Button saveaddress = bottomSheetDialog.findViewById(R.id.addaddressbutton);

        EditText exactaddress = bottomSheetDialog.findViewById(R.id.completeaddress1);

        EditText landmark = bottomSheetDialog.findViewById(R.id.landmark1);

        if(edit) {
            String [] tempaddress = address_list.get(editpos).address.split(":");
            yourlocation.setText(tempaddress[1]);
            exactaddress.setText(tempaddress[0]);
        }
        else {
            yourlocation.setText(liveaddress);
        }

        yourlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double templat,templong;
                if(edit) {
                    templat = address_list.get(editpos).latitude;
                    templong = address_list.get(editpos).longitude;
                }
                else {
                    templat = livelat;
                    templong = livelong;
                }

                Intent i = new PlacePicker.IntentBuilder()
                        .placeOptions(PlacePickerOptions.builder()
                                .statingCameraPosition(new CameraPosition.Builder()
                                        .target(new LatLng(templat, templong)).zoom(16).build())
                                .build()).build(Addressbook.this);
                startActivityForResult(i, 101);
            }
        });

        saveaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(exactaddress.getText())){
                    Toast.makeText(Addressbook.this, "Please fill the Wing, House Number and Building Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(exactaddress.getText().toString().contains(":")){
                    Toast.makeText(Addressbook.this, "Please don't use colon (:) in Wing,House NUmber field", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(landmark.getText().toString().contains(":")){
                    Toast.makeText(Addressbook.this, "Please don't use colon (:) in Landmark", Toast.LENGTH_SHORT).show();
                    return;
                }

                String fulladdress = exactaddress.getText().toString()+"; "+landmark.getText().toString()+ ": " + yourlocation.getText().toString();

                double newlat,newlong;
                if(maplocationfetch){
                    newlat = maplat;
                    newlong = maplong;
                }
                else if(edit){
                    newlat = address_list.get(editpos).latitude;
                    newlong = address_list.get(editpos).longitude;
                }
                else{
                    newlat = livelat;
                    newlong = livelong;
                }

                 com.example.bookmytiffin.Address newaddress = new com.example.bookmytiffin.Address(fulladdress, newlat, newlong);

                 if (edit) {
                     address_list.set(editpos, newaddress);
                 }
                 else{
                    countaddress++;
                    address_list.add(newaddress);
                 }

                DisplaySavedAddress();

                SharedPreferences userdatastore = getSharedPreferences("userdatastore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userdatastore.edit();
                editor.putString("address",fulladdress);
                editor.putString("lat",Double.toString(newlat));
                editor.putString("long",Double.toString(newlong));

                Current_Location loc = (Current_Location) getApplication();
                loc.setCurr_lat(newlat);
                loc.setCurr_long(newlong);
                loc.setCurr_address(fulladdress);

                firebaseAuth = FirebaseAuth.getInstance();
                addressreference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).child("address");

                if((edit && editpos==0) || countaddress == 1){
                    addressreference.child("address1").setValue(newaddress);
                    editor.putString("type","address1");
                    loc.setType("address1");
                    addresstext1.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.bright_sky_color));
                    addresstext2.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.grey));
                }
                else if((edit && editpos==1) ||countaddress == 2){
                    addressreference.child("address2").setValue(newaddress);
                    editor.putString("type","address2");
                    loc.setType("address2");
                    addresstext1.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.grey));
                    addresstext2.setBackgroundColor(ContextCompat.getColor(Addressbook.this, R.color.bright_sky_color));
                }

                editor.apply();

                maplocationfetch = false;
                edit = false;
                bottomSheetDialog.dismiss();
            }
        });

    }
}