package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DeleteFavouriteItems extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private List<String> mDeleteItemsId;


    public DeleteFavouriteItems(Context context, List<String> deleteItemsID) {
        super(context);
        mDeleteItemsId = deleteItemsID;
    }

    @Override
    public Cursor loadInBackground() {
        getProxyManager().delFavourites(mDeleteItemsId);
        getProxyManager().setFavouriteItemTable(mDeleteItemsId, false);
        Cursor cursor = getProxyManager().getFavouriteItems();
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
}
