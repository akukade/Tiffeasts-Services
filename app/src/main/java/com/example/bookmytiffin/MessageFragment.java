package com.example.bookmytiffin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessageFragment extends Fragment {

    static RecyclerView msgview;
    static ArrayList<Messageinfo>msg_list;
    ArrayList<String>customerid_list;
    ArrayList<String>password_list;
    Messagecustomadapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ImageView emtImage;
    TextView emtText;
    ProgressBar pro;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        msgview = view.findViewById(R.id.messageview);
        msg_list = new ArrayList<Messageinfo>();
        emtImage = view.findViewById(R.id.emtImage);
        emtText = view.findViewById(R.id.emtText);
        pro = view.findViewById(R.id.pro);
        pro.setVisibility(View.VISIBLE);

        layoutManager = new LinearLayoutManager(getActivity());
        msgview.setLayoutManager(layoutManager);
        msgview.setItemAnimator(new DefaultItemAnimator());

        adapter = new Messagecustomadapter(msg_list);
        msgview.setAdapter(adapter);

        emtText.setVisibility(View.INVISIBLE);
        emtImage.setVisibility(View.INVISIBLE);

        FirebaseAuth fauth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages").child(fauth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Messageinfo messageinfo = postSnapshot.getValue(Messageinfo.class);
                    messageinfo.setOrderid(postSnapshot.getKey());
                    msg_list.add(messageinfo);
                }
                Collections.reverse(msg_list);
                if(msg_list.isEmpty()){
                    emtText.setVisibility(View.VISIBLE);
                    emtImage.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                pro.setVisibility(View.INVISIBLE);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
