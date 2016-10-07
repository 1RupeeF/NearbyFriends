package com.example.aditya.nearbyfriends.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.aditya.nearbyfriends.Pojos.User;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;

import java.util.ArrayList;


/**
 * Created by aditya on 3/10/16.
 */

public class FriendDB extends SQLiteOpenHelper{
    private static final String COL_1="_id";
    private static final String COL_2="Name";
    private static final String COL_3="lat";
    private static final String COL_4="lon";
    private static final String COL_5="address";
    private static final String COL_6="city";
    private static final int DB_VERSION=1;
    private static final String DB_NAME="NearbyFriends";
    private static final String TABLE="Friends";
    PrefUtils prefUtils;

    public FriendDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,DB_NAME, factory,version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //from where that come
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE+" ("+
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " VARCHAR(50) UNIQUE, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " VARCHAR(50));"
        );
    }


    public boolean addFriend(String name,double lat,double lon,String address,String city){
        ContentValues values=new ContentValues();
        values.put(COL_2, name);
        values.put(COL_3, Double.toString(lat));
        values.put(COL_4, Double.toString(lon));
        values.put(COL_5,address);
        values.put(COL_6,city);
        int er =(int)getWritableDatabase().insert(TABLE,null,values);

        if(er==-1){
            return false;
        }
        return true;
    }

    public boolean addFriend(User user){
        ContentValues values=new ContentValues();
        values.put(COL_2, user.getName());
        values.put(COL_3, Double.toString(user.getLat()));
        values.put(COL_4, Double.toString(user.getLon()));
        values.put(COL_5,user.getAddress());
        values.put(COL_6,user.getCity());
        int er =(int)getWritableDatabase().insert(TABLE,null,values);

        if(er==-1){
            return false;
        }
        return true;
    }

    public boolean updateFriend(User user,String name){
        ContentValues values=new ContentValues();
        values.put(COL_2, user.getName());
        values.put(COL_3, Double.toString(user.getLat()));
        values.put(COL_4, Double.toString(user.getLon()));
        values.put(COL_5,user.getAddress());
        values.put(COL_6,user.getCity());
        int er=(int)getWritableDatabase().update(TABLE,values,COL_2+"=?",new String[]{name});

        if(er==-1){
            return false;
        }
        return true;
    }


    public ArrayList<User> getAllFriends(){
        ArrayList<User> friends=new ArrayList<>();
        Cursor cursor=getWritableDatabase().query(TABLE,
               new String[]{COL_2,COL_3,COL_4,COL_5,COL_6},null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            User u=new User(cursor.getString(3),
                    Double.parseDouble(cursor.getString(1)),
                    Double.parseDouble(cursor.getString(2)),
                    cursor.getString(4));
            u.setName(cursor.getString(0));
            friends.add(u);
            cursor.moveToNext();
        }
        return friends;
    }


    public ArrayList<String> getAllFriendsName(){
        ArrayList<String> names=new ArrayList<>();
        Cursor cursor=getWritableDatabase().query(TABLE,
                new String[]{COL_2},null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            names.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return names;
    }

    public void deleteFriend(String name){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("Delete from "+TABLE+" where "+COL_2+"= \""+name+"\";");
    }

    public User getFriend(String name){
        Cursor cursor=getWritableDatabase().query(TABLE,
                new String[]{COL_2,COL_3,COL_4,COL_5,COL_6},COL_2+"=?",new String[]{name},null,null,null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            User u=new User(cursor.getString(3),
                    Double.parseDouble(cursor.getString(1)),
                    Double.parseDouble(cursor.getString(2)),
                    cursor.getString(4));
            u.setName(cursor.getString(0));
            return u;
        }
        else {
            return null;
        }
    }

    public int getFriendId(String name){
        Cursor cursor=getWritableDatabase().query(TABLE,
                new String[]{COL_1},COL_2+"=?",new String[]{name},null,null,null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            return cursor.getInt(0);
        }
        else {
            return -1;
        }
    }


}
