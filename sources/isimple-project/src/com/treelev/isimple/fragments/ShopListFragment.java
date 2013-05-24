package com.treelev.isimple.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

public class ShopListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shop_list_fragment_layout, null);
    }
}
