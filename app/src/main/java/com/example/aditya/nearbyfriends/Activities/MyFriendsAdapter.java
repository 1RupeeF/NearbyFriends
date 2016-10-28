package com.example.aditya.nearbyfriends.Activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.FriendDB;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.ViewHolder>{

    private static ArrayList<User> friends;
    static FriendDB fdb;
    static Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name) TextView name;
        @BindView(R.id.address) TextView address;
        @BindView(R.id.addtotracklist) Switch tracker;
        @BindView(R.id.coords) TextView coords;
        @BindView(R.id.lastupdate) TextView lastupdate;
        @BindView(R.id.remove) ImageButton delete;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tracker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast;
                    String uid=friends.get(getAdapterPosition()).getUid();
                    if(tracker.isChecked()){
                        fdb.addToTrackList(friends.get(getAdapterPosition()).getUid(),true);
                        toast=Toast.makeText(context,uid +" added to tracking list.", Toast.LENGTH_SHORT);
                    }
                    else{
                        fdb.addToTrackList(friends.get(getAdapterPosition()).getUid(),false);
                        toast=Toast.makeText(context,uid +" removed from tracking list.",Toast.LENGTH_SHORT);
                    }
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid=friends.get(getAdapterPosition()).getUid();
                    fdb.deleteFriend(uid);
                    Toast t=Toast.makeText(context,uid +" deleted from friend list. \n Refresh(Swipe down) to update Friends List.",Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER,0,0);
                    t.show();
                }
            });
        }
    }

    public MyFriendsAdapter(ArrayList<User> friends, Context context) {
        this.friends=friends;
        this.context=context;
        fdb=new FriendDB(context,null,null,1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user=friends.get(position);
        if(user.getName()==null){
            holder.name.setText(user.getUid());
            holder.address.setText("Friend Request not Accepted");
            holder.tracker.setChecked(fdb.isInTrackerList(user.getUid()));
        }
        else if(user.getLat() == null && user.getLon() == null){
            holder.name.setText(user.getName() + "(" + user.getUid() + ")");
            holder.address.setText("?(User not updated his location!!)");
            holder.tracker.setChecked(fdb.isInTrackerList(user.getUid()));
            holder.coords.setText("?" + " , " + "?");
            holder.lastupdate.setText(user.getLastupdate());
        }
        else if(user.getLat() != null && user.getLon() != null && user.getAddress()==null){
            holder.name.setText(user.getName() + "(" + user.getUid() + ")");
            holder.address.setText("?(Unknown Address!!)");
            holder.tracker.setChecked(fdb.isInTrackerList(user.getUid()));
            holder.coords.setText(user.getLat() + "," + user.getLon());
            holder.lastupdate.setText(user.getLastupdate());
        }
        else{
            holder.name.setText(user.getName() + "(" + user.getUid() + ")");
            holder.address.setText(user.getAddress()+", "+user.getCity());
            holder.tracker.setChecked(fdb.isInTrackerList(user.getUid()));
            holder.coords.setText(user.getLat() + "," + user.getLon());
            holder.lastupdate.setText(user.getLastupdate());
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
