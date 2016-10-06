package com.example.aditya.nearbyfriends.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.widget.Button;
import android.widget.Toast;

import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUp extends AppCompatActivity {

    @BindView(R.id.username) TextInputEditText uname;
    PrefUtils pref;

    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET};
    private int REQUEST_PERMISSIONS_KEY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        pref=new PrefUtils(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_KEY);
        }
        if(pref.isUsernameSet()){
            startActivity(new Intent(this,MainActivity.class));
        }
        ButterKnife.bind(this);
    }

    @OnClick(R.id.submit)
    public void submitClick(){
        if(!uname.getText().equals("")) {
            pref.setUsername(uname.getText().toString());
            uname.setEnabled(false);
            changeActivity();
        }
        else{
            Toast.makeText(getApplicationContext(),"Enter a Username",Toast.LENGTH_SHORT).show();
        }
    }

    public void changeActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
