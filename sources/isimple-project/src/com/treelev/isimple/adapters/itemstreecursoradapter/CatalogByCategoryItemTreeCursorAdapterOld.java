package com.treelev.isimple.adapters.itemstreecursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.treelev.isimple.cursorloaders.SelectAllItemsByCategory;
import com.treelev.isimple.cursorloaders.SelectFeaturedByCategoryItems;

public class CatalogByCategoryItemTreeCursorAdapterOld extends AbsItemTreeCursorAdapter {

    private Integer mCategoryID;

    public CatalogByCategoryItemTreeCursorAdapterOld(Context context, Cursor cursor, LoaderManager manager,
                                                     Integer categoryID, int sortBy) {
        super(context, cursor, manager, sortBy);
        mCategoryID = categoryID;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch(i){
            case 1:
                return new SelectFeaturedByCategoryItems(mContext, mCategoryID, null, mSortBy);
            case 2:
                return new SelectAllItemsByCategory(mContext, mCategoryID, mSortBy);
        }
        return null;
    }
}
