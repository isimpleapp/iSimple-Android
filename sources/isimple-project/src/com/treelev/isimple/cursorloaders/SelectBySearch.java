package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.treelev.isimple.utils.managers.ProxyManager;

public class SelectBySearch extends CursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private String mLocationId;
    private int mSortBy;
    private Context mContext;
    private ProxyManager mProxyManager;
    private String mSearchQuery;

    public SelectBySearch(Context context, String searchQuery, Integer categoryID, String locationId, int sortBy) {
        super(context);
        mContext = context;
        mSearchQuery = searchQuery;
        mCategoryID = categoryID;
        mLocationId = locationId;
        mSortBy = sortBy;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;
        if(mLocationId == null) {
            cursor = getProxyManager().getSearchItemsByCategory(mCategoryID, mSearchQuery, mSortBy);
        } else{
            cursor = getProxyManager().getSearchItemsByCategory(mCategoryID, mLocationId, mSearchQuery, mSortBy);
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
