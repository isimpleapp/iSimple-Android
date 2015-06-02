package com.treelev.isimple.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ChainStoresActivity;
import com.treelev.isimple.activities.ProductInfoActivity;
import com.treelev.isimple.adapters.ChainAdapter;
import com.treelev.isimple.domain.db.Chain;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.app.ProgressDialog;

public class ShopChainFragment extends ListFragment {

    private ProxyManager mProxyManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment shopListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shops_chain_fragment_layout, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopListFragment = new ShopListFragment();
    }

    private ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = ProxyManager.getInstanse();
        }
        return mProxyManager;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        String itemId = getArguments().getString(ProductInfoActivity.ITEM_ID_TAG);
        new SelectDataChain(getActivity(), itemId).execute();
    }

    @Override
    public void onListItemClick(org.holoeverywhere.widget.ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor iChain = (Cursor) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(getActivity(), ChainStoresActivity.class);
        startIntent.putExtra(ChainStoresActivity.ITEM_CHAIN_ID, iChain.getString(0));
        startActivity(startIntent);
        getActivity().overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
    }

    private class SelectDataChain extends AsyncTask<Void, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;
        private String mItemId;

        private SelectDataChain(Context context, String itemId) {
            mContext = context;
            mItemId = itemId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), true, false);
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            if(mItemId == null){
                return getProxyManager().getChains();
            } else {
                return getProxyManager().getChainsByItemId(mItemId);
            }

        }

        @Override
        protected void onPostExecute(Cursor items) {
            ChainAdapter adapter = new ChainAdapter(getActivity(), R.layout.item_chain_layout, items,
                    new String[] {Chain.UI_TAG_NAME_CHAIN}, new int[]{R.id.chain_item});
            getListView().setAdapter(adapter);
            mDialog.dismiss();
        }

    }
}
