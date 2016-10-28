package com.example.aditya.nearbyfriends.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Welcome extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private PrefUtils prefUtils;
    @BindView(R.id.appname) TextView appname;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.next) Button next;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
    private int REQUEST_PERMISSIONS_KEY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        prefUtils=new PrefUtils(getApplicationContext());
        ButterKnife.bind(this);
        if(!prefUtils.isFirstTime()){ startActivity(new Intent(this, MainActivity.class)); }
        appname.setText("Welcome\n to \nFriends Nearby");
        appname.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/birdmanbold.ttf"));
        title.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/birdmanbold.ttf"));
        next.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/birdmanbold.ttf"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_KEY);
        }

    }

    @OnClick(R.id.next)
    public void onNext(){
        prefUtils.firstTimeDone();
        startActivity(new Intent(this, SignUp.class));
    }
}
