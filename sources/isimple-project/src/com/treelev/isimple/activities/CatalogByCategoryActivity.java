package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.FilterAdapter;
import com.treelev.isimple.adapters.NavigationListAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.ui.FilterItem;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CatalogByCategoryActivity extends ListActivity implements RadioGroup.OnCheckedChangeListener,
        ActionBar.OnNavigationListener, ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupClickListener,
        ExpandableListView.OnChildClickListener {

    private final static String FIELD_TAG = "field_tag";
    public final static String FILTER_DATA_TAG = "filter_data";
    private Cursor cItems;
    private SimpleCursorAdapter mListCategoriesAdapter;
    private ExpandableListView listView;
    private CheckBox[] filterTypeCheckBoxArray;
    private View footerView;
    private View darkView;
    private Integer mCategoryID;
    private boolean mExpandFiltr = false;
    private ProxyManager mProxyManager;

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = new ProxyManager(this);
        }
        return mProxyManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        createNavigation();
        darkView = findViewById(R.id.category_dark_view);
        darkView.setVisibility(View.GONE);
        darkView.setOnClickListener(null);
        mCategoryID = getIntent().getIntExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, -1);
        initDataListView(mCategoryID);
        initFilterListView(createFilterList(), mCategoryID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backOrCollapse();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int rgb) {
        switch (rgb) {
            case R.id.alphabet_sort:
                updateList(ProxyManager.SORT_NAME_AZ);
                break;
            case R.id.price_sort:
                updateList(ProxyManager.SORT_PRICE_UP);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search, menu);
        SearchManager searcMenager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searcMenager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        final MenuItem mItemSearch = menu.findItem(R.id.menu_search);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchResultActivity.backActivity = CatalogByCategoryActivity.class;
                SearchResultActivity.categoryID = mCategoryID;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
       mSearchView.setOnQueryTextFocusChangeListener( new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if (hasFocus) {
                   darkView.setVisibility(View.VISIBLE);
                   darkView.getBackground().setAlpha(150);
               } else {
                   darkView.setVisibility(View.GONE);
                   mItemSearch.collapseActionView();
                   mSearchView.setQuery("", false);
               }
           }
       });
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                darkView.setVisibility(View.GONE);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        View groupView = listView.getChildAt(groupPosition);
        groupView.findViewById(R.id.group_name).setVisibility(View.GONE);
        footerView.findViewById(R.id.sort_group).setVisibility(View.GONE);
        footerView.findViewById(R.id.filter_button_bar).setVisibility(View.VISIBLE);
        RelativeLayout categoryTypeLayout = (RelativeLayout) groupView.findViewById(R.id.category_type_view);
        categoryTypeLayout.setVisibility(View.VISIBLE);
        categoryTypeLayout.findViewById(R.id.red_wine_butt).setOnClickListener(categoryTypeClick);
        categoryTypeLayout.findViewById(R.id.white_wine_butt).setOnClickListener(categoryTypeClick);
        categoryTypeLayout.findViewById(R.id.pink_wine_butt).setOnClickListener(categoryTypeClick);
        CheckBox checkBoxRedWine = (CheckBox) categoryTypeLayout.findViewById(R.id.red_wine_check);
        CheckBox checkBoxWhiteWine = (CheckBox) categoryTypeLayout.findViewById(R.id.white_wine_check);
        CheckBox checkBoxPinkWine = (CheckBox) categoryTypeLayout.findViewById(R.id.pink_wine_check);
        filterTypeCheckBoxArray = new CheckBox[]{checkBoxRedWine, checkBoxWhiteWine, checkBoxPinkWine};

        mExpandFiltr = true;
//        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_down_anim);
//        groupView.startAnimation(anim);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ошибка");
        builder.setMessage("Фильтрация недоступна");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent filterDataIntent = new Intent(this, FilterActivity.class);
        filterDataIntent.putExtra(FILTER_DATA_TAG, childPosition);
        startActivity(filterDataIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        return false;
    }

    @Override
    public void onBackPressed() {
        backOrCollapse();
    }

    private void backOrCollapse() {
        if(mExpandFiltr) {
            resetButtonClick.onClick(null);
            mExpandFiltr = false;
        } else {
            Intent intent = new Intent(this, CatalogListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);

        }
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor product = (Cursor)l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, product.getString(0));
        startActivity(startIntent);
        overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private List<FilterItem> createFilterList() {
        List<FilterItem> filterItems = new ArrayList<FilterItem>();
        filterItems.add(new FilterItem(FilterItem.ITEM_TYPE_TEXT, "Содержание сахара"));
        filterItems.add(new FilterItem(FilterItem.ITEM_TYPE_TEXT, "Регион"));
        filterItems.add(new FilterItem(FilterItem.ITEM_TYPE_PROGRESS));
        filterItems.add(new FilterItem(FilterItem.ITEM_TYPE_TEXT, "Год урожая"));
        return filterItems;
    }

    private void initDataListView(int categoryId) {
        cItems = getProxyManager().getItemsByCategory(categoryId, ProxyManager.SORT_NAME_AZ);
        startManagingCursor(cItems);
        mListCategoriesAdapter = new ItemCursorAdapter(cItems);
        getListView().setAdapter(mListCategoriesAdapter);
    }

    private void initFilterListView(List<FilterItem> content, int categoryId) {
        BaseExpandableListAdapter filterAdapter = new FilterAdapter(this, content);
        listView = (ExpandableListView) findViewById(R.id.filtration_view);
        listView.setOnGroupExpandListener(this);
        listView.setOnChildClickListener(this);
        if (categoryId != R.id.category_wine_butt) {
            listView.setOnGroupClickListener(this);
        }
        footerView = getLayoutInflater().inflate(R.layout.category_filtration_button_bar_layout, listView, false);
        ((RadioGroup) footerView.findViewById(R.id.sort_group)).setOnCheckedChangeListener(this);
        footerView.findViewById(R.id.reset_butt).setOnClickListener(resetButtonClick);
        listView.addFooterView(footerView, null, false);
        listView.setAdapter(filterAdapter);
    }

    private void updateList(int sortBy) {
        stopManagingCursor(cItems);
        cItems.close();
        new SortTask(this).execute(sortBy);
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

    protected void onDestroy() {
        super.onDestroy();
        if (mProxyManager != null) {
            mProxyManager.release();
            mProxyManager = null;
        }
    }

    private View.OnClickListener categoryTypeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CheckBox checkBox = filterTypeCheckBoxArray[getCheckIdByViewId(view.getId())];
            checkBox.setChecked(!checkBox.isChecked());
        }

        private int getCheckIdByViewId(int viewId) {
            switch (viewId) {
                case R.id.red_wine_butt:
                    return 0;
                case R.id.white_wine_butt:
                    return 1;
                case R.id.pink_wine_butt:
                    return 2;
                default:
                    return -1;
            }
        }
    };

    private View.OnClickListener resetButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Animation anim = null;
            organizeView();
            resetFilterCheckBox();
//            anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_anim);
//            listView.startAnimation(anim);
            listView.collapseGroup(0);
        }

        private void organizeView() {
            View groupView = listView.getChildAt(0);
            groupView.findViewById(R.id.group_name).setVisibility(View.VISIBLE);
            footerView.findViewById(R.id.sort_group).setVisibility(View.VISIBLE);
            footerView.findViewById(R.id.filter_button_bar).setVisibility(View.GONE);
            RelativeLayout categoryTypeLayout = (RelativeLayout) groupView.findViewById(R.id.category_type_view);
            categoryTypeLayout.setVisibility(View.GONE);
            categoryTypeLayout.findViewById(R.id.red_wine_butt).setOnClickListener(null);
            categoryTypeLayout.findViewById(R.id.white_wine_butt).setOnClickListener(null);
            categoryTypeLayout.findViewById(R.id.pink_wine_butt).setOnClickListener(null);
        }

        private void resetFilterCheckBox() {
            for (CheckBox checkBox : filterTypeCheckBoxArray) {
                checkBox.setChecked(false);
            }
        }
    };

    private class ItemCursorAdapter extends SimpleCursorAdapter {

        private final static String FORMAT_TEXT_LABEL = "%s...";
        private final static int FORMAT_NAME_MAX_LENGTH = 41;
        private final static int FORMAT_LOC_NAME_MAX_LENGTH = 30;

        public ItemCursorAdapter(Cursor c) {
            super(CatalogByCategoryActivity.this, R.layout.catalog_item_layout, c, Item.getUITags(),
                    new int[] { R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_volume, R.id.item_price, R.id.product_category} );
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ImageView imageView = (ImageView)view.findViewById(R.id.item_image);
            TextView nameView = (TextView)view.findViewById(R.id.item_name);
            TextView itemLocName = (TextView)view.findViewById(R.id.item_loc_name);
            TextView itemVolume = (TextView)view.findViewById(R.id.item_volume);
            TextView itemPrice = (TextView)view.findViewById(R.id.item_price);
            TextView itemDrinkCategory = (TextView)view.findViewById(R.id.product_category);

            imageView.setImageResource(R.drawable.bottle_list_image_default);
            nameView.setText(organizeItemNameLabel(cursor.getString(1)));
            itemLocName.setText(organizeLocItemNameLabel(cursor.getString(2)));
            String volumeLabel = Utils.organizeProductLabel(Utils.removeZeros(cursor.getString(3)));
            itemVolume.setText(volumeLabel != null ? volumeLabel : "");
            String priceLabel = Utils.organizePriceLabel(cursor.getString(4));
            itemPrice.setText(priceLabel != null ? priceLabel : "");
            itemDrinkCategory.setText(DrinkCategory.getDrinkCategory(cursor.getString(6)).getDescription());
        }

        private String organizeItemNameLabel(String itemName) {
            return organizeTextLabel(itemName, FORMAT_NAME_MAX_LENGTH);
        }

        private String organizeLocItemNameLabel(String locItemName) {
            return organizeTextLabel(locItemName, FORMAT_LOC_NAME_MAX_LENGTH);
        }

        private String organizeTextLabel(String itemName, int maxLength) {
            String result = itemName;
            if (result.length() > maxLength) {
                result = String.format(FORMAT_TEXT_LABEL, result.substring(0, maxLength));
            }
            return result;
        }
    }

    private class SortTask extends AsyncTask<Integer, Void, Cursor> {

        private Dialog mDialog;
        private Context context;

        private SortTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(context, context.getString(R.string.dialog_search_title),
                    context.getString(R.string.dialog_sort_message), false, false);
        }

        @Override
        protected Cursor doInBackground(Integer... params) {
            return getProxyManager().getItemsByCategory(mCategoryID, params[0]);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            cItems = cursor;
            startManagingCursor(cItems);
            mListCategoriesAdapter = new ItemCursorAdapter(cItems);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }
}