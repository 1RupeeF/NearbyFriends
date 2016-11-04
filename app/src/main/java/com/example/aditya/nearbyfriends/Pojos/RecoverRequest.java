package com.example.aditya.nearbyfriends.Pojos;

/**
 * Created by aditya on 3/11/16.
 */

public class RecoverRequest {

    private String email;
    private String pass;
    private int code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public RecoverRequest(String email, String pass, int code) {
        this.email = email;
        this.pass = pass;
        this.code = code;
    }
}
