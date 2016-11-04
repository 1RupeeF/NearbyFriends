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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

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

    @OnClick(R.id.forgot)
    public void forgot(){
        startActivity(new Intent(getContext(),PasswordRecovery.class));
    }

    @OnClick(R.id.submit)
    public void login() {
        PrefUtils prefUtils = new PrefUtils(getContext());
        if (!prefUtils.isUsernameSet()){
            String emailid = email.getText().toString();
            String password = pass.getText().toString();
            if (isvalidEmail(email.getText().toString())) {
                if(!password.equals("")) {
                    if (isInternetAvailable()) {
                        dataFetcher.signin(emailid, password, getContext());
                        email.setText("");
                        pass.setText("");
                    } else {
                        Toast.makeText(getContext(), "Seems you are offline. Get Online to Continue!!", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    pass.setError("Password cannot be empty");
                }
            } else {
                email.setError("Invalid Email");
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


    public boolean isvalidEmail(String e){
        return !e.equals("") && Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }
}
