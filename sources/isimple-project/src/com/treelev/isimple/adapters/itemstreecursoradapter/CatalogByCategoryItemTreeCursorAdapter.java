package com.treelev.isimple.adapters.itemstreecursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.treelev.isimple.cursorloaders.SelectAllItemsByCategory;
import com.treelev.isimple.cursorloaders.SelectFeaturedByCategoryItems;
import com.treelev.isimple.cursorloaders.SelectFilteredByCategoryItems;
import com.treelev.isimple.cursorloaders.SelectFilteredByCategoryItemsPreOrder;
import com.treelev.isimple.utils.managers.ProxyManager;

public class CatalogByCategoryItemTreeCursorAdapter extends AbsItemTreeCursorAdapter{

    private Integer mCategoryID;
    private String mLocationID;
    private int mTypeSection;

    public CatalogByCategoryItemTreeCursorAdapter(Context context, Cursor cursor, LoaderManager manager, int sortBy) {
        super(context, cursor, manager, sortBy);
    }

    public void initCategory(Integer categoryID){
        mCategoryID = categoryID;
        mTypeSection = ProxyManager.TYPE_SECTION_MAIN;
    }

    public void initCategoryShop(Integer categoryID, String locationID){
        mCategoryID = categoryID;
        mLocationID = locationID;
        mTypeSection = ProxyManager.TYPE_SECTION_SHOP_MAIN;
    }

    public void initFilter(String filterWhereClause, String locationID, int sortBy){
        mFilterWhereClause = filterWhereClause;
        mLocationID = locationID;
        mSortBy = sortBy;
        mTypeSection = ProxyManager.TYPE_SECTION_FILTRATION_SEARCH;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (mTypeSection){
            case ProxyManager.TYPE_SECTION_FILTRATION_SEARCH:
                switch (i){
                    case 1:
                        return new SelectFilteredByCategoryItems(mContext, mCategoryID, mFilterWhereClause, mLocationID, mSortBy, mGroup);
                    case 2:
                        return new SelectFilteredByCategoryItemsPreOrder(mContext, mCategoryID, mFilterWhereClause, mLocationID, mSortBy, mGroup);
                }
            break;
            case ProxyManager.TYPE_SECTION_MAIN:
                switch(i){
                    case 1:
                        return new SelectFeaturedByCategoryItems(mContext, mCategoryID, null, mSortBy);
                    case 2:
                        return new SelectAllItemsByCategory(mContext, mCategoryID, mSortBy);
                }
                break;
            case ProxyManager.TYPE_SECTION_SHOP_MAIN:
                return new SelectFeaturedByCategoryItems(mContext, mCategoryID, mLocationID, mSortBy);
        }
        return null;
    }
}
