package com.example.aditya.nearbyfriends;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{
    private GoogleMap gMap;
    private int REQUEST_PERMISSIONS_KEY=1;
    private int PLACE_PICKER_REQUEST_CODE=123;
    private LocationManager lm;
    private PrefUtils prefUtils;
    private String[] permissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefUtils=new PrefUtils(this);
        if(!prefUtils.isUsernameSet()){
            startActivity(new Intent(this,SignUp.class));
        }
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_PERMISSIONS_KEY);
        }
        SupportMapFragment smf=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        smf.getMapAsync(this);
        findViewById(R.id.auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetCurrentLocation();
            }
        });
        findViewById(R.id.manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetManualLocation();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            gMap.setMyLocationEnabled(true);
            lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            CameraPosition Mylocation = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();
            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(Mylocation));
        }
        else{

        }
    }

    public void onSetCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            final Geocoder gc=new Geocoder(this, Locale.getDefault());
            final Location location=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Snackbar.make(findViewById(R.id.activity_main),"Your location is set to current location",Snackbar.LENGTH_LONG)
                    .setAction("Address", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                String address=gc.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0).getAddressLine(0);
                                Snackbar.make(findViewById(R.id.activity_main),address,Snackbar.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setActionTextColor(Color.GREEN)
                    .show();
        }
        else{
            Snackbar.make(findViewById(R.id.activity_main),"Failed to get Current Location",Snackbar.LENGTH_LONG)
                    .setAction("Enter Manually", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSetManualLocation();
                        }
                    })
                    .setActionTextColor(Color.BLUE)
                    .show();
        }
    }

    public void onSetManualLocation(){
        try {
            Intent intent= new PlacePicker.IntentBuilder().build(this);
            startActivityForResult(intent,PLACE_PICKER_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        final Intent sdata=data;
        if(requestCode==PLACE_PICKER_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                Snackbar.make(findViewById(R.id.activity_main),"Your new location is set by you",Snackbar.LENGTH_LONG)
                        .setAction("Address", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String address=PlacePicker.getPlace(sdata,getApplicationContext()).getAddress().toString();
                                Snackbar.make(findViewById(R.id.activity_main),address,Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .setActionTextColor(Color.GREEN)
                        .show();
            }
        }
    }
}
