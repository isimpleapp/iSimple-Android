package com.treelev.isimple.domain.ui.filter_fragment;

import android.view.LayoutInflater;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.fragments.filters.FilterFragment;

public class YearItemFilter extends DefaultActivityItemFilter {

    private boolean mReset;

    public YearItemFilter(LayoutInflater inflater, FilterFragment filter, FilterItemData[] filterData) {
        super(inflater, filter, false, filterData);
   }

    @Override
    protected void initControl() {
        super.initControl();
        ((Button)mView).setText(mFilter.getString(R.string.lbl_year_item));
        mReset = true;
    }

    @Override
    public void reset() {
        super.reset();
        mReset = true;
        ((Button)mView).setText(mFilter.getString(R.string.lbl_year_item));
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
                    sqlBuilder.append(String.format("item.year=%s", item.getName()));
                }
            }
        }
        if (sqlBuilder.length() > 0) {
            sqlBuilder.insert(0, '(');
            sqlBuilder.append(')');
        }
        mReset = sqlBuilder.toString().isEmpty();
        return sqlBuilder.toString();
    }

    public boolean isReset(){
        return mReset;
    }
}
