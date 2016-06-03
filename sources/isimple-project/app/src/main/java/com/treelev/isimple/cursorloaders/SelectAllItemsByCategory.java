package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;

public class SelectAllItemsByCategory extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private int mSortBy;

    public SelectAllItemsByCategory(Context context, Integer categaryID,  int sortBy) {
        super(context);
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
}
