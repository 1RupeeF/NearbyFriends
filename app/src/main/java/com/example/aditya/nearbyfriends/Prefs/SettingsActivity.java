package com.example.aditya.nearbyfriends.Prefs;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.aditya.nearbyfriends.R;
public class SettingsActivity extends AppCompatPreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
