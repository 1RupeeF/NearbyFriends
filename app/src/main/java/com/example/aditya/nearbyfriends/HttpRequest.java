package com.example.aditya.nearbyfriends;

import com.example.aditya.nearbyfriends.Activities.FriendRequests;
import com.example.aditya.nearbyfriends.Pojos.DefaultResponse;
import com.example.aditya.nearbyfriends.Pojos.FriendRequest;
import com.example.aditya.nearbyfriends.Pojos.FriendsList;
import com.example.aditya.nearbyfriends.Pojos.FriendsLocation;
import com.example.aditya.nearbyfriends.Pojos.LocationUpdate;
import com.example.aditya.nearbyfriends.Pojos.RecoverRequest;
import com.example.aditya.nearbyfriends.Pojos.RegisterRequest;
import com.example.aditya.nearbyfriends.Pojos.SignInResponse;
import com.example.aditya.nearbyfriends.db.DataFetcher;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by aditya on 24/10/16.
 */

public class HttpRequest {
    public static final String API_URL = "http://adityajain.pe.hu/nbfsapp/";
    public static Retrofit retrofit= new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public interface MainInterface{

        @POST("register.php")
        Call<DefaultResponse> register(@Body RegisterRequest request);

        @Multipart
        @POST("addfriends.php")
        Call<DefaultResponse> addFriends(
                @Part("uid") RequestBody uid,
                @Part("fuid") RequestBody fuid );

        @Multipart
        @POST("getfriends.php")
        Call<FriendsList> getFriends(
                @Part("uid") RequestBody uid);

        @POST("updatelocation.php")
        Call<DefaultResponse> updateLocation(
                @Body LocationUpdate locationUpdate);


        @Multipart
        @POST("getfriendslocation.php")
        Call<FriendsLocation> getFriendsLocation(
                @Part("uid") RequestBody uid,
                @Part("fuid") RequestBody fuid);

        @Multipart
        @POST("acceptrequest.php")
        Call<DefaultResponse> acceptRequest(
                @Part("uid") RequestBody uid,
                @Part("fuid") RequestBody fuid);

        @Multipart
        @POST("signin.php")
        Call<SignInResponse> signIn(
                @Part("email") RequestBody email,
                @Part("pass") RequestBody pass);

        @Multipart
        @POST("getrequests.php")
        Call<FriendRequest> getRequests(
                @Part("uid") RequestBody uid);

        @Multipart
        @POST("rejectasstracker.php")
        Call<DefaultResponse> reject(
                @Part("uid") RequestBody uid,
                @Part("fuid") RequestBody fuid);

        @Multipart
        @POST("getPeopleTracking.php")
        Call<FriendRequest> getTrackers(
                @Part("uid") RequestBody uid);

        @Multipart
        @POST("sendcode.php")
        Call<DefaultResponse> sendCode(
                @Part("email") RequestBody email);

        @POST("recover.php")
        Call<DefaultResponse> recover(
                @Body RecoverRequest recoverRequest);

        @Multipart
        @POST("search.php")
        Call<FriendRequest> search(
                @Part("pattern") RequestBody pattern);
    }
}
