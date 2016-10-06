package com.example.aditya.nearbyfriends.Prefs;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by aditya on 1/10/16.
 */

public class PrefUtils {
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    private String username;
    private final String USER_SET="LoggedIn";
    private final String USERNAME="username";
    private final String FIRST_TIME="first";
    private final String LAST_LAT="lat";
    private final String LAST_LON="lon";
    public PrefUtils(Context context){
        sharedPreferences=context.getSharedPreferences("pref",MODE_PRIVATE);
        edit=sharedPreferences.edit();
    }

    public String getUsername() {
        return sharedPreferences.getString(USERNAME,null);
    }

    public void setUsername(String uname) {
        edit.putString(USERNAME,uname);
        edit.putBoolean(USER_SET,true);
        edit.commit();
    }

    public Double getLastLat() {
        return Double.parseDouble(sharedPreferences.getString(LAST_LAT,"18.5308225"));
    }

    public Double getLastLon() {
        return Double.parseDouble(sharedPreferences.getString(LAST_LON,"73.8474647"));
    }

    public void setNewLat(Double d){
        edit.putString(LAST_LAT,d.toString());
        edit.commit();
    }
    public void setNewLon(Double d){
        edit.putString(LAST_LON,d.toString());
        edit.commit();
    }

    public boolean isUsernameSet(){
        return sharedPreferences.getBoolean(USER_SET,false);
    }

    public boolean isFirstTime(){
        return sharedPreferences.getBoolean(FIRST_TIME,true);
    }

    public void firstTimeDone(){
        edit.putBoolean(FIRST_TIME,false);
        edit.commit();
    }
}
