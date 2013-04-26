package com.treelev.isimple.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.treelev.isimple.R;

public class NavigationListAdapter extends BaseAdapter {

    private Drawable[] mIcons;
    private String[] mTitles;
    private Context mContext;
    private LayoutInflater mInflator;

    public NavigationListAdapter(Context context, Drawable[] icons, String[] titles) {
        mContext = context;
        mInflator = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mIcons = icons;
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return mTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, true);
    }

    private View getView(int position, View convertView, ViewGroup parent, boolean isDropDownView) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflator.inflate(
                    isDropDownView ? R.layout.navigation_list_dropdown_item : R.layout.navigation_list_item,
                    parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(mTitles[position]);
        holder.icon.setImageDrawable(mIcons[position]);

//        if(isDropDownView) {
//            holder.title.setText(mTitles[position]);
//            holder.icon.setImageDrawable(mIcons[position]);
//        }
        if (!isDropDownView) {
            holder.title.setTextColor(mContext.getResources().getColor(R.color.select_item_navigation));
        }
        return convertView;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView title;
    }
}