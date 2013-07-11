package com.treelev.isimple.adapters.itemstreecursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
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
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.LinearLayout;

import java.util.HashSet;
import java.util.Set;

public abstract class AbsItemTreeCursorAdapter extends SimpleCursorTreeAdapter
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private final static String FORMAT_TEXT_LABEL = "%s...";
    private final static int FORMAT_NAME_MAX_LENGTH = 41;
    private final static int FORMAT_LOC_NAME_MAX_LENGTH = 29;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String sizePrefix;

    private Dialog mDialog;
    private Integer mCountCallBack;
    private Set<Integer> mEmptyGroupView;

    protected Context mContext;
    protected LoaderManager mManager;
    protected boolean mGroup;
    protected boolean mYearEnable;
    protected int mSortBy;
    protected String mFilterWhereClause;


    public AbsItemTreeCursorAdapter(Context context, Cursor cursor, LoaderManager manager, int sortBy) {
        super(context, cursor, R.layout.section_items,
                R.layout.section_items,
                new String[] {"name"},
                new int[]{R.id.section_name},
                R.layout.catalog_item_layout,
                Item.getUITags(),
                new int[]{ R.id.item_name, R.id.item_loc_name, R.id.item_volume, R.id.item_price, R.id.product_category});

        mContext = context;
        mManager = manager;
        mGroup = true;
        mYearEnable = false;
        mSortBy = sortBy;

        mCountCallBack = 0;

        mEmptyGroupView = new HashSet<Integer>();

        imageLoader = Utils.getImageLoader(mContext.getApplicationContext());
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.bottle_list_image_default)
                .showImageForEmptyUri(R.drawable.bottle_list_image_default)
                .showImageOnFail(R.drawable.bottle_list_image_default)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        sizePrefix =
                metrics.densityDpi == DisplayMetrics.DENSITY_HIGH ? "_hdpi" :
                        metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH? "_xhdpi" : "";
    }


    public void setSortBy(int sortBy){
        mSortBy = sortBy;
    }

    public  void setFilterWhereClause(String filterWhereClause){
        mFilterWhereClause = filterWhereClause;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor cursor) {
        int position = cursor.getInt(cursor.getColumnIndex("_id"));
        if (mManager.getLoader(position) != null && !mManager.getLoader(position).isReset()) {
            mManager.restartLoader(position, null, this);
        }
        else {
            mManager.initLoader(position, null, this);
        }
        startDialog();
        return null;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view;
        if(groupPosition == 0 || mEmptyGroupView.contains(new Integer(groupPosition))){
            view = LayoutInflater.from(mContext).inflate(R.layout.empty_item, null);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.section_items, null);
            Cursor cursor = getGroup(groupPosition);
            TextView sectionName = (TextView) view.findViewById(R.id.section_name);
            int indexName = cursor.getColumnIndex("name");
            sectionName.setText(cursor.getString(indexName));
        }
        return view;
    }

    @Override
    public void setGroupCursor(Cursor cursor) {
        mEmptyGroupView.clear();
        super.setGroupCursor(cursor);
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
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
            if( priceLabel.equalsIgnoreCase("0") || priceLabel.equalsIgnoreCase("999999")) {
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
                if(!TextUtils.isEmpty(priceLabel)) {
                    if(priceLabel.equalsIgnoreCase("0") ||  priceLabel.equalsIgnoreCase("999999")){
                        priceLabel = "";
                    } else {
                        String formatPrice = "от %s";
                        priceLabel = String.format(formatPrice, priceLabel);
                    }
                }
            }
        }
        itemVolume.setText(volumeLabel != null ? volumeLabel : "");
        itemPrice.setText(priceLabel);
//TODO:
        String strDrinkCategory = DrinkCategory.getDrinkCategory(cursor.getInt(itemDrinkCategoryIndex)).getDescription();
        if( mYearEnable ) {
            String strYear = cursor.getString(itemYearIndex);
            if( !TextUtils.isEmpty(strYear) ) {
                if( !strYear.equalsIgnoreCase("0") ){
                    String format = "%s, %s г";
                    strDrinkCategory = String.format(format, strDrinkCategory, strYear);
                }
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

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        setChildrenCursor(cursorLoader.getId(), cursor);
        stopDialog();
        if(cursor.getCount() == 0) {
            mEmptyGroupView.add(new Integer(cursorLoader.getId()));
        }
   }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public void refresh(){
        mEmptyGroupView.clear();
        notifyDataSetChanged();
    }

    private void startDialog(){
        if(mCountCallBack == 0 && mDialog == null){
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
            mContext.getString(R.string.dialog_select_data_message), false, false);
        }
        ++mCountCallBack;
    }

    private void stopDialog(){
        --mCountCallBack;
        if(mCountCallBack == 0 && mDialog != null){
            mDialog.dismiss();
            mDialog = null;
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
