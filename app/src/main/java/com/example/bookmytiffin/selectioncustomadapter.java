package com.example.bookmytiffin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class selectioncustomadapter extends ArrayAdapter<Dataforselection> {
    private Context mContext;
    private List<Dataforselection> List;
    TextView counter,item,price;
    Button inc,dec;

    public selectioncustomadapter(@NonNull Context mContext, List<Dataforselection> List) {
        super(mContext,0,List);
        this.mContext = mContext;
        this.List = List;
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Dataforselection getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.orderlistview, parent, false);

        final Dataforselection currlist = List.get(position);
        item = listItem.findViewById(R.id.litem);
        item.setText(currlist.getItem());

        price = listItem.findViewById(R.id.lprice);
        price.setText(currlist.getPrice());

        counter = listItem.findViewById(R.id.lcounter);
        counter.setText(Integer.toString(currlist.getCounter()));

        inc = listItem.findViewById(R.id.increment);
        dec = listItem.findViewById(R.id.decrement);

        if(Selection.nextpage == 1)
        {
            inc.setVisibility(View.INVISIBLE);
            dec.setVisibility(View.INVISIBLE);
        }
        else
        {
            inc.setVisibility(View.VISIBLE);
            dec.setVisibility(View.VISIBLE);
        }

        inc.setTag(position);
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = Integer.parseInt(view.getTag().toString());
                int currtotal = Integer.parseInt(Selection.total.getText().toString());
                currtotal += Integer.parseInt(List.get(pos).getPrice());
                Selection.total.setText(Integer.toString(currtotal));
                List.get(pos).inccount();
                Selection.adapter.notifyDataSetChanged();
            }
        });

        dec.setTag(position);
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = Integer.parseInt(view.getTag().toString());
                int currtotal = Integer.parseInt(Selection.total.getText().toString());
                if(List.get(pos).getCounter() !=0 ) {
                    currtotal -= Integer.parseInt(List.get(pos).getPrice());
                    Selection.total.setText(Integer.toString(currtotal));
                    List.get(pos).deccount();
                    Selection.adapter.notifyDataSetChanged();
                }
            }
        });

        return listItem;
    }

}
