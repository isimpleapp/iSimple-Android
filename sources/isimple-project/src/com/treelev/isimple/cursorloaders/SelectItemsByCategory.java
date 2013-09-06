package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class SelectItemsByCategory extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private String mFilterWhereClause;
    private String mLocationId;
    private int mSortBy;
    private boolean mGroup;

    public SelectItemsByCategory(Context context, Integer categoryID, String filterWhereClause, String locationId, int sortBy, boolean group) {
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
        if (!TextUtils.isEmpty(mFilterWhereClause)) {
            if (TextUtils.isEmpty(mLocationId)) {
                cursor = getProxyManager().getFilteredItemsByCategory(mCategoryID, mFilterWhereClause, mSortBy, mGroup);
            } else {
                cursor = getProxyManager().getFilteredItemsByCategory(mCategoryID, mLocationId, mFilterWhereClause, mSortBy, mGroup);
            }
        } else {
            if (TextUtils.isEmpty(mLocationId)) {
                cursor = getProxyManager().getFeaturedItemsByCategory(mCategoryID, mSortBy);
            } else {
                cursor = getProxyManager().getFeaturedItemsByCategory(mCategoryID, mLocationId, mSortBy);
            }
        }
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}
