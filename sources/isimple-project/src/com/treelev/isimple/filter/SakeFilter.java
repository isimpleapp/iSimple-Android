package com.treelev.isimple.filter;

import android.content.Context;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.filter.*;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import org.holoeverywhere.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class SakeFilter extends Filter implements View.OnClickListener {

    private List<FilterItem> filterItemList;

    public SakeFilter(Context context, int currentCategory) {
        super(context, currentCategory);
        filterItemList = createFilterContent();
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

    private List<FilterItem> createFilterContent() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new ExpandableActivityFilterItem(getContext(),
                getContext().getString(R.string.filter_item_premiality),
                FilterItemData.getAvailableClassifications(getContext(), DrinkCategory.SAKE),
                ClassificationSqlWhereClauseBuilder.INSTANCE, currentCategory));
        filterItems.add(new DefaultSeekBarFilterItem(getContext(), "item.price", this));
        return filterItems;
    }
}
