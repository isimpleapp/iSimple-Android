package com.treelev.isimple.activities.filter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.treelev.isimple.R;
import com.treelev.isimple.activities.BaseListActivity;
import com.treelev.isimple.domain.ui.filter.FilterItemData;

import java.util.Arrays;

public class DefaultListFilterActivity extends BaseListActivity {
    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String FILTER_DATA = "filter_data";
    private final static String CURRENT_CATEGORY = "current_category";

    private FilterItemData[] filterData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_list_layout);
        createNavigationMenuBar();
        getListView().setAdapter(new FilterDataAdapter(getFilterData()));
    }

    protected void createNavigationMenuBar() {
//        createDrawableMenu();
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        RelativeLayout relative = new RelativeLayout(getSupportActionBarContext());
//        TextView title = new TextView(getSupportActionBarContext());
//
//        title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
//        title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 27.0f);
//        title.setTextColor(getResources().getColor(R.color.isimple_pink));
//        title.setPadding(10, 0, 0, 0);
//        relative.addView(title);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(relative);
//        int currentCategory = getIntent().getIntExtra(FilterItem.CURRENT_CATEGORY, -1);
//        switch(currentCategory) {
//            case 0:
//                getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
//                title.setText("Каталог");
//                break;
//            case 1:
//                getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
//                title.setText("Магазины");
//                break;
//        }
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

    public static void putFilterData(Intent intent, FilterItemData[] filterData, int category) {
        Bundle bundle = intent.getBundleExtra(BUNDLE_EXTRA);
        if (bundle == null) {
            bundle = new Bundle();
            intent.putExtra(BUNDLE_EXTRA, bundle);
            intent.putExtra(CURRENT_CATEGORY, category);
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