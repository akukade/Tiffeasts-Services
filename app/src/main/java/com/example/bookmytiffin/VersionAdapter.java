package com.example.bookmytiffin;

import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import okhttp3.internal.Version;

public class VersionAdapter extends RecyclerView.Adapter<VersionAdapter.VersionVH> {


    List<Versions> versionsList;

    public VersionAdapter(List<Versions> versionsList) {
        this.versionsList = versionsList;
    }

    @NonNull
    @Override
    public VersionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent, false);
        return new VersionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VersionVH holder, int position) {

        Versions versions = versionsList.get(position);
        holder.question.setText(versions.getQuestion());
        holder.answer.setText(versions.getAnswer());

        boolean isExpandable = versionsList.get(position).isExpandable();
        //holder.relativeLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

        if(isExpandable != true){
            holder.relativeLayout.setVisibility(View.GONE);
        }

        else {
            holder.relativeLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return versionsList.size();
    }

    public class VersionVH extends RecyclerView.ViewHolder {

        TextView question, answer;
        LinearLayout linearLayout;
        RelativeLayout relativeLayout;

        public VersionVH(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);

            linearLayout = itemView.findViewById(R.id.faqlinear);
            relativeLayout = itemView.findViewById(R.id.expandablerelative);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Versions versions= versionsList.get(getAdapterPosition());

                    versions.setExpandable(!versions.isExpandable());

                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }
}

