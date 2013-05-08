package com.treelev.isimple.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.ProductContent;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.TextView;

import java.util.List;

public class ProductContentAdapter extends BaseExpandableListAdapter {

    private List<ProductContent> productContentList;
    private LayoutInflater layoutInflater;

    public ProductContentAdapter(Context context, List<ProductContent> productContentList) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productContentList = productContentList;
    }

    @Override
    public int getGroupCount() {
        return productContentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return productContentList.get(groupPosition) != null ? 1 : 0;
    }

    @Override
    public ProductContent getGroup(int groupPosition) {
        return productContentList.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return productContentList.get(groupPosition).getItemContent();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.product_info_expandable_group_layout, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.arrow = (ImageView) convertView.findViewById(R.id.group_arrow);
            groupViewHolder.text = (TextView) convertView.findViewById(R.id.group_name);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        ProductContent productContent = getGroup(groupPosition);
        groupViewHolder.arrow.setImageResource(isExpanded ? R.drawable.group_item_arrow_expand : R.drawable.group_item_arrow_collapse);
        groupViewHolder.text.setText(productContent.getItemName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.expandable_item_layout, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.text = (TextView) convertView.findViewById(R.id.item_content);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        String content = getChild(groupPosition, childPosition);
        childViewHolder.text.setText(content);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupViewHolder {
        public ImageView arrow;
        public TextView text;
    }

    private class ChildViewHolder {
        public TextView text;
    }
}
