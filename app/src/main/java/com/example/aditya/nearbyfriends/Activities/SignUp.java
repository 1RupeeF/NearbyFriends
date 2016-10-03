package com.example.aditya.nearbyfriends.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        pref=new PrefUtils(this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.submit)
    public void submitClick(){
        if(!uname.getText().equals("")) {
            String s=uname.getText().toString();
            pref.setUsername(uname.getText().toString());
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
