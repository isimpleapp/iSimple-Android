package com.treelev.isimple.filter;

import android.content.Context;
import com.treelev.isimple.domain.ui.filter.FilterItem;

import java.util.List;

public abstract class Filter {

    private Context context;

    protected Filter(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    public abstract String getSql();
    public abstract List<FilterItem> getFilterContent();
}
