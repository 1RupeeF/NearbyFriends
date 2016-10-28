package com.example.aditya.nearbyfriends.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 25/10/16.
 */

public class SignFragmentAdapter extends FragmentStatePagerAdapter  {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public SignFragmentAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
        //mFragmentTitleList.add(id);
        //HomeFragment.tabLayout.getTabAt(1).setIcon(id);
    }

    /*@Override
    public String getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }*/

}
