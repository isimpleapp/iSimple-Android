package com.treelev.isimple.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.treelev.isimple.R;

public class BannerFragment extends Fragment {
    
    public static final String BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL = "BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL";
    
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
        if (TextUtils.isEmpty(bannerImageUrl)) {
            throw new RuntimeException("Banner image url should be passed as an argument!");
        }
        imageLoaderProvider.getInitializedImageLoader().displayImage(bannerImageUrl, view,
                imageLoaderProvider.getImageLoaderOptions());
        return view;
    }
    
    public interface ImageLoaderProvider {
        public ImageLoader getInitializedImageLoader();
        public DisplayImageOptions getImageLoaderOptions();
    }

}
