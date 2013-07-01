package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import com.treelev.isimple.utils.managers.ProxyManager;

public class SelectFeaturedByCategoryItems  extends CursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private String mFilterWhereClause;
    private String mLocationId;
    private int mSortBy;
    private Context mContext;
    private ProxyManager mProxyManager;

    public SelectFeaturedByCategoryItems(Context context, Integer categaryID, String locationId, int sortBy) {
        super(context);
        mContext = context;
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
            cursor = getProxyManager().getFeaturedItemsByCategory(mCategoryID, mLocationId, mSortBy);
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
