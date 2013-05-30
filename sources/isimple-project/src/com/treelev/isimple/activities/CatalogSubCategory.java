package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
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
    private String mBarcode;
    private String mBarcodeFromBaseActivity;
    private String mBarcodeFromBaseExpandListActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        setCurrentCategory(0);
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
        if (mBarcode != null) {
            new SelectBy(this, SelectBy.BARCODE).execute(mBarcode);
        } else {
            new SelectBy(this, SelectBy.DRINK_ID).execute(mDrinkID);
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
        new SortTask(this).execute(sortBy);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
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
                    return getProxyManager().getItemByBarcode(mBarcode);
                case DRINK_ID:
                    return getProxyManager().getItemsByDrinkId(params[0], ProxyManager.SORT_NAME_AZ);
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

        private SortTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_sort_message), false, false);
        }

        @Override
        protected Cursor doInBackground(Integer... params) {
            return getProxyManager().getItemsByDrinkId(mDrinkID, params[0]);
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
