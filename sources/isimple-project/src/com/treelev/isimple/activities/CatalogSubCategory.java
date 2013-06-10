package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ItemCursorAdapter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

public class CatalogSubCategory extends BaseListActivity implements RadioGroup.OnCheckedChangeListener {

    public static Class backActivity;
    public static Integer categoryID;
    private Integer mCategoryID;
    private Cursor cItems;
    private SimpleCursorAdapter mListCategoriesAdapter;
    private ProxyManager mProxyManager;
    private String mDrinkID;
    private String mFilterWhereClause;
    private String mLocationId;
    private String mBarcode;
    private String mBarcodeFromBaseActivity;
    private String mBarcodeFromBaseExpandListActivity;
    private final static int BARCODE = 1;
    private final static int DRINK_ID = 2;
    private final static int FILTER_WHERE_CLAUSE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        mLocationId = getIntent().getStringExtra(ShopInfoActivity.LOCATION_ID);
        if(mLocationId == null ){
            setCurrentCategory(0); //Catalog
        } else {
            setCurrentCategory(1); //Shop
        }
        createNavigationMenuBar();
        RadioGroup rg = (RadioGroup) findViewById(R.id.sort_group);
        rg.setOnCheckedChangeListener(this);
        View mDarkView = findViewById(R.id.category_dark_view);
        mDarkView.setVisibility(View.GONE);
        mDarkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

//        mBarcode = getIntent().getStringExtra(BaseListActivity.BARCODE);
//        mBarcodeFromBaseActivity = getIntent().getStringExtra(BaseActivity.BARCODE);
//        mBarcodeFromBaseExpandListActivity = getIntent().getStringExtra(BaseExpandableListActivity.BARCODE);
        mDrinkID = getIntent().getStringExtra(CatalogByCategoryActivity.DRINK_ID);
        mBarcode = getIntent().getStringExtra(BaseListActivity.BARCODE);
        mFilterWhereClause = getIntent().getStringExtra(CatalogByCategoryActivity.FILTER_WHERE_CLAUSE);
        if (mBarcode != null) {
            new SelectBy(this, BARCODE).execute(mBarcode);
        } else if(!TextUtils.isEmpty(mFilterWhereClause)) {
            new SelectBy(this, FILTER_WHERE_CLAUSE).execute(mDrinkID, mFilterWhereClause);
        } else {
            new SelectBy(this, DRINK_ID).execute(mDrinkID);
        }

    }

    @Override
    public void createNavigationMenuBar(){
        super.createNavigationMenuBar();
        if(mLocationId != null) {
            getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
        }
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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int rgb) {
        int sortBy = 0;
        switch (rgb) {
            case R.id.alphabet_sort:
                sortBy = ProxyManager.SORT_NAME_AZ;
                break;
            case R.id.price_sort:
                sortBy = ProxyManager.SORT_PRICE_UP;
                break;
        }

        if (mBarcode != null) {
            new SortTask(this, BARCODE).execute(sortBy);
        } else if(!TextUtils.isEmpty(mFilterWhereClause)) {
            new SortTask(this, FILTER_WHERE_CLAUSE).execute(sortBy);
        } else {
            new SortTask(this, DRINK_ID).execute(sortBy);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startIntent.putExtra(ShopInfoActivity.LOCATION_ID, mLocationId);
        startIntent.putExtra(BaseListActivity.BARCODE, mBarcode);
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProxyManager != null) {
            mProxyManager.release();
            mProxyManager = null;
        }
    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = new ProxyManager(this);
        }
        return mProxyManager;
    }

    private class SelectBy extends AsyncTask<String, Void, Cursor> {

        private final static int BARCODE = 1;
        private final static int DRINK_ID = 2;

        private Dialog mDialog;
        private Context mContext;
        private ProxyManager mProxyManager;
        private int mSelectWhere;

        private SelectBy(Context context, int select) {
            mContext = context;
            mSelectWhere = select;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
        }

        @Override
        protected Cursor doInBackground(String... params) {
            switch (mSelectWhere) {
                case BARCODE:
                    Cursor myCursor = getProxyManager().getItemByBarcode(mBarcode, ProxyManager.SORT_NAME_AZ);
                    if(myCursor.getCount() == 0){
                        myCursor = getProxyManager().getItemDeprecatedByBarcode(mBarcode, ProxyManager.SORT_NAME_AZ);
                    }
                    return myCursor;
                case DRINK_ID:
                    return getProxyManager().getItemsByDrinkId(params[0], ProxyManager.SORT_NAME_AZ);
                case FILTER_WHERE_CLAUSE:
                    return getProxyManager().getItemsByDrinkId(params[0], params[1], ProxyManager.SORT_NAME_AZ);
                default:
                    return null;
            }

        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems, CatalogSubCategory.this, false, true);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }

    private class SortTask extends AsyncTask<Integer, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;
        private int mSelectWhere;

        private SortTask(Context context, int select) {
            mContext = context;
            mSelectWhere = select;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_sort_message), false, false);
        }

        @Override
        protected Cursor doInBackground(Integer... params) {
            switch (mSelectWhere) {
                case BARCODE:
                    Cursor myCursor = getProxyManager().getItemByBarcode(mBarcode, params[0]);
                    if(myCursor.getCount() == 0){
                        myCursor = getProxyManager().getItemDeprecatedByBarcode(mBarcode, params[0]);
                    }
                    return myCursor;
                case DRINK_ID:
                    return getProxyManager().getItemsByDrinkId(mDrinkID, params[0]);
                case FILTER_WHERE_CLAUSE:
                    return getProxyManager().getItemsByDrinkId(mDrinkID, mFilterWhereClause, params[0]);
                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems, CatalogSubCategory.this, false, true);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }

}
