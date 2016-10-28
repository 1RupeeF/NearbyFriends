package com.example.aditya.nearbyfriends.Prefs;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.aditya.nearbyfriends.MainActivity;
import com.example.aditya.nearbyfriends.R;
public class SettingsActivity extends AppCompatPreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
