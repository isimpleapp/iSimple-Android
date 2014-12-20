package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.Loader;

import com.treelev.isimple.activities.SearchResultActivity;

public class SelectBySearchPreOrder extends BaseCursorLoader {

    private Loader.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver();
    private Integer mCategoryID;
    private String mLocationId;
    private int mSortBy;
    private String mSearchQuery;

    public SelectBySearchPreOrder(Context context, String searchQuery, Integer categoryID, String locationId, int sortBy) {
        super(context);
        mSearchQuery = searchQuery;
        mCategoryID = categoryID;
        mLocationId = locationId;
        mSortBy = sortBy;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = null;
        if(mLocationId == null) {
            cursor = getProxyManager().getSearchItemsByCategoryPreOrder(mCategoryID, mSearchQuery, mSortBy);
        } else{
            cursor = getProxyManager().getSearchItemsByCategoryPreOrder(mCategoryID, mLocationId, mSearchQuery, mSortBy);
        }
        if(cursor != null){
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }
    
    @Override
    public void deliverResult(Cursor cursor) {
    	try {
    		((SearchResultActivity) mContext).showNotFoundView(cursor);
    	} catch (ClassCastException e){
    	}
    	super.deliverResult(cursor);
    }
}
