package com.treelev.isimple.filter;

import android.view.View;
import com.treelev.isimple.domain.ui.FilterItem;

import java.util.List;

public interface Filter {
    public View getFilterHeaderLayout();
    public String getSql();
    public List<FilterItem> getFilterContent();
}
