package com.treelev.isimple.adapters.itemstreecursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.treelev.isimple.cursorloaders.SelectFeaturedByCategoryItems;

public class CatalogByCategoryByShopItemTreeCursorAdapter extends AbsItemTreeCursorAdapter {

    private Integer mCategoryID;
    private String mLocationID;

    public CatalogByCategoryByShopItemTreeCursorAdapter(Context context, Cursor cursor, LoaderManager manager,
                                                        Integer categoryID, String locationID, int sortBy) {
        super(context, cursor, manager, sortBy);
        mCategoryID = categoryID;
        mLocationID = locationID;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new SelectFeaturedByCategoryItems(mContext, mCategoryID, mLocationID, mSortBy);
    }
}
