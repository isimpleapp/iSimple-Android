package com.treelev.isimple.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;

import java.util.List;

public class ShopsAdapter extends ArrayAdapter {

    private List<AbsDistanceShop> items;
    private LayoutInflater inflater;

    public ShopsAdapter(Context context, List<AbsDistanceShop> items) {
        super(context, 0, items);
        this.items = items;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final AbsDistanceShop i = items.get(position);
        if (i != null) {
            if(i.isSection()){
                v = inflater.inflate(R.layout.section_distance_shop, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.section_distance_shop);
                sectionView.setText(i.getTitle());
            }else{
                DistanceShop ei = (DistanceShop)i;
                v = inflater.inflate(R.layout.item_distance_shop, null);
                final TextView title = (TextView)v.findViewById(R.id.item_title_shop);
                final TextView address = (TextView)v.findViewById(R.id.item_adress_shop);

                if (title != null)
                    title.setText(ei.getTitle());
                if(address != null)
                    address.setText(ei.getShop().getLocationAddress());
            }
        }
        return v;
    }
}
