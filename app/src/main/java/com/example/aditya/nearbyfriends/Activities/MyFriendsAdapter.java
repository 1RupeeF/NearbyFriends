package com.example.aditya.nearbyfriends.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.FriendDB;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by aditya on 3/10/16.
 */

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.ViewHolder>{

    private static ArrayList<User> friends;
    static FriendDB fdb;
    static Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name) TextView name;
        @BindView(R.id.address) TextView address;
        @BindView(R.id.addtotracklist) Switch tracker;
        @BindView(R.id.remove) ImageButton delete;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tracker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast;
                    String name=friends.get(getAdapterPosition()).getName();
                    if(tracker.isChecked()){
                        fdb.addToTrackList(friends.get(getAdapterPosition()).getName(),true);
                        toast=Toast.makeText(context,name +" added to tracking list.",Toast.LENGTH_SHORT);
                    }
                    else{
                        fdb.addToTrackList(friends.get(getAdapterPosition()).getName(),false);
                        toast=Toast.makeText(context,name +" removed from tracking list.",Toast.LENGTH_SHORT);
                    }
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name=friends.get(getAdapterPosition()).getName();
                    fdb.deleteFriend(name);
                    Toast t=Toast.makeText(context,name +" deleted from friend list. \n Refresh(Swipe down) to update Friends List.",Toast.LENGTH_LONG);
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
        holder.name.setText(friends.get(position).getName());
        holder.address.setText(friends.get(position).getAddress());
        holder.tracker.setChecked(fdb.isInTrackerList(friends.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
