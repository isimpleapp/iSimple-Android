package com.treelev.isimple.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.itemstreecursoradapter.CatalogSubCategoryItemTreeCursorAdapter;
import com.treelev.isimple.cursorloaders.SelectSectionsItems;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.widget.ExpandableListView;

public class CatalogSubCategoryTree extends BaseExpandableListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private CatalogSubCategoryItemTreeCursorAdapter mTreeAdapter;
    private String mLocationId;
    private int mSortBy = ProxyManager.SORT_PRICE_UP;
    private String mFilterWhereClause;
    private String mDrinkID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_sub_category_tree);
        mLocationId = getIntent().getStringExtra(ShopInfoActivity.LOCATION_ID);
        if(mLocationId == null ){
            setCurrentCategory(0); //Catalog
        } else {
            setCurrentCategory(1); //Shop
        }
        createNavigationMenuBar();
        mFilterWhereClause = getIntent().getStringExtra(CatalogListActivity.FILTER_WHERE_CLAUSE);
        mDrinkID = getIntent().getStringExtra(CatalogListActivity.DRINK_ID);
        mTreeAdapter = new CatalogSubCategoryItemTreeCursorAdapter(this, null, getSupportLoaderManager(), mDrinkID, mFilterWhereClause, mLocationId, mSortBy);
        getExpandableListView().setAdapter(mTreeAdapter);
        disableOnGroupClick();
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean addFavourite = data.getBooleanExtra(ProductInfoActivity.CHANGE_FAVOURITE, false);
        if(addFavourite){
            getSupportLoaderManager().restartLoader(0, null, this);
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
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Cursor product = mTreeAdapter.getChild(groupPosition, childPosition);
        Intent startIntent;
        startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startActivityForResult(startIntent, 0);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new SelectSectionsItems(this, ProxyManager.TYPE_SECTION_SUB_CATEGORY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mTreeAdapter.setGroupCursor(cursor);
        expandAllGroup();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
