package com.example.aditya.nearbyfriends;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{
    private GoogleMap gMap;
    private int REQUEST_PERMISSIONS_KEY=1;
    private LocationManager lm;
    private String[] permissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_PERMISSIONS_KEY);
        }
        SupportMapFragment smf=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        smf.getMapAsync(this);
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
}
