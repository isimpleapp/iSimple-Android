package com.treelev.isimple.filter;

import android.content.Context;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.filter.DefaultExpandableListFilterActivity;
import com.treelev.isimple.domain.ui.*;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.enumerable.item.Sweetness;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PortoHeresFilter implements Filter, View.OnClickListener {

    private Context context;
    private View filterHeaderLayout;
    private List<FilterItem> filterItemList;

    public PortoHeresFilter(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        filterHeaderLayout = inflater.inflate(R.layout.category_porto_heres_filter_header_layout);
        setClickButt(R.id.white_color_butt, R.id.white_porto_heres_check);
        setClickButt(R.id.red_color_butt, R.id.red_porto_heres_check);
        filterItemList = createFilterContent();
    }

    @Override
    public View getFilterHeaderLayout() {
        return filterHeaderLayout;
    }

    @Override
    public String getSql() {
        return null;
    }

    @Override
    public List<FilterItem> getFilterContent() {
        return filterItemList;
    }

    @Override
    public void onClick(View v) {
        CheckBox checkBox = (CheckBox) v.getTag();
        checkBox.setChecked(!checkBox.isChecked());
    }

    private void setClickButt(int buttonId, int checkboxId) {
        TextView textView = (TextView) filterHeaderLayout.findViewById(buttonId);
        textView.setOnClickListener(this);
        textView.setTag(filterHeaderLayout.findViewById(checkboxId));
    }

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new DefaultActivityFilterItem(context, context.getString(R.string.filter_item_type),
                FilterItemData.createFromPresentable(new Presentable[]{ProductType.PORTO, ProductType.HERES})));
        filterItems.add(new DefaultActivityFilterItem(context, context.getString(R.string.filter_item_sweetness),
                FilterItemData.createFromPresentable(Sweetness.getPortoSweetness())));
        filterItems.add(new DefaultActivityFilterItem(context, context.getString(R.string.filter_item_classifier), null));
        filterItems.add(new DefaultActivityFilterItem(context, context.getString(R.string.filter_item_region),
                DefaultExpandableListFilterActivity.class, null));
        filterItems.add(new DefaultSeekBarFilterItem(context));
        return filterItems;
    }
}
