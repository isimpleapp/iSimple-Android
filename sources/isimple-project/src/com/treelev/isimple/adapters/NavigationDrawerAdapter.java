package com.treelev.isimple.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.treelev.isimple.R;

public class NavigationDrawerAdapter extends BaseAdapter {
    
    private String[] mainMenuLabelsArray;
    private Drawable[] iconLocation;
    private LayoutInflater inflater;

    public NavigationDrawerAdapter(Context context, String[] mainMenuLabelsArray, Drawable[] iconLocation) {
        super();
        this.mainMenuLabelsArray = mainMenuLabelsArray;
        this.iconLocation = iconLocation;
        if (mainMenuLabelsArray.length != iconLocation.length) {
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
//        drawerItem.setCompoundDrawables(iconLocation[position], null, null, null);
        return v;
    }

}
