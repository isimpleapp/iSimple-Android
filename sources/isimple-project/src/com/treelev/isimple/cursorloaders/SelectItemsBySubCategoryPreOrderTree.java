package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class SelectItemsBySubCategoryPreOrderTree extends BaseCursorLoader {
    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private String mDrinkID;
    private String mFilterWhereClause;
    private String mLocationId;
    private int mSortBy;

    public SelectItemsBySubCategoryPreOrderTree(Context context, String drinkID, String filterWhereClause, String locationId, int sortBy) {
        super(context);
        mDrinkID = drinkID;
        mFilterWhereClause = filterWhereClause;
        mLocationId = locationId;
        mSortBy = sortBy;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;
        if(!TextUtils.isEmpty(mFilterWhereClause)) {
            cursor = getProxyManager().getItemsByDrinkIdPreOrder(mDrinkID, mFilterWhereClause, mLocationId, mSortBy);
        } else {
            cursor = getProxyManager().getItemsByDrinkIdPreOrder(mDrinkID, mLocationId, mSortBy);
        }
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}