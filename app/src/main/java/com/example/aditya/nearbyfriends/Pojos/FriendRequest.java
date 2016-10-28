package com.example.aditya.nearbyfriends.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aditya on 25/10/16.
 */

public class FriendRequest {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("list")
    @Expose
    private ArrayList<HashMap<String,String>> list;

    public ArrayList<HashMap<String, String>> getList() {
        return list;
    }

    public void setList(ArrayList<HashMap<String, String>> list) {
        this.list = list;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
