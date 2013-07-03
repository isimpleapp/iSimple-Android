package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.Loader;

public class SelectSectionsItems extends BaseCursorLoader {

    private Loader.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver();
    private int mTypeSection;


    public SelectSectionsItems(Context context, int typeSection) {
        super(context);
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
}
