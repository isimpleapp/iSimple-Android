
package com.treelev.isimple.cursorloaders;

import com.treelev.isimple.enumerable.item.DrinkCategory;

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
            if (mCategoryID == DrinkCategory.WATER.ordinal()) {
                cursor = getProxyManager().getWaterFeaturedItems(mSortBy);
            } else {
                cursor = getProxyManager().getFeaturedItemsByCategory(mCategoryID, mSortBy);
            }
        } else {
            if (mCategoryID == DrinkCategory.WATER.ordinal()) {
                cursor = getProxyManager().getWaterFeaturedItems(mLocationId, mSortBy);
            } else {
                cursor = getProxyManager()
                        .getFeaturedItemsByCategory(mCategoryID, mLocationId, mSortBy);
            }
        }
        if (cursor != null) {
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}
