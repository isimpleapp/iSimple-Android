package com.treelev.isimple.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;
import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.TextView;

import java.util.List;

public class ShopsAdapter extends ArrayAdapter {

    private Context context;
    private List<AbsDistanceShop> items;
    private LayoutInflater inflater;

    public ShopsAdapter(Context context, List<AbsDistanceShop> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        final AbsDistanceShop i = items.get(position);
        if (i != null) {
            if(i.isSection()){
                v = inflater.inflate(R.layout.header_distance_shop, null);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

                final TextView sectionView = (TextView) v.findViewById(R.id.header_distance_shop);
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
