package com.treelev.isimple.domain.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.treelev.isimple.R;
import org.holoeverywhere.widget.TextView;

public class DefaultActivityFilterItem extends FilterItem {
    private LayoutInflater layoutInflater;
    private View itemView;

    public DefaultActivityFilterItem(Context context, String label, Class activityClass) {
        super(context, ITEM_ACTIVITY, label, activityClass);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View renderView(View convertView) {
        if (itemView == null) {
            itemView = layoutInflater.inflate(R.layout.category_filter_text_item_layout, null);
            TextView text = (TextView) itemView.findViewById(R.id.item_content);
            text.setText(getLabel());
        }
        return itemView;
    }
}
