package com.example.aditya.nearbyfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.design.widget.TextInputEditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {
    PrefUtils pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        pref=new PrefUtils(this);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText uname=(TextInputEditText)findViewById(R.id.username);
                if(!uname.getText().equals("")) {
                    String s=uname.getText().toString();
                    pref.setUsername(uname.getText().toString());
                    changeActivity();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Enter a Username",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void changeActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
