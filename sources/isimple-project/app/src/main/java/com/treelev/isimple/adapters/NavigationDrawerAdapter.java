package com.treelev.isimple.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.treelev.isimple.R;

public class NavigationDrawerAdapter extends BaseAdapter {
    
    private String[] mainMenuLabelsArray;
    private TypedArray iconLocation;
    private LayoutInflater inflater;

    public NavigationDrawerAdapter(Context context) {
        super();
        Resources resources = context.getResources();
        this.mainMenuLabelsArray = resources.getStringArray(R.array.main_menu_items);
        iconLocation = context.getResources().obtainTypedArray(R.array.main_menu_icons);
        if (mainMenuLabelsArray.length != iconLocation.length()) {
            throw new RuntimeException("Names and icons arrays must have same size!");
        }
        
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public int getCount() {
        return mainMenuLabelsArray.length;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.drawer_item, parent, false);
        TextView drawerItem = (TextView) v.findViewById(R.id.drawable_item_name);
        drawerItem.setText(mainMenuLabelsArray[position]);
        drawerItem.setCompoundDrawablesWithIntrinsicBounds(iconLocation.getResourceId(position, -1), 0, 0, 0);
        return v;
    }
    
}
