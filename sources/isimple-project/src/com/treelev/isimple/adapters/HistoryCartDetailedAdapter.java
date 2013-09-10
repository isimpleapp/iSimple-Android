package com.treelev.isimple.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.treelev.isimple.R;

public class HistoryCartDetailedAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public HistoryCartDetailedAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mInflater =  LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.history_cart_detailed_item, null);
        HolderView holderView = new HolderView();
        holderView.date = (TextView) view.findViewById(R.id.date_cart_item);
        holderView.label = (TextView) view.findViewById(R.id.cart_info);
        view.setTag(holderView);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        HolderView holderView = (HolderView)view.getTag();
//TODO
        holderView.date.setText("");
        holderView.label.setText("");
    }

    private class HolderView {
        public TextView date;
        public TextView label;
    }
}
