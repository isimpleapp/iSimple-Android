package com.treelev.isimple.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ShopAdapter;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
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

        Intent startIntent;

//        startActivity(startIntent);
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
            Location location = new Location("test_location");
            location.setLongitude(37.6167f);
            location.setLatitude(55.770f);

            List<AbsDistanceShop> items = getProxyManager().getNearestShops(location);
//add header
            DistanceShopHeader header = new DistanceShopHeader(0.0f, "НЕ ДАЛЕЕ 1 КМ");
            items.add(header);
            header = new DistanceShopHeader(1000.0f, "ДАЛЕЕ 1 КМ");
            items.add(header);
            Collections.sort(items);
            return items;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(List<AbsDistanceShop> items) {
            ShopAdapter adapter = new ShopAdapter(getActivity(), items);
            getListView().setAdapter(adapter);
            mDialog.dismiss();
        }
    }
}
