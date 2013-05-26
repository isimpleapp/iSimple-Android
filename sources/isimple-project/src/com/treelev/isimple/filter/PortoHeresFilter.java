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

public class PortoHeresFilter extends Filter {

    private List<FilterItem> filterItemList;

    public PortoHeresFilter(Context context) {
        super(context);
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

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new PortoColorFilterItem(getContext()));
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
