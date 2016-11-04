package com.example.aditya.nearbyfriends.Activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.nearbyfriends.HttpRequest;
import com.example.aditya.nearbyfriends.Pojos.DefaultResponse;
import com.example.aditya.nearbyfriends.Pojos.RecoverRequest;
import com.example.aditya.nearbyfriends.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PasswordRecovery extends AppCompatActivity {

    @BindView(R.id.email) TextInputEditText email;
    @BindView(R.id.password) TextInputEditText pass;
    @BindView(R.id.code) TextInputEditText code;
    @BindView(R.id.sendcode) Button sendcode;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.activity_password_recovery) CoordinatorLayout mainLayout;
    @BindView(R.id.bottom) LinearLayout linearLayout;
    HttpRequest.MainInterface mainInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        ButterKnife.bind(this);
        title.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/DroidSans.ttf"));
        sendcode.setText("Send");
        linearLayout.setVisibility(View.INVISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainInterface= HttpRequest.retrofit.create(HttpRequest.MainInterface.class);
    }

    @OnClick(R.id.recover)
    public void recover(){
        final RecoverRequest recoverRequest=new RecoverRequest(email.getText().toString(),
                pass.getText().toString(),Integer.parseInt(code.getText().toString()));
        Observable.create(new Observable.OnSubscribe<DefaultResponse>() {
            @Override
            public void call(final Subscriber<? super DefaultResponse> subscriber) {
                Call<DefaultResponse> responseCall=mainInterface.recover(recoverRequest);
                responseCall.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        if(response.body().getStatus().equals("ok")){
                            subscriber.onNext(response.body());
                        }
                        else {
                            Snackbar.make(mainLayout,"Invalid Email or Code",Snackbar.LENGTH_LONG)
                                    .setAction("Try Again", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            recover();
                                        }
                                    })
                                    .setActionTextColor(Color.YELLOW)
                                    .show();
                        }
                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {}
                });
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<DefaultResponse>() {
                    @Override
                    public void call(DefaultResponse defaultResponse) {
                        Snackbar.make(mainLayout,"Password Successfully Recovered",Snackbar.LENGTH_LONG)
                                .setAction("SignIn", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(getApplicationContext(),SignUp.class));
                                    }
                                })
                                .setActionTextColor(Color.YELLOW)
                                .show();
                        email.setText("");
                        pass.setText("");
                        code.setText("");
                    }
                });


    }
    @OnClick(R.id.sendcode)
    public void sendCode(){
        if(!isvalidEmail(email.getText().toString())){
            email.setError("Invalid Email");
            return;
        }
        final RequestBody remail=RequestBody.create(MediaType.parse("text/plain"),email.getText().toString());
        Observable.create(new Observable.OnSubscribe<DefaultResponse>() {
            @Override
            public void call(final Subscriber<? super DefaultResponse> subscriber) {
                Call<DefaultResponse> responseCall=mainInterface.sendCode(remail);
                responseCall.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        if(response.body().getStatus().equals("ok")){
                            subscriber.onNext(response.body());
                        }
                        else{
                            Snackbar.make(mainLayout,"Sending Code Failed",Snackbar.LENGTH_LONG)
                                    .setAction("Send Again", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sendCode();
                                        }
                                    })
                                    .setActionTextColor(Color.YELLOW)
                                    .show();
                        }
                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {}
                });
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DefaultResponse>() {
                    @Override
                    public void call(DefaultResponse defaultResponse) {
                        Snackbar.make(mainLayout,"Code send to "+email.getText().toString(),Snackbar.LENGTH_LONG).show();
                        Toast toast=Toast.makeText(getApplicationContext(),"Check your Email for code.",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                });
        sendcode.setText("Resend Code");
    }


    public boolean isvalidEmail(String e){
        return !e.equals("") && Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }
}
