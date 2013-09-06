package com.treelev.isimple.domain.ui.filter_fragment;


import android.view.LayoutInflater;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.fragments.filters.FilterFragment;

import java.util.Map;

public class CountryRegionItemFilter extends ExpandableActivityItemFilter {

    public CountryRegionItemFilter(LayoutInflater inflater, FilterFragment filter, Map<String, FilterItemData[]> childData) {
        super(inflater, filter, childData);
    }

    @Override
    protected void initControl() {
        super.initControl();
        ((Button)mView).setText(mFilter.getString(R.string.lbl_country_item));
    }

    @Override
    public void reset() {
        super.reset();
        ((Button)mView).setText(mFilter.getString(R.string.lbl_country_item));
    }

    @Override
    public String getWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        if (mGroupData != null) {
            for (FilterItemData item : mGroupData) {
                if (item.isChecked()) {
                    if(sqlBuilder.length() > 0) {
                        sqlBuilder.append(" OR ");
                    }
                    sqlBuilder.append(String.format("item.country='%s'", item.getName()));
                }
            }
        }

        if (mChildData != null) {
            for (String groupName : mChildData.keySet()) {
                for (FilterItemData item : mChildData.get(groupName)) {
                    if (item.isChecked()) {
                        if(sqlBuilder.length() > 0) {
                            sqlBuilder.append(" OR ");
                        }
                        sqlBuilder.append(String.format("(item.country='%1$s' and item.region='%2$s')", item.getName(), groupName));
                    }
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
