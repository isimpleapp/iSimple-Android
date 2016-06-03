package com.treelev.isimple.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.treelev.isimple.R;
import com.treelev.isimple.activities.ProductInfoActivity;
import com.treelev.isimple.activities.ShopInfoActivity;
import com.treelev.isimple.adapters.ShopsAdapter;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;
import com.treelev.isimple.domain.ui.DistanceShopHeader;
import com.treelev.isimple.utils.managers.LocationTrackingManager;
import com.treelev.isimple.utils.managers.ProxyManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopListFragment extends ListFragment {

    private ProxyManager mProxyManager;
    public final static String PRODUCT_ID_EXTRA = "product_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shop_list_fragment_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String itemId = getArguments().getString(ProductInfoActivity.ITEM_ID_TAG);
        new SelectDataShopDistance(getActivity(), itemId).execute();
    }

    @Override
    public void onStart() {
        super.onStart();

        Analytics.screen_StoreList(getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        DistanceShop item = (DistanceShop) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(getActivity(), ShopInfoActivity.class);
        startIntent.putExtra(ShopInfoActivity.SHOP, item.getShop());
        startActivity(startIntent);
        getActivity().overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = ProxyManager.getInstanse();
        }
        return mProxyManager;
    }

    private class SelectDataShopDistance extends AsyncTask<Void, Void, List<AbsDistanceShop>> {

        private Dialog mDialog;
        private Context mContext;
        private String mItemId;
        private List<AbsDistanceShop> shopListForMap;
        private Location location;

        private SelectDataShopDistance(Context context, String itemId) {
            mContext = context;
            mItemId = itemId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), true, false);
            location = LocationTrackingManager.getInstante(getActivity()).getCurrentLocation(getActivity());
        }

        @Override
        protected List<AbsDistanceShop> doInBackground(Void... voids) {
            List<AbsDistanceShop> items = null;
            if (mItemId == null) {
                if (location != null) {
                    items = getProxyManager().getNearestShops(location);
                }
            } else {
                items = getProxyManager().getNearestShopsByItemId(mItemId, location);
            }
            if (items != null) {
                shopListForMap = new ArrayList<AbsDistanceShop>();
                shopListForMap.addAll(items);
                organizeItemsByDistance(items);
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<AbsDistanceShop> items) {
            if (items != null) {
//                ((ShopsFragmentActivity) getActivity()).setShopMapFragmentArguments(shopListForMap);
                ShopsAdapter adapter = new ShopsAdapter(getActivity(), items);
                getListView().setAdapter(adapter);
            }
            mDialog.dismiss();
            if (items != null) {
                if (items.size() == 0) {
                    Toast.makeText(mContext, mContext.getString(R.string.not_exsist_product), Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void organizeItemsByDistance(List<AbsDistanceShop> items) {
            DistanceShopHeader header1 = new DistanceShopHeader(0.0f, "НЕ ДАЛЕЕ 1 КМ");
            items.add(header1);
            DistanceShopHeader header2 = new DistanceShopHeader(1000.0f, "ДАЛЕЕ 1 КМ");
            items.add(header2);
            DistanceShopHeader header3 = new DistanceShopHeader(5000.0f, "ДАЛЕЕ 5 КМ");
            items.add(header3);
            Collections.sort(items);
            //remove header empty distance category
            int index = items.indexOf(header1);
            if (items.get(index + 1) instanceof DistanceShopHeader) {
                items.remove(index);
            }
            index = items.indexOf(header2);
            if (items.get(index + 1) instanceof DistanceShopHeader) {
                items.remove(index);
            }
            index = items.indexOf(header3);
            if (index == items.size() - 1) {
                items.remove(index);
            }

        }
    }

}
