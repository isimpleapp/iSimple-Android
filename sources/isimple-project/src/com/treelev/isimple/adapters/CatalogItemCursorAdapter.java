package com.treelev.isimple.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.utils.Utils;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.LinearLayout;

public class CatalogItemCursorAdapter extends SimpleCursorAdapter {

    private final static String FORMAT_TEXT_LABEL = "%s...";
    private final static int FORMAT_NAME_MAX_LENGTH = 41;
    private final static int FORMAT_LOC_NAME_MAX_LENGTH = 29;
    private boolean mGroup;
    private boolean mYearEnable;

    public CatalogItemCursorAdapter(Cursor c, Activity activity, boolean group, boolean yearEnable) {
        super(activity, R.layout.catalog_item_layout, c, Item.getUITags(),
                new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_volume, R.id.item_price, R.id.product_category});
        mGroup = group;
        mYearEnable = yearEnable;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        TextView nameView = (TextView) view.findViewById(R.id.item_name);
        TextView itemLocName = (TextView) view.findViewById(R.id.item_loc_name);
        TextView itemVolume = (TextView) view.findViewById(R.id.item_volume);
        TextView itemPrice = (TextView) view.findViewById(R.id.item_price);
        TextView itemProductType = (TextView) view.findViewById(R.id.product_category);
        LinearLayout colorItem = (LinearLayout) view.findViewById(R.id.color_item);

        imageView.setImageResource(R.drawable.bottle_list_image_default);
        nameView.setText(organizeItemNameLabel(cursor.getString(1)));
        itemLocName.setText(organizeLocItemNameLabel(cursor.getString(2)));
        String volumeLabel = Utils.organizeProductLabel(Utils.removeZeros(cursor.getString(3)));
        String priceLabel = cursor.getString(8);
        if(priceLabel != null ) {
            if( priceLabel.equalsIgnoreCase("0")) {
                priceLabel = "";
            } else {
                priceLabel = Utils.organizePriceLabel(priceLabel);
            }
        }
        Float quantity = cursor.getFloat(10);
        String formatVolume = "%.0f x %s";
        if( quantity != null && quantity > 1) {
            volumeLabel =  String.format(formatVolume, quantity, volumeLabel);
        }
        if( mGroup ) {
            String strDrinkId = Utils.removeZeros(cursor.getString(14));
            int drinkId = strDrinkId != null && strDrinkId.length() != 0 ? Integer.valueOf(strDrinkId) : 1;
            if( drinkId > 1 && volumeLabel != null ) {
                formatVolume = "%s товар%s";
                String end = "";
                if( (drinkId >=10 && drinkId <=20) || strDrinkId.charAt(strDrinkId.length()-1) == '0') {
                    end = "ов";

                } else if(strDrinkId.charAt(strDrinkId.length()-1) == '2' ||
                    strDrinkId.charAt(strDrinkId.length()-1) == '3' ||
                    strDrinkId.charAt(strDrinkId.length()-1) == '4' ) {
                    end = "а";
                }else if(strDrinkId.charAt(strDrinkId.length()-1) == '1') {
                    end = "";
                }  else {    //5 - 9 last number
                   end= "ов";
                }

                volumeLabel = String.format(formatVolume, drinkId, end);
                if(priceLabel != null) {
                    String formatPrice = "от %s";
                    priceLabel = String.format(formatPrice, priceLabel);
                }
            }
        }
        itemVolume.setText(volumeLabel != null ? volumeLabel : "");
        itemPrice.setText(priceLabel != null ? priceLabel : "");
//TODO:
        String strDrinkCategory = DrinkCategory.getDrinkCategory(cursor.getInt(6)).getDescription();
        if( mYearEnable ) {
            if(cursor.getString(9) != null) {
                String format = "%s %s";
                strDrinkCategory = String.format(format, strDrinkCategory, cursor.getInt(9) != 0 ? cursor.getString(9) : "");
            }
        }
        itemProductType.setText(strDrinkCategory);

        ProductType productType = ProductType.getProductType(cursor.getInt(5));
        String colorStr = productType.getColor();
        if(colorStr == null ) {
               colorStr = ItemColor.getColor(cursor.getInt(11)).getCode();
        }
        colorItem.setBackgroundColor(Color.parseColor(colorStr));

        if(cursor.getInt(13) == 1){
            ImageView imageViewFavourite = (ImageView) view.findViewById(R.id.item_image_favourite);
            imageViewFavourite.setVisibility(View.VISIBLE);
        }
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
