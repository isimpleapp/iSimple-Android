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
import android.support.v4.widget.SimpleCursorAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ShoppingCartActivity;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.enumerable.item.DrinkCategory;
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
    private Context context;
    private final static String PRICE_LABEL_FORMAT = "%s р.";
    private final static String WATER_LABEL_FORMAT = "%s \u00D7 %s";
    private final static String LONG_NAME_FORMAT = "%s...";
    private final static int NAME_MAX_SYMBOLS = 38;
    private final static int LOC_NAME_MAX_SYMBOLS = 31;

    public ShoppingCartCursorAdapter(Context context, Cursor cursor, TextView shoppingCartPriceTextView, TextView shoppingCartFooterTextView) {
        super(context, R.layout.shopping_cart_item_layout, cursor, new String[]{
                DatabaseSqlHelper.ITEM_NAME, DatabaseSqlHelper.ITEM_LOCALIZED_NAME, BaseColumns._ID, DatabaseSqlHelper.ITEM_VOLUME,
                DatabaseSqlHelper.ITEM_YEAR, DatabaseSqlHelper.ITEM_PRICE, DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT,
                DatabaseSqlHelper.ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME, DatabaseSqlHelper.ITEM_PRODUCT_TYPE,
                DatabaseSqlHelper.ITEM_DRINK_CATEGORY, DatabaseSqlHelper.ITEM_QUANTITY, DatabaseSqlHelper.ITEM_COLOR
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
        this.context = context;
    }

    public void refresh() {
        ((Activity)context).stopManagingCursor(getCursor());
        getCursor().close();
        Cursor cursor = proxyManager.getShoppingCartItems();
        ((Activity)context).startManagingCursor(cursor);
        swapCursor(cursor);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        organizeTextLabels(cursor, view);
        organizeItemImageView(cursor, view);
        organizeItemColorView(cursor, view);

        Button decreaseButton = (Button) view.findViewById(R.id.decrease_butt);
        decreaseButton.setText("\u002D");
        Button increaseButton = (Button) view.findViewById(R.id.increase_butt);
        TextView textView = (TextView) view.findViewById(R.id.product_count);
        String itemId = getCursor().getString(getCursor().getColumnIndex(BaseColumns._ID));

        ShopCardItemHolder itemHolder = new ShopCardItemHolder(itemId, textView);
        int count = proxyManager.getItemCount(itemId);
        textView.setText(String.valueOf(count));

        decreaseButton.setTag(itemHolder);
        increaseButton.setTag(itemHolder);
        decreaseButton.setOnClickListener(this);
        increaseButton.setOnClickListener(this);
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
        ((ShoppingCartActivity) context).organizeCreateOrderButton(shoppingCartPrice);
        shoppingCartFooterTextView.setText(proxyManager.getDeliveryMessage(
                ((org.holoeverywhere.app.Activity) context).getPreferences(Context.MODE_PRIVATE).getString(ShoppingCartActivity.COUNTRY_LABEL, ""), shoppingCartPrice));
    }

    private void increaseItemCount(final View view) {
        ShopCardItemHolder itemHolder = (ShopCardItemHolder)view.getTag();
        int newCount = proxyManager.increaseShopCardItemCount(itemHolder.itemID);
        itemHolder.textView.setText(newCount + "");
    }

    private void decreaseItemCount(final View view) {
        ShopCardItemHolder itemHolder = (ShopCardItemHolder)view.getTag();
        int count = proxyManager.getItemCount(itemHolder.itemID);
        if (count <= 1) {
            proxyManager.removeShopCardItem(itemHolder.itemID);
            refresh();
            notifyDataSetChanged();
            if (getCursor().getCount() == 0) {
                ((Activity) context).findViewById(R.id.content_layout).setVisibility(View.GONE);
                ((Activity) context).findViewById(R.id.empty_shopping_list_view).setVisibility(View.VISIBLE);
                ((ISimpleApp)(((Activity) context).getApplication())).setDisactiveCartState();
            }
        } else {
            int newCount = proxyManager.decreaseShopCardItemCount(itemHolder.itemID);
            itemHolder.textView.setText(newCount + "");
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
        String name = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_NAME));
        textView.setText(name.length() < NAME_MAX_SYMBOLS ? name : String.format(LONG_NAME_FORMAT, name.substring(0, NAME_MAX_SYMBOLS)));
        textView = (TextView) view.findViewById(R.id.item_loc_name);
        String locName = cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_NAME));
        textView.setText(locName.length() < LOC_NAME_MAX_SYMBOLS ? locName : locName.substring(0, LOC_NAME_MAX_SYMBOLS));
        textView = (TextView) view.findViewById(R.id.product_id);
        textView.setText(String.format(ITEM_ID_FORMAT, cursor.getString(cursor.getColumnIndex(BaseColumns._ID))));
        int drinkCategory = cursor.getInt(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_CATEGORY));
        textView = (TextView) view.findViewById(R.id.product_volume);
        if (drinkCategory != DrinkCategory.WATER.ordinal()) {
            textView.setText(cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VOLUME)));
            organizeYearLabel(cursor, view);
        } else {
            textView.setText(String.format(WATER_LABEL_FORMAT, cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VOLUME))));
            view.findViewById(R.id.product_year).setVisibility(View.GONE);
            view.findViewById(R.id.shopping_cart_vol_year_separator).setVisibility(View.GONE);
        }
        textView = (TextView) view.findViewById(R.id.product_price);
        textView.setText(String.format(PRICE_LABEL_FORMAT, cursor.getString(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRICE))));
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

    public class ShopCardItemHolder {

        public ShopCardItemHolder(String itemID, TextView textView) {
            this.itemID = itemID;
            this.textView = textView;
        }

        String itemID;
        TextView textView;
    }

}
