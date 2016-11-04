package com.example.aditya.nearbyfriends.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.aditya.nearbyfriends.HttpRequest;
import com.example.aditya.nearbyfriends.Pojos.FriendRequest;
import com.example.aditya.nearbyfriends.R;


import java.util.ArrayList;
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

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.input) EditText input;
    @BindView(R.id.results) RecyclerView recyclerView;
    @BindView(R.id.activity_search) LinearLayout linearlayout;
    HttpRequest.MainInterface mainInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        mainInterface= HttpRequest.retrofit.create(HttpRequest.MainInterface.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @OnClick(R.id.go)
    public void search(){
        String pattern=input.getText().toString();
        if(pattern.length()>=4) {
            final ProgressDialog dialog=ProgressDialog.show(SearchActivity.this,"Search","Searching for "+pattern,true,false);
            final RequestBody rpattern = RequestBody.create(MediaType.parse("text/plain"), pattern);
            Observable.create(new Observable.OnSubscribe<FriendRequest>() {
                @Override
                public void call(final Subscriber<? super FriendRequest> subscriber) {
                    Call<FriendRequest> rssponseCall = mainInterface.search(rpattern);
                    rssponseCall.enqueue(new Callback<FriendRequest>() {
                        @Override
                        public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {
                            if (response.body().getStatus().equals("ok")) {
                                subscriber.onNext(response.body());
                            }
                            else dialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<FriendRequest> call, Throwable t) {
                        }
                    });
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<FriendRequest>() {
                        @Override
                        public void call(FriendRequest friendRequest) {
                            ArrayList<HashMap<String, String>> al = friendRequest.getList();
                            if(al.size()!=0) {
                                RecyclerView.Adapter adapter = new SearchAdapter(al);
                                recyclerView.setAdapter(adapter);
                                dialog.dismiss();
                            }
                            else{
                                dialog.dismiss();
                                Toast toast=Toast.makeText(getApplicationContext(),"No results",Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }
                        }
                    });
        }
        else{
            input.setError("Pattern must be of length 4 or greater");
        }

    }





}
