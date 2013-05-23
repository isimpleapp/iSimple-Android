package com.treelev.isimple.domain.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.views.RangeSeekBar;
import org.holoeverywhere.widget.LinearLayout;

public class DefaultSeekBarFilterItem extends FilterItem {
    private static final int DEFAULT_MIN_VALUE = 20;
    private static final int DEFAULT_MAX_VALUE = 75;

    private LayoutInflater layoutInflater;

    private int minValue = DEFAULT_MIN_VALUE;
    private int maxValue = DEFAULT_MAX_VALUE;

    public DefaultSeekBarFilterItem(Context context) {
        super(context, ITEM_INLINE);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View renderView(View convertView) {
        if (convertView == null || !(convertView.getTag() instanceof RangeSeekBar)) {
            convertView = layoutInflater.inflate(R.layout.category_filter_seekbar_item_layout, null);

            RangeSeekBar<Integer> seekBar = createSeekBar();
            LinearLayout seekBarLayout = (LinearLayout) convertView.findViewById(R.id.seek_bar_layout);
            seekBarLayout.addView(seekBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics())));

            convertView.setTag(seekBar);
        }
        RangeSeekBar<Integer> seekBar = (RangeSeekBar<Integer>) convertView.getTag();
        seekBar.setSelectedMinValue(minValue);
        seekBar.setSelectedMaxValue(maxValue);
        return convertView;
    }

    private RangeSeekBar<Integer> createSeekBar() {
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, getContext());
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

}
