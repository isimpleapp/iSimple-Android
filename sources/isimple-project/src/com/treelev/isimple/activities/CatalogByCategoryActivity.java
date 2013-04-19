package com.treelev.isimple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;

import java.util.HashMap;

public class CatalogByCategoryActivity extends ListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_category_layout);
        int categoryId = getIntent().getIntExtra(CatalogListActivity.CATEGORY_NAME_EXTRA_ID, -1);
        ProxyManager proxyManager = new ProxyManager(this);
        ((RadioGroup) findViewById(R.id.sort_group)).check(R.id.alphabet_sort);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, proxyManager.getItemsByCategory(categoryId), R.layout.catalog_item_layout,
                Item.getUITags(), new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type,
                R.id.item_volume, R.id.item_price});
        getListView().setAdapter(simpleAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
    }
}