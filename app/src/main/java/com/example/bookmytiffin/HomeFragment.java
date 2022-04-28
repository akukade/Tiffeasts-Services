package com.example.bookmytiffin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import com.mapbox.mapboxsdk.MapmyIndia;
import com.mmi.services.account.MapmyIndiaAccountManager;

public class HomeFragment extends Fragment{


    Homecustomadapter adapter;
    RecyclerView.LayoutManager layoutManager;
    static RecyclerView recyclerView;
    static ArrayList<Tiffininfo>tiffins_list = new ArrayList<>();
    ArrayList<Tiffininfo>complete_tiffin_list;
    static ArrayList<Orderinfo>orders_list = new ArrayList<>();
    TextView title,loc,searchtext,info,emptyText;
    FloatingActionButton upload;
    ImageView locationbutton;
    DatabaseReference ref;
    FirebaseAuth fauth;
    ImageButton filterandsort;
    FloatingTextButton joinus;
    FloatingActionButton whatsapp;
    ImageButton searchbutton;
    int sortby=0,veg_non=2,upload_type=2,deli_or_take=2;
    ProgressBar progressBar;
    ImageView emptyImage;
    String[] cuisineArray = new String[]{"Maharashtrian", "Gujarati", "Rajasthani", "Punjabi", "South Indian", "Bengali", "Bakery", "Konkani", "Chinese", "Italian", "Continental"};
    ArrayAdapter<String> cuisine_adapter;
    boolean[] checkedArray = new boolean[cuisineArray.length];
    static String selected = "home";
    String location;
    Double latitude=-1.1,longitude=-1.1;

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Bundle arg = getArguments();
        if( arg != null)
            selected = arg.getString("selected","home");

        joinus = view.findViewById(R.id.joinus);
        locationbutton = view.findViewById(R.id.locationbutton);
        filterandsort = view.findViewById(R.id.filterandsort);
        upload = view.findViewById(R.id.upload);
        whatsapp = view.findViewById(R.id.whatsapp);
        recyclerView = view.findViewById(R.id.homerecyclerview);
        title = view.findViewById(R.id.title);
        loc = view.findViewById(R.id.loc);
        progressBar = view.findViewById(R.id.progressbar);
        searchtext = view.findViewById(R.id.searchtext);
        searchbutton = view.findViewById(R.id.searchbutton);
        info = view.findViewById(R.id.infotext);
        emptyImage = view.findViewById(R.id.emptyImage);
        emptyText = view.findViewById(R.id.emptyText);
        emptyImage.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);

        if(Splash.curruser.getVerified() == 0)
           upload.setVisibility(View.GONE);
        else
            joinus.setVisibility(View.GONE);

        if(!selected.equals("home")) {
            title.setVisibility(View.VISIBLE);
            loc.setVisibility(View.GONE);
            locationbutton.setVisibility(View.GONE);
            searchbutton.setVisibility(View.GONE);
            searchtext.setVisibility(View.GONE);
            filterandsort.setVisibility(View.GONE);

            info.setVisibility(View.VISIBLE);

            if(selected.equals("mytiffin")){
                title.setText("Uploads");
                info.setText("Food items uploaded");
            }

            else if(selected.equals("myorder")){
                title.setText("Orders");
                info.setText("Food items ordered");
            }
        }

        Current_Location curr_loc = (Current_Location) Objects.requireNonNull(getActivity()).getApplication();
        latitude = curr_loc.getCurr_lat();
        longitude = curr_loc.getCurr_long();
        location = curr_loc.getCurr_address();

        loc.setText(location);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        tiffins_list = new ArrayList<>();
        complete_tiffin_list = new ArrayList<>();
        orders_list = new ArrayList<>();

        adapter = new Homecustomadapter(getActivity(),tiffins_list);
        recyclerView.setAdapter(adapter);

        searchtext.addTextChangedListener(searchtextwatcher);

        fauth = FirebaseAuth.getInstance();

        if (selected.equals("home") || selected.equals("mytiffin")) {
            ref = FirebaseDatabase.getInstance().getReference("uploads");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    tiffins_list.clear();
                    complete_tiffin_list.clear();
                    String userid = fauth.getUid();

                    Location userloc = new Location("UserLoc");
                    userloc.setLatitude(latitude);
                    userloc.setLongitude(longitude);
                    Location tifloc = new Location("TiffinLoc");

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Tiffininfo tiffin = postSnapshot.getValue(Tiffininfo.class);
                        if (selected.equals("home") && tiffin!=null) {
                            tifloc.setLatitude(tiffin.getLatitude());
                            tifloc.setLongitude(tiffin.getLongitude());

                            float distance = userloc.distanceTo(tifloc)/1000;
                            tiffin.setDistance(distance);
                            tiffin.setParentkey(postSnapshot.getKey());
                            if(distance <= 4000 ) {
                                tiffins_list.add(tiffin);
                                complete_tiffin_list.add(tiffin);
                            }
                        } else {
                            if ((selected.equals("mytiffin") && userid!=null && tiffin!=null &&userid.equals(tiffin.getOwnerid()))) {
                                tiffin.setParentkey(postSnapshot.getKey());
                                tiffins_list.add(tiffin);
                            }
                        }
                    }

                    if (tiffins_list.isEmpty()){
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }

                    if(selected.equals("home"))
                    {
                        Collections.sort(tiffins_list,new DistanceComparator());
                    }
                    else if(selected.equals("mytiffin"))
                    {
                        Collections.reverse(tiffins_list);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if (selected.equals("myorder")) {
            ref = FirebaseDatabase.getInstance().getReference("orders").child(Objects.requireNonNull(fauth.getUid()));

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    tiffins_list.clear();
                    orders_list.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Tiffininfo tiffin = postSnapshot.getValue(Tiffininfo.class);
                        if (tiffin != null) {
                            tiffin.setParentkey(postSnapshot.getKey());
                            tiffins_list.add(tiffin);
                        }

                        Orderinfo orderinfo = postSnapshot.getValue(Orderinfo.class);
                        if(orderinfo!=null)
                            orders_list.add(orderinfo);
                    }
                    if (tiffins_list.isEmpty()){
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }
                    Collections.reverse(tiffins_list);
                    Collections.reverse(orders_list);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        filterandsort.setOnClickListener(v -> {

            BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(getActivity());
            bottomSheetDialog1.setContentView(R.layout.bottom_sheet_sort);
            bottomSheetDialog1.setCanceledOnTouchOutside(true);
            bottomSheetDialog1.show();

            Button apply = bottomSheetDialog1.findViewById(R.id.apply);
            Button clear = bottomSheetDialog1.findViewById(R.id.clear);

            RadioGroup sortGroup = bottomSheetDialog1.findViewById(R.id.sortGroup);
            RadioGroup deli_take_Group = bottomSheetDialog1.findViewById(R.id.deli_take_group);
            RadioGroup veg_non_Group = bottomSheetDialog1.findViewById(R.id.veg_non_Group);
            RadioGroup tif_menu_Group = bottomSheetDialog1.findViewById(R.id.serviceGroup);
            ListView cuisine_listview = bottomSheetDialog1.findViewById(R.id.cuisinelistview);

            ((RadioButton) sortGroup.getChildAt(sortby)).setChecked(true);
            ((RadioButton) deli_take_Group.getChildAt(deli_or_take)).setChecked(true);
            ((RadioButton) veg_non_Group.getChildAt(veg_non)).setChecked(true);
            ((RadioButton) tif_menu_Group.getChildAt(upload_type)).setChecked(true);

            cuisine_adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_multiple_choice,cuisineArray);
            cuisine_listview.setAdapter(cuisine_adapter);

            for(int i=0;i<cuisineArray.length;i++) {
                cuisine_listview.setItemChecked(i,checkedArray[i]);
            }

            cuisine_listview.setOnItemClickListener((adapterView, view19, position, l) -> checkedArray[position] = cuisine_listview.isItemChecked(position));

            RelativeLayout sortLayout = bottomSheetDialog1.findViewById(R.id.sortLayout);
            RelativeLayout deliveryLayout = bottomSheetDialog1.findViewById(R.id.deliveryLayout);
            RelativeLayout cuisineLayout = bottomSheetDialog1.findViewById(R.id.cuisineLayout);
            RelativeLayout foodLayout = bottomSheetDialog1.findViewById(R.id.foodLayout);
            RelativeLayout serviceLayout = bottomSheetDialog1.findViewById(R.id.serviceLayout);

            Button sort = bottomSheetDialog1.findViewById(R.id.sortby);
            Button cuisinesort = bottomSheetDialog1.findViewById(R.id.cuisinesort);
            Button delivery = bottomSheetDialog1.findViewById(R.id.delivery);
            Button type_food = bottomSheetDialog1.findViewById(R.id.type_food);
            Button type_service = bottomSheetDialog1.findViewById(R.id.type_service);

            //RelativeLayout rightlayout = bottomSheetDialog1.findViewById(R.id.rightLayout);

            bottomSheetDialog1.setCancelable(false);
            bottomSheetDialog1.setCanceledOnTouchOutside(true);

            if (sort != null) {
                sort.setOnClickListener(view18 -> {
                    sortLayout.setVisibility(View.VISIBLE);
                    cuisineLayout.setVisibility(View.GONE);
                    deliveryLayout.setVisibility(View.GONE);
                    foodLayout.setVisibility(View.GONE);
                    serviceLayout.setVisibility(View.GONE);
                });
            }

            if (cuisinesort != null) {
                cuisinesort.setOnClickListener(view17 -> {
                    sortLayout.setVisibility(View.GONE);
                    cuisineLayout.setVisibility(View.VISIBLE);
                    deliveryLayout.setVisibility(View.GONE);
                    foodLayout.setVisibility(View.GONE);
                    serviceLayout.setVisibility(View.GONE);
                });
            }

            if (delivery != null) {
                delivery.setOnClickListener(view16 -> {
                    sortLayout.setVisibility(View.GONE);
                    cuisineLayout.setVisibility(View.GONE);
                    deliveryLayout.setVisibility(View.VISIBLE);
                    foodLayout.setVisibility(View.GONE);
                    serviceLayout.setVisibility(View.GONE);
                });
            }

            if (type_food != null) {
                type_food.setOnClickListener(view15 -> {
                    sortLayout.setVisibility(View.GONE);
                    cuisineLayout.setVisibility(View.GONE);
                    deliveryLayout.setVisibility(View.GONE);
                    foodLayout.setVisibility(View.VISIBLE);
                    serviceLayout.setVisibility(View.GONE);
                });
            }

            if (type_service != null) {
                type_service.setOnClickListener(view14 -> {
                    sortLayout.setVisibility(View.GONE);
                    cuisineLayout.setVisibility(View.GONE);
                    deliveryLayout.setVisibility(View.GONE);
                    foodLayout.setVisibility(View.GONE);
                    serviceLayout.setVisibility(View.VISIBLE);
                });
            }

            sortGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                if(i == R.id.distance)
                    sortby=0;
                else if(i == R.id.rating)
                    sortby=1;
            });

            veg_non_Group.setOnCheckedChangeListener((radioGroup, i) -> {
                if(i == R.id.veg)
                    veg_non=0;
                else if(i == R.id.nonveg)
                    veg_non=1;
                else if(i == R.id.vnboth)
                    veg_non=2;
            });

            deli_take_Group.setOnCheckedChangeListener((radioGroup, i) -> {
                if(i == R.id.deli)
                    deli_or_take=0;
                else if(i == R.id.take)
                    deli_or_take=1;
                else if(i == R.id.dtboth)
                    deli_or_take=2;
            });

            tif_menu_Group.setOnCheckedChangeListener((radioGroup, i) -> {
                if(i == R.id.type_tiffin)
                    upload_type=0;
                else if(i == R.id.type_menu)
                    upload_type=1;
                else if(i == R.id.tmboth)
                    upload_type=2;
            });

            if (apply != null) {
                apply.setOnClickListener(view13 -> {
                    applyfilters();
                    bottomSheetDialog1.dismiss();
                });
            }

            if (clear != null) {
                clear.setOnClickListener(view12 -> {
                    sortby=0;
                    veg_non=2;
                    upload_type=2;
                    deli_or_take=2;
                    checkedArray = new boolean[cuisineArray.length];
                    cuisine_adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_multiple_choice,cuisineArray);
                    cuisine_listview.setAdapter(cuisine_adapter);
                    ((RadioButton) sortGroup.getChildAt(sortby)).setChecked(true);
                    ((RadioButton) deli_take_Group.getChildAt(deli_or_take)).setChecked(true);
                    ((RadioButton) veg_non_Group.getChildAt(veg_non)).setChecked(true);
                    ((RadioButton) tif_menu_Group.getChildAt(upload_type)).setChecked(true);
                });
            }

        });

        joinus.setOnClickListener(v -> {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.setCancelable(true);
            bottomSheetDialog.show();

            Button whatsapp = bottomSheetDialog.findViewById(R.id.startupload);

            if (whatsapp != null) {
                whatsapp.setOnClickListener(v1 -> {
                    String mobilenumber = "7719832265";
                    String message = "I would love to get started and become a member";

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+"+91"+mobilenumber + "&text="+message));
                    startActivity(intent);
                });
            }

        });


        upload.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), Uploadtiffin.class)));

        locationbutton.setOnClickListener(v -> Toast.makeText(getActivity(),"Save/Update your location from Profile - Address Book",Toast.LENGTH_LONG).show());

        loc.setOnClickListener(v -> {
            Toast.makeText(getActivity(),"Save/Update your location from Profile - Address Book",Toast.LENGTH_LONG).show();
            //Current_Location curr_loc = (Current_Location) getActivity().getApplication();
            startActivity(new Intent(getActivity(),HereSearch.class));
        });

        return view;
    }

    public TextWatcher searchtextwatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        //Search Code
        public void afterTextChanged(Editable s) {
            progressBar.setVisibility(View.VISIBLE);
            String query = searchtext.getText().toString().toLowerCase();
            tiffins_list.clear();

            for(int i=0;i<complete_tiffin_list.size();i++)
            {
                Tiffininfo temptif = complete_tiffin_list.get(i);
                if(temptif.getName().toLowerCase().contains(query))
                {
                    tiffins_list.add(temptif);
                    continue;
                }
                if(temptif.getCuisine().toLowerCase().contains(query))
                {
                    tiffins_list.add(temptif);
                }
            }

            if (tiffins_list.isEmpty()){
                emptyImage.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.VISIBLE);
            }
            else {
                emptyImage.setVisibility(View.GONE);
                emptyText.setVisibility(View.GONE);
            }

            Homecustomadapter inneradapter = new Homecustomadapter(getActivity(),tiffins_list);
            recyclerView.setAdapter(inneradapter);
            progressBar.setVisibility(View.GONE);
        }
    };

    public void applyfilters()
    {
        progressBar.setVisibility(View.VISIBLE);
        tiffins_list.clear();
        adapter.notifyDataSetChanged();
        for (Tiffininfo tif : complete_tiffin_list)
        {
            if(veg_non == 0 && !tif.isVeg())
                continue;
            else if(veg_non == 1 && !tif.isNonveg())
                continue;


            if(deli_or_take == 0 && tif.getDelivery() < tif.getDistance())
                continue;
            else if(deli_or_take == 1 && !tif.isTakeaway())
                continue;

            boolean include = false,checked = false;
            for(int i=0;i<cuisineArray.length;i++)
            {
                if(checkedArray[i])
                    checked = true;
                if(checkedArray[i] && tif.getCuisine().contains(cuisineArray[i]))
                {
                    include = true;
                    break;
                }
            }

            if(!checked)
                tiffins_list.add(tif);
            else if(include)
                tiffins_list.add(tif);
        }

        if (sortby == 0)
            Collections.sort(tiffins_list, new DistanceComparator());
        else if (sortby == 1)
            Collections.sort(tiffins_list, new RatingComparator());

        Homecustomadapter inneradapter = new Homecustomadapter(getActivity(),tiffins_list);
        recyclerView.setAdapter(inneradapter);
        if(tiffins_list.isEmpty()){
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyImage.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
    }

}


class RatingComparator implements Comparator<Tiffininfo> {

    public int compare(Tiffininfo t1, Tiffininfo t2) {

        return Float.compare(t2.getRating(), t1.getRating());

    }
}


class DistanceComparator implements Comparator<Tiffininfo> {

    public int compare(Tiffininfo t1, Tiffininfo t2) {

        return Float.compare(t1.getDistance(), t2.getDistance());

    }
}