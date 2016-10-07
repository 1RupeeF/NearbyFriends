package com.example.aditya.nearbyfriends.services;


import android.app.IntentService;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.FriendDB;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
/**
 * Created by aditya on 7/10/16.
 */

public class LocationUpdateService extends IntentService {
    private final String TAGs="bgservice";
    private FriendDB fdb;
    private DatabaseReference dRef;
    private PrefUtils prefUtils;


    public LocationUpdateService(){
        super("locationupdateservice");
        Log.w(TAGs,"constructor initialized");
        fdb=new FriendDB(this,null,null,1);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dRef = database.getReference("Users");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        prefUtils=new PrefUtils(getApplicationContext());
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        final Boolean shownoti= sp.getBoolean(getString(R.string.pref_show_notifications_key),true);
        final int mindist= Integer.parseInt(sp.getString(
                getString(R.string.pref_dist_key),getString(R.string.pref_dist_default))
        );
        if(isInternetAvailable()) {
            final ArrayList<String> fnames = fdb.getAllFriendsName();
            dRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot users : dataSnapshot.getChildren()) {
                        User user = users.getValue(User.class);
                        Log.w(TAGs, user.getName() + " got data.");
                        if(fnames.contains(user.getName())) {
                            Log.w(TAGs, user.getName() + " updated");
                            fdb.updateFriend(user, user.getName());
                            if(shownoti) {
                                double dist = SphericalUtil.computeDistanceBetween(new LatLng(user.getLat(), user.getLon()),
                                        new LatLng(prefUtils.getLastLat(), prefUtils.getLastLon()));
                                Log.w(TAGs, user.getName() + ": dist=" + dist);
                                if (dist < mindist) {
                                    NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext());
                                    notification.setContentText(user.getName() + " is " + String.format("%.3f", dist) + " meters away");
                                    notification.setSmallIcon(R.drawable.app_icon);
                                    notification.setStyle(new NotificationCompat.InboxStyle());
                                    //notification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                    notification.setContentTitle(user.getName() + " is near you.");
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(fdb.getFriendId(user.getName()), notification.build());
                                }
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAGs, "Failed to read value.", error.toException());
                }
            });
        }
    }

    public boolean isInternetAvailable(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    public static class AlarmReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent inte=new Intent(context,LocationUpdateService.class);
            Log.w("bgservice","Starting service");
            context.startService(inte);
        }
    }


}
