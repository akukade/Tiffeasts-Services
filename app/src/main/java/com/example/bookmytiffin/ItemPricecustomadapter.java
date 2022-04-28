package com.example.bookmytiffin;
import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemPricecustomadapter extends ArrayAdapter<String> {

    Activity context;
    ArrayList<String> item,price;

    public ItemPricecustomadapter(Activity context, ArrayList<String> item, ArrayList<String> price) {
        super(context, R.layout.itempricerowlayout, item);
        // TODO Auto-generated constructor stub
        this.item = item;
        this.price = price;
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent) {

        View rowView = view;
        if (rowView == null)
            rowView = LayoutInflater.from(context).inflate(R.layout.itempricerowlayout, parent, false);

        TextView itemtext = rowView.findViewById(R.id.tempitem);
        TextView pricetext = rowView.findViewById(R.id.tempprice);

        itemtext.setText(item.get(position));
        pricetext.setText("\u20B9 " + price.get(position));


        return rowView;

    };
}