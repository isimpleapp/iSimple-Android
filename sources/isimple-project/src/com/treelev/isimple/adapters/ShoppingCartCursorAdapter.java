package com.treelev.isimple.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import com.treelev.isimple.R;
import com.treelev.isimple.data.DatabaseSqlHelper;
import org.holoeverywhere.widget.TextView;

public class ShoppingCartCursorAdapter extends SimpleCursorAdapter {

    public ShoppingCartCursorAdapter(Context context, Cursor cursor) {
        super(context, R.layout.shopping_cart_item_layout, cursor, new String[] {
                DatabaseSqlHelper.ITEM_NAME, DatabaseSqlHelper.ITEM_LOCALIZED_NAME, BaseColumns._ID, DatabaseSqlHelper.ITEM_VOLUME, DatabaseSqlHelper.ITEM_YEAR
        }, new int[] {
                R.id.item_name, R.id.item_loc_name, R.id.product_label
        });
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.item_name);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_NAME)));
        textView = (TextView) view.findViewById(R.id.item_loc_name);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_NAME)));
        textView = (TextView) view.findViewById(R.id.product_label);
        textView.setText("Артикул " + cursor.getString(cursor.getColumnIndex(BaseColumns._ID)) + "   |   " +
                cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VOLUME)) + "   |   " +
                cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_YEAR)));

    }
}
