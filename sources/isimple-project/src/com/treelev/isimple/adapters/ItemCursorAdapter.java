package com.treelev.isimple.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.utils.Utils;
import org.holoeverywhere.app.Activity;

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 20.05.13
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public class ItemCursorAdapter extends SimpleCursorAdapter {

    private final static String FORMAT_TEXT_LABEL = "%s...";
    private final static int FORMAT_NAME_MAX_LENGTH = 41;
    private final static int FORMAT_LOC_NAME_MAX_LENGTH = 30;
    private boolean mGroup;

    public ItemCursorAdapter(Cursor c, Activity activity, boolean group) {
        super(activity, R.layout.catalog_item_layout, c, Item.getUITags(),
                new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_volume, R.id.item_price, R.id.product_category});
        mGroup = group;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        TextView nameView = (TextView) view.findViewById(R.id.item_name);
        TextView itemLocName = (TextView) view.findViewById(R.id.item_loc_name);
        TextView itemVolume = (TextView) view.findViewById(R.id.item_volume);
        TextView itemPrice = (TextView) view.findViewById(R.id.item_price);
        TextView itemDrinkCategory = (TextView) view.findViewById(R.id.product_category);

        imageView.setImageResource(R.drawable.bottle_list_image_default);
        nameView.setText(organizeItemNameLabel(cursor.getString(1)));
        itemLocName.setText(organizeLocItemNameLabel(cursor.getString(2)));
        String volumeLabel = Utils.organizeProductLabel(Utils.removeZeros(cursor.getString(3)));
        String strDrinkId = Utils.removeZeros(cursor.getString(8));
        int drinkId = strDrinkId != null ? Integer.valueOf(strDrinkId) : 1;

        if( drinkId > 1 && volumeLabel != null && mGroup)
        {
            String formatPrice = "%s бутыл%s";
            String end = "кa";
            if( (drinkId >=5 && drinkId <=20) || strDrinkId.charAt(strDrinkId.length()-1) == '0') {
                end = "ок";
            }
            else if(strDrinkId.charAt(strDrinkId.length()-1) == '2' ||
                    strDrinkId.charAt(strDrinkId.length()-1) == '3' ||
                    strDrinkId.charAt(strDrinkId.length()-1) == '4' ) {
                end = "ки";
            }

            volumeLabel = String.format(formatPrice, drinkId, end);
        }
        itemVolume.setText(volumeLabel != null ? volumeLabel : "");
        String priceLabel = Utils.organizePriceLabel(cursor.getString(7));
        if( drinkId > 1 && priceLabel != null && mGroup)
        {
            String formatPrice = "от %s";
            priceLabel = String.format(formatPrice, priceLabel);
        }
        itemPrice.setText(priceLabel != null ? priceLabel : "");

        itemDrinkCategory.setText(DrinkCategory.getDrinkCategory(cursor.getString(5)).getDescription());
    }

    private String organizeItemNameLabel(String itemName) {
        return organizeTextLabel(itemName, FORMAT_NAME_MAX_LENGTH);
    }

    private String organizeLocItemNameLabel(String locItemName) {
        return organizeTextLabel(locItemName, FORMAT_LOC_NAME_MAX_LENGTH);
    }

    private String organizeTextLabel(String itemName, int maxLength) {
        String result = itemName;
        if (result.length() > maxLength) {
            result = String.format(FORMAT_TEXT_LABEL, result.substring(0, maxLength));
        }
        return result;
    }
}
