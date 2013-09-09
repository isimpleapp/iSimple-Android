package com.treelev.isimple.domain.ui.filter_fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import com.treelev.isimple.fragments.filters.FilterFragment;

public abstract class ItemFilter {

    protected View mView;
    protected LayoutInflater mInflater;
    protected FilterFragment mFilter;
    protected int mRequestCode;
    private boolean mInteractive;

    protected ItemFilter(LayoutInflater inflater, FilterFragment filter, boolean interactive) {
        mInflater = inflater;
        mView = createView();
        mRequestCode = generateUniqueRequestCode();
        mFilter = filter;
        mInteractive = interactive;
    }

    protected ItemFilter(LayoutInflater inflater){
        mInflater = inflater;
        mView = createView();
        mRequestCode = generateUniqueRequestCode();
    }


    public abstract void reset();

    protected abstract View createView();
    protected abstract void initControl();
    public abstract String getWhereClause();

    public  void onResult(int requestCode, int resultCode, Intent data) {

    }

    public View getView(){
        return mView;
    }

    protected void onChangeStateItemFilter(){
        if(mFilter != null && mInteractive){
            mFilter.onChangeFilterState();
        }
    }

    private int generateUniqueRequestCode() {
        return System.identityHashCode(this) & 0xFFFF;
    }
}
