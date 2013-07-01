package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.treelev.isimple.utils.managers.ProxyManager;

public class SelectAllItemsByCategory extends CursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private int mSortBy;
    private Context mContext;
    private ProxyManager mProxyManager;

    public SelectAllItemsByCategory(Context context, Integer categaryID,  int sortBy) {
        super(context);
        mContext = context;
        mCategoryID = categaryID;
        mSortBy = sortBy;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = getProxyManager().getAllItemsByCategory(mCategoryID, mSortBy);
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
