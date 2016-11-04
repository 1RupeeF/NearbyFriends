package com.example.aditya.nearbyfriends.Pojos;

/**
 * Created by aditya on 24/10/16.
 */

public class User {
    private String uid;
    private String name;
    private String lat;
    private String lon;
    private String address;
    private String city;
    private String lastupdate;


    public User(String uid, String name, String lat, String lon, String address, String city, String lastupdate) {
        this.uid = uid;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.city = city;
        this.lastupdate = lastupdate;
    }

    public User() {}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }
}
