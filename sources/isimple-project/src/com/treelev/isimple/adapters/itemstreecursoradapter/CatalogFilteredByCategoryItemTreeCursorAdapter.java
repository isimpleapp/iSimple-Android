package com.treelev.isimple.adapters.itemstreecursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.treelev.isimple.cursorloaders.SelectFilteredByCategoryItems;

public class CatalogFilteredByCategoryItemTreeCursorAdapter extends AbsItemTreeCursorAdapter{

    private Integer mCategoryID;
    private String mLocationID;

    public CatalogFilteredByCategoryItemTreeCursorAdapter(Context context, Cursor cursor, LoaderManager manager,
                                                          Integer categoryID, String locationID, String filterWhereClause, int sortBy) {
        super(context, cursor, manager, sortBy);
        mCategoryID = categoryID;
        mLocationID = locationID;
        mFilterWhereClause = filterWhereClause;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case 1:
                return new SelectFilteredByCategoryItems(mContext, mCategoryID, mFilterWhereClause, mLocationID, mSortBy);
            case 2:
                return new SelectFilteredByCategoryItems(mContext, mCategoryID, mFilterWhereClause, mLocationID, mSortBy);
        }
        return null;
    }
}
