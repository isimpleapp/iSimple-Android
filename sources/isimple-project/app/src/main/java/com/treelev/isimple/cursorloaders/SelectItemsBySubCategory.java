package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class SelectItemsBySubCategory extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private String mDrinkID;
    private String mFilterWhereClause;
    private String mLocationId;
    private int mSortBy;
    private String mBarcode;

    public SelectItemsBySubCategory(Context context, String drinkID, String filterWhereClause, String locationId, String barcode, int sortBy) {
        super(context);
        mDrinkID = drinkID;
        mFilterWhereClause = filterWhereClause;
        mLocationId = locationId;
        mBarcode = barcode;
        mSortBy = sortBy;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;
        if (mBarcode != null) {
            cursor =  getProxyManager().getItemByBarcode(mBarcode, mSortBy);
            if(cursor.getCount() == 0){
                cursor = getProxyManager().getItemDeprecatedByBarcode(mBarcode, mSortBy);
            }
        } else if(!TextUtils.isEmpty(mFilterWhereClause)) {
            cursor = getProxyManager().getItemsByDrinkId(mDrinkID, mFilterWhereClause, mLocationId, mSortBy);
        } else {
            cursor = getProxyManager().getItemsByDrinkId(mDrinkID, mLocationId, "", false, mSortBy);
        }
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}
