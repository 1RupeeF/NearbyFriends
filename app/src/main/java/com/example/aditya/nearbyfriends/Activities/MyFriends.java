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
import android.widget.Toast;

import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.FriendDB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyFriends extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.recyclerView) RecyclerView rv;
    @BindView(R.id.activity_my_friends) SwipeRefreshLayout swipeToRefresh;
    RecyclerView.Adapter radapter;
    private DatabaseReference dRef;
    private PrefUtils prefUtils;

    FriendDB fdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        ButterKnife.bind(this);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dRef = database.getReference("Users");
        fdb = new FriendDB(getApplicationContext(), null, null, 1);
        rv.setHasFixedSize(true);
        prefUtils=new PrefUtils(this);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        builder.setTitle("Enter Friends Name");
        final EditText name = new EditText(this);
        name.setTextColor(Color.BLACK);
        name.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
        builder.setView(name);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean found = false;
                        String nam=name.getText().toString();
                        if(!nam.equals(prefUtils.getUsername())) {
                            for (DataSnapshot users : dataSnapshot.getChildren()) {
                                if (users.getKey().equals(nam)) {
                                    found = true;
                                    User u = users.getValue(User.class);
                                    u.setName(nam);
                                    fdb.addFriend(u);
                                    break;
                                }
                            }
                            if (!found) {
                                Toast.makeText(getApplicationContext(), "Not such User", Toast.LENGTH_SHORT).show();
                            } else refresh();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
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
