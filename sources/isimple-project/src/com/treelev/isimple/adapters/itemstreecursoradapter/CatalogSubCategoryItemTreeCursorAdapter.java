package com.treelev.isimple.adapters.itemstreecursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.treelev.isimple.cursorloaders.SelectItemsBySubCategoryPreOrderTree;
import com.treelev.isimple.cursorloaders.SelectItemsBySubCategoryTree;

public class CatalogSubCategoryItemTreeCursorAdapter extends AbsItemTreeCursorAdapter {

    private String mDrinkID;
    private String mLocationID;

    public CatalogSubCategoryItemTreeCursorAdapter(Context context, Cursor cursor, LoaderManager manager,
                                                   String drinkID, String filterWhereClause, String locationID,  int sortBy) {
        super(context, cursor, manager, sortBy);
        mDrinkID = drinkID;
        mFilterWhereClause = filterWhereClause;
        mLocationID = locationID;

        mGroup = false;
        mYearEnable = true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case 1:
                return new SelectItemsBySubCategoryTree(mContext, mDrinkID, mFilterWhereClause, mLocationID, mSortBy);
            case 2:
                return new SelectItemsBySubCategoryPreOrderTree(mContext, mDrinkID, mFilterWhereClause, mLocationID, mSortBy);
        }
        return null;
    }
}
