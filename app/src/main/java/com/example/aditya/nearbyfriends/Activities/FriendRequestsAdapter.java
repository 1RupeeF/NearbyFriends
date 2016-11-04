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

public class FriendRequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static ArrayList<HashMap<String,String>> requests;
    static FriendDB fdb;
    static Context context;
    static DataFetcher dataFetcher;
    static PrefUtils prefUtils;
    static int itemType;

    public static class ViewHolder0 extends RecyclerView.ViewHolder{
        @BindView(R.id.name) TextView name;
        @BindView(R.id.email) TextView email;
        public ViewHolder0(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.reject)
        public void reject(){
            HashMap<String,String> hash=requests.get(getAdapterPosition());
            dataFetcher.reject(prefUtils.getUID()+"",hash.get("uid"),context);
        }
        @OnClick(R.id.accept)
        public void acceptRequest(){
            HashMap<String,String> hash=requests.get(getAdapterPosition());
            dataFetcher.acceptRequest(prefUtils.getUID()+"",hash.get("uid"),hash.get("name"),context);
        }
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder{
        @BindView(R.id.name) TextView name;
        @BindView(R.id.email) TextView email;
        public ViewHolder2(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.remove)
        public void remove(){
            HashMap<String,String> hash=requests.get(getAdapterPosition()%3);
            dataFetcher.removeTracker(prefUtils.getUID()+"",hash.get("uid"),context);
        }
    }

    public FriendRequestsAdapter(ArrayList<HashMap<String,String>> requests,Context context,int type) {
        this.context=context;
        dataFetcher=DataFetcher.getInstance();
        prefUtils=new PrefUtils(context);
        this.requests=requests;
        fdb=new FriendDB(context,null,null,1);
        itemType=type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==1){
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.request_grid_item,parent,false);
            FriendRequestsAdapter.ViewHolder0 vh=new FriendRequestsAdapter.ViewHolder0(v);
            return vh;
        }
        else {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.tracker_grid_item,parent,false);
            FriendRequestsAdapter.ViewHolder2 vh=new FriendRequestsAdapter.ViewHolder2(v);
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (getItemViewType(position) == 1) {
            FriendRequestsAdapter.ViewHolder0 holder=(FriendRequestsAdapter.ViewHolder0) holder1;
            holder.name.setText(requests.get(position).get("name") + "\n ( " +
                    requests.get(position).get("uid") + " ) ");
            holder.email.setText(requests.get(position).get("email"));
        }
        else {
            FriendRequestsAdapter.ViewHolder2 holder=(FriendRequestsAdapter.ViewHolder2) holder1;
            holder.name.setText(requests.get(position%3).get("name") + "\n ( " +
                    requests.get(position%3).get("uid") + " ) ");
            holder.email.setText(requests.get(position%3).get("email"));
        }
    }

    @Override
    public int getItemCount() {
        if(itemType==1) {
            return requests.size();
        }
        else{
            if(requests.size()<3) return requests.size();
            return Integer.MAX_VALUE;
        }
    }



    @Override
    public int getItemViewType(int position) {
        return itemType;
    }
}
