package com.treelev.isimple.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResult extends ListActivity implements RadioGroup.OnCheckedChangeListener{

    public static Integer categoryID;

    private List<Item> mItems;
    private List<Map<String, ?>> mUiItemList;
    private SimpleAdapter mListCategoriesAdapter;
    private ProxyManager mProxyManager;

    @Override
    public void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        RadioGroup rg = (RadioGroup) findViewById(R.id.sort_group);
        rg.setOnCheckedChangeListener(this);
//        Intent intent = getIntent();
//        mItems = (List<Item>) intent.getSerializableExtra(LIST_RESULT);
        mProxyManager = new ProxyManager(this);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Search search = new Search(this, categoryID);
            search.execute(query);
        }

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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
    }

    private void updateList(int sortBy) {
        mUiItemList.clear();
        mUiItemList.addAll(mProxyManager.convertItemsToUI(mItems, sortBy));
        mListCategoriesAdapter.notifyDataSetChanged();
    }

    class Search extends AsyncTask<String, Void, List<Item>> {

        private Dialog mDialog;
        private Context mContext;
        private ProxyManager mProxyManager;
        private Integer mCategoryId;

        public Search(Context context, Integer categoryId) {
            mContext = context;
            mCategoryId = categoryId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_search_title),
                    mContext.getString(R.string.dialog_search_message), false, false);
        }

        @Override
        protected List<Item> doInBackground(String... strings) {
            mProxyManager = new ProxyManager(mContext);
            return mProxyManager.getSearchItemsByCategory(mCategoryId, strings[0]);
        }

        @Override
        protected void onPostExecute(List<Item> result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            mItems = result;
            mProxyManager = new ProxyManager(mContext);
            mUiItemList = mProxyManager.convertItemsToUI(mItems, ProxyManager.SORT_NAME_AZ);
            mListCategoriesAdapter = new SimpleAdapter(mContext,
                    mUiItemList,
                    R.layout.catalog_item_layout,
                    Item.getUITags(),
                    new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type, R.id.item_volume, R.id.item_price});
            ListView listView = getListView();
            getListView().setAdapter(mListCategoriesAdapter);
        }
    }

}
