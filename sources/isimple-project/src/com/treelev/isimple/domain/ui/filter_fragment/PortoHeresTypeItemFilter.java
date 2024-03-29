package com.treelev.isimple.domain.ui.filter_fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.treelev.isimple.R;
import com.treelev.isimple.fragments.filters.FilterFragment;

public class PortoHeresTypeItemFilter extends ItemFilter {

    private CheckBox mPorto;
    private CheckBox mHeres;


    public PortoHeresTypeItemFilter(LayoutInflater inflater, FilterFragment filter) {
        super(inflater, filter, true);
        initControl();
    }

    @Override
    public void reset() {
        mPorto.setChecked(false);
        mHeres.setChecked(false);
        mPorto.setTextColor(R.color.product_text_color1);
        mHeres.setTextColor(R.color.product_text_color1);
    }

    @Override
    protected View createView() {
        return mInflater.inflate(R.layout.porto_heres_type_layout, null);
    }

    @Override
    protected void initControl() {
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                compoundButton.setTextColor(checked ?
                        mFilter.getResources().getColor(R.color.isimple_pink)
                        : mFilter.getResources().getColor(R.color.product_text_color1));
                onChangeStateItemFilter();
            }
        };

        View.OnClickListener newListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox)view;
                checkBox.setTextColor(checkBox.isChecked() ?
                        mFilter.getResources().getColor(R.color.isimple_pink)
                        : mFilter.getResources().getColor(R.color.product_text_color1));
                onChangeStateItemFilter();
            }
        };
        mPorto = (CheckBox)mView.findViewById(R.id.chb_porto);
        mHeres = (CheckBox)mView.findViewById(R.id.chb_heres);
        mPorto.setOnCheckedChangeListener(listener);
        mHeres.setOnCheckedChangeListener(listener);
    }

    @Override
    public String getWhereClause() {
        StringBuilder sqlBuilder = new StringBuilder();
        if (mPorto.isChecked()) {
            sqlBuilder.append("item.product_type = 3");
        }
        if (mHeres.isChecked()) {
            if (sqlBuilder.length() > 0) {
                sqlBuilder.append(" OR ");
            }
            sqlBuilder.append("item.product_type = 4" );
        }
        if (sqlBuilder.length() > 0) {
            sqlBuilder.insert(0, '(');
            sqlBuilder.append(')');
        }
        return sqlBuilder.toString();
    }
}
