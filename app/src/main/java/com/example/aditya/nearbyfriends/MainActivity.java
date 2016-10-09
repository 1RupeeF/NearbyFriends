package com.example.aditya.nearbyfriends;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.nearbyfriends.Activities.MyFriends;
import com.example.aditya.nearbyfriends.Activities.SignUp;
import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.Prefs.SettingsActivity;
import com.example.aditya.nearbyfriends.db.FriendDB;
import com.example.aditya.nearbyfriends.services.LocationUpdateService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private DatabaseReference dRef;
    private FriendDB fdb;
    @BindView(R.id.activity_main) DrawerLayout mainLayout;
    @BindView(R.id.navView) NavigationView navigationView;
    @BindView(R.id.auto) FloatingActionButton auto;
    @BindView(R.id.manual) FloatingActionButton manual;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private ActionBar toolbar;
    private int PLACE_PICKER_REQUEST_CODE = 123;
    private LocationManager lm;
    private final String TAG = "mainTag";
    private final String TAGcurrent="rxsetCurrent";
    private final String TAGmap="rxmap";
    private PrefUtils prefUtils;
    private Observable<Location> observableLocation;
    private SharedPreferences sp;
    private Intent notificationIntent;
    private int[] nav_bgs=new int[]{R.drawable.nav_bg_1,R.drawable.nav_bg_2,R.drawable.nav_bg_3,R.drawable.nav_bg_4,
            R.drawable.nav_bg_5,R.drawable.nav_bg_6,R.drawable.nav_bg_7,R.drawable.nav_bg_8,R.drawable.nav_bg_9,
            R.drawable.nav_bg_10,R.drawable.nav_bg_11,R.drawable.nav_bg_12,R.drawable.nav_bg_13,R.drawable.nav_bg_14,
            R.drawable.nav_bg_15,R.drawable.nav_bg_16,R.drawable.nav_bg_17,R.drawable.nav_bg_18,R.drawable.nav_bg_19,
            R.drawable.nav_bg_20,R.drawable.nav_bg_21};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = getSupportActionBar();
        ButterKnife.bind(this);
        prefUtils = new PrefUtils(this);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dRef = database.getReference("Users");
        fdb = new FriendDB(this, null, null, 1);
        SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        smf.getMapAsync(this);
        notificationIntent=getIntent();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onNavigationDrawrItemClicked(item);
                return true;
            }
        });
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.username)).setText(prefUtils.getUsername());
        ((LinearLayout)navigationView.getHeaderView(0).findViewById(R.id.nav_header)).
                setBackground(getResources().getDrawable(nav_bgs[new Random().nextInt(21)]));
        navigationView.getHeaderView(0).findViewById(R.id.changebg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (navigationView.getHeaderView(0).findViewById(R.id.nav_header)).
                setBackground(getResources().getDrawable(nav_bgs[new Random().nextInt(21)]));
            }
        });

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(googleApiClient==null){
            googleApiClient=new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        observableLocation=Observable.fromCallable(new Callable<Location>() {
            @Override
            public Location call() throws Exception{
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG,"location: api_client");
                    return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                }
                else {
                    Log.w(TAG,"location: network_provider");
                    return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }).subscribeOn(Schedulers.io());

        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LocationUpdateService.AlarmReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                0, alarmIntent);

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public  void updateCamera(Double lat,Double lon){
        CameraPosition Mylocation = new CameraPosition.Builder()
                .target(new LatLng(lat,lon))
                .zoom(15.5f)
                .bearing(0)
                .tilt(25)
                .build();
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(Mylocation));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            Log.w(TAGmap,"gMap setmylocationenable done");
        }
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (auto.getVisibility() == View.VISIBLE) {
                    auto.setVisibility(View.INVISIBLE);
                    manual.setVisibility(View.INVISIBLE);
                }
            }
        });

        observableLocation
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(final Location location) {
                        if (location == null) {
                            Log.w(TAGmap,"location == null");
                            updateCamera(prefUtils.getLastLat(),prefUtils.getLastLon());
                        }
                        else {
                            Log.w(TAGmap,"location != null");
                            updateCamera(location.getLatitude(),location.getLongitude());
                            prefUtils.setNewLat(location.getLatitude());
                            prefUtils.setNewLon(location.getLongitude());
                        }
                    }
                });
        updateMarkers();
        String fname=notificationIntent.getStringExtra("LessDistName");
        if(fname!=null){
            User friend=fdb.getFriend(fname);
            updateCamera(friend.getLat(),friend.getLon());
        }
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        if (auto.getVisibility() == View.INVISIBLE) {
            auto.setVisibility(View.VISIBLE);
            manual.setVisibility(View.VISIBLE);
        } else {
            auto.setVisibility(View.INVISIBLE);
            manual.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.auto)
    public void onSetCurrentLocation() {
        final boolean internet=isInternetAvailable();
        observableLocation
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<Location>() {
                    @Override public void onCompleted() {}
                    @Override public void onError(Throwable e) {}
                    @Override
                    public void onNext(final Location location) {
                         if(location==null){
                             Log.w(TAGcurrent,"location: null");
                             Snackbar.make(mainLayout, "Failed to get Current Location!! Try Again Later", Snackbar.LENGTH_SHORT)
                                     .setAction("Pick Manually", new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             onSetManualLocation();
                                         }
                                     })
                                     .setActionTextColor(Color.BLUE)
                                     .show();
                         }
                         else {
                             Snackbar.make(mainLayout, "Your location is set to current location", Snackbar.LENGTH_SHORT)
                                     .setAction("Address", new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (internet) {
                                                 try {
                                                     Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
                                                     String address = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getAddressLine(0);
                                                     String city = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getLocality();
                                                     Snackbar.make(mainLayout, address + ", " + city, Snackbar.LENGTH_SHORT).show();
                                                     Log.w(TAGcurrent, "Address Displayed");
                                                 } catch (IOException e) {
                                                     e.printStackTrace();
                                                 }
                                             } else {
                                                 Snackbar.make(mainLayout,"Connect to Internet to get Address", Snackbar.LENGTH_SHORT).show();
                                             }
                                         }
                                     })
                             .show();
                        }
                        try {
                             User me = new User();
                             if(internet) {
                                 Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
                                 List<Address> add = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                 me.setAddress(add.get(0).getAddressLine(0));
                                 me.setCity(add.get(0).getLocality());
                             }
                             else{
                                 me.setAddress("Error: Not Fetched");
                                 me.setCity("Error: Not Fetched");
                             }
                             me.setName(prefUtils.getUsername());
                             me.setLat(location.getLatitude());
                             me.setLon(location.getLongitude());
                             Log.w(TAGcurrent,location.getLatitude()+","+location.getLongitude());
                             dRef.child(prefUtils.getUsername()).setValue(me);
                             if(!internet){
                                Toast.makeText(getApplicationContext(),
                                        "Your new Location will be visible to others once you get connected to Internet",
                                        Toast.LENGTH_SHORT)
                                        .show();
                             }
                             Log.w(TAGcurrent,"location: Firebase updated");
                             prefUtils.setNewLat(location.getLatitude());
                             prefUtils.setNewLon(location.getLongitude());
                            }
                        catch (IOException e) {
                                e.printStackTrace();
                        }
                    }
                });
        onClickFab();
    }

    @OnClick(R.id.manual)
    public void onSetManualLocation() {
        try {
            Intent intent = new PlacePicker.IntentBuilder().build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        onClickFab();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Intent sdata = data;
        if (requestCode == PLACE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(mainLayout, "Your new location is set by you", Snackbar.LENGTH_SHORT)
                        .setAction("Address", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String address = PlacePicker.getPlace(sdata, getApplicationContext()).getAddress().toString();
                                Snackbar.make(mainLayout, address, Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .setActionTextColor(Color.GREEN)
                        .show();
                final Geocoder gc = new Geocoder(this, Locale.getDefault());
                try {
                    User me = new User();
                    me.setLat(PlacePicker.getPlace(this, data).getLatLng().latitude);
                    me.setLon(PlacePicker.getPlace(this, data).getLatLng().longitude);
                    List<Address> add = gc.getFromLocation(me.getLat(), me.getLon(), 1);
                    me.setAddress(PlacePicker.getPlace(this, data).getAddress().toString());
                    me.setCity(add.get(0).getLocality());
                    me.setName(prefUtils.getUsername());
                    dRef.child(prefUtils.getUsername()).setValue(me);
                    prefUtils.setNewLat(me.getLat());
                    prefUtils.setNewLon(me.getLon());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void updateMarkers(){
        gMap.clear();
        ArrayList<User> allFriends=fdb.getAllFriends();
        for(User user:allFriends){
            double dist= SphericalUtil.computeDistanceBetween(
                    new LatLng(prefUtils.getLastLat(),prefUtils.getLastLon()),
                    new LatLng(user.getLat(),user.getLon()))/1000.0;

            gMap.addMarker(new MarkerOptions()
                    .snippet(String.format("%.2f",dist)+" kms away from you")
                    .position(new LatLng(user.getLat(), user.getLon()))
                    .title(user.getName()));
        }
    }

    public void refreshFriendsLocation() {
        if(isInternetAvailable()) {
            final ArrayList<String> fnames=fdb.getAllFriendsName();
            Observable.create(new Observable.OnSubscribe<User>() {
                @Override
                public void call(final Subscriber<? super User> subscriber) {
                    dRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot users : dataSnapshot.getChildren()) {
                                User user = users.getValue(User.class);
                                Log.w(TAG,user.getName()+" got data.");
                                subscriber.onNext(user);
                            }
                            subscriber.onCompleted();
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                }
            })
            .subscribeOn(Schedulers.newThread())
            .filter(new Func1<User, Boolean>() {
                @Override
                public Boolean call(User user) {
                    Log.w(TAG,"Filtering friends");
                    return fnames.contains(user.getName());
                }
            })
            .observeOn(Schedulers.newThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        Log.w(TAG,user.getName()+" updated");
                        fdb.updateFriend(user,user.getName());
                    }
                });

        }
        else{
            Toast.makeText(this,"Connect to Internet for updating location of Friends",Toast.LENGTH_LONG).show();
        }
        updateMarkers();
    }

    public void findFriend(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Search Friend");
        final AutoCompleteTextView friend = new AutoCompleteTextView(getApplicationContext());
        friend.setHint("Friend's Username");
        friend.setDropDownBackgroundResource(android.R.color.white);
        friend.setTextColor(Color.LTGRAY);
        friend.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,fdb.getAllFriendsName()));
        friend.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_LTR);
        builder.setView(friend);
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = friend.getText().toString();
                User u = fdb.getFriend(name);
                if (u == null) {
                    Toast.makeText(getApplicationContext(), "This person is not your Friend", Toast.LENGTH_SHORT).show();
                } else {
                    updateCamera(u.getLat(),u.getLon());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem not= menu.findItem(R.id.notifications);
        if(sp.getBoolean(getString(R.string.pref_show_notifications_key),true)){
            not.setIcon(R.drawable.noton);
        }
        else {
            not.setIcon(R.drawable.notoff);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.refresh: {
                Toast toast = Toast.makeText(this, "Markers Updated", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                refreshFriendsLocation();
                }
                break;
            case R.id.find:
                findFriend();
                break;
            case R.id.exit:
                /*this.finish();
                moveTaskToBack(true);*/
                break;
            case R.id.help:
                break;
            case R.id.notifications:
                if(sp.getBoolean(getString(R.string.pref_show_notifications_key),true)){
                    item.setIcon(R.drawable.notoff);
                    sp.edit().putBoolean(getString(R.string.pref_show_notifications_key),false).commit();
                    Toast toast=Toast.makeText(this,"Notifications are turned OFF",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);toast.show();
                }
                else {
                    item.setIcon(R.drawable.noton);
                    sp.edit().putBoolean(getString(R.string.pref_show_notifications_key),true).commit();
                    Toast toast=Toast.makeText(this,"Notifications are turned ON",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);toast.show();
                }
        }
        return true;
    }

    public void onNavigationDrawrItemClicked(MenuItem item){
        switch (item.getItemId()) {
            case R.id.SignIn:
                if (!prefUtils.isUsernameSet()) {
                    startActivity(new Intent(getApplicationContext(), SignUp.class));
                } else {
                    mainLayout.closeDrawer(Gravity.LEFT);
                    Toast.makeText(getApplicationContext(), "You are already signed In", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.addFriends:
                startActivity(new Intent(getApplicationContext(), MyFriends.class));
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.search:
                mainLayout.closeDrawer(Gravity.LEFT);
                findFriend();
                break;
            case R.id.privacy: {
                mainLayout.closeDrawer(Gravity.LEFT);
                AlertDialog.Builder terms = new AlertDialog.Builder(this);
                terms.setTitle("Privacy Policy");
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(10, 10, 10, 10);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                TextView tv1 = new TextView(this);
                tv1.setTextSize(17.0f);
                tv1.setTextColor(Color.DKGRAY);
                tv1.setText("\n\u25A0 This app is copy right protected by Aditya." +
                        "\n\n\u25A0 You are not authorized to distribute this app.");
                linearLayout.addView(tv1);
                terms.setView(linearLayout);
                terms.setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                terms.create().show();
                }
                break;
            case R.id.terms: {
                mainLayout.closeDrawer(Gravity.LEFT);
                AlertDialog.Builder terms = new AlertDialog.Builder(this);
                terms.setTitle("Terms and Condition");
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(10, 10, 10, 10);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                TextView tv1 = new TextView(this);
                tv1.setTextSize(17.0f);
                tv1.setTextColor(Color.DKGRAY);
                tv1.setText("\n\u25A0 This app is not to be meant for stalking purposes." +
                        "\n\n\u25A0 This app should be used for communication or information purposes only." +
                        "\n\n\u25A0 You are yourself responsible for your privacy." +
                        "\n\n\u25A0 Company is not at all responsible for any harm done through this app.");
                linearLayout.addView(tv1);
                terms.setView(linearLayout);
                terms.setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                terms.create().show();
                }
                break;
        }
    }

    public void turnOnGps(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public boolean isInternetAvailable(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    @Override public void onConnected(@Nullable Bundle bundle) {}
    @Override public void onConnectionSuspended(int i) { }
    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    protected void onPause() {
        int millisec=Integer.parseInt(sp.getString(getString(R.string.pref_update_min_key),
                getString(R.string.pref_update_min_default)))*60*1000;
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LocationUpdateService.AlarmReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                millisec, millisec, alarmIntent);
        super.onPause();
    }
}
