package com.example.aditya.nearbyfriends.db;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.aditya.nearbyfriends.Activities.FriendRequests;
import com.example.aditya.nearbyfriends.HttpRequest;
import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.Pojos.DefaultResponse;
import com.example.aditya.nearbyfriends.Pojos.FriendRequest;
import com.example.aditya.nearbyfriends.Pojos.FriendsList;
import com.example.aditya.nearbyfriends.Pojos.FriendsLocation;
import com.example.aditya.nearbyfriends.Pojos.LocationUpdate;
import com.example.aditya.nearbyfriends.Pojos.RegisterRequest;
import com.example.aditya.nearbyfriends.Pojos.SignInResponse;
import com.example.aditya.nearbyfriends.Prefs.PrefUtils;
import com.example.aditya.nearbyfriends.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * Created by aditya on 24/10/16.
 */
public class DataFetcher {
    //Singleton Class
    private HttpRequest.MainInterface mainInterface;
    private static DataFetcher dataFetcher =new DataFetcher();

    public DataFetcher() {
        mainInterface=HttpRequest.retrofit.create(HttpRequest.MainInterface.class);
    }

    public static DataFetcher getInstance(){
        return dataFetcher;
    }

    public void register(final String email, final String pass, final String name, final Context context){
        final PrefUtils prefUtils=new PrefUtils(context);
        Observable.create(new Observable.OnSubscribe<DefaultResponse>() {
            @Override
            public void call(final Subscriber<? super DefaultResponse> subscriber) {
                RegisterRequest registerRequest=new RegisterRequest(email,pass,name);
                Call<DefaultResponse> responseCall=mainInterface.register(registerRequest);
                responseCall.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        DefaultResponse defaultResponse=response.body();
                        if(defaultResponse.getStatus().equals("ok"))
                            subscriber.onNext(defaultResponse);
                        else
                            Toast.makeText(context,"Registration Failed",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {

                    }
                });
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<DefaultResponse>() {
                    @Override
                    public void call(DefaultResponse defaultResponse) {
                        if(defaultResponse.getStatus().equals("ok")){
                            prefUtils.setUsername(name);
                            prefUtils.setEMAIL(email);
                            prefUtils.setUID(defaultResponse.getUid());
                            Toast.makeText(context,"Registration Done(UID: "+defaultResponse.getUid()+")",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context,"Email id already registered with us.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addFriend(String uid,String fuid,final Context context){
        final RequestBody ruid=RequestBody.create(MediaType.parse("text/plain"),""+uid);
        final RequestBody rfuid=RequestBody.create(MediaType.parse("text/plain"),""+fuid);
        
        Observable.create(new Observable.OnSubscribe<DefaultResponse>() {
            @Override
            public void call(final Subscriber<? super DefaultResponse> subscriber) {
                Call<DefaultResponse> responseCall=mainInterface.addFriends(ruid,rfuid);
                responseCall.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        if(response.body().getStatus().equals("ok")){
                            Toast.makeText(context,"Request sent..",Toast.LENGTH_SHORT).show();
                            subscriber.onNext(response.body());
                        }
                        else{
                            Toast.makeText(context,"Sending Friend request failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {}
                });
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<DefaultResponse>() {
                    @Override
                    public void call(DefaultResponse defaultResponse) {
                        FriendDB fdb=new FriendDB(context,null,null,1);
                        boolean b=fdb.addFriend(defaultResponse.getUid());
                    }
                });
    }


    public void getFriends(String uid, final Context context){
        final RequestBody ruid=RequestBody.create(MediaType.parse("text/plain"),uid);
        Observable.create(new Observable.OnSubscribe<FriendsList>() {
            @Override
            public void call(final Subscriber<? super FriendsList> subscriber) {
                Call<FriendsList> responseCall=mainInterface.getFriends(ruid);
                responseCall.enqueue(new Callback<FriendsList>() {
                    @Override
                    public void onResponse(Call<FriendsList> call, Response<FriendsList> response) {
                        if(response.body().getCount()>0){
                            subscriber.onNext(response.body());
                        }
                        else{
                            Toast.makeText(context,"Currently there are no friends",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<FriendsList> call, Throwable t) {}
                });
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<FriendsList>() {
                    @Override
                    public void call(FriendsList friendsList) {
                        boolean b=false;
                        for(int uid:friendsList.getList()){
                            FriendDB fdb=new FriendDB(context,null,null,1);
                            b=fdb.addFriend(uid);
                        }
                        if(b)
                            Toast.makeText(context,"Friend List Synchronized",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context,"Unable to Synchronized",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateLocation(final int uid, Observable<Location> obs, final Context context){
        obs.observeOn(Schedulers.newThread())
        .subscribe(new Action1<Location>() {
            @Override
            public void call(final Location location) {
                if(location==null){
                    Toast.makeText(context, "Unable to Update Location.", Toast.LENGTH_SHORT).show();
                }
                else {
                    final LocationUpdate locationUpdate = new LocationUpdate(uid, "" + location.getLatitude()
                            , "" + location.getLongitude());
                    Call<DefaultResponse> responseCall = mainInterface.updateLocation(locationUpdate);
                    responseCall.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                            if (response.body().getStatus().equals("ok")) {
                                Toast.makeText(context, "Your Location Updated.", Toast.LENGTH_SHORT).show();
                                PrefUtils prefUtils=new PrefUtils(context);
                                prefUtils.setNewLat(location.getLatitude());
                                prefUtils.setNewLon(location.getLongitude());
                            }
                            else
                                Toast.makeText(context, "Unable to Update Location", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        }
                    });
                }
            }
        });
    }


    public void updateLocation(final int uid,final String lat,final String lon, final Context context){
        final LocationUpdate locationUpdate=new LocationUpdate(uid,lat,lon);
        Observable.create(new Observable.OnSubscribe<DefaultResponse>() {
            @Override
            public void call(final Subscriber<? super DefaultResponse> subscriber) {
                Call<DefaultResponse> responsecall=mainInterface.updateLocation(locationUpdate);
                responsecall.enqueue(new Callback<DefaultResponse>() {
                     @Override
                     public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                         if(response.body().getStatus().equals("ok")) {
                             Toast.makeText(context, "Your Location Updated.", Toast.LENGTH_SHORT).show();
                             subscriber.onNext(response.body());
                         }
                         else
                             Toast.makeText(context, "Unable to Update Location", Toast.LENGTH_SHORT).show();
                     }
                     @Override
                     public void onFailure(Call<DefaultResponse> call, Throwable t) {}
                 }
                );
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<DefaultResponse>() {
                    @Override
                    public void call(DefaultResponse defaultResponse) {
                        PrefUtils prefUtils=new PrefUtils(context);
                        prefUtils.setNewLat(Double.parseDouble(lat));
                        prefUtils.setNewLon(Double.parseDouble(lon));
                    }
                });
    }

    public void getFriendsLocation(int uid,final int fuid,final Context context){
        final RequestBody ruid=RequestBody.create(MediaType.parse("text/plain"),""+uid);
        final RequestBody rfuid=RequestBody.create(MediaType.parse("text/plain"),""+fuid);
        Observable.create(new Observable.OnSubscribe<FriendsLocation>() {
            @Override
            public void call(final Subscriber<? super FriendsLocation> subscriber) {
                Call<FriendsLocation> friendsLocation=mainInterface.getFriendsLocation(ruid,rfuid);
                friendsLocation.enqueue(new Callback<FriendsLocation>() {
                    @Override
                    public void onResponse(Call<FriendsLocation> call, Response<FriendsLocation> response) {
                        if(response.body().getStatus().equals("ok")){
                            subscriber.onNext(response.body());
                        }
                        subscriber.onCompleted();
                    }
                    @Override
                    public void onFailure(Call<FriendsLocation> call, Throwable t) {}
                });
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<FriendsLocation>() {
                    @Override
                    public void call(FriendsLocation friendsLocation) {
                        FriendDB fdb = new FriendDB(context, null, null, 1);
                        if (friendsLocation.getLat() != null && friendsLocation.getLon() != null) {
                            String address;
                            String city;
                            try {
                                Geocoder gc = new Geocoder(context, Locale.getDefault());
                                address = gc.getFromLocation(Double.parseDouble(friendsLocation.getLat()),
                                        Double.parseDouble(friendsLocation.getLon()), 1).get(0).getAddressLine(0);
                                city = gc.getFromLocation(Double.parseDouble(friendsLocation.getLat()),
                                        Double.parseDouble(friendsLocation.getLon()), 1).get(0).getLocality();

                            } catch (IOException e) {
                                address = null;
                                city = null;
                            }
                            boolean b = fdb.updateFriend("" + fuid, friendsLocation.getLat(), friendsLocation.getLon(),
                                    friendsLocation.getName(), friendsLocation.getTime(), address, city);
                            Log.v("datafetcher", fuid + "friend location updated");
                        }
                        else{
                            boolean b = fdb.updateFriend("" + fuid, friendsLocation.getLat(), friendsLocation.getLon(),
                                    friendsLocation.getName(), friendsLocation.getTime(), null, null);
                            Log.v("datafetcher", fuid + "friend location updated");
                        }
                    }
                });
    }

    public void signin(String email, String pass,final Context context){
        final RequestBody remail=RequestBody.create(MediaType.parse("text/plain"),email);
        final RequestBody rpass=RequestBody.create(MediaType.parse("text/plain"),pass);
        Observable.create(new Observable.OnSubscribe<SignInResponse>() {
            @Override
            public void call(final Subscriber<? super SignInResponse> subscriber) {
                Call<SignInResponse> signin=mainInterface.signIn(remail,rpass);
                signin.enqueue(new Callback<SignInResponse>() {
                    @Override
                    public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                        if(response.body().getStatus().equals("ok")){
                            Toast.makeText(context,"Sign In successful ( "+response.body().getName()+" , "+
                                    response.body().getUid()+" )",Toast.LENGTH_SHORT).show();
                            subscriber.onNext(response.body());
                        }
                        else
                            Toast.makeText(context,"Sign In Failed",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<SignInResponse> call, Throwable t) {}
                });
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<SignInResponse>() {
                    @Override
                    public void call(SignInResponse signInResponse) {
                        PrefUtils prefUtils=new PrefUtils(context);
                        prefUtils.setUID(Integer.parseInt(signInResponse.getUid()));
                        prefUtils.setUsername(signInResponse.getName());
                        prefUtils.setEMAIL(signInResponse.getEmail());
                        getFriends(signInResponse.getUid(),context);
                        getFriendRequests(signInResponse.getUid(),context);
                    }
                });
    }

    public void getFriendRequests(String uid,final Context context){
        final RequestBody ruid= RequestBody.create(MediaType.parse("text/plain"),uid);
        final FriendDB fdb=new FriendDB(context,null,null,1);
        Observable.create(new Observable.OnSubscribe<FriendRequest>() {
            @Override
            public void call(final Subscriber<? super FriendRequest> subscriber) {
                Call<FriendRequest> responseCall=mainInterface.getRequests(ruid);
                responseCall.enqueue(new Callback<FriendRequest>() {
                    @Override
                    public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {
                        if(response.body().getStatus().equals("ok")){
                            subscriber.onNext(response.body());
                        }
                    }
                    @Override
                    public void onFailure(Call<FriendRequest> call, Throwable t) {}
                });

            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<FriendRequest>() {
                    @Override
                    public void call(FriendRequest friendRequest) {
                        ArrayList<HashMap<String,String>> al=friendRequest.getList();
                        for(HashMap<String,String> hash:al){
                            if(fdb.isNewRequest(hash.get("uid"))){
                                fdb.addRequest(hash.get("uid"),hash.get("name"),hash.get("email"));
                                NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                                notification.setContentText(hash.get("name")+" has sent you friend request");
                                notification.setSmallIcon(R.drawable.app_icon);
                                notification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                notification.setContentTitle("A New Friend Request");
                                TaskStackBuilder stackBuilder=TaskStackBuilder.create(context);
                                stackBuilder.addNextIntent(new Intent(context,FriendRequests.class));
                                stackBuilder.addParentStack(MainActivity.class);
                                PendingIntent pi2=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                                notification.setContentIntent(pi2);
                                notification.addAction(R.drawable.open_app,"View in App",pi2);
                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(Integer.parseInt(hash.get("uid")), notification.build());
                            }
                        }
                    }
                });
    }

    public void acceptRequest(String uid, String fuid,final String name,final Context context){
        final RequestBody ruid=RequestBody.create(MediaType.parse("text/plain"),uid);
        final RequestBody rfuid=RequestBody.create(MediaType.parse("text?plain"),fuid);

        Observable.create(new Observable.OnSubscribe<DefaultResponse>() {
            @Override
            public void call(final Subscriber<? super DefaultResponse> subscriber) {
                Call<DefaultResponse> responseCall=mainInterface.acceptRequest(ruid,rfuid);
                responseCall.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        if(response.body().getStatus().equals("ok")){
                            Toast.makeText(context,name+" now is able to track you!!",Toast.LENGTH_SHORT).show();
                            subscriber.onNext(response.body());
                        }
                        else {
                            Toast.makeText(context,"Unable to accept FriendRequest",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {}
                });
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<DefaultResponse>() {
                    @Override
                    public void call(DefaultResponse defaultResponse) {
                        FriendDB fdb=new FriendDB(context,null,null,1);
                        fdb.deleteRequests(defaultResponse.getUid()+"");
                    }
                });
    }

}