package com.example.bookmytiffin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shakebugs.shake.Shake;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Customlocation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ImageView pin;
    Button setloc;
    static Double newlat,newlong;
    static String newaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Shake.start(getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customlocation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pin = findViewById(R.id.imgLocationPinUp);
        setloc = findViewById(R.id.setloc);

        setloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                newaddress = addresses.get(0).getAddressLine(0);
                newlat = mMap.getCameraPosition().target.latitude;
                newlong = mMap.getCameraPosition().target.longitude;

                Current_Location loc = (Current_Location) getApplication();
                loc.setCurr_lat(mMap.getCameraPosition().target.latitude);
                loc.setCurr_long(mMap.getCameraPosition().target.longitude);
                loc.setCurr_address(addresses.get(0).getAddressLine(0));

                Intent i = new Intent(Customlocation.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Current_Location curr_loc = (Current_Location) getApplication();

        LatLng curr = new LatLng(curr_loc.getCurr_lat(), curr_loc.getCurr_long());

        mMap.addMarker(new MarkerOptions().position(curr).title("Current Location")).setDraggable(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mMap.clear();
                pin.setVisibility(View.VISIBLE);
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                pin.setVisibility(View.INVISIBLE);
                mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).title("Updated location"));
                System.out.println("new pos" + mMap.getCameraPosition().target.latitude + mMap.getCameraPosition().target.longitude);
            }

        });

    }
}