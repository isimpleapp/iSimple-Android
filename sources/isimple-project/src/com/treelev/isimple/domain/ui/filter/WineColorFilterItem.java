package com.treelev.isimple.domain.ui.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.treelev.isimple.R;
import com.treelev.isimple.enumerable.item.ItemColor;

public class WineColorFilterItem extends FilterItem implements View.OnTouchListener {
    private LayoutInflater inflater;
    private View colorView;
    private boolean red;
    private boolean white;
    private boolean pink;

    public boolean isRed() {
        return red;
    }

    public boolean isWhite() {
        return white;
    }

    public boolean isPink() {
        return pink;
    }

    public WineColorFilterItem(Context context) {
        super(context, FilterItem.ITEM_INLINE);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View renderView(View convertView, ViewGroup parent) {
        if (colorView == null) {
            colorView = inflater.inflate(R.layout.category_filter_winecolor_item_layout, parent, false);
            setClickButt(R.id.red_wine_butt, R.id.red_wine_check);
            setClickButt(R.id.white_wine_butt, R.id.white_wine_check);
            setClickButt(R.id.pink_wine_butt, R.id.pink_wine_check);
        }
        return colorView;
    }

    @Override
    public String getSQLWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        if (isRed()) {
            sqlBuilder.append(String.format("item.color=%s", ItemColor.RED.ordinal()));
        }
        if (isWhite()) {
            if (sqlBuilder.length() > 0) {
                sqlBuilder.append(" or ");
            }
            sqlBuilder.append(String.format("item.color=%s", ItemColor.WHITE.ordinal()));
        }
        if (isPink()) {
            if (sqlBuilder.length() > 0) {
                sqlBuilder.append(" or ");
            }
            sqlBuilder.append(String.format("item.color=%s", ItemColor.PINK.ordinal()));
        }

        if (sqlBuilder.length() > 0) {
            sqlBuilder.insert(0, '(');
            sqlBuilder.append(')');
        }

        return sqlBuilder.toString();
    }

    private void setClickButt(int buttonId, int checkboxId) {
        TextView textView = (TextView) colorView.findViewById(buttonId);
        textView.setOnTouchListener(this);
        textView.setTag(colorView.findViewById(checkboxId));
    }

    @Override
    public boolean process() {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            boolean result = false;
            if (v.getId() == R.id.red_wine_butt)
                result = red = !red;
            else if (v.getId() == R.id.white_wine_butt)
                result = white = !white;
            else if (v.getId() == R.id.pink_wine_butt)
                result = pink = !pink;

            CheckBox checkBox = (CheckBox) v.getTag();
            checkBox.setChecked(result);
        }
        return false;
    }
}
