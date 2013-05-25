package com.treelev.isimple.filter;

import android.content.Context;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.*;
import com.treelev.isimple.domain.ui.filter.DefaultActivityFilterItem;
import com.treelev.isimple.domain.ui.filter.DefaultSeekBarFilterItem;
import com.treelev.isimple.domain.ui.filter.FilterItem;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.enumerable.item.ProductType;
import org.holoeverywhere.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class SakeFilter extends Filter implements View.OnClickListener {

    private List<FilterItem> filterItemList;

    public SakeFilter(Context context) {
        super(context);
//        this.context = context;
//        LayoutInflater inflater = LayoutInflater.from(context);
//        filterHeaderLayout = inflater.inflate(R.layout.category_sake_filter_header_layout);
//        setClickButt(R.id.classic_butt, R.id.classic_sake_check);
//        setClickButt(R.id.author_butt, R.id.author_sake_check);
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
        filterItems.add(new DefaultActivityFilterItem(getContext(), getContext().getString(R.string.filter_item_style),
                FilterItemData.createFromPresentable(new Presentable[]{ProductType.SAKE, ProductType.SAKE_AUT})));
        filterItems.add(new DefaultActivityFilterItem(getContext(), getContext().getString(R.string.filter_item_premiality), null));
        filterItems.add(new DefaultSeekBarFilterItem(getContext()));
        return filterItems;
    }
}
