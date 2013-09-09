package com.treelev.isimple.activities;

import android.os.Bundle;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.HistoryCartDetailedAdapter;

public class HistoryCartDetailed extends BaseListActivity{

    private HistoryCartDetailedAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_cart_detailed_layout);
        mAdapter = new HistoryCartDetailedAdapter(this, null);
        getListView().setAdapter(mAdapter);
    }
}
