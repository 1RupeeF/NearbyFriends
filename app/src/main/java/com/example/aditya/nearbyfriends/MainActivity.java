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
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.nearbyfriends.Activities.FriendRequests;
import com.example.aditya.nearbyfriends.Activities.MyFriends;
import com.example.aditya.nearbyfriends.Activities.SignUp;
import com.example.aditya.nearbyfriends.Activities.Welcome;
import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.Prefs.SettingsActivity;
import com.example.aditya.nearbyfriends.db.DataFetcher;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    DataFetcher dataFetcher;
    private FriendDB fdb;
    @BindView(R.id.activity_main) DrawerLayout mainLayout;
    @BindView(R.id.navView) NavigationView navigationView;
    @BindView(R.id.auto) FloatingActionButton auto;
    @BindView(R.id.mapType) Button mapView;
    @BindView(R.id.manual) FloatingActionButton manual;
    private TextView reqcount;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private ActionBar toolbar;
    private int PLACE_PICKER_REQUEST_CODE = 123;
    private LocationManager lm;
    private ActionBarDrawerToggle toogle;
    private final String TAG = "mainTag";
    private final String TAGcurrent="rxsetCurrent";
    private final String TAGmap="rxmap";
    private PrefUtils prefUtils;
    private Observable<Location> observableLocation;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = getSupportActionBar();
        ButterKnife.bind(this);
        prefUtils = new PrefUtils(this);
        if (prefUtils.isFirstTime()) {
            startActivity(new Intent(this, Welcome.class));
        }
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        fdb = new FriendDB(this, null, null, 1);
        SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        smf.getMapAsync(this);
        dataFetcher = DataFetcher.getInstance();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onNavigationDrawrItemClicked(item);
                return true;
            }
        });
        if (prefUtils.isUsernameSet()) {
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.username))
                    .setText(prefUtils.getUsername() + " \n( "+ prefUtils.getUID()+" )" );
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email))
                    .setText(prefUtils.getEMAIL());
        } else {
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email))
                    .setText("You Are Not Signed In");
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.username))
                    .setText("Welcome");
        }

        navigationView.getHeaderView(0).findViewById(R.id.nav_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationView.getHeaderView(0).findViewById(R.id.appName).setVisibility(View.VISIBLE);
                ((TextView)navigationView.getHeaderView(0).findViewById(R.id.appName))
                        .setTypeface(Typeface.createFromAsset(getAssets(),"fonts/birdmanbold.ttf"));
                navigationView.getHeaderView(0).findViewById(R.id.username).setVisibility(View.INVISIBLE);
                navigationView.getHeaderView(0).findViewById(R.id.email).setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigationView.getHeaderView(0).findViewById(R.id.appName).setVisibility(View.GONE);
                        navigationView.getHeaderView(0).findViewById(R.id.username).setVisibility(View.VISIBLE);
                        navigationView.getHeaderView(0).findViewById(R.id.email).setVisibility(View.VISIBLE);
                    }
                },1000);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
                turnOnGps();
        }
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        observableLocation = Observable.fromCallable(new Callable<Location>() {
            @Override
            public Location call() throws Exception {
                Location loc = null;
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "location: api_client");
                    loc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    if (loc == null) {
                        Log.w(TAG, "location: network_provider");
                        loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                return loc;
            }
        }).subscribeOn(Schedulers.newThread());

        toolbar.setDisplayHomeAsUpEnabled(true);
        toogle = new ActionBarDrawerToggle(this, mainLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                toolbar.setHomeAsUpIndicator(R.drawable.drawer_open);
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                toolbar.setHomeAsUpIndicator(R.drawable.drawer_close);
                super.onDrawerOpened(drawerView);
            }
        };
        registerForContextMenu(mapView);
        mapView.setAlpha(.7f);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.performLongClick();
            }
        });
        mainLayout.setDrawerListener(toogle);
        toogle.syncState();
        toolbar.setHomeAsUpIndicator(R.drawable.drawer_open);
        if (prefUtils.isUsernameSet()) {
            startService(new Intent(this, LocationUpdateService.class));
        }
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
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(Mylocation));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            Log.w(TAGmap,"gMap setmylocationenable done");
            if(prefUtils.getMapType().equals("Satellite"))
                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            else if(prefUtils.getMapType().equals("Terrain"))
                gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            else if(prefUtils.getMapType().equals("Hybrid"))
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            else
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
            refreshFriendsLocation();

    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        if(prefUtils.isUsernameSet()) {
            if (auto.getVisibility() == View.INVISIBLE) {
                auto.setVisibility(View.VISIBLE);
                manual.setVisibility(View.VISIBLE);
            } else {
                auto.setVisibility(View.INVISIBLE);
                manual.setVisibility(View.INVISIBLE);
            }
        }
        else{
            Snackbar.make(mainLayout,"SignIn before updating your location",Snackbar.LENGTH_LONG)
                    .setAction("SignIn", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this,SignUp.class));
                        }
                    })
                    .setActionTextColor(Color.YELLOW)
                    .show();
        }
    }

    @OnClick(R.id.auto)
    public void onSetCurrentLocation() {
        if(prefUtils.isUsernameSet()) {
            dataFetcher.updateLocation(prefUtils.getUID(),observableLocation,getApplicationContext());
        }
        else {
            Snackbar.make(mainLayout,"Not Signed In",Snackbar.LENGTH_SHORT).show();
        }
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

                dataFetcher.updateLocation(prefUtils.getUID(),
                        ""+PlacePicker.getPlace(this, data).getLatLng().latitude,
                        ""+PlacePicker.getPlace(this, data).getLatLng().longitude,
                        getApplicationContext());
            }
        }
    }
    public void updateMarkers(){
        gMap.clear();
        ArrayList<User> allFriends=fdb.getAllFriends();
        for(User user:allFriends){
            if(user.getLat()!=null && user.getLon()!=null) {
                double dist = SphericalUtil.computeDistanceBetween(
                        new LatLng(prefUtils.getLastLat(), prefUtils.getLastLon()),
                        new LatLng(Double.parseDouble(user.getLat())
                                , Double.parseDouble(user.getLon()))) / 1000.0;
                gMap.addMarker(new MarkerOptions()
                        .snippet(String.format("%.2f", dist) + " kms away, " +
                                "Address: " + user.getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title(user.getName()+" : "+user.getLastupdate())
                        .position(new LatLng(Double.parseDouble(user.getLat()), Double.parseDouble(user.getLon())))
                );
            }
        }
        if(prefUtils.isUsernameSet()) {
            gMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title("Your Location")
                    .position(new LatLng(prefUtils.getLastLat(), prefUtils.getLastLon()))
            );
        }
    }

    public void refreshFriendsLocation() {
       if(isInternetAvailable()) {
           final ArrayList<Integer> fuids = fdb.getAllFriendsUids();
           for (int fuid : fuids) {
               dataFetcher.getFriendsLocation(prefUtils.getUID(), fuid, getApplicationContext());
               Log.w(TAG,fuid+" updated");
           }
       }
       else{
               Toast.makeText(this,"Connect to Internet for updating location of Friends",Toast.LENGTH_LONG).show();
       }
       updateMarkers();
        reqcount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.requests));
        reqcount.setTypeface(null, Typeface.BOLD_ITALIC);
        reqcount.setGravity(Gravity.CENTER_VERTICAL);
        reqcount.setTextColor(Color.GREEN);
        reqcount.setText("   "+fdb.getAllRequestsId().size() + "   ");
    }

    public void findFriend(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Search Friend");
        final AutoCompleteTextView friend = new AutoCompleteTextView(getApplicationContext());
        friend.setHint("Friend's Username");
        friend.setDropDownBackgroundResource(android.R.color.white);
        friend.setTextColor(Color.BLACK);
        friend.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,fdb.getAllFriendsNames()));
        friend.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_LTR);
        builder.setView(friend);
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = friend.getText().toString();
                String id = fdb.getFriendId(name);
                if(id!=null) {
                    User u = fdb.getFriend(id);
                    if (u == null) {
                        Toast.makeText(getApplicationContext(), "This person is not your Friend", Toast.LENGTH_SHORT).show();
                    } else {
                        if(u.getLat()!=null && u.getLon()!=null) {
                            updateCamera(Double.parseDouble(u.getLat()), Double.parseDouble(u.getLon()));
                        }
                        else
                            Toast.makeText(getApplicationContext(),u.getName()+" has not updated his location",Toast.LENGTH_LONG).show();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "This person is not your Friend", Toast.LENGTH_SHORT).show();
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
        if(toogle.onOptionsItemSelected(item)){
            return true;
        }
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
            case R.id.signout:
                signOut();
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
        return super.onOptionsItemSelected(item);
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
            case R.id.signout:
                mainLayout.closeDrawer(Gravity.LEFT);
                signOut();
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
            case R.id.requests:
                startActivity(new Intent(this, FriendRequests.class));
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
        alertDialog.setTitle("GPS ");
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

    public void signOut(){
        prefUtils.logOut();
        for(int uid:fdb.getAllFriendsUids()){
            fdb.deleteFriend(uid+"");
        }
        for(String uid:fdb.getAllRequestsId()){
            fdb.deleteRequests(uid);
        }
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email))
                .setText("You Are Not Signed In");
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.username))
                .setText("Welcome");
        reqcount.setText("   "+0+ "   ");
        gMap.clear();
        Toast.makeText(getApplicationContext(),"LogOut Successful",Toast.LENGTH_SHORT).show();
    }

    @Override public void onConnected(@Nullable Bundle bundle) {}
    @Override public void onConnectionSuspended(int i) { }
    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    protected void onDestroy() {
        int millisec=Integer.parseInt(sp.getString(getString(R.string.pref_update_min_key),
                getString(R.string.pref_update_min_default)))*60*1000;
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LocationUpdateService.AlarmReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                millisec, millisec, alarmIntent);
        super.onDestroy();
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderIcon(R.drawable.google_maps);
        menu.setHeaderTitle("Change Map Type");
        menu.add(0,v.getId(),0,"Satellite");
        menu.add(0,v.getId(),0,"Terrain");
        menu.add(0,v.getId(),0,"Normal");
        menu.add(0,v.getId(),0,"Hybrid");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Satellite"))
            gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else if(item.getTitle().equals("Terrain"))
            gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        else if(item.getTitle().equals("Hybrid"))
            gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        prefUtils.setMapType(item.getTitle().toString());
        return true;
    }
}
