package com.example.aditya.nearbyfriends;

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
    PrefUtils(Context context){
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

    public boolean isUsernameSet(){
        return sharedPreferences.getBoolean(USER_SET,false);
    }

    private void isUserSet(){

    }
}
