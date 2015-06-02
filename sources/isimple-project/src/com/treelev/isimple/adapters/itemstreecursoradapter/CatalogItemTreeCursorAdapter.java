package com.treelev.isimple.adapters.itemstreecursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.treelev.isimple.cursorloaders.SelectAllItems;
import com.treelev.isimple.cursorloaders.SelectFeaturedMainItems;


public class CatalogItemTreeCursorAdapter extends AbsItemTreeCursorAdapter{

    public CatalogItemTreeCursorAdapter(Context context, Cursor cursor, LoaderManager manager, int sortBy) {
        super(context, cursor, manager, sortBy);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case 1:
                return new SelectFeaturedMainItems(mContext);
            case 2:
                return  new SelectAllItems(mContext, mSortBy);
        }
        return null;
    }

}
