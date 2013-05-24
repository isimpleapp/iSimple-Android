package com.treelev.isimple.filter;

import android.view.View;
import com.treelev.isimple.domain.ui.FilterItem;

import java.util.List;

public interface Filter {
    View getFilterHeaderLayout();
    String getSql();
    List<FilterItem> getFilterContent();
}
