package com.example.aditya.nearbyfriends.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Welcome extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private PrefUtils prefUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        LocationManager lm=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            turnOnGps();
        prefUtils=new PrefUtils(getApplicationContext());
        if(!prefUtils.isFirstTime()){
            startActivity(new Intent(this,MainActivity.class));
        }
        ButterKnife.bind(this);
    }

    @OnClick(R.id.next)
    public void onNext(){
        prefUtils.firstTimeDone();
        if(prefUtils.isUsernameSet()){
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            startActivity(new Intent(this,SignUp.class));
        }

    }
    public void turnOnGps(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
