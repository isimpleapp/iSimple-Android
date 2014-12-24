package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;

import com.treelev.isimple.activities.SearchResultActivity;

public class SelectBySearch extends BaseCursorLoader {

    private ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private Integer mCategoryID;
    private String mLocationId;
    private int mSortBy;
    private String mSearchQuery;

    public SelectBySearch(Context context, String searchQuery, Integer categoryID, String locationId, int sortBy) {
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
            cursor = getProxyManager().getSearchItemsByCategory(mCategoryID, mSearchQuery, mSortBy);
        } else{
            cursor = getProxyManager().getSearchItemsByCategory(mCategoryID, mLocationId, mSearchQuery, mSortBy);
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
    		((SearchResultActivity) mContext).showNotFoundView(cursor, 1);
    	} catch (ClassCastException e){
    	}
    	super.deliverResult(cursor);
    }
}
