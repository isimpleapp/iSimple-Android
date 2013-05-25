package com.treelev.isimple.filter;

import android.content.Context;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.*;
import com.treelev.isimple.domain.ui.filter.*;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.enumerable.item.Sweetness;
import org.holoeverywhere.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class PortoHeresFilter extends Filter implements View.OnClickListener {

    private List<FilterItem> filterItemList;

    public PortoHeresFilter(Context context) {
        super(context);
//        this.context = context;
//        LayoutInflater inflater = LayoutInflater.from(context);
//        filterHeaderLayout = inflater.inflate(R.layout.category_porto_heres_filter_header_layout);
//        setClickButt(R.id.white_color_butt, R.id.white_porto_heres_check);
//        setClickButt(R.id.red_color_butt, R.id.red_porto_heres_check);
        filterItemList = createFilterContent();
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

//    private void setClickButt(int buttonId, int checkboxId) {
//        TextView textView = (TextView) filterHeaderLayout.findViewById(buttonId);
//        textView.setOnClickListener(this);
//        textView.setTag(filterHeaderLayout.findViewById(checkboxId));
//    }

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new DefaultActivityFilterItem(getContext(), getContext().getString(R.string.filter_item_type),
                FilterItemData.createFromPresentable(new Presentable[]{ProductType.PORTO, ProductType.HERES})));
        filterItems.add(new DefaultActivityFilterItem(getContext(), getContext().getString(R.string.filter_item_sweetness),
                FilterItemData.createFromPresentable(Sweetness.getPortoSweetness())));
        filterItems.add(new DefaultActivityFilterItem(getContext(), getContext().getString(R.string.filter_item_classifier), null));
        filterItems.add(new ExpandableActivityFilterItem(getContext(), getContext().getString(R.string.filter_item_region),
                FilterItemData.getAvailableCountryRegions(getContext(), R.id.category_wine_butt)));
        filterItems.add(new DefaultSeekBarFilterItem(getContext()));
        return filterItems;
    }
}
