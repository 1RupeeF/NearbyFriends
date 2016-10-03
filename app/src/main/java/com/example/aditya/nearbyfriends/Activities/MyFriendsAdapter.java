package com.example.aditya.nearbyfriends.Activities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditya on 3/10/16.
 */

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.ViewHolder>{

    private ArrayList<User> friends;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name) TextView name;
        @BindView(R.id.address) TextView address;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public MyFriendsAdapter(ArrayList<User> friends) {
        this.friends=friends;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(friends.get(position).getName());
        holder.address.setText(friends.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
