package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;

public class SelectFeaturedMainItems extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();

    public SelectFeaturedMainItems(Context context) {
        super(context);
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
}