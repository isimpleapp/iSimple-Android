package com.treelev.isimple.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import com.treelev.isimple.R;
import com.treelev.isimple.data.DatabaseSqlHelper;
import org.holoeverywhere.widget.TextView;

public class ShoppingCartCursorAdapter extends SimpleCursorAdapter {

    public ShoppingCartCursorAdapter(Context context, Cursor cursor) {
        super(context, R.layout.shopping_cart_item_layout, cursor, new String[] {
                DatabaseSqlHelper.ITEM_NAME, DatabaseSqlHelper.ITEM_LOCALIZED_NAME, BaseColumns._ID, DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_YEAR, DatabaseSqlHelper.ITEM_PRICE, DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT
        }, new int[] {
                R.id.item_name, R.id.item_loc_name, R.id.product_id, R.id.product_volume, R.id.product_year, R.id.product_price,
                R.id.product_count
        });
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.item_name);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_NAME)));
        textView = (TextView) view.findViewById(R.id.item_loc_name);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_NAME)));
        textView = (TextView) view.findViewById(R.id.product_id);
        textView.setText("Артикул " + cursor.getString(cursor.getColumnIndex(BaseColumns._ID)));
        textView = (TextView) view.findViewById(R.id.product_volume);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VOLUME)));
        textView = (TextView) view.findViewById(R.id.product_year);
        String year = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_YEAR));
        if (!TextUtils.isEmpty(year) && !year.equals("0")) {
            textView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.shopping_cart_vol_year_separator).setVisibility(View.VISIBLE);
            textView.setText(year);
        } else {
            textView.setVisibility(View.GONE);
            view.findViewById(R.id.shopping_cart_vol_year_separator).setVisibility(View.GONE);
        }
        textView = (TextView) view.findViewById(R.id.product_price);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRICE)));
        /*textView = (TextView) view.findViewById(R.id.multiply_symbol);
        textView.setText();*/
        textView = (TextView) view.findViewById(R.id.product_count);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT)));
    }
}
