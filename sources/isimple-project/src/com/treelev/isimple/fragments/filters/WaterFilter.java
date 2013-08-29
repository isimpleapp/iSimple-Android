package com.treelev.isimple.fragments.filters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;

public class WaterFilter extends FilterFragment{

    @Override
    protected String getWhereClause() {
        return null;
    }

    @Override
    protected void resetFilter() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.water_filter_layout, container);
        return view;
    }
}
