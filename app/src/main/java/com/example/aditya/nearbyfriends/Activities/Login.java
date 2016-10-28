package com.example.aditya.nearbyfriends.Activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.DataFetcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends Fragment {

    @BindView(R.id.error) TextView error;
    @BindView(R.id.password) TextInputEditText pass;
    @BindView(R.id.email) TextInputEditText email;
    @BindView(R.id.title) TextView title;
    private DataFetcher dataFetcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this,view);
        title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/DroidSans.ttf"));
        dataFetcher=DataFetcher.getInstance();
        return view;
    }

    @OnClick(R.id.submit)
    public void login() {
        PrefUtils prefUtils = new PrefUtils(getContext());
        if (!prefUtils.isUsernameSet()){
            String emailid = email.getText().toString();
            String password = pass.getText().toString();
            if (!emailid.equals("") && !password.equals("")) {
                error.setVisibility(View.INVISIBLE);
                if (isInternetAvailable()) {
                    dataFetcher.signin(emailid, password, getContext());
                    email.setText("");pass.setText("");
                }
            } else {
                error.setText("Please enter all Fields");
                error.setVisibility(View.VISIBLE);
            }
        }
        else{
            SignUp su=(SignUp)getContext();
            su.alreadySignedIn();
        }
    }

    public boolean isInternetAvailable(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }
}
