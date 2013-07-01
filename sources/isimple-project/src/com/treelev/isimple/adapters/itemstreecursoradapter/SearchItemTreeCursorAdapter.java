package com.treelev.isimple.adapters.itemstreecursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.treelev.isimple.cursorloaders.SelectBySearch;
import com.treelev.isimple.cursorloaders.SelectBySearchPreOrder;


public class SearchItemTreeCursorAdapter extends AbsItemTreeCursorAdapter{

    private String mSearchQuery;
    private Integer mCategoryID;
    private String mLocationID;

    public SearchItemTreeCursorAdapter(Context context, Cursor cursor, LoaderManager manager,
                                       String searchQuery, Integer categoryID, String locationID, int sortBy) {
        super(context, cursor, manager, sortBy);
        mSearchQuery = searchQuery;
        mCategoryID = categoryID;
        mLocationID = locationID;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case 1:
                return new SelectBySearch(mContext, mSearchQuery, mCategoryID, mLocationID, mSortBy);
            case 2:
                return  new SelectBySearchPreOrder(mContext, mSearchQuery, mCategoryID, mLocationID, mSortBy);
        }
        return null;
    }
}
