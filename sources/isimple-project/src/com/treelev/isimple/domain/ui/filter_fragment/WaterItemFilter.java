package com.treelev.isimple.domain.ui.filter_fragment;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.treelev.isimple.R;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.fragments.filters.FilterFragment;
import com.treelev.isimple.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class WaterItemFilter extends ItemFilter {

    private List<Item> mItems;

    public WaterItemFilter(android.view.LayoutInflater inflater, FilterFragment filter) {
        super(inflater, filter, true);
        initControl();
    }

    @Override
    public void reset() {
        for(Item item : mItems){
            LogUtils.i("", "WaterItemFilter reset item");
            item.setChecked(false);
        }
    }

    @Override
    protected View createView() {
        return mInflater.inflate(R.layout.water_filter_layout, null);
    }

    @Override
    protected void initControl() {
//TODO check type SYRUP
        mItems = new ArrayList<Item>();
        initItem(R.id.btn_water, String.valueOf(ProductType.WATER.ordinal()));
        initItem(R.id.btn_juice, String.valueOf(ProductType.JUICE.ordinal()));
        initItem(R.id.btn_energetik, String.valueOf(ProductType.ENERGY.ordinal()));
        initItem(R.id.btn_bar, String.valueOf(ProductType.SYRUP.ordinal()));
    }

    private void initItem(int idControl, String whereClause){
        Item item = new Item();
        item.whereClause = whereClause;
        item.button = (Button) mView.findViewById(idControl);
        item.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Item item : mItems){
                    if(item.button.getId() != view.getId()){
                        item.setChecked(false);
                    } else {
                        item.setChecked(!item.isChecked);
                    }
                }
                onChangeStateItemFilter();
            }
        });
        mItems.add(item);
    }


    @Override
    public String getWhereClause() {
        String whereClause = "";
        for(Item item : mItems){
            if(item.isChecked){
                whereClause = item.whereClause;
                break;
            }
        }
        return TextUtils.isEmpty(whereClause) ? whereClause : String.format("(item.product_type = %s)", whereClause);
    }

    private class Item {
        public String whereClause;
        public Button button;
        public boolean isChecked;

        public void setChecked(boolean checked){
            isChecked = checked;
            if(isChecked){
                button.setTextColor(Color.MAGENTA);
            } else {
                button.setTextColor(mFilter.getResources().getColor(R.color.product_text_color1));
            }
        }


    }
}
