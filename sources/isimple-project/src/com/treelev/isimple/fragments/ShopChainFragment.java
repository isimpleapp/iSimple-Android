package com.treelev.isimple.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ChainStoresActivity;
import com.treelev.isimple.adapters.ChainAdapter;
import com.treelev.isimple.domain.db.Chain;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ListFragment;

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
            mProxyManager = new ProxyManager(getActivity());
        }
        return mProxyManager;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Cursor cursor = getProxyManager().getChains();
        ChainAdapter adapter = new ChainAdapter(getActivity(), R.layout.item_chain_layout, cursor,
                new String[] {Chain.UI_TAG_NAME_CHAIN}, new int[]{R.id.chain_item});
        getListView().setAdapter(adapter);
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
}
