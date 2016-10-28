package com.example.aditya.nearbyfriends.Pojos;

/**
 * Created by aditya on 24/10/16.
 */

public class LocationUpdate {

    int uid;
    String lat;
    String lon;

    public LocationUpdate(int uid, String lat, String lon) {
        this.uid = uid;
        this.lat = lat;
        this.lon = lon;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
