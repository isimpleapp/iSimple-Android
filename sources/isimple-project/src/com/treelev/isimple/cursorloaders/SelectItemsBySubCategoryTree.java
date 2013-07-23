package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class SelectItemsBySubCategoryTree extends BaseCursorLoader {
    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private String mDrinkID;
    private String mFilterWhereClause;
    private String mLocationId;
    private String mQuery;
    private int mSortBy;

    public SelectItemsBySubCategoryTree(Context context, String drinkID, String filterWhereClause, String locationId, String query, int sortBy) {
        super(context);
        mDrinkID = drinkID;
        mFilterWhereClause = filterWhereClause;
        mLocationId = locationId;
        mQuery = query;
        mSortBy = sortBy;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;
        if(!TextUtils.isEmpty(mFilterWhereClause)) {
            cursor = getProxyManager().getItemsByDrinkId(mDrinkID, mFilterWhereClause, mLocationId, mSortBy);
        } else {
            boolean search = mQuery != null;
            cursor = getProxyManager().getItemsByDrinkId(mDrinkID, mLocationId, mQuery, search, mSortBy);
        }
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}
