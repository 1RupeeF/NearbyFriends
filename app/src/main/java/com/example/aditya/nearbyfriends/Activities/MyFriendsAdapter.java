package com.example.aditya.nearbyfriends.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.FriendDB;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            /*final Context context1=context;
            final ArrayList<User> fr=friends;
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context1);
                    builder.setTitle("Remove "+name.getText().toString()+" from your Friends List?");
                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fdb.deleteFriend(name.getText().toString());
                            Toast.makeText(context1,name.getText().toString()+" removed",Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog ad=builder.create();
                    ad.show();
                }
            });*/
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
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
