package com.treelev.isimple.domain.ui.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;

public class WineColorFilterItem extends FilterItem {
    private LayoutInflater inflater;

    public WineColorFilterItem(Context context) {
        super(context, FilterItem.ITEM_INLINE);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View renderView(View convertView, ViewGroup parent) {
        return inflater.inflate(R.layout.category_filter_winecolor_item_layout, parent, false);
    }
}
