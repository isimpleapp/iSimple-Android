package com.treelev.isimple.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ShopsAdapter;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;
import com.treelev.isimple.domain.ui.DistanceShopHeader;
import com.treelev.isimple.utils.managers.LocationTrackingManager;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.widget.ListView;

import java.util.Collections;
import java.util.List;


public class ChainStoresActivity extends BaseListActivity {

    public final static String ITEM_CHAIN_ID = "id";

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.shops_chain_fragment_layout);
        setCurrentCategory(1);
        createNavigationMenuBar();
        String itemId = getIntent().getStringExtra(ITEM_CHAIN_ID);
        ProxyManager proxyManager = new ProxyManager(this);
        Location location = LocationTrackingManager.getInstante().getCurrentLocation(this);
        List<AbsDistanceShop> iShop = proxyManager.getShopByChain(location, itemId);
        ShopsAdapter adapter = new ShopsAdapter(this, iShop);
        addHeader(iShop);
        getListView().setAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        DistanceShop item = (DistanceShop) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ShopInfoActivity.class);
        startIntent.putExtra(ShopInfoActivity.SHOP, item.getShop());
        startActivity(startIntent);
        this.overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private void addHeader(List<AbsDistanceShop> items) {
        DistanceShopHeader header1 = new DistanceShopHeader(0.0f, "НЕ ДАЛЕЕ 1 КМ");
        items.add(header1);
        DistanceShopHeader header2 = new DistanceShopHeader(1000.0f, "ДАЛЕЕ 1 КМ");
        items.add(header2);
        DistanceShopHeader header3 = new DistanceShopHeader(5000.0f, "ДАЛЕЕ 5 КМ");
        items.add(header3);
        Collections.sort(items);
//remove header empty distance category
        int index = items.indexOf(header1);
        if( items.get(index + 1) instanceof DistanceShopHeader ) {
            items.remove(index);
        }
        index = items.indexOf(header2);
        if( items.get(index + 1) instanceof DistanceShopHeader ) {
            items.remove(index);
        }
        index = items.indexOf(header3);
        if( index == items.size() - 1 ) {
            items.remove(index);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    @Override
    protected  void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
    }
}
