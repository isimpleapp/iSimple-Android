package com.treelev.isimple.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.BannerInfoActivity;

public class BannerFragment extends Fragment {
    
    public static final String BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL = "BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL";
    public static final String BANNER_FRAGMENT_ARGUMENT_OFFER_ID = "BANNER_FRAGMENT_ARGUMENT_OFFER_ID";
    
    private ImageLoaderProvider imageLoaderProvider;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            imageLoaderProvider = (ImageLoaderProvider) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity should implement ImageLoaderProvider interface!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView view = (ImageView) inflater.inflate(R.layout.fragment_banner, container, false);
        String bannerImageUrl = getArguments().getString(BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL);
        final long offerId = getArguments().getLong(BANNER_FRAGMENT_ARGUMENT_OFFER_ID, -1);
        if (offerId == -1) {
            throw new RuntimeException("Offer id should be passed as an argument!");
        }
        view.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BannerInfoActivity.class);
                intent.putExtra(BannerInfoActivity.OFFER_ID, offerId);
                getActivity().startActivity(intent);
            }
        });
        imageLoaderProvider.getInitializedImageLoader().displayImage(bannerImageUrl, view,
                imageLoaderProvider.getImageLoaderOptions());
        return view;
    }
    
    public interface ImageLoaderProvider {
        public ImageLoader getInitializedImageLoader();
        public DisplayImageOptions getImageLoaderOptions();
    }

}
