package com.example.aditya.nearbyfriends.services;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;
import com.example.aditya.nearbyfriends.db.DataFetcher;
import com.example.aditya.nearbyfriends.db.FriendDB;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

/**
 * Created by aditya on 7/10/16.
 */

public class LocationUpdateService extends IntentService {
    private final String TAGs="bgservice";
    private FriendDB fdb;
    private PrefUtils prefUtils;
    private DataFetcher dataFetcher;

    public LocationUpdateService(){
        super("locationupdateservice");
        Log.w(TAGs,"constructor initialized");
        fdb=new FriendDB(this,null,null,1);
        dataFetcher=DataFetcher.getInstance();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        prefUtils=new PrefUtils(getApplicationContext());
        dataFetcher.getFriendRequests(prefUtils.getUID()+"",getApplicationContext());
        for(int uid:fdb.getAllFriendsUids()){
            dataFetcher.getFriendsLocation(prefUtils.getUID(),uid,getApplicationContext());
        }
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        final boolean shownoti= sp.getBoolean(getString(R.string.pref_show_notifications_key),true);
        final int mindist= Integer.parseInt(sp.getString(getString(R.string.pref_dist_key),getString(R.string.pref_dist_default)));
        final boolean notsound=sp.getBoolean(getString(R.string.pref_notification_sound),true);
        if(isInternetAvailable()) {
            ArrayList<User> allFriends = fdb.getAllFriends();
            for (User user : allFriends) {
                if (user.getLat() != null && user.getLon() != null) {
                    if (shownoti) {
                        double dist = SphericalUtil.computeDistanceBetween(new LatLng(Double.parseDouble(user.getLat()),
                                Double.parseDouble(user.getLon())),
                                new LatLng(prefUtils.getLastLat(), prefUtils.getLastLon()));
                        Log.w(TAGs, user.getName() + ": dist=" + dist);
                        if (dist < mindist) {
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext());
                            notification.setContentText(user.getName() + " is " + String.format("%.3f", dist) + " meters away");
                            notification.setSmallIcon(R.drawable.app_icon);
                            if (notsound)
                                notification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            notification.setContentTitle(user.getName() + " is near you.");
                            Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=" + prefUtils.getLastLat() + "," + prefUtils.getLastLon() +
                                            "&daddr=" + user.getLat() + "," + user.getLon()));
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                            stackBuilder.addNextIntent(new Intent(getApplicationContext(), MainActivity.class)
                                    .putExtra("serviceFname", user.getName()));
                            stackBuilder.addParentStack(MainActivity.class);
                            PendingIntent pi2 = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            notification.setContentIntent(pi2);
                            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent1, 0);
                            notification.addAction(R.drawable.ic_navigation_black_24dp, "Navigation", pi);
                            notification.addAction(R.drawable.open_app, "Open App", pi2);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(Integer.parseInt(user.getUid()), notification.build());
                        }
                    }
                }
            }
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
            PrefUtils prefUtils=new PrefUtils(context);
            if(prefUtils.isUsernameSet()) {
                context.startService(inte);
            }
        }
    }


}
