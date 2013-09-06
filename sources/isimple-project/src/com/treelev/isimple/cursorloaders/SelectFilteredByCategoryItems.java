package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class SelectFilteredByCategoryItems extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private String mFilterWhereClause;
    private String mLocationId;
    private int mSortBy;
    private boolean mGroup;

    public SelectFilteredByCategoryItems(Context context, Integer categoryID, String filterWhereClause, String locationId, int sortBy, boolean group) {
        super(context);
        mCategoryID = categoryID;
        mFilterWhereClause = filterWhereClause;
        mLocationId = locationId;
        mSortBy = sortBy;
        mGroup = group;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;
        if (TextUtils.isEmpty(mLocationId)) {
            cursor = getProxyManager().getFilteredItemsByCategory(mCategoryID, mFilterWhereClause, mSortBy, mGroup);
        } else {
            cursor = getProxyManager().getFilteredItemsByCategory(mCategoryID, mLocationId, mFilterWhereClause, mSortBy, mGroup);
        }
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}
