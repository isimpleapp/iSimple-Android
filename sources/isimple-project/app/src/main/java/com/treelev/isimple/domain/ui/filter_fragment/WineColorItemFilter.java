package com.treelev.isimple.domain.ui.filter_fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.treelev.isimple.R;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.fragments.filters.FilterFragment;

public class WineColorItemFilter extends ItemFilter
    implements View.OnTouchListener{

    private CheckBox mRed;
    private CheckBox mWhite;
    private CheckBox mPink;

    public WineColorItemFilter(LayoutInflater inflater, FilterFragment filter) {
        super(inflater, filter, true);
        initControl();
    }

    @Override
    public void reset() {
        mRed.setChecked(false);
        mWhite.setChecked(false);
        mPink.setChecked(false);
        mView.refreshDrawableState();
    }

    @Override
    protected View createView() {
        return mInflater.inflate(R.layout.category_filter_winecolor_item_layout, null);
    }

    @Override
    protected void initControl() {
        mRed = (CheckBox)mView.findViewById(R.id.red_wine_check);
        mWhite = (CheckBox)mView.findViewById(R.id.white_wine_check);
        mPink = (CheckBox)mView.findViewById(R.id.pink_wine_check);
        setClickButt(R.id.red_wine_butt, R.id.red_wine_check);
        setClickButt(R.id.white_wine_butt, R.id.white_wine_check);
        setClickButt(R.id.pink_wine_butt, R.id.pink_wine_check);
    }

    private void setClickButt(int buttonId, int checkboxId) {
        TextView textView = (TextView) mView.findViewById(buttonId);
        textView.setOnTouchListener(this);
        textView.setTag(mView.findViewById(checkboxId));
    }

    @Override
    public String getWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        if (mRed.isChecked()) {
            sqlBuilder.append(String.format("item.color=%s", ItemColor.RED.ordinal()));
        }
        if (mWhite.isChecked()) {
            if (sqlBuilder.length() > 0) {
                sqlBuilder.append(" OR ");
            }
            sqlBuilder.append(String.format("item.color=%s", ItemColor.WHITE.ordinal()));
        }
        if (mPink.isChecked()) {
            if (sqlBuilder.length() > 0) {
                sqlBuilder.append(" OR ");
            }
            sqlBuilder.append(String.format("item.color=%s", ItemColor.PINK.ordinal()));
        }
        if (sqlBuilder.length() > 0) {
            sqlBuilder.insert(0, '(');
            sqlBuilder.append(')');
        }
        return sqlBuilder.toString();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
            CheckBox checkBox = (CheckBox) view.getTag();
            checkBox.setChecked(!checkBox.isChecked());
            onChangeStateItemFilter();
        }
        return false;
    }
}
