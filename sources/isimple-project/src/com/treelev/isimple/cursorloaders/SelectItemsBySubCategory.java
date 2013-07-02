package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import com.treelev.isimple.utils.managers.ProxyManager;

public class SelectItemsBySubCategory extends CursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private String mDrinkID;
    private String mFilterWhereClause;
    private String mLocationId;
    private int mSortBy;
    private Context mContext;
    private ProxyManager mProxyManager;
    private String mBarcode;

    public SelectItemsBySubCategory(Context context, Integer categaryID, String drinkID, String filterWhereClause, String locationId, String barcode, int sortBy) {
        super(context);
        mContext = context;
        mCategoryID = categaryID;
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
            cursor = getProxyManager().getItemsByDrinkId(mDrinkID, mLocationId, mSortBy);
        }
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = new ProxyManager(mContext);
        }
        return mProxyManager;
    }
}
