package com.example.aditya.nearbyfriends.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by aditya on 24/10/16.
 */

public class FriendsList {

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("list")
    @Expose
    private ArrayList<Integer> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Integer> getList() {
        return list;
    }

    public void setList(ArrayList<Integer> list) {
        this.list = list;
    }
}
