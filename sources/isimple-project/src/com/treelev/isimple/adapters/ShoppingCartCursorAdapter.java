package com.treelev.isimple.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ShoppingCartActivity;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

public class ShoppingCartCursorAdapter extends SimpleCursorAdapter implements View.OnClickListener {

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String sizePrefix;
    private final static String ITEM_ID_FORMAT = "Артикул %s";
    private ProxyManager proxyManager;
    private TextView shoppingCartPriceTextView;
    private TextView shoppingCartFooterTextView;
    private String country;
    private Context context;

    public ShoppingCartCursorAdapter(Context context, Cursor cursor, TextView shoppingCartPriceTextView, TextView shoppingCartFooterTextView, String country) {
        super(context, R.layout.shopping_cart_item_layout, cursor, new String[]{
                DatabaseSqlHelper.ITEM_NAME, DatabaseSqlHelper.ITEM_LOCALIZED_NAME, BaseColumns._ID, DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_YEAR, DatabaseSqlHelper.ITEM_PRICE, DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT,
                DatabaseSqlHelper.ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME, DatabaseSqlHelper.ITEM_PRODUCT_TYPE, DatabaseSqlHelper.ITEM_COLOR
        }, new int[]{
                R.id.item_name, R.id.item_loc_name, R.id.product_id, R.id.product_volume, R.id.product_year, R.id.product_price,
                R.id.product_count, R.id.item_image, R.id.color_item
        });
        imageLoader = Utils.getImageLoader(context);
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.bottle_list_image_default)
                .showImageForEmptyUri(R.drawable.bottle_list_image_default)
                .showImageOnFail(R.drawable.bottle_list_image_default)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        sizePrefix = metrics.densityDpi == DisplayMetrics.DENSITY_HIGH ? "_hdpi" : metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH ? "_xhdpi" : "";
        proxyManager = new ProxyManager(context);
        this.shoppingCartPriceTextView = shoppingCartPriceTextView;
        this.shoppingCartFooterTextView = shoppingCartFooterTextView;
        this.country = country;
        this.context = context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        organizeTextLabels(cursor, view);
        organizeItemImageView(cursor, view);
        organizeItemColorView(cursor, view);
        TextView textView = (TextView) view.findViewById(R.id.product_count);
        int count = cursor.getInt(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT));
        textView.setText(String.valueOf(count));
        String itemId = getCursor().getString(getCursor().getColumnIndex(BaseColumns._ID));
        Button decreaseButton = (Button) view.findViewById(R.id.decrease_butt);
        /*if (count == 1) {
            decreaseButton.setBackgroundResource(R.drawable.shopping_cart_recycle_bin);
            decreaseButton.set("");
        } else {
            decreaseButton.setBackgroundColor(Color.TRANSPARENT);
            decreaseButton.setText("\u002D");
        }*/
        decreaseButton.setText("\u002D");
        decreaseButton.setOnClickListener(this);
        decreaseButton.setTag(itemId);
        Button increaseButton = (Button) view.findViewById(R.id.increase_butt);
        increaseButton.setOnClickListener(this);
        increaseButton.setTag(itemId);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.increase_butt:
                increaseItemCount(view);
                break;
            case R.id.decrease_butt:
                decreaseItemCount(view);
                break;
        }
        updatePriceLabel();
    }

    private void updatePriceLabel() {
        int shoppingCartPrice = proxyManager.getShoppingCartPrice();
        String priceStr = String.format(ShoppingCartActivity.PRICE_LABEL_FORMAT, shoppingCartPrice);
        shoppingCartPriceTextView.setText(priceStr);
        shoppingCartFooterTextView.setText(proxyManager.getDeliveryMessage(country, shoppingCartPrice));
    }

    private void increaseItemCount(View view) {
        proxyManager.increaseItemCount((String) view.getTag());
        getCursor().requery();
        ((TextView) ((LinearLayout) view.getParent()).findViewById(R.id.product_count))
                .setText(String.valueOf(getCursor().getInt(getCursor().getColumnIndex(DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT))));
    }

    private void decreaseItemCount(View view) {
        String itemId = (String) view.getTag();
        int count = proxyManager.getItemCount(itemId);
        getCursor().requery();
        if (count == 1) {
            proxyManager.deleteItem(itemId);
            notifyDataSetChanged();
            if (getCursor().getCount() == 0) {
                ((Activity) context).findViewById(R.id.content_layout).setVisibility(View.GONE);
                ((Activity) context).findViewById(R.id.empty_shopping_list_view).setVisibility(View.VISIBLE);
            }
        } else {
            proxyManager.decreaseItemCount((String) view.getTag());
            ((TextView) ((LinearLayout) view.getParent()).findViewById(R.id.product_count))
                    .setText(String.valueOf(getCursor().getInt(getCursor().getColumnIndex(DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT))));
        }
    }

    private void organizeItemColorView(Cursor cursor, View view) {
        ProductType productType = ProductType.getProductType(cursor.getInt(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRODUCT_TYPE)));
        String colorStr = productType.getColor();
        if (colorStr == null) {
            colorStr = ItemColor.getColor(cursor.getInt(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_COLOR))).getCode();
        }
        LinearLayout colorItem = (LinearLayout) view.findViewById(R.id.color_item);
        colorItem.setBackgroundColor(Color.parseColor(colorStr));
    }

    private void organizeItemImageView(Cursor cursor, View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        String imageName = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME));
        if (!TextUtils.isEmpty(imageName)) {
            imageLoader.displayImage(
                    String.format("http://s1.isimpleapp.ru/img/ver0/%1$s%2$s_listing.jpg", imageName.replace('\\', '/'), sizePrefix),
                    imageView, options);
        } else {
            imageView.setImageResource(R.drawable.bottle_list_image_default);
        }
    }

    private void organizeTextLabels(Cursor cursor, View view) {
        TextView textView = (TextView) view.findViewById(R.id.item_name);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_NAME)));
        textView = (TextView) view.findViewById(R.id.item_loc_name);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_NAME)));
        textView = (TextView) view.findViewById(R.id.product_id);
        textView.setText(String.format(ITEM_ID_FORMAT, cursor.getString(cursor.getColumnIndex(BaseColumns._ID))));
        textView = (TextView) view.findViewById(R.id.product_volume);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VOLUME)));
        organizeYearLabel(cursor, view);
        textView = (TextView) view.findViewById(R.id.product_price);
        textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRICE)));
        textView = (TextView) view.findViewById(R.id.multiply_symbol);
        textView.setText("\u00D7");
    }

    private void organizeYearLabel(Cursor cursor, View view) {
        TextView textView = (TextView) view.findViewById(R.id.product_year);
        String year = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_YEAR));
        if (!TextUtils.isEmpty(year) && !year.equals("0")) {
            textView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.shopping_cart_vol_year_separator).setVisibility(View.VISIBLE);
            textView.setText(year);
        } else {
            textView.setVisibility(View.GONE);
            view.findViewById(R.id.shopping_cart_vol_year_separator).setVisibility(View.GONE);
        }
    }

}
