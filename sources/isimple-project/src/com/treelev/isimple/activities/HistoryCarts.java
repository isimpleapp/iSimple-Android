package com.treelev.isimple.activities;

import android.os.Bundle;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.HistoryCartsAdapter;

public class HistoryCarts extends BaseListActivity{

    private HistoryCartsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_carts_layout);
        mAdapter = new HistoryCartsAdapter(this, null);
        getListView().setAdapter(mAdapter);
        createDrawableMenu();
    }
}
