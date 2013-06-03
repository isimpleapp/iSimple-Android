package com.treelev.isimple.domain.ui.filter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.filter.Filter;
import com.treelev.isimple.utils.managers.ProxyManager;
import com.treelev.isimple.views.RangeSeekBar;
import org.holoeverywhere.widget.LinearLayout;

public class DefaultSeekBarFilterItem extends FilterItem {
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 5000;
    private static final int DEFAULT_VALUE = -1;

    private LayoutInflater layoutInflater;

    private int minValue = DEFAULT_VALUE;
    private int maxValue = DEFAULT_VALUE;
    private String targetColumn;
    private Filter filterObject;

    public DefaultSeekBarFilterItem(Context context, String targetColumn, Filter filterObject) {
        super(context, ITEM_INLINE);
        this.layoutInflater = LayoutInflater.from(context);
        this.targetColumn = targetColumn;
        this.filterObject = filterObject;
    }

    @Override
    public View renderView(View convertView, ViewGroup parent) {
        minValue = DEFAULT_MIN_VALUE;
        maxValue = getSeekBarMaxValue();
        if (convertView == null || !(convertView.getTag() instanceof RangeSeekBar)) {
            convertView = layoutInflater.inflate(R.layout.category_filter_seekbar_item_layout, parent, false);
            RangeSeekBar<Integer> seekBar = createSeekBar(maxValue);
            LinearLayout seekBarLayout = (LinearLayout) convertView.findViewById(R.id.seek_bar_layout);
            seekBarLayout.addView(seekBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                            getContext().getResources().getDisplayMetrics())));
            convertView.setTag(seekBar);
        }
        RangeSeekBar<Integer> seekBar = (RangeSeekBar<Integer>) convertView.getTag();
        seekBar.setSelectedMinValue(minValue);
        seekBar.setSelectedMaxValue(maxValue);
        return convertView;
    }

    @Override
    public String getSQLWhereClause() {
        return (minValue == -1 || maxValue == -1) ? "" :
                String.format("(%1$s >= %2$s and %1$s <= %3$s)", targetColumn, minValue, maxValue);
    }

    private RangeSeekBar<Integer> createSeekBar() {
        return createSeekBar(null);
    }

    private RangeSeekBar<Integer> createSeekBar(Integer maxValue) {
        if (maxValue == null) {
            maxValue = DEFAULT_MAX_VALUE;
        }
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(DEFAULT_MIN_VALUE, maxValue, getContext());
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                DefaultSeekBarFilterItem.this.maxValue = maxValue;
                DefaultSeekBarFilterItem.this.minValue = minValue;
            }
        });
        seekBar.setBackgroundColor(Color.WHITE);
        return seekBar;
    }

    private int getSeekBarMaxValue() {
        return new ProxyManager(getContext()).getMaxValuePriceByCategoryId(DrinkCategory.getItemCategoryByFilter(filterObject));
    }
}
