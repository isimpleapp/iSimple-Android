package com.treelev.isimple.domain.ui.filter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.filter.DefaultListFilterActivity;
import org.holoeverywhere.widget.TextView;

public class DefaultActivityFilterItem extends FilterItem {
    private LayoutInflater layoutInflater;
    private FilterItemData[] filterData;
    private SqlWhereClauseBuilder clauseBuilder;

    public DefaultActivityFilterItem(Context context, String label, FilterItemData[] filterData,
                                     SqlWhereClauseBuilder clauseBuilder, int currentCategory) {
        super(context, ITEM_ACTIVITY, label, DefaultListFilterActivity.class, currentCategory);
        layoutInflater = LayoutInflater.from(context);
        this.filterData = filterData;
        this.clauseBuilder = clauseBuilder;
    }

    private boolean isAnyItemChecked() {
        if (filterData != null) {
            for (FilterItemData item : filterData) {
                if (item.isChecked())
                    return true;
            }
        }
        return false;
    }

    @Override
    protected Intent createIntent() {
        Intent intent = super.createIntent();
        if (filterData != null) {
            DefaultListFilterActivity.putFilterData(intent, filterData);
        }
        return intent;
    }

    @Override
    public boolean processResult(int requestCode, int resultCode, Intent data) {
        if (super.processResult(requestCode, resultCode, data)) {
            filterData = DefaultListFilterActivity.getFilterData(data);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public View renderView(View convertView, ViewGroup parent) {
        if (convertView == null || !(convertView.getTag() instanceof TextView)) {
            convertView = layoutInflater.inflate(R.layout.category_filter_text_item_layout, parent, false);
            TextView text = (TextView) convertView.findViewById(R.id.item_content);
            convertView.setTag(text);
        }
        TextView text = (TextView) convertView.getTag();
        text.setText(getLabel());
        text.setTextColor(isAnyItemChecked() ? Color.BLACK : Color.LTGRAY);

        return convertView;
    }

    @Override
    public String getSQLWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        if (filterData != null) {
            for (FilterItemData item : filterData) {
                if (item.isChecked()) {
                    if(sqlBuilder.length() > 0) {
                        sqlBuilder.append(" or ");
                    }
                    sqlBuilder.append(clauseBuilder.buildClause(item.getName()));
                }
            }
        }
        if (sqlBuilder.length() > 0) {
            sqlBuilder.insert(0, '(');
            sqlBuilder.append(')');
        }

        return sqlBuilder.toString();
    }

    public static interface SqlWhereClauseBuilder {
        String buildClause(String filterName);
    }
}
