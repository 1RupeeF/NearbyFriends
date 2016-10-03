package com.example.aditya.nearbyfriends.Pojos;

/**
 * Created by aditya on 3/10/16.
 */

public class User {
    private String address;
    private double lat;
    private double lon;
    private String city;


    public User(){
    }

    public User(String address, double lat, double lon, String city) {
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.city=city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
