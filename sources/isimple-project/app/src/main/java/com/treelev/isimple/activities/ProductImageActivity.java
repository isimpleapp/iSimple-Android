package com.treelev.isimple.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.treelev.isimple.R;
import com.treelev.isimple.utils.Utils;

public class ProductImageActivity extends BaseActivity {

    public static final String HI_RESOLUTION_IMAGE_FILE_NAME = "bottleHiResolutionImageFilename";

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String mImageFilename;
    private int mCurrentCategory;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_image_layout);
        createNavigationMenuBar();
        mImageFilename = getIntent().getStringExtra(HI_RESOLUTION_IMAGE_FILE_NAME);
        initImageLoader();
        setImage();
    }

    private void setImage() {
        ImageView imageView = (ImageView) findViewById(R.id.view_product_image);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        String sizePrefix = "_xhdpi";
//        String sizePrefix = metrics.densityDpi == DisplayMetrics.DENSITY_HIGH ? "_hdpi" :
//                        metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH ? "_xhdpi" : "";
        imageLoader.displayImage(String.format("http://s1.isimpleapp.ru/img/ver0/%1$s%2$s_product.jpg", mImageFilename.replace('\\', '/'), sizePrefix), imageView, options);
    }

    private void initImageLoader() {
        imageLoader = Utils.getImageLoader(getApplicationContext());
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.product_default_image)
                .showImageForEmptyUri(R.drawable.product_default_image)
                .showImageOnFail(R.drawable.product_default_image)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
    }

    protected void createNavigationMenuBar() {
        createDrawableMenu();
        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }
}