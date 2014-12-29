
package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class SelectFeaturedByCategoryItems extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private String mFilterWhereClause;
    private String mLocationId;
    private int mSortBy;

    public SelectFeaturedByCategoryItems(Context context, Integer categaryID, String locationId,
            int sortBy) {
        super(context);
        mCategoryID = categaryID;
        mLocationId = locationId;
        mSortBy = sortBy;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;
        if (TextUtils.isEmpty(mLocationId)) {
            cursor = getProxyManager().getFeaturedItemsByCategory(mCategoryID, mSortBy);
        } else {
            cursor = getProxyManager()
                    .getFeaturedItemsByCategory(mCategoryID, mLocationId, mSortBy);
        }
        if (cursor != null) {
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}
