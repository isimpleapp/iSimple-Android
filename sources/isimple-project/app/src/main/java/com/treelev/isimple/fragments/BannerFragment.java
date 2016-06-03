
package com.treelev.isimple.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.BannerInfoActivity;
import com.treelev.isimple.activities.CatalogListActivityNew;

public class BannerFragment extends Fragment {

    public static final String BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL = "BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL";
    public static final String BANNER_FRAGMENT_ARGUMENT_OFFER_ID = "BANNER_FRAGMENT_ARGUMENT_OFFER_ID";

    private ImageLoaderProvider imageLoaderProvider;
    
    private CatalogListActivityNew activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            imageLoaderProvider = (ImageLoaderProvider) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity should implement ImageLoaderProvider interface!");
        }
        
        this.activity = (CatalogListActivityNew) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView view = (ImageView) inflater.inflate(R.layout.fragment_banner, container, false);
        String bannerImageUrl = getArguments().getString(BANNER_FRAGMENT_ARGUMENT_BANNER_IMAGE_URL);
        final long offerId = getArguments().getLong(BANNER_FRAGMENT_ARGUMENT_OFFER_ID, -1);
        if (offerId == -1) {
            throw new RuntimeException("Offer id should be passed as an argument!");
        }
        // gestureDetector = new GestureDetector(getActivity(), new
        // OnGestureListener() {
        //
        // @Override
        // public boolean onSingleTapUp(MotionEvent e) {
        // Intent intent = new Intent(getActivity(), BannerInfoActivity.class);
        // intent.putExtra(BannerInfoActivity.OFFER_ID, offerId);
        // getActivity().startActivity(intent);
        // return true;
        // }
        //
        // @Override
        // public void onShowPress(MotionEvent e) {
        //
        // }
        //
        // @Override
        // public boolean onScroll(MotionEvent e1, MotionEvent e2, float
        // distanceX, float distanceY) {
        // return false;
        // }
        //
        // @Override
        // public void onLongPress(MotionEvent e) {
        //
        // }
        //
        // @Override
        // public boolean onFling(MotionEvent e1, MotionEvent e2, float
        // velocityX, float velocityY) {
        // return false;
        // }
        //
        // @Override
        // public boolean onDown(MotionEvent e) {
        // return true;
        // }
        // });
        // view.setOnTouchListener(new OnTouchListener() {
        //
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        // return gestureDetector.onTouchEvent(event);
        // }
        // });
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BannerInfoActivity.class);
                intent.putExtra(BannerInfoActivity.OFFER_ID, offerId);
                getActivity().startActivity(intent);
            }
        });
        view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        activity.removeHandlerCallbacks();
                        break;
                    case MotionEvent.ACTION_UP:
                        activity.restoreHandlerCallbacks();
                        break;
                }
                return false;
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
