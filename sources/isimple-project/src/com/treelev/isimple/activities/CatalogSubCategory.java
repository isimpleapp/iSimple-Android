package com.treelev.isimple.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.CatalogItemCursorAdapter;
import com.treelev.isimple.cursorloaders.SelectItemsBySubCategory;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

public class CatalogSubCategory extends BaseListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static Class backActivity;
    public static Integer categoryID;
    private Integer mCategoryID;
    private CatalogItemCursorAdapter mListSubCategoriesAdapter;
    private ProxyManager mProxyManager;
    private String mDrinkID;
    private int mSortBy = ProxyManager.SORT_PRICE_UP;
    private String mFilterWhereClause;
    private String mLocationId;
    private String mBarcode;
    private String mBarcodeFromBaseActivity;
    private String mBarcodeFromBaseExpandListActivity;
    private Dialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_sub_category);
        mLocationId = getIntent().getStringExtra(ShopInfoActivity.LOCATION_ID);
        if(mLocationId == null ){
            setCurrentCategory(0); //Catalog
        } else {
            setCurrentCategory(1); //Shop
        }
        createNavigationMenuBar();
        View mDarkView = findViewById(R.id.category_dark_view);
        mDarkView.setVisibility(View.GONE);
        mDarkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mDrinkID = getIntent().getStringExtra(CatalogListActivityNew.DRINK_ID);
        mBarcode = getIntent().getStringExtra(BaseListActivity.BARCODE);
        mFilterWhereClause = getIntent().getStringExtra(CatalogListActivityNew.FILTER_WHERE_CLAUSE);
        mListSubCategoriesAdapter = new CatalogItemCursorAdapter(null, CatalogSubCategory.this, false, true);
        getListView().setAdapter(mListSubCategoriesAdapter);
        getSupportLoaderManager().restartLoader(0, null, this);
    }


    @Override
    protected void onResume() {
        if(mEventChangeDataBase){
            getSupportLoaderManager().restartLoader(0, null, this);
            mEventChangeDataBase = false;
        }
        super.onResume();
    }

    public void createNavigationMenuBar(){
        createDrawableMenu();
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startIntent.putExtra(ShopInfoActivity.LOCATION_ID, mLocationId);
        startIntent.putExtra(BaseListActivity.BARCODE, mBarcode);
        startActivityForResult(startIntent, 0);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mDialog = ProgressDialog.show(this, this.getString(R.string.dialog_title),
                this.getString(R.string.dialog_select_data_message), false, false);
        return new SelectItemsBySubCategory(this, mDrinkID, mFilterWhereClause, mLocationId, mBarcode, mSortBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListSubCategoriesAdapter.swapCursor(cursor);
        mDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListSubCategoriesAdapter.swapCursor(null);
    }
}
