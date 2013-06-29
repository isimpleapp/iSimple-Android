package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import com.treelev.isimple.utils.managers.ProxyManager;

public class SelectAllItems extends CursorLoader {

    private Loader.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver();
    private Context mContext;
    private ProxyManager mProxyManager;
    private int mSortBy;

    public SelectAllItems(Context context) {
        super(context);
        mContext = context;
        mSortBy = ProxyManager.SORT_NAME_AZ;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = getProxyManager().getAllItems(mSortBy);
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
