package com.example.aditya.nearbyfriends.Activities;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignUp extends AppCompatActivity {


    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.activity_sign_up) CoordinatorLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        setupViewPager(viewPager);

        String tabText[]={"SignIn (Old User)","SignUp (New User)"};
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setText(tabText[i]);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        SignFragmentAdapter adapter = new SignFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new Login());
        adapter.addFragment(new Register());
        viewPager.setAdapter(adapter);
    }

    public void alreadySignedIn(){
        Snackbar.make(mainLayout, "Already Signed In", Snackbar.LENGTH_SHORT)
                .setAction("Home", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .setActionTextColor(Color.YELLOW)
                .show();
    }
}
