package com.treelev.isimple.domain.ui.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.treelev.isimple.R;

public class PortoColorFilterItem extends FilterItem implements View.OnTouchListener {
    private LayoutInflater inflater;
    private View colorView;

    private boolean white;
    private boolean red;

    public boolean isWhite() {
        return white;
    }

    public boolean isRed() {
        return red;
    }

    public PortoColorFilterItem(Context context) {
        super(context, FilterItem.ITEM_INLINE);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View renderView(View convertView, ViewGroup parent) {
        if (colorView == null) {
            colorView = inflater.inflate(R.layout.category_filter_portocolor_item_layout, parent, false);
            setClickButt(R.id.white_color_butt, R.id.white_porto_heres_check);
            setClickButt(R.id.red_color_butt, R.id.red_porto_heres_check);
        }
        return colorView;
    }

    private void setClickButt(int buttonId, int checkboxId) {
        TextView textView = (TextView) colorView.findViewById(buttonId);
        textView.setOnTouchListener(this);
        textView.setTag(colorView.findViewById(checkboxId));
    }

    @Override
    public boolean process() {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            boolean result = false;
            if (v.getId() == R.id.white_color_butt)
                result = white = !white;
            else if (v.getId() == R.id.red_color_butt)
                result = red = !red;

            CheckBox checkBox = (CheckBox) v.getTag();
            checkBox.setChecked(result);
        }
        return false;
    }
}
