package com.example.bookmytiffin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import static com.instabug.library.Instabug.getApplicationContext;

public class ProfileFragment extends Fragment {

    TextView  feedback, about, logout,rateus, share, addressbook;
    Userinfo profuser;

    EditText name,email, mobileno;
    //Button logout, update, delete;
    //ImageButton backarrow;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        rateus = view.findViewById(R.id.rateplaystore);
        addressbook = view.findViewById(R.id.addressbook);
        share = view.findViewById(R.id.share);
        about = view.findViewById(R.id.about);
        feedback = view.findViewById(R.id.feedback);
        logout = view.findViewById(R.id.logouttext);
        //backarrow = view.findViewById(R.id.backarrowprofile);
        //delete = view.findViewById(R.id.deleteaccount);
        name = view.findViewById(R.id.tname);
        email = view.findViewById(R.id.temail);
        //update = view.findViewById(R.id.update);
        mobileno = view.findViewById(R.id.tmobileno);
        //logout = view.findViewById(R.id.logout);
        profuser = Splash.curruser;

        name.setText(profuser.getName());
        email.setText(profuser.getEmail());
        mobileno.setText(profuser.getMobileno());


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String sharebody = "Order delicious homemade food from Tiffeasts:-https://play.google.com/store/apps/details?id=com.tiffeasts.services";
                String sharesub = "Tiffeasts";

                shareIntent.putExtra(Intent.EXTRA_SUBJECT,sharesub);
                shareIntent.putExtra(Intent.EXTRA_TEXT,sharebody);

                startActivity(Intent.createChooser(shareIntent, "Share Using"));
            }
        });


        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="+getActivity().getApplicationContext().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                SharedPreferences userdatastore = getActivity().getSharedPreferences("userdatastore", Context.MODE_PRIVATE);
                userdatastore.edit().clear().apply();

                Intent i = new Intent(getActivity(), SendOTP.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), About.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), Feedback.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

        addressbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), Addressbook.class);
                i.putExtra("origin", "profile");
                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });



        /*DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Userinfo ownerinfo = dataSnapshot.getValue(Userinfo.class);
                name.setText(ownerinfo.getName());
                email.setText(ownerinfo.getEmail());
                mobileno.setText(ownerinfo.getMobileno());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        return view;
    }


}
