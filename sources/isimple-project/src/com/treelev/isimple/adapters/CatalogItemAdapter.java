
package com.treelev.isimple.adapters;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.treelev.isimple.R;
import com.treelev.isimple.cursorloaders.SelectFeaturedByCategoryItems;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.utils.LogUtils;
import com.treelev.isimple.utils.Utils;

public class CatalogItemAdapter extends BaseAdapter implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private final static String FORMAT_TEXT_LABEL = "%s...";
    private final static int FORMAT_NAME_MAX_LENGTH = 41;

    private Cursor cursor;
    private int groupId;
    private int sortBy;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String sizePrefix;

    private Dialog mDialog;
    private Integer mCountCallBack;
    private Set<Integer> mEmptyGroupView;

    protected Context mContext;
    protected LoaderManager mManager;

    private int itemNameIndex;
    private int itemVolumeIndex;
    private int itemPriceIndex;
    private int itemDiscountIndex;
    private int itemOriginPriceIndex;
    private int itemQuantityIndex;
    private int itemHiImageIndex;
    private int itemCountIndex;
    private int itemFavouriteIndex;

    private boolean mEmpty;
    private FinishListener mListener;

    public interface FinishListener {
        void onFinish();
    }

    public CatalogItemAdapter(int groupId, Context context, Cursor c, LoaderManager manager,
            int sortBy) {

        this.groupId = groupId;
        mContext = context;
        mManager = manager;
        this.sortBy = sortBy;

        mCountCallBack = 0;

        mEmptyGroupView = new HashSet<Integer>();

        imageLoader = Utils.getImageLoader(mContext.getApplicationContext());
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.bottle_list_image_default)
                .showImageOnLoading(R.drawable.bottle_list_image_default)
                .showImageForEmptyUri(R.drawable.bottle_list_image_default)
                .showImageOnFail(R.drawable.bottle_list_image_default).cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300)).resetViewBeforeLoading(false).build();

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        sizePrefix = metrics.densityDpi == DisplayMetrics.DENSITY_HIGH ? "_hdpi"
                : metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH ? "_xhdpi" : "";

        if (mManager.getLoader(groupId) != null && !mManager.getLoader(groupId).isReset()) {
            LogUtils.i("", "getCursor restartLoader");
            mManager.restartLoader(groupId, null, this);
        } else {
            LogUtils.i("", "getCursor initLoader");
            mManager.initLoader(groupId, null, this);
        }
        startDialog();
    }

    public void setFinishListener(FinishListener listener) {
        mListener = listener;
    }

    private void initIndexs(Cursor cursor) {
        itemNameIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_NAME);
        itemVolumeIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_VOLUME);
        itemPriceIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRICE);
        itemDiscountIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_DISCOUNT);
        itemOriginPriceIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_ORIGIN_PRICE);
        itemHiImageIndex = cursor
                .getColumnIndex(DatabaseSqlHelper.ITEM_BOTTLE_HI_RESOLUTION_IMAGE_FILENAME);
        itemFavouriteIndex = cursor.getColumnIndex(DatabaseSqlHelper.ITEM_IS_FAVOURITE);
    }

    private ViewHolder getViewHolder(View view) {
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
        viewHolder.nameView = (TextView) view.findViewById(R.id.item_name);
        viewHolder.itemVolume = (TextView) view.findViewById(R.id.item_volume);
        viewHolder.itemPrice = (TextView) view.findViewById(R.id.item_price);
        viewHolder.itemOldPrice = (TextView) view.findViewById(R.id.item_old_price);
        viewHolder.itemOldPrice.setPaintFlags(viewHolder.itemOldPrice.getPaintFlags()
                | Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.imageViewDiscountTriangle = (ImageView) view
                .findViewById(R.id.discount_triangle);
        viewHolder.imageViewFavourite = (ImageView) view.findViewById(R.id.item_image_favourite);

        return viewHolder;
    }

    private void bindItemView(View view, Cursor cursor, ViewHolder viewHolder, int position) {

        cursor.moveToPosition(position);
        String imageName = cursor.getString(itemHiImageIndex);
        if (!TextUtils.isEmpty(imageName)) {
            imageLoader.displayImage(
                    String.format("http://s1.isimpleapp.ru/img/ver0/%1$s%2$s_listing.jpg",
                            imageName.replace('\\', '/'), sizePrefix), viewHolder.imageView,
                    options);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.bottle_list_image_default);
        }
        viewHolder.nameView.setText(organizeItemNameLabel(cursor.getString(itemNameIndex)));
        String volumeLabel = Utils.organizeProductLabel(Utils.removeZeros(cursor
                .getString(itemVolumeIndex)));
        String priceLabel = String.valueOf(cursor.getFloat(itemPriceIndex));
        String discountLabel = String.valueOf(cursor.getFloat(itemDiscountIndex));
        String originPriceLabel = String.valueOf(cursor.getFloat(itemOriginPriceIndex));
        if (priceLabel != null) {
            if (priceLabel.equalsIgnoreCase("0") || priceLabel.equalsIgnoreCase("999999")) {
                priceLabel = "";
            } else {
                priceLabel = Utils.organizePriceLabel(priceLabel);
            }
        }
        if (discountLabel != null) {
            if (discountLabel.equalsIgnoreCase("0.0") || discountLabel.equalsIgnoreCase("999999.0")) {
                discountLabel = "";
            } else {
                discountLabel = Utils.organizePriceLabel(discountLabel);
            }
        }
        if (originPriceLabel != null) {
            if (originPriceLabel.equalsIgnoreCase("0.0")
                    || originPriceLabel.equalsIgnoreCase("999999.0")) {
                originPriceLabel = "";
            } else {
                originPriceLabel = Utils.organizePriceLabel(discountLabel);
            }
        }
        viewHolder.itemVolume.setText(volumeLabel != null ? volumeLabel : "");
        if (!TextUtils.isEmpty(discountLabel)) {
            viewHolder.imageViewDiscountTriangle.setVisibility(View.VISIBLE);
            viewHolder.itemOldPrice.setText(originPriceLabel);
        } else {
            viewHolder.imageViewDiscountTriangle.setVisibility(View.GONE);
            viewHolder.itemOldPrice.setText("");
        }
        viewHolder.itemPrice.setText(priceLabel);

        if (cursor.getInt(itemFavouriteIndex) == 1) {
            viewHolder.imageViewFavourite.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewFavourite.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        LogUtils.i("", "onCreateLoader");
        SelectFeaturedByCategoryItems cursorLoader = new SelectFeaturedByCategoryItems(mContext,
                groupId, null, sortBy);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        LogUtils.i("", "onLoadFinished");
        this.cursor = cursor;
        initIndexs(this.cursor);
        mEmpty &= cursor.getCount() == 0;
        stopDialog();
        if (this.cursor.getCount() == 0) {
            LogUtils.i("", "getCursor initLoader");
            mEmptyGroupView.add(Integer.valueOf(cursorLoader.getId()));
        } else {
            refresh();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public void refresh() {
        mEmptyGroupView.clear();
        notifyDataSetChanged();
    }

    private void startDialog() {
        if (mCountCallBack == 0 && mDialog == null) {
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
            mEmpty = true;
        }
        ++mCountCallBack;
    }

    private void stopDialog() {
        --mCountCallBack;
        if (mCountCallBack == 0 && mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
            if (mListener != null) {
                mListener.onFinish();
            }
        }
    }

    public boolean isEmpty() {
        return mEmpty;
    }

    private String organizeItemNameLabel(String itemName) {
        return organizeTextLabel(itemName, FORMAT_NAME_MAX_LENGTH);
    }

    private String organizeTextLabel(String itemName, int maxLength) {
        String result = itemName;
        if (result.length() > maxLength) {
            result = String.format(FORMAT_TEXT_LABEL, result.substring(0, maxLength));
        }
        return result;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView nameView;
        TextView itemVolume;
        TextView itemPrice;
        TextView itemOldPrice;
        ImageView imageViewDiscountTriangle;
        ImageView imageViewFavourite;
    }

    @Override
    public int getCount() {
        if (cursor == null) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    @Override
    public Object getItem(int position) {
        String[] columnNames = cursor.getColumnNames();
        ContentValues cv = new ContentValues();
        cursor.moveToPosition(position);
        DatabaseUtils.cursorRowToContentValues(cursor, cv);
        
        MatrixCursor matrixCursor = new MatrixCursor(columnNames);
        Object[] values = new Object[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            values[i] = cv.get(columnNames[i]);
        }
        matrixCursor.addRow(values);
        matrixCursor.moveToFirst();
        return matrixCursor;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.catalog_list_item, parent, false);
            viewHolder = getViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        bindItemView(convertView, cursor, viewHolder, position);
        return convertView;
    }

}
