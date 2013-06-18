package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.treelev.isimple.utils.managers.ProxyManager;

import java.util.ArrayList;

public class DeleteFavouriteItems  extends CursorLoader{

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Context mContext;
    private ProxyManager mProxyManager;
    private ArrayList<String> mDeleteItemsId;


    public DeleteFavouriteItems(Context context, ArrayList<String> deleteItemsID) {
        super(context);
        mContext = context;
        mDeleteItemsId = deleteItemsID;
    }

    @Override
    public Cursor loadInBackground() {
        getProxyManager().delFavourites(mDeleteItemsId);
        getProxyManager().setFavouriteItemTable(mDeleteItemsId, false);
        Cursor cursor = getProxyManager().getFavouriteItems();;
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
