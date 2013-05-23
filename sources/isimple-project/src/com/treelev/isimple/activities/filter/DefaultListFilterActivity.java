package com.treelev.isimple.activities.filter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.CatalogByCategoryActivity;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.ui.FilterItemData;
import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

public class DefaultListFilterActivity extends ListActivity implements ActionBar.OnNavigationListener {

    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String FILTER_DATA = "filter_data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int categoryId = getIntent().getIntExtra(FilterItem.EXTRA_CATEGORY_ID, -1);
//        filterChildPosition = getIntent().getIntExtra(FilterItem.EXTRA_POSITION, -1);
        setContentView(R.layout.filter_list_layout);
        createNavigation();
        getListView().setAdapter(new FilterDataAdapter(this, 0, getFilterData()));
    }

    private FilterItemData[] getFilterData() {
        Bundle bundle = getIntent().getBundleExtra(BUNDLE_EXTRA);
        FilterItemData[] items = (FilterItemData[])bundle.getParcelableArray(FILTER_DATA);
        if (items == null)
            items = new FilterItemData[0];

        return items;
    }

    public static void putFilterData(Intent intent, FilterItemData[] filterData) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        if (bundle == null) {
            bundle = new Bundle();
            intent.putExtra(BUNDLE_EXTRA, bundle);
        }
        bundle.putParcelableArray(FILTER_DATA, filterData);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
        Intent resultIntent = new Intent();
        boolean isAnyItemCheck = isAnyItemCheck();
//        resultIntent.putExtra(CatalogByCategoryActivity.EXTRA_CHILD_POSITION, filterChildPosition);
        if (isAnyItemCheck) {
            resultIntent.putExtra(CatalogByCategoryActivity.EXTRA_RESULT_CHECKED, isAnyItemCheck);
        }
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    private boolean isAnyItemCheck() {
        for (int i = 0; i < getListView().getChildCount(); i++) {
            RelativeLayout itemLayout = (RelativeLayout) getListView().getChildAt(i);
            CheckBox checkBox = (CheckBox) itemLayout.findViewById(R.id.filter_data_check);
            if (checkBox.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private void createNavigation() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Context context = getSupportActionBar().getThemedContext();
        String[] locations = getResources().getStringArray(R.array.main_menu_items);
        TypedArray typedArray = getResources().obtainTypedArray(R.array.main_menu_icons);
        Drawable[] iconLocation = new Drawable[typedArray.length()];
        for (int i = 0; i < locations.length; ++i) {
            iconLocation[i] = typedArray.getDrawable(i);
        }
        NavigationListAdapter list = new NavigationListAdapter(this, iconLocation, locations);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
    }

    private class FilterDataAdapter extends ArrayAdapter<FilterItemData> implements View.OnClickListener {

        private LayoutInflater inflater;

        public FilterDataAdapter(Context context, int textViewResourceId, FilterItemData[] filterData) {
            super(context, textViewResourceId, filterData);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.filter_item_view, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.filter_data_name);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.filter_data_check);
                convertView.setOnClickListener(this);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FilterItemData filterData = getItem(position);
            viewHolder.textView.setText(filterData.getName());
            viewHolder.checkBox.setChecked(filterData.isChecked());
            return convertView;
        }

        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = ((ViewHolder) v.getTag());
            viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
        }

        private class ViewHolder {
            TextView textView;
            CheckBox checkBox;
        }
    }
}