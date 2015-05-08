package com.treelev.isimple.adapters;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.treelev.isimple.fragments.BannerFragment;
import com.treelev.isimple.utils.LogUtils;

public class BannersPagerAdapter extends FragmentStatePagerAdapter {
    
    List<String> bannersUrlsList;

    public BannersPagerAdapter(FragmentManager fm, List<String> bannersUrlsList) {
        super(fm);
        
        this.bannersUrlsList = bannersUrlsList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new BannerFragment();
        Bundle args = new Bundle();
        LogUtils.i("", "BannerUrl = " + bannersUrlsList.get(position));
        args.putString(BannerFragment.BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL, bannersUrlsList.get(position));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return bannersUrlsList.size();
    }

}
