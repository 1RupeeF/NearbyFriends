package com.example.aditya.nearbyfriends.Activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;


import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.DataFetcher;
import com.example.aditya.nearbyfriends.db.FriendDB;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyFriends extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.recyclerView) RecyclerView rv;
    @BindView(R.id.activity_my_friends) SwipeRefreshLayout swipeToRefresh;
    RecyclerView.Adapter radapter;
    private PrefUtils prefUtils;
    DataFetcher dataFetcher;
    FriendDB fdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        ButterKnife.bind(this);
        dataFetcher=DataFetcher.getInstance();
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        fdb = new FriendDB(getApplicationContext(), null, null, 1);
        rv.setHasFixedSize(true);
        prefUtils=new PrefUtils(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        refresh();
        swipeToRefresh.setOnRefreshListener(this);
        swipeToRefresh.setColorSchemeColors(Color.BLUE);
        swipeToRefresh.setProgressBackgroundColorSchemeColor(Color.DKGRAY);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    public void refresh(){
        swipeToRefresh.setRefreshing(true);
        ArrayList<User> friends = fdb.getAllFriends();
        Collections.reverse(friends);
        radapter = new MyFriendsAdapter(friends,getApplicationContext());
        rv.setAdapter(radapter);
        swipeToRefresh.setRefreshing(false);
    }

    @OnClick(R.id.fab)
    public void addFriend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Friends UID (ASK him/her)");
        final EditText name = new EditText(this);
        name.setTextColor(Color.BLACK);
        builder.setView(name);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dataFetcher.addFriend(prefUtils.getUID()+"",name.getText().toString(),getApplicationContext());
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
}
