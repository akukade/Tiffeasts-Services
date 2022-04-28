package com.example.bookmytiffin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class Homecustomadapter extends RecyclerView.Adapter<Homecustomadapter.MyViewHolder> {

    private ArrayList<Tiffininfo> dataSet;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvname,tvcuisine,rating,costfor2;
        ImageView homeimage;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.tvname = itemView.findViewById(R.id.name);
            this.tvcuisine = itemView.findViewById(R.id.cuisine);
            this.rating = itemView.findViewById(R.id.ratingstar);
            this.costfor2 = itemView.findViewById(R.id.HomeCostfor2);
            this.homeimage = itemView.findViewById(R.id.homeimage);
        }
    }

    public Homecustomadapter(Context context, ArrayList<Tiffininfo> data) {
        this.context = context;
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homecardview, parent, false);
        return new MyViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int lpos) {

        Tiffininfo temp = dataSet.get(lpos);
        if(temp.getHomeimage() != null && !temp.getHomeimage().isEmpty() )
        {
            holder.homeimage.setScaleType(ImageView.ScaleType.FIT_XY);
            Picasso.with(context).load(temp.getHomeimage()).into(holder.homeimage);
        }
        holder.tvname.setText(temp.getName());
        holder.tvcuisine.setText(temp.getCuisine());

        holder.costfor2.setText("Rs 100 for 2");

        holder.rating.setText(temp.getRating() + "\u2605");

        if(dataSet.get(lpos).getRating() == -1)
          holder.rating.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(view -> {

            int pos = HomeFragment.recyclerView.getChildAdapterPosition(view);
            Intent i =new Intent(view.getContext(), Uploadtiffin.class);
            i.putExtra("position", pos);
            i.putExtra("selected",HomeFragment.selected);
            view.getContext().startActivity(i);
        });


    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
