package com.treelev.isimple.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ChainAdapter;
import com.treelev.isimple.domain.db.Chain;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;

public class ShopChainFragment extends ListFragment {

    private ProxyManager mProxyManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shops_chain_fragment_layout, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        String item = (String) getListAdapter().getItem(position);
//        FragmentDetail fragment = (FragmentDetail)getFragmentManager().findFragmentById(R.id.fragment_detail);
//        if (fragment != null && fragment.isInLayout()) {
//            fragment.goToLink(item);
//        } else {
//            Intent intent = new Intent(getActivity().getApplicationContext(), FragmentDetailActivity.class);
//            intent.putExtra("selectedValue", item);
//            startActivity(intent);
//        }
//    }

}
