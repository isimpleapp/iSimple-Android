package com.treelev.isimple.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Chain;

public class ChainAdapter extends SimpleCursorAdapter {

    public ChainAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
//        super(context, R.layout.item_chain_layout, c, Chain.getUITags(), new int[]{R.id.chain_item});
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView chainName = (TextView) view.findViewById(R.id.chain_item);
        chainName.setText(cursor.getString(1));
    }
}
