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

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 19.04.13
 * Time: 19:52
 * To change this template use File | Settings | File Templates.
 */
public class NavigationListAdapter extends BaseAdapter {
    private Drawable[] mIcons;
    private String[] mTitles;
    private Context mContext;
    private LayoutInflater mInflator;

    public NavigationListAdapter(Context context, Drawable[] icons, String[] titles){
        mContext = context;
        mInflator = (LayoutInflater)mContext.getSystemService(
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
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflator.inflate(R.layout.navigation_list_item, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView)convertView.findViewById(R.id.icon);
            holder.title = (TextView)convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.title.setText(mTitles[position]);
        holder.title.setTextColor(mContext.getResources().getColor(R.color.select_item_navigation));
        holder.icon.setImageDrawable(mIcons[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflator.inflate(R.layout.navigation_list_dropdown_item, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView)convertView.findViewById(R.id.icon);
            holder.title = (TextView)convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.title.setText(mTitles[position]);
        holder.icon.setImageDrawable(mIcons[position]);

        return convertView;
    }

    private class ViewHolder{
        public ImageView icon;
        public TextView title;
    }
}