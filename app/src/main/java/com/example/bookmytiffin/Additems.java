package com.example.bookmytiffin;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Additems extends AppCompatDialogFragment {

    private DialogListener listener;
    EditText item, price;
    speciallistview itempricelistview;
    Button add;
    ArrayList<String> itemlist, pricelist;

    ArrayAdapter<String> itempriceadapter;

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_additems, null);

        item = view.findViewById(R.id.item);
        price = view.findViewById(R.id.price);
        add = view.findViewById(R.id.additem);
        itempricelistview = view.findViewById(R.id.itempricelistview);

        itemlist = new ArrayList<>();
        pricelist = new ArrayList<>();


        itempriceadapter = new ItemPricecustomadapter(getActivity(),itemlist,pricelist);
        itempricelistview.setAdapter(itempriceadapter);

        add.setOnClickListener(v -> {
            String resultitem = item.getText().toString().trim();
            String resultprice = price.getText().toString().trim();

            if(TextUtils.isEmpty(resultitem) || TextUtils.isEmpty(resultprice)) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                return;
            }

            if(Integer.parseInt(resultprice) <= 0) {
                Toast.makeText(getContext(), "Price cannot be less then one", Toast.LENGTH_LONG).show();
                return;
            }


            itemlist.add(resultitem);
            item.setText("");
            item.setHint("Item Name");

            pricelist.add(resultprice);
            price.setText("");
            price.setHint("Item Price (Rs)");


            itempriceadapter.notifyDataSetChanged();
            item.requestFocus();

        });

        builder.setView(view)
                .setTitle("Add Items")
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setPositiveButton("Ok", (dialog, which) -> listener.applyTexts(itemlist, pricelist));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            try {
                listener = (DialogListener) context;
            }
            catch (ClassCastException e){
                throw new ClassCastException(context.toString() + "must implement DialogListener");
            }
        }
}

interface DialogListener{
    void applyTexts(ArrayList<String> ilist, ArrayList<String> plist);
}




