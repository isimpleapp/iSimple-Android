package com.treelev.isimple.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.treelev.isimple.R;
import com.treelev.isimple.activities.ChainStoresActivity;
import com.treelev.isimple.activities.ProductInfoActivity;
import com.treelev.isimple.adapters.ChainAdapter;
import com.treelev.isimple.domain.db.Chain;
import com.treelev.isimple.utils.managers.ProxyManager;


public class ShopChainFragment extends android.support.v4.app.Fragment {

    private ProxyManager mProxyManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment shopListFragment;
    private ListView listView;
    private ChainAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shops_chain_fragment_layout, null);
        listView = (ListView) view.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor iChain = (Cursor) adapter.getItem(position);
                Intent startIntent = new Intent(getActivity(), ChainStoresActivity.class);
                startIntent.putExtra(ChainStoresActivity.ITEM_CHAIN_ID, iChain.getString(0));
                startActivity(startIntent);
                getActivity().overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        shopListFragment = new ShopListFragment();
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
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title), mContext.getString(R.string.dialog_select_data_message), true, false);
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            if (mItemId == null) {
                return getProxyManager().getChains();
            } else {
                return getProxyManager().getChainsByItemId(mItemId);
            }
        }

        @Override
        protected void onPostExecute(Cursor items) {
            adapter = new ChainAdapter(getActivity(), R.layout.item_chain_layout, items, new String[]{Chain.UI_TAG_NAME_CHAIN}, new int[]{R.id.chain_item});
            listView.setAdapter(adapter);
            mDialog.dismiss();
        }
    }
}
