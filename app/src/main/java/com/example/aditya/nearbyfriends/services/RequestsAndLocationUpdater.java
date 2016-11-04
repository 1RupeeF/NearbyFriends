package com.example.aditya.nearbyfriends.services;

import android.Manifest;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.aditya.nearbyfriends.HttpRequest;
import com.example.aditya.nearbyfriends.Pojos.DefaultResponse;
import com.example.aditya.nearbyfriends.Pojos.LocationUpdate;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.db.DataFetcher;
import com.example.aditya.nearbyfriends.db.FriendDB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestsAndLocationUpdater extends IntentService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAGs="bgservice2";
    private FriendDB fdb;
    private PrefUtils prefUtils;
    private DataFetcher dataFetcher;
    private GoogleApiClient googleApiClient;
    private HttpRequest.MainInterface mainInterface;
    public RequestsAndLocationUpdater() {
        super("requestandlocationupdater");
        fdb=new FriendDB(this,null,null,1);
        dataFetcher=DataFetcher.getInstance();
        mainInterface=HttpRequest.retrofit.create(HttpRequest.MainInterface.class);
        Log.w(TAGs,"constructor initialized");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        prefUtils=new PrefUtils(getApplicationContext());
        if (prefUtils.isUsernameSet() && isInternetAvailable()) {
            dataFetcher.getFriendRequests(prefUtils.getUID() + "", getApplicationContext());
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            googleApiClient.connect();
            Log.w(TAGs,"googleApiClientConnected");
            Location loc = null;
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                Log.w(TAGs, "location: api_client");
                loc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (loc == null) {
                    Log.w(TAGs, "location: network_provider");
                    loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if(loc==null) return;
            Log.w(TAGs,"loc!=null");
            final Location location=loc;
            LocationUpdate locationUpdate = new LocationUpdate(prefUtils.getUID(), "" + location.getLatitude()
                    , "" + location.getLongitude());
            Call<DefaultResponse> responseCall = mainInterface.updateLocation(locationUpdate);
            responseCall.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    if (response.body().getStatus().equals("ok")) {
                        Log.w(TAGs,"location updated");
                        prefUtils.setNewLat(location.getLatitude());
                        prefUtils.setNewLon(location.getLongitude());
                    }
                }
                @Override
                public void onFailure(Call<DefaultResponse> call, Throwable t) {}
            });
            googleApiClient.disconnect();
        }
        else Log.w(TAGs,"Not login or net not working");
    }


    public boolean isInternetAvailable(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {}
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    public static class AlarmReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent inte=new Intent(context,RequestsAndLocationUpdater.class);
            Log.w("bgservice2","Starting service");
            PrefUtils prefUtils=new PrefUtils(context);
            if(prefUtils.isUsernameSet()) {
                context.startService(inte);
            }
        }
    }

}
