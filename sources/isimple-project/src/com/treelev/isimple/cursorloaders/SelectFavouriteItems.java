package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.Loader;

public class SelectFavouriteItems extends BaseCursorLoader {

    private Loader.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver();

    public SelectFavouriteItems(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = getProxyManager().getFavouriteItems();
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}
