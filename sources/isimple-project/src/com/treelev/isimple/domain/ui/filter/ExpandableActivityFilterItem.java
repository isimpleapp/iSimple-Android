package com.treelev.isimple.domain.ui.filter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.filter.ExpandableListFilterActivity;
import org.holoeverywhere.widget.TextView;

import java.util.Map;

public class ExpandableActivityFilterItem extends FilterItem {
    private LayoutInflater layoutInflater;
    private FilterItemData[] groupData;
    private Map<String, FilterItemData[]> childData;
    private SqlWhereClauseBuilder clauseBuilder;
    private TextView text;

    public ExpandableActivityFilterItem(Context context, String label, Map<String, FilterItemData[]> childData,
                                        SqlWhereClauseBuilder clauseBuilder, int currentCategory) {
        super(context, ITEM_ACTIVITY, label, ExpandableListFilterActivity.class, currentCategory);
        layoutInflater = LayoutInflater.from(context);
        String[] groups = childData.keySet().toArray(new String[0]);
        this.groupData = new FilterItemData[groups.length];
        for (int i = 0; i < groupData.length; i++) {
            groupData[i] = new FilterItemData(groups[i]);
        }
        this.childData = childData;
        this.clauseBuilder = clauseBuilder;
    }

    private boolean isAnyItemChecked() {
        if (groupData != null) {
            for (FilterItemData item : groupData) {
                if (item.isChecked())
                    return true;
            }
        }

        if (childData != null) {
            for (String key : childData.keySet()) {
                for (FilterItemData item : childData.get(key)) {
                    if (item.isChecked())
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Intent createIntent() {
        Intent intent = super.createIntent();
        ExpandableListFilterActivity.putFilterData(intent, groupData, childData);
        return intent;
    }

    @Override
    public boolean processResult(int requestCode, int resultCode, Intent data) {
        if (super.processResult(requestCode, resultCode, data)) {
            groupData = ExpandableListFilterActivity.getGroupData(data);
            childData = ExpandableListFilterActivity.getChildData(data, groupData);
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
            text = (TextView) convertView.findViewById(R.id.item_content);
            convertView.setTag(text);
        }
        text = (TextView) convertView.getTag();
        text.setText(getLabel());
        text.setTextColor(isAnyItemChecked() ? Color.BLACK : Color.LTGRAY);

        return convertView;
    }

    @Override
    public String getSQLWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        if (groupData != null) {
            for (FilterItemData item : groupData) {
                if (item.isChecked()) {
                    if(sqlBuilder.length() > 0) {
                        sqlBuilder.append(" or ");
                    }
                    sqlBuilder.append(clauseBuilder.buildGroupClause(item.getName()));
                }
            }
        }

        if (childData != null) {
            for (String groupName : childData.keySet()) {
                for (FilterItemData item : childData.get(groupName)) {
                    if (item.isChecked()) {
                        if(sqlBuilder.length() > 0) {
                            sqlBuilder.append(" or ");
                        }
                        sqlBuilder.append(clauseBuilder.buildChildClause(item.getName(), groupName));
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

    @Override
    public void reset() {
        for(FilterItemData item : groupData) {
            item.setChecked(false);
        }
        for(FilterItemData[] items : childData.values()){
            for(FilterItemData item : items) {
                item.setChecked(false);
            }
        }
        text.setTextColor(Color.LTGRAY);
    }

    @Override
    public boolean isChangedState() {
        boolean result = false;
        for(FilterItemData item : groupData) {
            result |= item.isChecked();
        }
        for(FilterItemData[] items : childData.values()){
            for(FilterItemData item : items) {
                result |= item.isChecked();
            }
        }
        return result;
    }

    public static interface SqlWhereClauseBuilder {
        String buildGroupClause(String groupName);
        String buildChildClause(String childName, String groupName);
    }
}
