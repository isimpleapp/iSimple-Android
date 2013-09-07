package com.treelev.isimple.domain.ui.filter_fragment;

import android.view.LayoutInflater;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.enumerable.item.Sweetness;
import com.treelev.isimple.fragments.filters.FilterFragment;


public class SweetnesItemFilter extends DefaultActivityItemFilter {

    public SweetnesItemFilter(LayoutInflater inflater, FilterFragment filter, boolean  interactive, FilterItemData[] filterData) {
        super(inflater, filter,  interactive, filterData);
    }

    @Override
    protected void initControl() {
        super.initControl();
        ((Button)mView).setText(mFilter.getString(R.string.lbl_sweetnes_item));
    }

    @Override
    public void reset() {
        super.reset();
        ((Button)mView).setText(mFilter.getString(R.string.lbl_sweetnes_item));
    }

    @Override
    public String getWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        if (mFilterData != null) {
            for (FilterItemData item : mFilterData) {
                if (item.isChecked()) {
                    if(sqlBuilder.length() > 0) {
                        sqlBuilder.append(" OR ");
                    }
                    sqlBuilder.append(String.format("item.sweetness=%s", Sweetness.getSweetness(item.getName()).ordinal()));
                }
            }
        }
        if (sqlBuilder.length() > 0) {
            sqlBuilder.insert(0, '(');
            sqlBuilder.append(')');
        }
        return sqlBuilder.toString();
    }
}
