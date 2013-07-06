package com.treelev.isimple.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.treelev.isimple.R;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.utils.Utils;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.LinearLayout;

import java.util.ArrayList;

public class CatalogItemCursorAdapter extends SimpleCursorAdapter {

    private final static String FORMAT_TEXT_LABEL = "%s...";
    private final static int FORMAT_NAME_MAX_LENGTH = 41;
    private final static int FORMAT_LOC_NAME_MAX_LENGTH = 29;
    private boolean mGroup;
    private boolean mYearEnable;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String sizePrefix;
    private ArrayList<String> mDeleteItemsId;
    private boolean mDeleteMode;

    public CatalogItemCursorAdapter(Cursor c, Activity activity, boolean group, boolean yearEnable) {
        super(activity, R.layout.catalog_item_layout, c, Item.getUITags(),
                new int[]{ R.id.item_name, R.id.item_loc_name, R.id.item_volume, R.id.item_price, R.id.product_category});
        mGroup = group;
        mYearEnable = yearEnable;
        imageLoader = Utils.getImageLoader(activity.getApplicationContext());
        options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.bottle_list_image_default)
            .showImageForEmptyUri(R.drawable.bottle_list_image_default)
            .showImageOnFail(R.drawable.bottle_list_image_default)
            .cacheInMemory()
            .cacheOnDisc()
            .build();

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        sizePrefix =
                metrics.densityDpi == DisplayMetrics.DENSITY_HIGH ? "_hdpi" :
                metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH? "_xhdpi" : "";
    }

    public void setDeleteItemsId(ArrayList<String> list){
        mDeleteItemsId = list;
    }

    public void addDeleteItem(String itemID){
        if(mDeleteItemsId != null){
            mDeleteItemsId.add(itemID);
            notifyDataSetChanged();
        }
    }

    public void enableDeleteMode(){
        mDeleteMode = true;
        mDeleteItemsId = new ArrayList<String>(getCount());
    }

    public void disableDeleteMode(){
        mDeleteMode = false;
        mDeleteItemsId.clear();
        mDeleteItemsId = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(mDeleteMode){
            if(mDeleteItemsId.contains(getItemID(position))){
                convertView = inflater.inflate(mContext, R.layout.delete_item_layout);
                convertView.setOnTouchListener(null);
            } else {
                convertView = inflater.inflate(mContext, R.layout.catalog_item_layout);
                bindInfo(convertView, (Cursor)getItem(position));
            }
        } else {
            convertView = inflater.inflate(mContext, R.layout.catalog_item_layout);
            bindInfo(convertView, (Cursor)getItem(position));
        }
        return convertView;
    }

    private String getItemID(int position){
        return ((Cursor)getItem(position)).getString(0);
    }

    private void bindInfo(View view, Cursor cursor){
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        TextView nameView = (TextView) view.findViewById(R.id.item_name);
        TextView itemLocName = (TextView) view.findViewById(R.id.item_loc_name);
        TextView itemVolume = (TextView) view.findViewById(R.id.item_volume);
        TextView itemPrice = (TextView) view.findViewById(R.id.item_price);
        TextView itemProductType = (TextView) view.findViewById(R.id.product_category);
        LinearLayout colorItem = (LinearLayout) view.findViewById(R.id.color_item);

        int itemNameIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_NAME);
        int itemLocNameIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_NAME);
        int itemVolumeIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VOLUME);
        int itemPriceIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRICE);
        int itemQuantityIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_QUANTITY);
        int itemHiImageIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME);
        int itemLowImageIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_BOTTLE_LOW_RESOLUTION_IMAGE_FILENAME);
        int itemCountIndex = cursor.getColumnIndex("count");
        int itemDrinkCategoryIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_CATEGORY);
        int itemYearIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_YEAR);
        int itemProductTypeIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRODUCT_TYPE);
        int itemColorIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_COLOR);
        int itemFavouriteIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_IS_FAVOURITE);

        String imageName = cursor.getString(itemHiImageIndex);
        if (!TextUtils.isEmpty(imageName)) {
            imageLoader.displayImage(
                    String.format("http://s1.isimpleapp.ru/img/ver0/%1$s%2$s_listing.jpg", imageName.replace('\\', '/'), sizePrefix),
                    imageView, options);
        }
        else {
            imageView.setImageResource(R.drawable.bottle_list_image_default);
        }
        nameView.setText(organizeItemNameLabel(cursor.getString(itemNameIndex)));
        itemLocName.setText(organizeLocItemNameLabel(cursor.getString(itemLocNameIndex)));
        String volumeLabel = Utils.organizeProductLabel(Utils.removeZeros(cursor.getString(itemVolumeIndex)));
        String priceLabel = cursor.getString(itemPriceIndex);
        if(priceLabel != null ) {
            if( priceLabel.equalsIgnoreCase("0")) {
                priceLabel = "";
            } else {
                priceLabel = Utils.organizePriceLabel(priceLabel);
            }
        }
        Float quantity = cursor.getFloat(itemQuantityIndex);
        String formatVolume = "%.0f x %s";
        if( quantity != null && quantity > 1) {
            volumeLabel =  String.format(formatVolume, quantity, volumeLabel);
        }
        if( mGroup ) {
            String strDrinkId = Utils.removeZeros(cursor.getString(itemCountIndex));
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
        String strDrinkCategory = DrinkCategory.getDrinkCategory(cursor.getInt(itemDrinkCategoryIndex)).getDescription();
        if( mYearEnable ) {
            if(cursor.getString(itemYearIndex) != null) {
                String format = "%s %s";
                strDrinkCategory = String.format(format, strDrinkCategory,
                        cursor.getInt(itemYearIndex) != 0 ? cursor.getString(itemYearIndex) : "");
            }
        }
        itemProductType.setText(strDrinkCategory);

        ProductType productType = ProductType.getProductType(cursor.getInt(itemProductTypeIndex));
        String colorStr = productType.getColor();
        if(colorStr == null ) {
            colorStr = ItemColor.getColor(cursor.getInt(itemColorIndex)).getCode();
        }
        colorItem.setBackgroundColor(Color.parseColor(colorStr));

        ImageView imageViewFavourite = (ImageView) view.findViewById(R.id.item_image_favourite);
        if(cursor.getInt(itemFavouriteIndex) == 1){
            imageViewFavourite.setVisibility(View.VISIBLE);
        }   else {
            imageViewFavourite.setVisibility(View.GONE);
        }

        if(mDeleteItemsId != null){
            ImageView viewDiscard = (ImageView) view.findViewById(R.id.item_image_delete);
            if(mDeleteItemsId.contains(cursor.getString(0))){
                viewDiscard.setVisibility(View.VISIBLE);
            } else {
                viewDiscard.setVisibility(View.GONE);
            }
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
