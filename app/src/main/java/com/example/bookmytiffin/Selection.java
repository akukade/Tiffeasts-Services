package com.example.bookmytiffin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.shakebugs.shake.Shake;

import java.util.ArrayList;
import java.util.Iterator;

public class Selection extends AppCompatActivity {

    static ArrayList<Dataforselection> selectdata;
    static selectioncustomadapter adapter;
    ListView selectlistview;
    static TextView title,total;
    TextView adtitle,mode,foodtotal,delicharges;
    MaterialEditText address;
    static Spinner delitimeslot;
    String[] items = new String[]{"8 AM : 10 AM", "10 AM : 12 PM", "12 PM : 2 PM", "2 PM : 4 PM", "4 PM : 6 PM", "6 PM : 8 PM", "8 PM : 9 PM"};


    Button confirm;
    static int nextpage=0;
    RelativeLayout nextPLayout,foodLayout,deliLayout;
    RadioGroup DeliorTakeway;
    Tiffininfo selectedtif = new Tiffininfo();
    static String selectedmode = "Delivery";
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Shake.start(getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        title = findViewById(R.id.selecttitle);
        total = findViewById(R.id.total);
        confirm = findViewById(R.id.confirm);
        adtitle = findViewById(R.id.adtitle);
        address = findViewById(R.id.deliaddress);
        delitimeslot = findViewById(R.id.delitimeslot);
        selectlistview = findViewById(R.id.selectlistview);
        nextPLayout = findViewById(R.id.nextpagelayout);
        mode = findViewById(R.id.mode);
        DeliorTakeway = findViewById(R.id.deli_take_radio);

        foodtotal = findViewById(R.id.foodtotal);
        delicharges = findViewById(R.id.delicharges);
        foodLayout = findViewById(R.id.foodLayout);
        deliLayout = findViewById(R.id.deliLayout);


        ArrayAdapter<String> timeslotadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        delitimeslot.setAdapter(timeslotadapter);

        selectdata = new ArrayList<>();
        adapter = new selectioncustomadapter(this,selectdata);
        selectlistview.setAdapter(adapter);

        for(int i=0;i<Uploadtiffin.itemlist.size();i++)
        {
            selectdata.add(new Dataforselection(Uploadtiffin.itemlist.get(i),Uploadtiffin.pricelist.get(i),0));
        }
        adapter.notifyDataSetChanged();

        selectedtif = Uploadtiffin.currtif;

        confirm.setOnClickListener(view -> {
            if (nextpage == 0) {
                //Removing non selected food items from list
                int sel = 0;
                for(int i=0;i<selectdata.size();i++) {
                    if (selectdata.get(i).getCounter() > 0) {
                        sel = 1;
                        break;
                    }
                }
                if(sel == 1)
                {
                    Iterator<Dataforselection> itr = selectdata.iterator();
                    while(itr.hasNext())
                    {
                        if(itr.next().getCounter() == 0)
                            itr.remove();
                    }
                }
                else
                {
                    Toast.makeText(Selection.this,"Select atleast one food item",Toast.LENGTH_SHORT).show();
                    return;
                }

                adapter.notifyDataSetChanged();

                nextPLayout.setVisibility(View.VISIBLE);
                foodLayout.setVisibility(View.VISIBLE);
                deliLayout.setVisibility(View.VISIBLE);

                Current_Location curr_loc = (Current_Location) getApplication();
                address.setText(curr_loc.getCurr_address());
                title.setText("Order Details");
                confirm.setText("Proceed to Payment");

                foodtotal.setText(total.getText().toString());

                if(selectedtif.isTakeaway() && selectedtif.getDelivery() >= selectedtif.getDistance())
                {
                    selectedmode = "Delivery";
                    DeliorTakeway.check(R.id.deliradio);
                }
                else if( selectedtif.getDelivery() >= selectedtif.getDistance())
                {
                    selectedmode = "Delivery";
                    DeliorTakeway.setVisibility(View.GONE);
                    mode.setText("Mode : Delivery");
                }
                else if(selectedtif.isTakeaway())
                {
                    selectedmode = "Takeaway";
                    DeliorTakeway.setVisibility(View.GONE);
                    mode.setText("Mode : Takeaway");
                    address.setText(selectedtif.getAddress());
                }
                calculateCharges();
                nextpage = 1;
            }
            else if(nextpage == 1)
            {
                Intent i = new Intent(Selection.this, Payment.class);
                i.putExtra("amount",total.getText().toString());
                startActivity(i);
            }
        });

        DeliorTakeway.setOnCheckedChangeListener((radioGroup, i) -> {
            if(i == R.id.deliradio)
            {
                selectedmode = "Delivery";
                Current_Location curr_loc = (Current_Location) getApplication();
                address.setText(curr_loc.getCurr_address());
            }
            else if(i ==R.id.takeradio)
            {
                selectedmode = "Takeaway";
                address.setText(selectedtif.getAddress());
            }
            calculateCharges();
        });
    }

    public void calculateCharges()
    {
        delicharges.setText("0");
        total.setText(foodtotal.getText().toString());
        if(selectedmode.equals("Delivery"))
        {


            double delitemp;
            /*if(selectedtif.getDistance() < 5){
                delitemp = 25;
                delicharges.setText(Double.toString(delitemp));
            }
            else{
                double extra;
                double cost;
                extra = (selectedtif.getDistance()-3);
                cost = 25 + (extra *10);
                delicharges.setText(Double.toString(cost));
            }*/
            delitemp = selectedtif.getDistance() * selectedtif.getCharge();
            delitemp = Math.round(delitemp * 100.0) / 100.0;
            delicharges.setText(Double.toString(delitemp));
            delitemp += Integer.parseInt(foodtotal.getText().toString());
            total.setText(Double.toString(delitemp));
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed() {
        if (nextpage == 1) {
            selectdata.clear();
            for(int i=0;i<Uploadtiffin.itemlist.size();i++)
            {
                selectdata.add(new Dataforselection(Uploadtiffin.itemlist.get(i),Uploadtiffin.pricelist.get(i),0));
            }

            adapter.notifyDataSetChanged();
            total.setText("0");
            nextPLayout.setVisibility(View.GONE);
            foodLayout.setVisibility(View.GONE);
            deliLayout.setVisibility(View.GONE);
            title.setText("Select Items");
            confirm.setText("Confirm Order");
            nextpage = 0;
        }
        else if(nextpage == 0)
            super.onBackPressed();
    }
}