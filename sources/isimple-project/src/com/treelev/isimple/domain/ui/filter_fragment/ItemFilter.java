package com.treelev.isimple.domain.ui.filter_fragment;

import android.view.LayoutInflater;
import android.view.View;

public abstract class ItemFilter {

    protected View mView;
    protected LayoutInflater mInflater;

    protected ItemFilter(LayoutInflater inflater){
        mInflater = inflater;
        mView = createView();
    }

    public abstract void reset();

    protected abstract View createView();
    protected abstract void initControl();
    public abstract String getWhereClause();

    public View getView(){
        return mView;
    }
}
