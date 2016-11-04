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

import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.DataFetcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Register extends Fragment {

    @BindView(R.id.username) TextInputEditText uname;
    @BindView(R.id.password) TextInputEditText pass;
    @BindView(R.id.email) TextInputEditText email;
    @BindView(R.id.error) TextView error;
    @BindView(R.id.title) TextView title;
    private DataFetcher dataFetcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this,view);
        title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/DroidSans.ttf"));
        dataFetcher=DataFetcher.getInstance();
        return view;
    }

    @OnClick(R.id.submit)
    public void submitClick(){
        PrefUtils prefUtils = new PrefUtils(getContext());
        if (!prefUtils.isUsernameSet()) {
            String usernmae = uname.getText().toString();
            String password = pass.getText().toString();
            String emailid = email.getText().toString();
            if (isvalidEmail(emailid)) {
                if(!password.equals("") ) {
                    if (uname.getText().toString().length() >= 4) {
                        error.setVisibility(View.INVISIBLE);
                        if (isInternetAvailable()) {
                            dataFetcher.register(emailid, password, usernmae, getContext());
                            email.setText("");
                            pass.setText("");
                            uname.setText("");
                        } else {
                            Toast.makeText(getContext(), "Seems you are offline. Get Online to Continue!!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        uname.setError("Username atleast 4 characters");
                    }
                }
                else {
                    pass.setError("Password cannot be empty");
                }
            } else {
                email.setError("InValid email");
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
