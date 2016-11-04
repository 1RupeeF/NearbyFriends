package com.example.aditya.nearbyfriends.Activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aditya.nearbyfriends.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditya on 3/11/16.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<HashMap<String,String>> al;

    public static class ViewHolder0  extends RecyclerView.ViewHolder{
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.email) TextView email;
        public ViewHolder0(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public SearchAdapter(ArrayList<HashMap<String, String>> al) {
        this.al = al;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item,parent,false);
        RecyclerView.ViewHolder vh=new SearchAdapter.ViewHolder0(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        SearchAdapter.ViewHolder0 holder=(SearchAdapter.ViewHolder0) holder1;
        holder.name.setText(al.get(position).get("name") + " ( " +
                al.get(position).get("uid") + " ) ");
        holder.email.setText(al.get(position).get("email"));
    }

    @Override
    public int getItemCount() {
        return al.size();
    }
}
