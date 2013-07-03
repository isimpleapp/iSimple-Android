package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.Loader;

public class SelectAllItems extends BaseCursorLoader {

    private Loader.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver();
    private int mSortBy;

    public SelectAllItems(Context context, int sortBy) {
        super(context);
        mSortBy = sortBy;
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
}
