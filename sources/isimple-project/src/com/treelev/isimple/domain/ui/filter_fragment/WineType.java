package com.treelev.isimple.domain.ui.filter_fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.treelev.isimple.R;
import com.treelev.isimple.fragments.filters.FilterFragment;

public class WineType extends ItemFilter {

    protected WineType(LayoutInflater inflater, FilterFragment filter) {
        super(inflater, filter);
    }

    @Override
    public void reset() {
        ((Button)mView).setText(mFilter.getString(R.string.lbl_type_wine_item));
    }

    @Override
    protected View createView() {
        return mInflater.inflate(R.layout.button_item_filter, null);
    }

    @Override
    protected void initControl() {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mFilter.getActivity().startActivityForResult();
                mFilter.getActivity().overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
            }
        });
        ((Button)mView).setText(mFilter.getString(R.string.lbl_type_wine_item));
    }

    @Override
    public String getWhereClause() {
        return null;
    }
}
