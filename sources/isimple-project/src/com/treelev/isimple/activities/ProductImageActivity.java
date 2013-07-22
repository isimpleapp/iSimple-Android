package com.treelev.isimple.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import com.actionbarsherlock.view.MenuItem;
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
        createNavigationMenuBar();
        setContentView(R.layout.product_image_layout);
        mImageFilename = getIntent().getStringExtra(HI_RESOLUTION_IMAGE_FILE_NAME);
        initImageLoader();
        setImage();
//        Button btnBack = (Button) findViewById(R.id.btn_back);
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    private void setImage(){
        ImageView imageView = (ImageView)findViewById(R.id.view_product_image);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        String sizePrefix = "_xhdpi";
//        String sizePrefix = metrics.densityDpi == DisplayMetrics.DENSITY_HIGH ? "_hdpi" :
//                        metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH ? "_xhdpi" : "";
        imageLoader.displayImage(
                String.format("http://s1.isimpleapp.ru/img/ver0/%1$s%2$s_product.jpg", mImageFilename.replace('\\', '/'), sizePrefix),
                imageView, options);
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

//    protected void createNavigationMenuBar() {
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        Resources resources = getResources();
//        String[] mainMenuLabelsArray = resources.getStringArray(R.array.main_menu_items);
//        TypedArray typedIconsArray = resources.obtainTypedArray(R.array.main_menu_icons);
//        Drawable[] iconLocation = getIconsList(typedIconsArray, mainMenuLabelsArray.length);
//        organizeNavigationMenu(iconLocation, mainMenuLabelsArray);
//    }
//
//    private Drawable[] getIconsList(TypedArray typedIconsArray, int navigationMenuBarLenght) {
//        Drawable[] iconLocation = new Drawable[typedIconsArray.length()];
//        for (int i = 0; i < navigationMenuBarLenght; ++i) {
//            iconLocation[i] = typedIconsArray.getDrawable(i);
//        }
//        return iconLocation;
//    }
//
//    private void organizeNavigationMenu(Drawable[] iconLocation, String[] mainMenuLabelsArray) {
//        NavigationListAdapter navigationAdapter = new NavigationListAdapter(this, iconLocation, mainMenuLabelsArray);
//        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//        getSupportActionBar().setListNavigationCallbacks(navigationAdapter, this);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
//        getSupportActionBar().setSelectedNavigationItem(mCurrentCategory);
//    }

//    public void setCurrentCategory(int currentCategory) {
//        mCurrentCategory = currentCategory;
//    }

    @Override
    protected void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
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