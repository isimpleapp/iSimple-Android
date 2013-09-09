package com.treelev.isimple.domain.ui.filter_fragment;

import android.view.LayoutInflater;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.fragments.filters.FilterFragment;

import java.util.Map;

public class ClassificationItemFilter extends ExpandableActivityItemFilter{

    public ClassificationItemFilter(LayoutInflater inflater, String label, boolean interactive, FilterFragment filter, Map<String, FilterItemData[]> childData) {
        super(inflater, filter, interactive, childData);
        mLabel = label;
        initControl();
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
                    sqlBuilder.append(String.format("item.product_type=%s", ProductType.getProductTypeByLabel(item.getName()).ordinal()));
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
                        sqlBuilder.append(
                                String.format("(item.product_type=%1$s AND item.classification='%2$s')",
                                        ProductType.getProductTypeByLabel(groupName).ordinal(), item.getName()));
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
