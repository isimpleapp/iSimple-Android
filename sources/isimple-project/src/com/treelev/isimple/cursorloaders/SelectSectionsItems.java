package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import com.treelev.isimple.utils.managers.ProxyManager;

public class SelectSectionsItems extends CursorLoader {

    private Loader.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver();
    private Context mContext;
    private ProxyManager mProxyManager;
    private int mTypeSection;


    public SelectSectionsItems(Context context, int typeSection) {
        super(context);
        mContext = context;
        mTypeSection = typeSection;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = getProxyManager().getSectionsItems(mTypeSection);
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
