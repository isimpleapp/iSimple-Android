package com.treelev.isimple.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.treelev.isimple.R;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CatalogItemCursorAdapter extends SimpleCursorAdapter {

    private final static String FORMAT_TEXT_LABEL = "%s...";
    private final static int FORMAT_NAME_MAX_LENGTH = 41;
    private final static int FORMAT_LOC_NAME_MAX_LENGTH = 29;
    private boolean mGroup;
    private boolean mYearEnable;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String sizePrefix;
    private List<String> mDeleteItemsId;
    private boolean mDeleteMode;
    private OnCancelDismis mCancelDismiss;

    public interface OnCancelDismis {
        void cancelDeleteItem(int position);
    }

    public void setOnCancelDismiss(OnCancelDismis cancelDismiss) {
        mCancelDismiss = cancelDismiss;
    }

    public CatalogItemCursorAdapter(Cursor c, Activity activity, boolean group, boolean yearEnable) {
        super(activity, R.layout.catalog_item_layout, c, Item.getUITags(),
                new int[]{R.id.item_name, R.id.item_loc_name, R.id.item_volume, R.id.item_price, R.id.product_category});
        mGroup = group;
        mYearEnable = yearEnable;
        imageLoader = Utils.getImageLoader(activity.getApplicationContext());
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.bottle_list_image_default)
                .showImageForEmptyUri(R.drawable.bottle_list_image_default)
                .showImageOnFail(R.drawable.bottle_list_image_default)
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        sizePrefix =
                metrics.densityDpi == DisplayMetrics.DENSITY_HIGH ? "_hdpi_listing.jpg" :
                        metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH ? "_xhdpi_listing.jpg" :
                                metrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH ? "_product.jpg" : "_listing.jpg";
    }

    public List<String> getDeleteItemsId() {
        return new ArrayList<String>(mDeleteItemsId);
    }


    public boolean removeDeleteItemsID(String itemID) {
        return mDeleteItemsId.remove(itemID);
    }

    public void addDeleteItemsID(List<String> list) {
        mDeleteItemsId = list;
    }

    public void addDeleteItem(String itemID) {
        if (mDeleteItemsId != null) {
            mDeleteItemsId.add(itemID);
            notifyDataSetChanged();
        }
    }

    public boolean containsDeleteItemID(String itemID) {
        return mDeleteItemsId.contains(itemID);
    }

    public void enableDeleteMode() {
        mDeleteMode = true;
        mDeleteItemsId = new ArrayList<String>(getCount());
    }

    public void disableDeleteMode() {
        mDeleteMode = false;
        mDeleteItemsId.clear();
        mDeleteItemsId = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mDeleteMode) {
            if (mDeleteItemsId.contains(getItemID(position)) && android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
//                convertView = View.inflate(mContext, R.layout.catalog_category_layout, null);
                convertView = View.inflate(mContext, R.layout.catalog_item_layout, null);

                initButtonCancel(convertView, position);
            } else {
//                convertView = View.inflate(mContext, R.layout.catalog_category_layout, null);
                convertView = View.inflate(mContext, R.layout.catalog_item_layout, null);

                bindInfo(convertView, (Cursor) getItem(position));
            }
        } else {
//            convertView = View.inflate(mContext, R.layout.catalog_category_layout, null);
            convertView = View.inflate(mContext, R.layout.catalog_item_layout, null);

            bindInfo(convertView, (Cursor) getItem(position));
        }
        return convertView;
    }

    private void initButtonCancel(View view, int position) {
        Button btn = (Button) view.findViewById(R.id.cancel_delete);
        final int positionItem = position;
        final String itemID = getItemID(positionItem);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteItemsId.remove(itemID);
                if (mCancelDismiss != null) {
                    mCancelDismiss.cancelDeleteItem(positionItem);
                }
                notifyDataSetChanged();
            }
        });
    }

    private String getItemID(int position) {
        return ((Cursor) getItem(position)).getString(0);
    }

    private void bindInfo(View view, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        TextView nameView = (TextView) view.findViewById(R.id.item_name);
        TextView itemLocName = (TextView) view.findViewById(R.id.item_loc_name);
        TextView itemVolume = (TextView) view.findViewById(R.id.item_volume);
        TextView itemPrice = (TextView) view.findViewById(R.id.item_price);
        ImageView discountTriangleImageView = (ImageView) view.findViewById(R.id.discount_triangle);
        TextView itemOldPrice = (TextView) view.findViewById(R.id.item_old_price);
        itemOldPrice.setPaintFlags(itemOldPrice.getPaintFlags()
                | Paint.STRIKE_THRU_TEXT_FLAG);
        TextView itemProductType = (TextView) view.findViewById(R.id.product_category);
        LinearLayout colorItem = (LinearLayout) view.findViewById(R.id.color_item);

        int itemNameIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_NAME);
        int itemLocNameIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_NAME);
        int itemVolumeIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VOLUME);
        int itemPriceIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRICE);
        int itemDiscountIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_DISCOUNT);
        int itemOriginPriceIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_ORIGIN_PRICE);
        int itemQuantityIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_QUANTITY);
        int itemHiImageIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME);
        int itemCountIndex = cursor.getColumnIndex("count");
        int itemDrinkCategoryIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_CATEGORY);
        int itemYearIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_YEAR);
        int itemProductTypeIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRODUCT_TYPE);
        int itemColorIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_COLOR);
        int itemFavouriteIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_IS_FAVOURITE);

        String imageName = cursor.getString(itemHiImageIndex);
        if (!TextUtils.isEmpty(imageName)) {
            imageLoader.displayImage(
                    String.format("http://s1.isimpleapp.ru/img/ver0/%1$s%2$s", imageName.replace('\\', '/'), sizePrefix),
                    imageView, options);
        } else {
            imageView.setImageResource(R.drawable.bottle_list_image_default);
        }
        nameView.setText(organizeItemNameLabel(cursor.getString(itemNameIndex)));
        itemLocName.setText(organizeLocItemNameLabel(cursor.getString(itemLocNameIndex)));
        String volumeLabel = Utils.organizeProductLabel(Utils.removeZeros(cursor.getString(itemVolumeIndex)));
        String priceLabel = cursor.getString(itemPriceIndex);
        String discountLabel = String.valueOf(cursor.getInt(itemDiscountIndex));
        String originPriceLabel = String.valueOf(cursor.getInt(itemOriginPriceIndex));
        if (priceLabel != null) {
            if (priceLabel.equalsIgnoreCase("0") || priceLabel.equalsIgnoreCase("999999")) {
                priceLabel = "";
            } else {
                priceLabel = Utils.organizePriceLabel(priceLabel);
            }
        }
        if (discountLabel != null) {
            if (discountLabel.equalsIgnoreCase("0") || discountLabel.equalsIgnoreCase("999999")) {
                discountLabel = "";
            }
//            else {
//                discountLabel = Utils.organizePriceLabel(discountLabel);
//            }
        }
        if (originPriceLabel != null) {
            if (originPriceLabel.equalsIgnoreCase("0")
                    || originPriceLabel.equalsIgnoreCase("999999")) {
                originPriceLabel = "";
            } else {
                originPriceLabel = Utils.organizePriceLabel(originPriceLabel);
            }
        }
        Float quantity = cursor.getFloat(itemQuantityIndex);
        String formatVolume = "%.0f x %s";
        if (quantity != null && quantity > 1) {
            volumeLabel = String.format(formatVolume, quantity, volumeLabel);
        }
        if (mGroup) {
            String strDrinkId = Utils.removeZeros(cursor.getString(itemCountIndex));
            int drinkId = strDrinkId != null && strDrinkId.length() != 0 ? Integer.valueOf(strDrinkId) : 1;
            if (drinkId > 1 && volumeLabel != null) {
                formatVolume = "%s товар%s";
                String end = "";
                if ((drinkId >= 10 && drinkId <= 20) || strDrinkId.charAt(strDrinkId.length() - 1) == '0') {
                    end = "ов";

                } else if (strDrinkId.charAt(strDrinkId.length() - 1) == '2' ||
                        strDrinkId.charAt(strDrinkId.length() - 1) == '3' ||
                        strDrinkId.charAt(strDrinkId.length() - 1) == '4') {
                    end = "а";
                } else if (strDrinkId.charAt(strDrinkId.length() - 1) == '1') {
                    end = "";
                } else {    //5 - 9 last number
                    end = "ов";
                }

                volumeLabel = String.format(formatVolume, drinkId, end);
                if (!TextUtils.isEmpty(priceLabel)) {
                    if (priceLabel.equalsIgnoreCase("0") || priceLabel.equalsIgnoreCase("999999")) {
                        priceLabel = "";
                    } else {
                        String formatPrice = "от %s";
                        priceLabel = String.format(formatPrice, priceLabel);
                    }
                }
                if (!TextUtils.isEmpty(originPriceLabel)) {
                    String formatPrice = "от %s";
                    originPriceLabel = String.format(formatPrice, originPriceLabel);
                }
            }
        }
        itemVolume.setText(volumeLabel != null ? volumeLabel : "");
        itemPrice.setText(priceLabel != null ? priceLabel : "");
        if (!TextUtils.isEmpty(discountLabel)) {
            discountTriangleImageView.setVisibility(View.VISIBLE);
            itemOldPrice.setText(originPriceLabel);
        } else {
            discountTriangleImageView.setVisibility(View.GONE);
            itemOldPrice.setText("");
        }
//TODO:
        String strDrinkCategory = DrinkCategory.getDrinkCategory(cursor.getInt(itemDrinkCategoryIndex)).getDescription();
        if (mYearEnable) {
            String strYear = cursor.getString(itemYearIndex);
            if (!TextUtils.isEmpty(strYear)) {
                if (!strYear.equalsIgnoreCase("0")) {
                    String format = "%s, %s г";
                    strDrinkCategory = String.format(format, strDrinkCategory, strYear);
                }
            }
        }
        itemProductType.setText(strDrinkCategory);

        ProductType productType = ProductType.getProductType(cursor.getInt(itemProductTypeIndex));
        String colorStr = productType.getColor();
        if (colorStr == null) {
            colorStr = ItemColor.getColor(cursor.getInt(itemColorIndex)).getCode();
        }
        colorItem.setBackgroundColor(Color.parseColor(colorStr));

        ImageView imageViewFavourite = (ImageView) view.findViewById(R.id.item_image_favourite);
        if (cursor.getInt(itemFavouriteIndex) == 1) {
            imageViewFavourite.setVisibility(View.VISIBLE);
        } else {
            imageViewFavourite.setVisibility(View.GONE);
        }

        if (mDeleteItemsId != null) {
            ImageView viewDiscard = (ImageView) view.findViewById(R.id.item_image_delete);
            if (mDeleteItemsId.contains(cursor.getString(0))) {
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
