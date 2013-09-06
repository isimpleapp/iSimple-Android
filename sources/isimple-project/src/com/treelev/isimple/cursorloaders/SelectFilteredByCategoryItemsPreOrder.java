package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class SelectFilteredByCategoryItemsPreOrder extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private String mFilterWhereClause;
    private String mLocationId;
    private int mSortBy;
    private boolean mGroup;

    public SelectFilteredByCategoryItemsPreOrder(Context context, Integer categoryID, String filterWhereClause, String locationId, int sortBy, boolean group) {
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
            cursor = getProxyManager().getFilteredItemsByCategoryPreOrder(mCategoryID, mFilterWhereClause, mSortBy, mGroup);
        } else {
            cursor = getProxyManager().getFilteredItemsByCategoryPreOrder(mCategoryID, mLocationId, mFilterWhereClause, mSortBy, mGroup);
        }
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}
