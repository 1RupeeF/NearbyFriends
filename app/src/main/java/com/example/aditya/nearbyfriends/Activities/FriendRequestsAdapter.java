package com.example.aditya.nearbyfriends.Activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.DataFetcher;
import com.example.aditya.nearbyfriends.db.FriendDB;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by aditya on 25/10/16.
 */

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder>{

    public static ArrayList<HashMap<String,String>> requests;
    static FriendDB fdb;
    static Context context;
    static DataFetcher dataFetcher;
    static PrefUtils prefUtils;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name) TextView name;
        @BindView(R.id.email) TextView email;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.accept)
        public void acceptRequest(){
            HashMap<String,String> hash=requests.get(getAdapterPosition());
            dataFetcher.acceptRequest(prefUtils.getUID()+"",hash.get("uid"),hash.get("name"),context);
        }
    }


    public FriendRequestsAdapter(ArrayList<HashMap<String,String>> requests,Context context) {
        this.context=context;
        dataFetcher=DataFetcher.getInstance();
        prefUtils=new PrefUtils(context);
        this.requests=requests;
        fdb=new FriendDB(context,null,null,1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.request_grid_item,parent,false);
        FriendRequestsAdapter.ViewHolder vh=new FriendRequestsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(requests.get(position).get("name")+" ( "+
                requests.get(position).get("uid")+ " ) ");
        holder.email.setText(requests.get(position).get("email"));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
