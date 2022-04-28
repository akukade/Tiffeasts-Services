package com.example.bookmytiffin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.ScooterOptions;
import com.here.sdk.routing.Waypoint;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapmyindia.sdk.plugins.places.placepicker.PlacePicker;
import com.mapmyindia.sdk.plugins.places.placepicker.model.PlacePickerOptions;
import com.mmi.services.api.Place;
import com.mmi.services.api.directions.DirectionsCriteria;
import com.mmi.services.api.distance.MapmyIndiaDistanceMatrix;
import com.mmi.services.api.distance.models.DistanceResponse;
import com.mmi.services.api.distance.models.DistanceResults;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;
import soup.neumorphism.NeumorphCardView;

public class Uploadtiffin extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DialogListener{

    MaterialEditText textname;
    EditText chargePerKm,textaddress;
    Button select_image;
    TextView addmenu;
    LocationManager locationManager;
    Button proceed,rate;
    CardView detailstab,menutab;
    ScrollView detailsscroll,menuscroll;
    FloatingTextButton deleteupload;
    TextView  textmenu;
    TextView  userdistance, chargetitle;
    LottieAnimationView lottiAnimationView;
    int pos,i , edittif =0;
    String name,cuisine,openingtime,closingtime,orderprior,address,selected;
    int costfor2;
    FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    static Tiffininfo currtif = new Tiffininfo();
    Double latitude,longitude;
    ProgressBar progressBar;
    RatingBar ratingBar;
    Userinfo tiffinownerinfo;
    static ArrayList<String> itemlist,pricelist = new ArrayList<>() ;
    ListView itempricelv;
    ArrayAdapter<String> itempriceadapter;
    //RelativeLayout tiffinlayout,menulayout
    RelativeLayout deliverylayout;
    String[] cuisineItemsList;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    float tracktimepicker;
    CheckBox delivery,takeaway,veg,nonveg;
    ViewPager display_image;
    TabLayout dots;
    Uri mImageUri;
    ArrayList<String>ImageUrlList;
    ArrayList<Uri>ImagesUri;
    ImageCustomAdapter imageCustomAdapter;
    float ownerrating;
    //AlertDialog addressdialog;
    boolean isDistanceSet = false;
    MaterialEditText textcostfor2, textcuisine, textopeningtime,textclosingtime, days,mins, hrs, range;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadtiffin);

        Intent intent = getIntent();
        pos = intent.getIntExtra("position",-1);
        selected = intent.getStringExtra("selected");

        if(selected==null)
            selected = "home";

        if(pos != -1 && selected.equals("mytiffin"))
            edittif = 1;



        select_image = findViewById(R.id.select_image);
        addmenu = findViewById(R.id.menu);
        lottiAnimationView = findViewById(R.id.knife);
        cuisineItemsList = getResources().getStringArray(R.array.shopping_item);
        checkedItems = new boolean[cuisineItemsList.length];
        deleteupload = findViewById(R.id.deleteupload);

        deliverylayout = findViewById(R.id.deliveryLayout);
        range  = findViewById(R.id.range);
        chargePerKm = findViewById(R.id.charge);

        detailstab = findViewById(R.id.detailstab);
        menutab = findViewById(R.id.menutab);
        detailsscroll = findViewById(R.id.detailsscroll);
        menuscroll = findViewById(R.id.menuscroll);

        textaddress = findViewById(R.id.address);
        textname = findViewById(R.id.vendorname);
        textcostfor2 = findViewById(R.id.costfor2);
        textcuisine = findViewById(R.id.cuisine);
        textmenu = findViewById(R.id.menu);
        veg = findViewById(R.id.veg);
        nonveg = findViewById(R.id.nonveg);
        proceed = findViewById(R.id.upload);
        ratingBar = findViewById(R.id.ratingbar);
        rate = findViewById(R.id.rate);
        //clear_items = findViewById(R.id.clear_items);
        //menulayout = findViewById(R.id.menulayout);
        //tiffinlayout = findViewById(R.id.tiffinlayout);
        textopeningtime = findViewById(R.id.openingtime);
        textclosingtime = findViewById(R.id.closingtime);
        days = findViewById(R.id.days);
        hrs = findViewById(R.id.hrs);
        mins = findViewById(R.id.mins);
        delivery = findViewById(R.id.delivery);
        takeaway = findViewById(R.id.takeaway);
        display_image = (ViewPager) findViewById(R.id.display_image);
        progressBar = findViewById(R.id.addprogress);
        dots = (TabLayout) findViewById(R.id.dots);
        itempricelv = (speciallistview) findViewById(R.id.itempricelv);
        userdistance = findViewById(R.id.userdistance);
        chargetitle = findViewById(R.id.chargetitle);

        ImageUrlList = new ArrayList<>();
        ImagesUri = new ArrayList<>();
        imageCustomAdapter = new ImageCustomAdapter(Uploadtiffin.this,ImageUrlList);
        display_image.setAdapter(imageCustomAdapter);
        dots.setupWithViewPager(display_image,true);

        itemlist = new ArrayList<>();
        pricelist = new ArrayList<>();
        itempriceadapter = new ItemPricecustomadapter(this,itemlist,pricelist);
        itempricelv.setAdapter(itempriceadapter);

        firebaseAuth = FirebaseAuth.getInstance();


        if(pos!=-1)                 // Tiffin Details Display to User
        {
            currtif = HomeFragment.tiffins_list.get(pos);
            textname.setText(currtif.getName());
            textcuisine.setText(currtif.getCuisine());
            textaddress.setText(currtif.getAddress());

            if(currtif.getCostfor2() != 0)
            {
                textcostfor2.setText(String.valueOf(currtif.getCostfor2()));
            }
            else {
                textcostfor2.setVisibility(View.GONE);
            }

            if (currtif.getDelivery() != 0) {       //Delivery
                delivery.setChecked(true);
                deliverylayout.setVisibility(View.VISIBLE);
                String rangemsg = Float.toString(currtif.getDelivery());
                if (edittif == 0) {
                    rangemsg += " km";
                }
                range.setText(rangemsg);
            }
            else if(edittif == 0)
                delivery.setVisibility(View.GONE);

            if (currtif.isTakeaway())          //Takeaway
                takeaway.setChecked(true);
            else if(edittif == 0)
                takeaway.setVisibility(View.GONE);

            if(currtif.isVeg())               //Veg
                veg.setChecked(true);
            else if(edittif == 0)
                veg.setVisibility(View.GONE);

            if(currtif.isNonveg())             //Non-Veg
                nonveg.setChecked(true);
            else if(edittif == 0)
                nonveg.setVisibility(View.GONE);

            textopeningtime.setText(currtif.getOpeningtime());
            textclosingtime.setText(currtif.getClosingtime());
            String[] split = currtif.getOrderprior().split(":");
            days.setText(split[0]);
            hrs.setText(split[1]);
            mins.setText(split[2]);


            //Item and Price List loading from firebase
            if(selected.equals("mytiffin") || selected.equals("home")) {
                ref = FirebaseDatabase.getInstance().getReference("uploads").child(currtif.getParentkey()).child("menu");
            }
            else if(selected.equals("myorder")) {
                ref = FirebaseDatabase.getInstance().getReference("orders").child(firebaseAuth.getUid()).child(currtif.getParentkey()).child("menu");
            }

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        itemlist.add(postSnapshot.getKey().replace("_","/"));
                        String value = postSnapshot.getValue(String.class);
                        String[] price_desc = value.split("_");
                        if(selected.equals("myorder")) {
                            price_desc[0] += " (QTY)";
                        }
                        pricelist.add(price_desc[0]);
                    }
                    itempriceadapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            //Images url list loading from firebase
            ref = FirebaseDatabase.getInstance().getReference("uploads").child(currtif.getParentkey()).child("images");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        ImageUrlList.add(postSnapshot.getValue(String.class));
                    }
                    if (ImageUrlList.size() != 0) {
                        imageCustomAdapter.notifyDataSetChanged();
                        display_image.setVisibility(View.VISIBLE);
                        dots.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            //Making elements GONE and UNCLIKABLE
            select_image.setVisibility(View.GONE);
            if(edittif == 0)
            {
                textmenu.setVisibility(View.GONE);
                chargetitle.setVisibility(View.GONE); chargePerKm.setVisibility(View.GONE);
                delivery.setClickable(false);   takeaway.setClickable(false);
                veg.setClickable(false);    nonveg.setClickable(false);
                textname.setFocusable(false); textname.setClickable(false); textname.setLongClickable(false);
                textcostfor2.setFocusable(false); textcostfor2.setClickable(false); textcostfor2.setLongClickable(false);
                textcuisine.setFocusable(false); textcuisine.setClickable(false);
                textmenu.setFocusable(false); textmenu.setClickable(false);
                days.setFocusable(false); days.setClickable(false);
                hrs.setFocusable(false); hrs.setClickable(false);
                mins.setFocusable(false); mins.setClickable(false);
                range.setFocusable(false); range.setClickable(false);

                addmenu.setFocusable(false);
                addmenu.setVisibility(View.GONE);
            }
            else if(edittif == 1)
            {
                deleteupload.setVisibility(View.VISIBLE);
                textcostfor2.setVisibility(View.VISIBLE);
                latitude = currtif.getLatitude();
                longitude = currtif.getLongitude();
            }

            switch (selected) {
                case "mytiffin":
                    proceed.setText("Re-Upload (Updated) ");
                    pos = -1;
                    break;
                case "home":
                    proceed.setText("Place Order");

                    userdistance.setVisibility(View.VISIBLE);
                    CalculateAndAssignDistance();
                    //double distance = Math.round(currtif.getDistance() * 10.0) / 10.0;
                    //userdistance.setText("Your Distance : " + distance + " km");

                    String[] splits = currtif.getOrderprior().split(":");
                    if (splits[0].equals("0") && !isCurrentlyOpen(currtif.getOpeningtime(), currtif.getClosingtime())) {
                        proceed.setText("Currently closed\nOrder during open hours");
                        proceed.setEnabled(false);
                    }
                    else if (!currtif.isTakeaway() && currtif.getDelivery() < currtif.getDistance())             //Delivery and takeaway not applicable
                    {
                        proceed.setText("Delivery not applicable\nChange your location for ordering");
                        proceed.setEnabled(false);
                    }
                    break;
                case "myorder":
                    range.setVisibility(View.GONE);

                    final Orderinfo orderdetails = HomeFragment.orders_list.get(pos);

                    if(orderdetails.getOrderstatus().equals("delivered")) {
                        rate.setVisibility(View.VISIBLE);
                        if (orderdetails.getOrderrating() != -1) {
                            rate.setText("Ratings given: " + orderdetails.getOrderrating());
                            rate.setClickable(false);
                        } else
                            ratingBar.setVisibility(View.VISIBLE);
                    }

                    ref = FirebaseDatabase.getInstance().getReference("users").child(currtif.getOwnerid());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tiffinownerinfo = dataSnapshot.getValue(Userinfo.class);
                            String temp = "Vendor contact no - " + tiffinownerinfo.getMobileno() + "\nAmount - " + "\u20B9" + orderdetails.getAmount();
                            switch (orderdetails.getOrderstatus()) {
                                case "none":
                                    temp += "\nOrder Processing";
                                    temp += "\nOrder Password - " + orderdetails.getPassword();
                                    break;
                                case "accepted":
                                    temp += "\nOrder Accepted";
                                    temp += "\nOrder Password - " + orderdetails.getPassword();
                                    break;
                                case "delivered":
                                    temp += "\nOrder Delivered";
                                    break;
                                case "rejected":
                                    temp += "\nOrder Rejected By Vendor";
                                    break;
                            }
                            proceed.setText(temp);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    break;
            }
        }
        else            //Set location in address felid
        {
            Current_Location curr_loc = (Current_Location) getApplication();
            textaddress.setText(curr_loc.getCurr_address());
            latitude = curr_loc.getCurr_lat();
            longitude = curr_loc.getCurr_long();
        }


        proceed.setOnClickListener(view -> {
            if (pos == -1)   //Tiffin upload
            {
                name = textname.getText().toString();
                costfor2 = Integer.parseInt(textcostfor2.getText().toString());
                cuisine = textcuisine.getText().toString();
                openingtime = textopeningtime.getText().toString();
                closingtime = textclosingtime.getText().toString();
                orderprior = days.getText().toString() +":"+hrs.getText().toString() +":"+ mins.getText().toString() +"";
                address = textaddress.getText().toString();
                String delirange = range.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(textcostfor2.getText()) || TextUtils.isEmpty(cuisine) || itemlist.isEmpty() || pricelist.isEmpty() || TextUtils.isEmpty(openingtime) || TextUtils.isEmpty(closingtime) || TextUtils.isEmpty(address) || (!delivery.isChecked() && !takeaway.isChecked()) || (delivery.isChecked() && TextUtils.isEmpty(delirange)) || (!veg.isChecked() && !nonveg.isChecked())) {
                    Toast.makeText(Uploadtiffin.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(delivery.isChecked() && (TextUtils.isEmpty(delirange) || TextUtils.isEmpty(chargePerKm.getText()) ))
                {
                    Toast.makeText(Uploadtiffin.this, "Please fill all delivery related fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Upload Animation
                //lottiAnimationView.setVisibility(View.VISIBLE);
                //cv1.setVisibility(View.GONE);
                textname.setVisibility(View.GONE);
                detailsscroll.setVisibility(View.GONE);
                detailstab.setVisibility(View.GONE);
                menuscroll.setVisibility(View.GONE);
                lottiAnimationView.setVisibility(View.VISIBLE);
                deleteupload.setVisibility(View.GONE);
                menutab.setVisibility(View.GONE);
                proceed.setVisibility(View.GONE);
                //cv1.setBackground(getDrawable(android.R.color.transparent));
                lottiAnimationView.playAnimation();

                //For rating;
                float rating;
                Userinfo user = Splash.curruser;
                if(user.getRating_count() == 0)
                    rating = -1;
                else {
                    rating = (float) user.getRating_sum() / user.getRating_count();
                    rating = (float) (Math.round(rating * 10.0) / 10.0);
                }
                //Delivery Range
                float range = 0, charge = 0;
                if(delivery.isChecked()) {
                    range = Float.parseFloat(delirange);

                    charge = Float.parseFloat(chargePerKm.getText().toString());
                }

                Tiffininfo tiffin = new Tiffininfo(firebaseAuth.getUid(), name, costfor2, cuisine, openingtime, closingtime, orderprior, address, veg.isChecked(),nonveg.isChecked(), rating, range, charge,takeaway.isChecked());
                tiffin.setLatitude(latitude);
                tiffin.setLongitude(longitude);
                ref = FirebaseDatabase.getInstance().getReference("uploads");
                String key;
                if(edittif == 1) {
                    ref = ref.child(currtif.getParentkey());
                    key = currtif.getParentkey();
                }
                else {
                    ref = ref.push();
                    key = ref.getKey();
                }

                ref.setValue(tiffin);
                ref.child("uploadtype").setValue("menu");
                //ref.child("latitude").setValue(latitude);
                //ref.child("longitude").setValue(longitude);
                ref = ref.child("menu");
                for (int i = 0; i < itemlist.size(); i++) {
                    String value = pricelist.get(i);

                    ref.child(itemlist.get(i).replace("/","_")).setValue(value);
                }

                ref = FirebaseDatabase.getInstance().getReference("uploads").child(key).child("images");

                //Images Upload in Firebase Storage
                if (ImagesUri.size() !=0 ) {
                    StorageReference sref = FirebaseStorage.getInstance().getReference(firebaseAuth.getUid()).child(key);

                    for (i = 0; i < ImagesUri.size(); i++) {
                        StorageReference fileReference = sref.child(System.currentTimeMillis() + "." + getFileExtension(ImagesUri.get(i)));
                        fileReference.putFile(ImagesUri.get(i)).addOnSuccessListener(taskSnapshot -> {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(uri -> ref.push().setValue(uri.toString()));
                        })
                                .addOnFailureListener(e -> Toast.makeText(Uploadtiffin.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
                new Handler().postDelayed(() -> {
                    Intent i = new Intent(Uploadtiffin.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }, 2000);
            }
            else if (selected.equals("home"))   //From home page - tiffin order
            {
                if(itemlist.size()==0 || pricelist.size()==0) {
                    Toast.makeText(Uploadtiffin.this,"Loading your food items",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isDistanceSet){
                    Toast.makeText(Uploadtiffin.this,"Calculating your distance",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(Uploadtiffin.this,Selection.class);
                startActivity(i);
            }
        });


        detailstab.setOnClickListener(view -> {
            detailsscroll.setVisibility(View.VISIBLE);
            menuscroll.setVisibility(View.GONE);
        });

        menutab.setOnClickListener(view -> {
            detailsscroll.setVisibility(View.GONE);
            menuscroll.setVisibility(View.VISIBLE);
        });

        deleteupload.setOnClickListener(view -> {
            if(selected.equals("mytiffin"))
            {
                ref = FirebaseDatabase.getInstance().getReference("deluploads");
                ref.child(currtif.getParentkey()).setValue(currtif);
                ref = FirebaseDatabase.getInstance().getReference("uploads");
                ref.child(currtif.getParentkey()).setValue(null);
                Toast.makeText(getApplicationContext(),"Upload Deleted\nRefresh your uploads to see the effect",Toast.LENGTH_LONG).show();
            }
        });

        if(pos == -1) {
            delivery.setOnClickListener(v -> {
                if (delivery.isChecked())
                    deliverylayout.setVisibility(View.VISIBLE);
                else
                    deliverylayout.setVisibility(View.GONE);
            });
        }

        textopeningtime.setOnClickListener(v -> {
            if (pos == -1) {
                tracktimepicker=4;
                DialogFragment timepicker = new TimePickerFragment();
                timepicker.show(getSupportFragmentManager(), "openingtime");
            }
        });

        textclosingtime.setOnClickListener(v -> {
            if (pos == -1) {
                tracktimepicker=5;
                DialogFragment timepicker = new TimePickerFragment();
                timepicker.show(getSupportFragmentManager(), "closingtime");
            }
        });

        textaddress.setOnClickListener(v -> {
            if(pos==-1) {
            /*    Current_Location curr_loc = (Current_Location) getApplication();

                Intent i = new PlacePicker.IntentBuilder()
                        .placeOptions(PlacePickerOptions.builder()
                                .statingCameraPosition(new CameraPosition.Builder()
                                        .target(new LatLng(curr_loc.getCurr_lat(), curr_loc.getCurr_long())).zoom(16).build())
                                .build()).build(Uploadtiffin.this);
                startActivityForResult(i, 101);
            */
            }

        });

        addmenu.setOnClickListener(v -> {
            if(pos==-1)
            {
                Additems dialog = new Additems();
                dialog.show(getSupportFragmentManager(), "example dialog");
            }
        });

        rate.setOnClickListener((View.OnClickListener) view -> {
            float rating = ratingBar.getRating();
            if(rating == 0) {
                Toast.makeText(Uploadtiffin.this, "Please provide rating first", Toast.LENGTH_SHORT).show();
                return;
            }

            ratingBar.setVisibility(View.GONE);
            rate.setText("Ratings given: "+ rating);
            rate.setClickable(false);
            HomeFragment.orders_list.get(pos).setOrderrating(rating);
            int count = tiffinownerinfo.getRating_count();
            float sum = tiffinownerinfo.getRating_sum();
            count++;
            sum += rating;
            //Update rating details in vendor profile
            ref = FirebaseDatabase.getInstance().getReference("users").child(currtif.getOwnerid());
            ref.child("rating_count").setValue(count);
            ref.child("rating_sum").setValue(sum);

            //set order rating
            ref = FirebaseDatabase.getInstance().getReference("orders").child(firebaseAuth.getUid());
            ref.child(currtif.getParentkey()).child("orderrating").setValue(rating);

            ownerrating = (float) sum/count;
            ownerrating = (float) (Math.round(ownerrating * 10.0) / 10.0);

            //Update vendor's rating in vendor uploads
            ref = FirebaseDatabase.getInstance().getReference("uploads");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String id = postSnapshot.child("ownerid").getValue(String.class);
                        if(id!= null && id.equals(currtif.getOwnerid()))
                        {
                            ref.child(postSnapshot.getKey()).child("rating").setValue(ownerrating);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        select_image.setOnClickListener(view -> {
            Intent intent1 = new Intent();
            intent1.setType("image/*");
            intent1.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent1, 1);
        });

        textcuisine.setOnClickListener(v -> {
            if (pos == -1) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Uploadtiffin.this);
                mBuilder.setTitle("Cuisines");
                mBuilder.setMultiChoiceItems(cuisineItemsList, checkedItems, (dialog, position, isChecked) -> {
                    if (isChecked) {

                        mUserItems.add(position);
                    } else {
                        mUserItems.remove((Integer.valueOf(position)));
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder item = new StringBuilder();

                    int max = mUserItems.size();
                    if(mUserItems.size() >3){
                        Toast.makeText(this, "Maximum 3 cuisines allowed", Toast.LENGTH_SHORT).show();

                        max = 3;
                    }

                    for (int i = 0; i < max; i++) {
                        item.append(cuisineItemsList[mUserItems.get(i)]);
                        if (i != max- 1) {
                            item.append(", ");
                        }
                    }
                    textcuisine.setText(item.toString());
                });

                mBuilder.setNegativeButton("Dismiss", (dialogInterface, i) -> dialogInterface.dismiss());

                mBuilder.setNeutralButton("Clear", (dialogInterface, which) -> {
                    for (int i = 0; i < checkedItems.length; i++) {
                        checkedItems[i] = false;
                        mUserItems.clear();
                        textcuisine.setText("");
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            ImageUrlList.add(mImageUri.toString());
            imageCustomAdapter.notifyDataSetChanged();
            ImagesUri.add(mImageUri);
            display_image.setVisibility(View.VISIBLE);
            dots.setVisibility(View.VISIBLE);

        }
        /*else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {

            Place place = PlacePicker.getPlace(data);
            if(place!= null || place.getFormattedAddress()!= null || place.getLat()!= null || place.getLng()!= null){
                textaddress.setText(place.getFormattedAddress());
                latitude = Double.parseDouble(place.getLat());
                longitude = Double.parseDouble(place.getLng());

            }else {
                Toast.makeText(Uploadtiffin.this, "Something went wrong, not able to fetch your location", Toast.LENGTH_SHORT).show();
            }

        }*/
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String timeset = "AM";
        if(hourOfDay > 12)
        {
            hourOfDay -= 12;
            timeset = "PM";
        }
        else if(hourOfDay == 0)
        {
            hourOfDay = 12;
            timeset = "AM";
        }
        else if(hourOfDay == 12)
            timeset = "PM";

        String minin = Integer.toString(minute);

        if (minute < 10){

            minin = "0" + minute;
        }

        String finaltime = hourOfDay + ":" + minin + " " + timeset;
        if(tracktimepicker == 4)
            textopeningtime.setText(finaltime);
        else if(tracktimepicker == 5)
            textclosingtime.setText(finaltime);
    }

    @Override
    public void applyTexts(ArrayList<String> ilist, ArrayList<String> plist){

        for(int i=0;i<ilist.size();i++)
        {
            itemlist.add(ilist.get(i));
            pricelist.add(plist.get(i));

        }
        itempriceadapter.notifyDataSetChanged();
    }

    public boolean isCurrentlyOpen(String openingtime,String closingtime)
    {
        try {

            String[] open = openingtime.split(" "); //10:35 am

            String openmode = open[1];                      //am
            String[] openhrmin = open[0].split(":");    //10:35

            int openhr = Integer.parseInt(openhrmin[0]);    //10
            int openmin = Integer.parseInt(openhrmin[1]);   //35

            String[] close = closingtime.split(" "); //10:35 pm

            String closemode = close[1];                      //pm
            String[] closehrmin = close[0].split(":");    //10:35

            float closehr = Integer.parseInt(closehrmin[0]);    //10
            float closemin = Integer.parseInt(closehrmin[1]);   //35

            if (openmode.equals("AM")) {
                if (openhr == 12)
                    openhr = 0;
            } else if (openmode.equals("PM")) {
                if (openhr != 12)
                    openhr += 12;
            }

            if (closemode.equals("AM")) {
                if (closehr == 12)
                    closehr = 0;
            } else if (closemode.equals("PM")) {
                if (closehr != 12)
                    closehr += 12;
            }

            Calendar calendar = Calendar.getInstance();
            float currhr = calendar.get(Calendar.HOUR_OF_DAY);
            float currmin = calendar.get(Calendar.MINUTE);

            if (currhr < openhr || (currhr == openhr && currmin < openmin) || currhr > closehr || (currhr == closehr && currmin > closemin))
                return false;

            return true;
        }
        catch (Exception e)
        {
            return true;
        }
    }


    /*public void calculateAndAssignDistance()
    {
        Current_Location curr_loc = (Current_Location) Objects.requireNonNull(getApplication());
        MapmyIndiaDistanceMatrix.builder()
                .profile(DirectionsCriteria.PROFILE_BIKING)
                .resource(DirectionsCriteria.RESOURCE_DISTANCE)
                .coordinate(Point.fromLngLat(curr_loc.getCurr_long(), curr_loc.getCurr_lat()))
                .coordinate(Point.fromLngLat(currtif.getLongitude(), currtif.getLatitude()))
                .build()
                .enqueueCall(new Callback<DistanceResponse>() {
                    @Override
                    public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                        //handle response
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                DistanceResponse legacyDistanceResponse = response.body();
                                DistanceResults distanceResults = legacyDistanceResponse.results();

                                if (distanceResults != null) {
                                    //updateroaddistance(distanceResults);
                                } else {
                                    Toast.makeText(Uploadtiffin.this, "Distance calculation result error: " + legacyDistanceResponse.responseCode(), Toast.LENGTH_SHORT).show();
                                    updatestraightdistance();
                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(Uploadtiffin.this, "Distance calculation failed: " + response.code(), Toast.LENGTH_SHORT).show();
                            updatestraightdistance();
                        }
                    }

                    @Override
                    public void onFailure(Call<DistanceResponse> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(Uploadtiffin.this, "Distance calculation call failed: ", Toast.LENGTH_SHORT).show();
                        updatestraightdistance();
                    }
                });
    }
    */

    private void CalculateAndAssignDistance() {
        RoutingEngine routingEngine;
        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            //throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
            System.out.println("Error in routing engine");
            updatestraightdistance();
            return;
        }

        Current_Location curr_loc = (Current_Location) Objects.requireNonNull(getApplication());

        GeoCoordinates customerCoordinates = new GeoCoordinates(curr_loc.getCurr_lat(), curr_loc.getCurr_long());
        GeoCoordinates vendorCoordinates = new GeoCoordinates(currtif.getLatitude(), currtif.getLongitude());
        Waypoint customerWaypoint = new Waypoint(customerCoordinates);
        Waypoint vendorWaypoint = new Waypoint(vendorCoordinates);

        List<Waypoint> wayPoints =
                new ArrayList<>(Arrays.asList(customerWaypoint, vendorWaypoint));

        routingEngine.calculateRoute(
                wayPoints,
                new ScooterOptions(),
                new CalculateRouteCallback() {
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                        if (routingError != null || routes == null) {
                            System.out.println("Error in calculating route");
                            updatestraightdistance();
                        }
                        else {
                            if (routes.size() == 0) {
                                System.out.println("Return route is zero");
                                updatestraightdistance();
                            } else {
                                Route route = routes.get(0);
                                updateroaddistance(route.getLengthInMeters());
                            }
                        }
                    }
                });
    }


    public void updateroaddistance(int distance)
    {
        double distanceInKM = distance/1000;   // KM
        distanceInKM = Math.round(distanceInKM * 10.0) / 10.0;  //Round off single digit
        userdistance.setText("Your Distance : " + distanceInKM + " km");
        currtif.setDistance((float)distanceInKM);
        Toast.makeText(this,"Road distance",Toast.LENGTH_LONG).show();
        isDistanceSet = true;
    }

    public void updatestraightdistance()
    {
        double distance = Math.round(currtif.getDistance() * 10.0) / 10.0;
        userdistance.setText("Your Distance : " + distance + " km");
        Toast.makeText(this,"Straight distance",Toast.LENGTH_LONG).show();
        isDistanceSet = true;
    }

}

