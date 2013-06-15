package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.treelev.isimple.utils.managers.ProxyManager;

public class SelectFeaturedMainItems extends CursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Context mContext;
    private ProxyManager mProxyManager;

    public SelectFeaturedMainItems(Context context) {
        super(context);
        mContext = context;

    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = getProxyManager().getFeaturedMainItems();
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