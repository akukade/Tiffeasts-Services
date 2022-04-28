
package com.example.bookmytiffin;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class Messagecustomadapter extends RecyclerView.Adapter<Messagecustomadapter.MyViewHolder> {


    private ArrayList<Messageinfo> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView messagetext,submsg;
        CardView cv;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.messagetext = itemView.findViewById(R.id.messagetext);
            this.submsg = itemView.findViewById(R.id.submsg);
        }
    }

    public Messagecustomadapter(ArrayList<Messageinfo> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagecardview, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int lpos) {

        Messageinfo msginfo = dataSet.get(lpos);
        holder.messagetext.setText(msginfo.getMsg());

        if(msginfo.getOrderstatus().equals("none"))
            holder.submsg.setText("Accept or Reject Order");
        else if(msginfo.getOrderstatus().equals("accepted"))
            holder.submsg.setText("Enter Order Password");
        else if(msginfo.getOrderstatus().equals("delivered"))
            holder.submsg.setText("Order Delivered");
        else if(msginfo.getOrderstatus().equals("rejected"))
            holder.submsg.setText("Order Rejected");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = MessageFragment.msgview.getChildAdapterPosition(view);
                Intent i = new Intent(view.getContext(), Orderotp.class);
                i.putExtra("position",pos);
                view.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
