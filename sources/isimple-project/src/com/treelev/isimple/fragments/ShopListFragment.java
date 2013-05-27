package com.treelev.isimple.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ShopActivity;
import com.treelev.isimple.adapters.ShopsAdapter;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;
import com.treelev.isimple.domain.ui.DistanceShopHeader;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ListView;

import java.util.Collections;
import java.util.List;

public class ShopListFragment extends ListFragment {

    private ProxyManager mProxyManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shop_list_fragment_layout, null);
    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = new ProxyManager(getActivity());
        }
        return mProxyManager;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new SelectDataShopDistance(getActivity()).execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        DistanceShop item = (DistanceShop) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(getActivity(), ShopActivity.class);
        startIntent.putExtra(ShopActivity.SHOP, item.getShop());
        startActivity(startIntent);
        getActivity().overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private class SelectDataShopDistance extends AsyncTask<Void, Void, List<AbsDistanceShop>> {


        private Dialog mDialog;
        private Context mContext;

        private SelectDataShopDistance(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
        }
        @Override
        protected List<AbsDistanceShop> doInBackground(Void... voids) {
//get location
//TODO replace test to real location
            Location location = new Location("test_location");
            location.setLongitude(37.6167f);
            location.setLatitude(55.770f);
//            location.setLongitude(27.0f);
//            location.setLatitude(53.0f);
            List<AbsDistanceShop> items = getProxyManager().getNearestShops(location);
//add header
            addHeader(items);
            return items;  //To change body of implemented methods use File | Settings | File Templates.
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
        protected void onPostExecute(List<AbsDistanceShop> items) {
            ShopsAdapter adapter = new ShopsAdapter(getActivity(), items);
            getListView().setAdapter(adapter);
            mDialog.dismiss();
        }
    }

}
