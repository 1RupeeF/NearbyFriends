package com.example.aditya.nearbyfriends.Activities;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.FriendDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.aditya.nearbyfriends.Activities.FriendRequestsAdapter.prefUtils;

public class FriendRequests extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.recyclerView) RecyclerView rv;
    @BindView(R.id.activity_friend_requests) SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.Adapter radapter;
    FriendDB fdb;
    GridLayoutManager glm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        ButterKnife.bind(this);
        fdb=new FriendDB(this,null,null,1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv.setHasFixedSize(true);
        prefUtils=new PrefUtils(this);
        rv.setLayoutManager(new GridLayoutManager(this,2));
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.DKGRAY);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        ArrayList<HashMap<String,String>> requests = fdb.getAllRequests();
        Collections.reverse(requests);
        radapter = new FriendRequestsAdapter(requests,getApplicationContext());
        rv.setAdapter(radapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
