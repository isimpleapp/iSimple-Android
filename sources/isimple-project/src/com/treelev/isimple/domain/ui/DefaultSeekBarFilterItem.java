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
    private LayoutInflater layoutInflater;

    public DefaultSeekBarFilterItem(Context context) {
        super(context, ITEM_INLINE);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View renderView(View convertView) {
        RangeSeekBar<Integer> seekBar;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.category_filter_seekbar_item_layout, null);
            LinearLayout seekBarLayout = (LinearLayout) convertView.findViewById(R.id.seek_bar_layout);
            seekBar = createSeekBar();
            seekBarLayout.addView(seekBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics())));
            convertView.setTag(seekBar);
        } else {
            seekBar = (RangeSeekBar<Integer>) convertView.getTag();
        }
        return convertView;
    }

    private RangeSeekBar<Integer> createSeekBar() {
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(20, 75, getContext());
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Log.i("TAG", "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
            }
        });
        seekBar.setBackgroundColor(Color.WHITE);
        return seekBar;
    }

}
