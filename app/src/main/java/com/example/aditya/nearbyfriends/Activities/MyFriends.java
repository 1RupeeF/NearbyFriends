package com.example.aditya.nearbyfriends.Activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.aditya.nearbyfriends.HttpRequest;
import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.Pojos.FriendRequest;
import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.DataFetcher;
import com.example.aditya.nearbyfriends.db.FriendDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MyFriends extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView) RecyclerView rv;
    @BindView(R.id.recyclerView0) RecyclerView rv0;
    @BindView(R.id.activity_my_friends) SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.minmax) ImageButton minmax;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fdb = new FriendDB(getApplicationContext(), null, null, 1);
        rv.setHasFixedSize(true);
        rv0.setHasFixedSize(true);
        prefUtils=new PrefUtils(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv0.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        refresh();
        rv0setAdapter();
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

    @OnClick(R.id.minmax)
    public void minMaxClicked(){
        if(rv0.getVisibility()==View.VISIBLE){
            minmax.setImageDrawable(getResources().getDrawable(R.drawable.arrows_down));
            rv0.setVisibility(View.GONE);
        }
        else{
            minmax.setImageDrawable(getResources().getDrawable(R.drawable.arrows_up));
            rv0.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.refresh)
    public void rv0setAdapter(){
        final RequestBody ruid=RequestBody.create(MediaType.parse("text/plain"),prefUtils.getUID()+"");
        final HttpRequest.MainInterface mainInterface= HttpRequest.retrofit.create(HttpRequest.MainInterface.class);
        Observable.create(new Observable.OnSubscribe<FriendRequest>() {
            @Override
            public void call(final Subscriber<? super FriendRequest> subscriber) {
                Call<FriendRequest> responseCall=mainInterface.getTrackers(ruid);
                responseCall.enqueue(new Callback<FriendRequest>() {
                    @Override
                    public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {
                        if(response.body().getStatus().equals("ok")){
                            subscriber.onNext(response.body());
                        }
                    }
                    @Override
                    public void onFailure(Call<FriendRequest> call, Throwable t) {}
                });
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FriendRequest>() {
                    @Override
                    public void call(FriendRequest friendRequest) {
                        ArrayList<HashMap<String,String>> al=friendRequest.getList();
                        if(al.size()!=0) {
                            findViewById(R.id.trackingyoutext).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.trackingyoutext)).setText("\t             Friends Tracking You");
                            RecyclerView.Adapter adapter = new FriendRequestsAdapter(al, getApplicationContext(), 0);
                            rv0.setAdapter(adapter);
                            minmax.setVisibility(View.VISIBLE);
                            rv0.setVisibility(View.VISIBLE);
                        }
                        else {
                            ((TextView)findViewById(R.id.trackingyoutext)).setText("\t          Unable to load Trackers");
                            minmax.setVisibility(View.GONE);
                            rv0.setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.trackingyoutext)).setText("No one Tracking You");
                        }
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.freinds_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.searchbar:
                startActivity(new Intent(this,SearchActivity.class));
                break;
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return true;
    }
}
