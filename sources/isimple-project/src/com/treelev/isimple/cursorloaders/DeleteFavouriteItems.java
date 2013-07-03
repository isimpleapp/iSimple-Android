package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class DeleteFavouriteItems extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private ArrayList<String> mDeleteItemsId;


    public DeleteFavouriteItems(Context context, ArrayList<String> deleteItemsID) {
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
