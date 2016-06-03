package com.treelev.isimple.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.treelev.isimple.domain.db.Offer;
import com.treelev.isimple.fragments.BannerFragment;

import java.util.List;

public class BannersPagerAdapter extends FragmentStatePagerAdapter {
    
    List<Offer> offersList;

    public BannersPagerAdapter(FragmentManager fm, List<Offer> bannersUrlsList) {
        super(fm);
        
        this.offersList = bannersUrlsList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new BannerFragment();
        Bundle args = new Bundle();
        Offer offer = offersList.get(position);
        args.putString(BannerFragment.BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL, offer.getImage1200());
        args.putLong(BannerFragment.BANNER_FRAGMENT_ARGUMENT_OFFER_ID, offer.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return offersList.size();
    }

}
