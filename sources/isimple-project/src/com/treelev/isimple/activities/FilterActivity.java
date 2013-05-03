package com.treelev.isimple.activities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.enumerable.item.Sweetness;
import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends ListActivity implements ActionBar.OnNavigationListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int filterChildPosition = getIntent().getIntExtra(CatalogByCategoryActivity.FILTER_DATA_TAG, -1);
        setContentView(R.layout.filter_data_layout);
        createNavigation();
        getListView().setAdapter(new FilterDataAdapter(this, 0, createFilterDataList(filterChildPosition)));
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
        super.onBackPressed();
    }

    private List<FilterData> createFilterDataList(int filterId) {
        List<FilterData> filterList = new ArrayList<FilterData>();
        if (filterId == 0) {
            for (Sweetness sweetness : Sweetness.values()) {
                if (sweetness != Sweetness.UNKNOWN) {
                    filterList.add(new FilterData(sweetness.getDescription()));
                }
            }
        }
        return filterList;
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

    private class FilterDataAdapter extends ArrayAdapter<FilterData> {

        private LayoutInflater inflater;

        public FilterDataAdapter(Context context, int textViewResourceId, List<FilterData> objects) {
            super(context, textViewResourceId, objects);
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
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FilterData filterData = getItem(position);
            viewHolder.textView.setText(filterData.name);
            viewHolder.checkBox.setChecked(filterData.check);
            return convertView;
        }

        private class ViewHolder {
            TextView textView;
            CheckBox checkBox;
        }
    }

    private class FilterData {
        String name;
        boolean check;

        private FilterData(String name) {
            this.name = name;
            this.check = false;
        }
    }
}