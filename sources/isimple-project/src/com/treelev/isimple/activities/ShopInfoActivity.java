package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.domain.db.Shop;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.TextView;

import java.util.List;

public class ShopInfoActivity extends BaseActivity implements View.OnClickListener {

    public static final String SHOP = "SHOP";
    public static final String LOCATION_ID = "LOCATION_ID";

    private Shop mShop;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.shop_info_layout);
        setCurrentCategory(1);
        createNavigationMenuBar();
        mShop = (Shop) getIntent().getSerializableExtra(SHOP);
        TextView tv = (TextView) findViewById(R.id.title_shop);
        tv.setText(mShop.getLocationName());
        tv = (TextView) findViewById(R.id.adress_shop);
        tv.setText(mShop.getLocationAddress());
        Button btn = (Button) findViewById(R.id.category_wine_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_spirits_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_sparkling_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_porto_heres_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_sake_butt);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.category_water_butt);
        btn.setOnClickListener(this);
        new InitButtonCategory(this).execute(mShop);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Analytics.screen_StoreCard(this);
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

    protected void createNavigationMenuBar() {
        createDrawableMenu();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
    }

    @Override
    public void onClick(View view) {
        Intent startIntent = new Intent(this, CatalogByCategoryActivity.class);
        Integer category = null;
        switch (view.getId()) {
            case R.id.category_wine_butt:
                category = 0;
                break;
            case R.id.category_spirits_butt:
                category = 3;
                break;
            case R.id.category_sparkling_butt:
                category = 1;
                break;
            case R.id.category_porto_heres_butt:
                category = 2;
                break;
            case R.id.category_sake_butt:
                category = 4;
                break;
            case R.id.category_water_butt:
                category = 5;
                break;
        }
        startIntent.putExtra(CatalogListActivity.CATEGORY_ID, category);
        startIntent.putExtra(LOCATION_ID, mShop.getLocationID());
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    public void onBuildRouteButtonClick(View view) {
        Intent startIntent = new Intent(this, RouteDisplayActivity.class);
        startIntent.putExtra(SHOP, mShop);
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private void initButton(List<Boolean> listEnable){
        int idButton = -1;
        Button btnCategory;
        for(int categoryID = 0; categoryID < 6; ++categoryID){
            switch(categoryID) {
                case 0:
                    idButton = R.id.category_wine_butt;
                    break;
                case 1:
                    idButton = R.id.category_sparkling_butt;
                    break;
                case 2:
                    idButton = R.id.category_porto_heres_butt;
                    break;
                case 3:
                    idButton = R.id.category_spirits_butt;
                    break;
                case 4:
                    idButton = R.id.category_sake_butt;
                    break;
                case 5:
                    idButton = R.id.category_water_butt;
                    break;
            }
            btnCategory = (Button)findViewById(idButton);
            btnCategory.setEnabled(listEnable.get(categoryID));
            if(listEnable.get(categoryID) == false){
                btnCategory.setBackgroundResource(R.drawable.category_butt_disable_selector);
            }
        }

    }

    private class InitButtonCategory extends AsyncTask<Shop, Void, List<Boolean>>{

        private Context mContext;
        private Dialog mDialog;
        private ProxyManager mProxyManager;

        public InitButtonCategory(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
        }

        @Override
        protected List<Boolean> doInBackground(Shop... shops) {
            List<Boolean> listEnableButton = null;
            if(shops[0] != null){
                listEnableButton = getProxyManager().getCountItemsByCategoryByShop(shops[0].getLocationID());
            }
            return listEnableButton;
        }

        @Override
        protected void onPostExecute(List<Boolean> listEnable) {
            super.onPostExecute(listEnable);
            if(listEnable != null){
                initButton(listEnable);
            }
            mDialog.dismiss();
        }

        protected ProxyManager getProxyManager() {
            if (mProxyManager == null) {
                mProxyManager = new ProxyManager(mContext);
            }
            return mProxyManager;
        }
    }
}
