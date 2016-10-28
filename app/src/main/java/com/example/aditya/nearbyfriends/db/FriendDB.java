package com.example.aditya.nearbyfriends.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.aditya.nearbyfriends.Pojos.FriendRequest;
import com.example.aditya.nearbyfriends.Pojos.User;

import java.util.ArrayList;
import java.util.HashMap;


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
    private static final String COL_7="track";
    private static final String COL_8="lastupdated";
    private static final int DB_VERSION=1;
    private static final String DB_NAME="NearbyFriends";
    private static final String TABLE="Friends";
    private static final String TABLE2="Requests";
    private static final String COL_21="_id";
    private static final String COL_22="name";
    private static final String COL_23="email";

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
                COL_1 + " INT(11) UNIQUE ," +
                COL_2 + " VARCHAR(50), " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " VARCHAR(50), " +
                COL_7 + " BOOL," +
                COL_8 + " TEXT);"
        );
        db.execSQL("CREATE TABLE "+TABLE2+" ("+
                COL_21 + " INT(11) UNIQUE ," +
                COL_22 + " VARCHAR(50) ," +
                COL_23 + " VARCHAR(50) );"
        );
    }

    public boolean isNewRequest(String uid){
        Cursor cursor=getWritableDatabase().query(TABLE2,
                new String[]{COL_21},COL_21+"=?",new String[]{uid},null,null,null);
        return cursor.getCount()==0;
    }

    public boolean addRequest(String uid,String name,String email){
        ContentValues values=new ContentValues();
        values.put(COL_21, Integer.parseInt(uid));
        values.put(COL_22,name);
        values.put(COL_23,email);
        int er =(int)getWritableDatabase().insert(TABLE2,null,values);

        if(er==-1){
            return false;
        }
        return true;
    }

    public ArrayList<HashMap<String,String>> getAllRequests(){
        ArrayList<HashMap<String,String>> al=new ArrayList<>();
        Cursor cursor=getWritableDatabase().query(TABLE2,
                new String[]{COL_21,COL_22,COL_23},null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("uid",cursor.getString(0));
            hashMap.put(COL_22,cursor.getString(1));
            hashMap.put(COL_23,cursor.getString(2));
            al.add(hashMap);
            cursor.moveToNext();
        }
        return al;
    }

    public ArrayList<String> getAllRequestsId(){
        ArrayList<String> al=new ArrayList<>();
        Cursor cursor=getWritableDatabase().query(TABLE2,
                new String[]{COL_21},null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            al.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return al;
    }
    public void deleteRequests(String uid){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("Delete from "+TABLE2+" where "+COL_21+"= \""+uid+"\";");
    }

    public boolean addFriend(int uid,String name){
        ContentValues values=new ContentValues();
        values.put(COL_1, uid);
        values.put(COL_2,name);
        int er =(int)getWritableDatabase().insert(TABLE,null,values);

        if(er==-1){
            return false;
        }
        return true;
    }

    public boolean addFriend(int uid){
        ContentValues values=new ContentValues();
        values.put(COL_1, uid);
        int er =(int)getWritableDatabase().insert(TABLE,null,values);
        if(er==-1){
            return false;
        }
        return true;
    }

    public boolean updateFriend(String uid,String lat,String lon,
                                String name,String lastupdate,
                                String address,String city){
        ContentValues values=new ContentValues();
        values.put(COL_2, name);
        values.put(COL_3, lat);
        values.put(COL_4, lon);
        values.put(COL_5, address);
        values.put(COL_6, city);
        values.put(COL_8,lastupdate);
        int er=(int)getWritableDatabase().update(TABLE,values,COL_1+"=?",new String[]{uid});

        if(er==-1){
            return false;
        }
        return true;
    }


    public ArrayList<User> getAllFriends(){
        ArrayList<User> friends=new ArrayList<>();
        Cursor cursor=getWritableDatabase().query(TABLE,
               new String[]{COL_1,COL_2,COL_3,COL_4,COL_5,COL_6,COL_8},null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            User u=new User(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
            friends.add(u);
            cursor.moveToNext();
        }
        return friends;
    }

    public ArrayList<Integer> getAllFriendsUids(){
        ArrayList<Integer> names=new ArrayList<>();
        Cursor cursor=getWritableDatabase().query(TABLE,
                new String[]{COL_1},null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            names.add(Integer.parseInt(cursor.getString(0)));
            cursor.moveToNext();
        }
        return names;
    }

    public void deleteFriend(String uid){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("Delete from "+TABLE+" where "+COL_1+"= \""+uid+"\";");
    }

    public User getFriend(String uid){
        Cursor cursor=getWritableDatabase().query(TABLE,
                new String[]{COL_1,COL_2,COL_3,COL_4,COL_5,COL_6,COL_8},COL_1+"=?",new String[]{uid},null,null,null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            User u=new User(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
            return u;
        }
        else {
            return null;
        }
    }

    public ArrayList<String> getAllFriendsNames(){
        ArrayList<String> names=new ArrayList<>();
        Cursor cursor=getWritableDatabase().query(TABLE,
                new String[]{COL_2},null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            if(cursor.getString(0)!=null)
                names.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return names;
    }

    public String getFriendName(String uid){
        Cursor cursor=getWritableDatabase().query(TABLE,
                new String[]{COL_2},COL_1+"=?",new String[]{uid},null,null,null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            return cursor.getString(0);
        }
        else {
            return null;
        }
    }

    public String getFriendId(String name){
        Cursor cursor=getWritableDatabase().query(TABLE,
                new String[]{COL_1},COL_2+"=?",new String[]{name},null,null,null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            return cursor.getString(0);
        }
        else {
            return null;
        }
    }

    public boolean addToTrackList(String uid,boolean status){
        int er;
        if(status){
            ContentValues values=new ContentValues();
            values.put(COL_7,Boolean.TRUE);
            er=(int)getWritableDatabase().update(TABLE,values,COL_1+"=?",new String[]{uid});
        }else{
            ContentValues values=new ContentValues();
            values.put(COL_7,Boolean.FALSE);
            er=(int)getWritableDatabase().update(TABLE,values,COL_1+"=?",new String[]{uid});
        }
        if(er==-1){
            return false;
        }
        return true;
    }

    public boolean isInTrackerList(String uid){
        Cursor cursor=getWritableDatabase().query(TABLE,
                new String[]{COL_7},COL_1+"=?",new String[]{uid},null,null,null);
        cursor.moveToFirst();
        Boolean b=cursor.getInt(0) > 0;
        return b;
    }


}
