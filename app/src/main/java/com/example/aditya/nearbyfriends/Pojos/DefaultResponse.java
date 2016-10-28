package com.example.aditya.nearbyfriends.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultResponse {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("uid")
    @Expose
    private int uid;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid)  {
        this.uid = uid;
    }
}
