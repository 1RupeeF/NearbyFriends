package com.example.aditya.nearbyfriends;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private final String FIREBASE_URL= "https://nearbyfriends-1475248751089.firebaseio.com/";

    private DatabaseReference dRef;
    @BindView(R.id.activity_main) DrawerLayout mainLayout;
    @BindView(R.id.navView) NavigationView navigationView;
    private GoogleMap gMap;
    private int REQUEST_PERMISSIONS_KEY=1;
    private int PLACE_PICKER_REQUEST_CODE=123;
    private LocationManager lm;
    private final String TAG="firebase";

    private PrefUtils prefUtils;
    private String[] permissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        prefUtils=new PrefUtils(this);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        dRef=database.getReference("Users");
        if(!prefUtils.isUsernameSet()){
            startActivity(new Intent(this,SignUp.class));
        }
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_PERMISSIONS_KEY);
        }
        SupportMapFragment smf=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        smf.getMapAsync(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.SignIn:
                        startActivity(new Intent(getApplicationContext(),SignUp.class));
                        break;
                    case R.id.addFriends:
                        //startActivity(new Intent(getApplicationContext(),AddFre));
                        break;
                    case R.id.search:
                        mainLayout.closeDrawer(Gravity.LEFT);

                        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Search Friend");
                        final EditText friend=new EditText(getApplicationContext());
                        friend.setHint("Friend's Username");
                        friend.setTextColor(Color.BLACK);
                        friend.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
                        builder.setView(friend);
                        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name=friend.getText().toString();
                                //goToPosition();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                            }
                        });
                        builder.show();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
        refreshMarkers();
    }

    @OnClick(R.id.auto)
    public void onSetCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            final Geocoder gc=new Geocoder(this, Locale.getDefault());
            final Location location=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Snackbar.make(mainLayout,"Your location is set to current location",Snackbar.LENGTH_LONG)
                    .setAction("Address", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                String address=gc.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0).getAddressLine(0);
                                String city=gc.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0).getLocality();
                                Snackbar.make(mainLayout,address+", "+city,Snackbar.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setActionTextColor(Color.GREEN)
                    .show();
            try{
                List<Address> add=gc.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                User me=new User();
                me.setAddress(add.get(0).getAddressLine(0));
                me.setCity(add.get(0).getLocality());
                me.setLat(location.getLatitude());
                me.setLon(location.getLongitude());
                dRef.child(prefUtils.getUsername()).setValue(me);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else{
            Snackbar.make(mainLayout,"Failed to get Current Location",Snackbar.LENGTH_LONG)
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

    @OnClick(R.id.manual)
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
                Snackbar.make(mainLayout,"Your new location is set by you",Snackbar.LENGTH_LONG)
                        .setAction("Address", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String address=PlacePicker.getPlace(sdata,getApplicationContext()).getAddress().toString();
                                Snackbar.make(mainLayout,address,Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .setActionTextColor(Color.GREEN)
                        .show();
                final Geocoder gc=new Geocoder(this, Locale.getDefault());
                try{
                    User me=new User();
                    me.setLat(PlacePicker.getPlace(this,data).getLatLng().latitude);
                    me.setLon(PlacePicker.getPlace(this,data).getLatLng().longitude);
                    List<Address> add=gc.getFromLocation(me.getLat(),me.getLon(),1);
                    me.setAddress(PlacePicker.getPlace(this,data).getAddress().toString());
                    me.setCity(add.get(0).getLocality());
                    dRef.child(prefUtils.getUsername()).setValue(me);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void refreshMarkers(){
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot users:dataSnapshot.getChildren()){
                    User nuser=users.getValue(User.class);
                    if(!users.getKey().equals(prefUtils.getUsername())){
                        gMap.addMarker(new MarkerOptions()
                                .position(new LatLng(nuser.getLat(),nuser.getLon()))
                                .title(users.getKey()));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
