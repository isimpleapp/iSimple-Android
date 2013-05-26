package com.treelev.isimple.activities.filter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.BaseListActivity;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import java.util.Arrays;

public class DefaultListFilterActivity extends BaseListActivity {
    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String FILTER_DATA = "filter_data";

    private FilterItemData[] filterData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_list_layout);
        createNavigationMenuBar();
        getListView().setAdapter(new FilterDataAdapter(getFilterData()));
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
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        putFilterData(resultIntent, getFilterData());
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    public static FilterItemData[] getFilterData(Intent intent) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        if (bundle != null) {
            Parcelable[] parcelableItems = bundle.getParcelableArray(FILTER_DATA);
            return (parcelableItems != null) ?
                    Arrays.copyOf(parcelableItems, parcelableItems.length, FilterItemData[].class) :
                    new FilterItemData[0];
        }
        else {
            return new FilterItemData[0];
        }
    }

    public static void putFilterData(Intent intent, FilterItemData[] filterData) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        if (bundle == null) {
            bundle = new Bundle();
            intent.putExtra(BUNDLE_EXTRA, bundle);
        }
        bundle.putParcelableArray(FILTER_DATA, filterData);
    }

    private FilterItemData[] getFilterData() {
        if (filterData == null) {
            filterData = getFilterData(getIntent());
        }
        return filterData;
    }

    private class FilterDataAdapter extends ArrayAdapter<FilterItemData> implements View.OnClickListener {
        private LayoutInflater inflater;

        public FilterDataAdapter(FilterItemData[] filterData) {
            super(DefaultListFilterActivity.this, 0, filterData);
            inflater = LayoutInflater.from(DefaultListFilterActivity.this);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.filter_item_view, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.filter_data_name);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.filter_data_check);

                viewHolder.checkBox.setOnClickListener(this);
                convertView.setOnClickListener(this);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FilterItemData filterData = getItem(position);
            viewHolder.textView.setText(filterData.getName());
            viewHolder.checkBox.setChecked(filterData.isChecked());
            viewHolder.position = position;
            return convertView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.filter_data_check) {
                v = (View)v.getParent();
            }
            ViewHolder viewHolder = ((ViewHolder) v.getTag());
            FilterItemData filterData = getItem(viewHolder.position);
            filterData.setChecked(!filterData.isChecked());
            viewHolder.checkBox.setChecked(filterData.isChecked());
        }

        private class ViewHolder {
            TextView textView;
            CheckBox checkBox;
            int position;
        }
    }
}