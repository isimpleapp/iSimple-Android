package com.treelev.isimple.activities.filter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.ui.FilterItemData;
import com.treelev.isimple.domain.ui.Presentable;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.enumerable.item.Sweetness;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DefaultListFilterActivity extends ListActivity implements ActionBar.OnNavigationListener {

    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String FILTER_DATA = "filter_data";
    private ProxyManager proxyManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int categoryId = getIntent().getIntExtra(FilterItem.EXTRA_CATEGORY_ID, -1);
//        filterChildPosition = getIntent().getIntExtra(FilterItem.EXTRA_POSITION, -1);
        setContentView(R.layout.filter_list_layout);
        createNavigation();
        proxyManager = new ProxyManager(this);
        getListView().setAdapter(new FilterDataAdapter(this, 0, getFilterData()));
    }

    //TODO class cast exception
    private FilterItemData[] getFilterData() {
        Bundle bundle = getIntent().getBundleExtra(BUNDLE_EXTRA);
        Parcelable[] parcelableItems = bundle.getParcelableArray(FILTER_DATA);
        FilterItemData[] items = new FilterItemData[0];
        if (parcelableItems != null) {
            items = new FilterItemData[parcelableItems.length];
            for (int i = 0; i < parcelableItems.length; i++) {
                items[i] = (FilterItemData) parcelableItems[i];
            }
        }
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
        /*boolean isAnyItemCheck = isAnyItemCheck();
        resultIntent.putExtra(CatalogByCategoryActivity.EXTRA_CHILD_POSITION, filterChildPosition);
        if (isAnyItemCheck) {
            resultIntent.putExtra(CatalogByCategoryActivity.EXTRA_RESULT_CHECKED, isAnyItemCheck);
        }*/
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

    private List<FilterData> createFilterDataList(int categoryId, int filterId) {
        return getCategoryData(categoryId, filterId);
    }

    private List<FilterData> getCategoryData(int categoryId, int filterId) {
        switch (categoryId) {
            case R.id.category_wine_butt:
                return getWineFilterData(categoryId, filterId);
            case R.id.category_spirits_butt:
                return getSpiritsFilterData(categoryId, filterId);
            case R.id.category_sparkling_butt:
                return getSparklingFilterData(categoryId, filterId);
            case R.id.category_sake_butt:
                return null;
            case R.id.category_porto_heres_butt:
                return null;
            case R.id.category_water_butt:
                return null;
            default:
                return null;
        }
    }

    private List<FilterData> getSakeFilterData(int categoryId, int filterId) {
        switch (filterId) {
            case 0:
                return convertEnumDescToFilterData(Sweetness.getSparklingSweetness());
        }
        return null;
    }

    private List<FilterData> getSparklingFilterData(int categoryId, int filterId) {
        switch (filterId) {
            case 0:
                return convertEnumDescToFilterData(Sweetness.getSparklingSweetness());
            case 1:
                return convertYearsToFilterData(categoryId);
            case 3:
                break;
        }
        return null;
    }

    private List<FilterData> getSpiritsFilterData(int categoryId, int filterId) {
        switch (filterId) {
            case 0:
                return convertEnumDescToFilterData(Sweetness.getWineSweetness());
            case -1:
                return convertEnumDescToFilterData(ProductType.getSpiritsTypes());
        }
        return null;
    }

    private List<FilterData> getWineFilterData(int categoryId, int filterId) {
        switch (filterId) {
            case 0:
                return convertEnumDescToFilterData(Sweetness.getWineSweetness());
            case 2:
                break;
            case 3:
                return convertYearsToFilterData(categoryId);
        }
        return null;
    }

    private List<FilterData> convertEnumDescToFilterData(Presentable[] enumDescArray) {
        List<FilterData> filterList = new ArrayList<FilterData>();
        for (Presentable enumItem : enumDescArray) {
            filterList.add(new FilterData(enumItem.getLabel()));
        }
        return filterList;
    }

    private List<FilterData> convertYearsToFilterData(int categoryId) {
        List<FilterData> filterList = new ArrayList<FilterData>();
        List<String> years = proxyManager.getYearsByCategory(categoryId);
        for (String str : years) {
            filterList.add(new FilterData(str));
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

    private class FilterData {
        String name;
        boolean check;

        private FilterData(String name) {
            this.name = name;
            this.check = false;
        }
    }
}