package com.treelev.isimple.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ProductContentAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.ui.ProductContent;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.widget.BaseExpandableListAdapter;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ExpandableListView;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductInfoActivity extends BaseExpandableListActivity {

    public final static String ITEM_ID_TAG = "id";
    private final static String FORMAT_FIELDS = "- %s";
    private final static String EMPTY_PRICE_LABEL = "-";
    private final static String FORMAT_ALCOHOL = "%s%% алк.";
    private final static String FORMAT_VOLUME = "%s л.";
    private String mLocationId;
    private String mBarcode;
    private String itemId;
    private Item mProduct;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mLocationId = getIntent().getStringExtra(ShopActivity.LOCATION_ID);
        if (mLocationId == null) {
            setCurrentCategory(0);
        } else if(getIntent().getBooleanExtra(FavoritesActivity.FAVORITES, false)){
            setCurrentCategory(2);
        } else {
            setCurrentCategory(1);
        }
        createNavigationMenuBar();
//        String itemId = getIntent().getStringExtra(ITEM_ID_TAG);
        setContentView(R.layout.product_layout);
        ProxyManager proxyManager = new ProxyManager(this);

        itemId = getIntent().getStringExtra(ITEM_ID_TAG);
        mBarcode = getIntent().getStringExtra(BaseListActivity.BARCODE);

        if (itemId != null && mBarcode == null) {
            mProduct = proxyManager.getItemById(itemId);
        } else {

            if(proxyManager.getItemByBarcodeTypeItem(mBarcode) == null){
                mProduct = proxyManager.getItemDeprecatedByBarcodeTypeItem(mBarcode);
            } else{
                mProduct = proxyManager.getItemByBarcodeTypeItem(mBarcode);
            }
        }


        ExpandableListView listView = getExpandableListView();
        final View headerView = getLayoutInflater().inflate(R.layout.product_header_view, listView, false);
//TODO: replace
        TextView itemTitle = (TextView) findViewById(R.id.title_item);
//        ProductType productType = mProduct.getProductType(); ProductType.getProductType(

        Log.e("!!!!!!!!!!!!!!!","mProduct = " + mProduct);

        itemTitle.setText(mProduct.getProductType() != null ? mProduct.getProductType().getLabel() : "");

        String colorStr = mProduct.getProductType().getColor();
        if (colorStr == null) {
            colorStr = mProduct.getColor().getCode();
        }
        itemTitle.setBackgroundColor(Color.parseColor(colorStr));

        listView.addHeaderView(headerView, null, false);
        populateFormsFields(headerView, mProduct);
        List<ProductContent> productContentList = createExpandableItems(mProduct);

        BaseExpandableListAdapter listAdapter = new ProductContentAdapter(this, productContentList);
        listView.setAdapter(listAdapter);


//        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int widthDisplay = display.getWidth();
        LinearLayout list_layout = (LinearLayout) headerView.findViewById(R.id.list_layout);
        final LinearLayout lin_for_two_button = (LinearLayout) headerView.findViewById(R.id.linear_for_two_button);
        ((RelativeLayout.LayoutParams) lin_for_two_button.getLayoutParams()).width = widthDisplay();
        ((RelativeLayout.LayoutParams) list_layout.getLayoutParams()).width = widthDisplay() - (widthDisplay() / 3);
        lin_for_two_button.requestLayout();

//        TextView titleItem = (TextView)findViewById(R.id.title_item);
//        titleItem.setPadding(0,0,getViewsWidth(headerView) - width,0);
//        ((LinearLayout.LayoutParams) titleItem.getLayoutParams()).rightMargin = getViewsWidth(headerView) - width;

        Button btWhereToBuy = (Button) headerView.findViewById(R.id.shops_butt);
        btWhereToBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(mContext, ShopsActivity.class);
                newIntent.putExtra(ShopsActivity.ITEM_PRODUCT_ID, itemId);
                startActivity(newIntent);
                overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
            }
        });

        Button btAddToBasket = (Button) headerView.findViewById(R.id.add_to_basket_butt);

        if( !proxyManager.availibilityItem(mProduct.getDrinkID()) ){
            btWhereToBuy.setVisibility(View.GONE);
            btAddToBasket.setVisibility(View.GONE);
            headerView.findViewById(R.id.retail_price).setVisibility(View.GONE);
        }
    }

    @Override
    public void createNavigationMenuBar() {
        super.createNavigationMenuBar();
//        if (mLocationId != null) {
//            getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
//        }
        if (mLocationId == null) {
            getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
        } else if(getIntent().getBooleanExtra(FavoritesActivity.FAVORITES, false)){
            getSupportActionBar().setIcon(R.drawable.menu_ico_fav);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_shared, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider)item.getActionProvider();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
        intent.putExtra(Intent.EXTRA_HTML_TEXT,"Extra Text");
        myShareActionProvider.setShareIntent(intent);
//        myShareActionProvider.setShareHistoryFileName(
//                ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
//        myShareActionProvider.setShareIntent(createShareIntent());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    private int widthDisplay() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    private int getViewsWidth(View view) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenWidth = metrics.widthPixels;

        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredWidth();
    }

    private List<ProductContent> createExpandableItems(Item product) {
        String[] itemsNames = getResources().getStringArray(R.array.expandable_groups_names);
        List<ProductContent> items = new ArrayList<ProductContent>();
        addExpandableItem(items, itemsNames[0], product.getStyleDescription());
        addExpandableItem(items, itemsNames[1], product.getGastronomy());
        addExpandableItem(items, itemsNames[2], product.getTasteQualities());
        addExpandableItem(items, itemsNames[3], product.getInterestingFacts());
        addExpandableItem(items, itemsNames[4], product.getProductionProcess());
        addExpandableItem(items, itemsNames[5], product.getDrinkCategory().getDescription());
        addExpandableItem(items, itemsNames[6], product.getVineyard());
        return items;
    }

    private void addExpandableItem(List<ProductContent> listItems, String itemName, String itemContent) {
        if (!TextUtils.isEmpty(itemContent)) {
            listItems.add(new ProductContent(itemName, itemContent));
        }
    }

    private void populateFormsFields(View formView, Item product) {
        String priceLabel = Utils.organizePriceLabel(String.valueOf(product.getPrice()));
        ((Button) formView.findViewById(R.id.add_to_basket_butt)).setText(product.getPrice() != 0 ? priceLabel : EMPTY_PRICE_LABEL);
        ((TextView) formView.findViewById(R.id.product_name)).setText(product.getName());
        ((TextView) formView.findViewById(R.id.product_manufacturer)).setText(product.getManufacturer());
        ((TextView) formView.findViewById(R.id.product_localizated_name)).setText(product.getLocalizedName());
        ((TextView) formView.findViewById(R.id.product_item_id)).setText(product.getItemID());
        organizeTextView((TextView) formView.findViewById(R.id.product_region), !product.getRegion().equals("-")? product.getRegion():"");
        organizeTextView((TextView) formView.findViewById(R.id.product_sweetness), !product.getSweetness().getDescription().isEmpty()? product.getSweetness().getDescription():"");
        organizeTextView((TextView) formView.findViewById(R.id.product_style), product.getStyle());
        organizeTextView((TextView) formView.findViewById(R.id.product_grapes), product.getGrapesUsed());
        organizeTextView((TextView) formView.findViewById(R.id.product_alcohol), ! trimTrailingZeros(product.getAlcohol()).equals("0") ?  Utils.organizeProductLabel(FORMAT_ALCOHOL, trimTrailingZeros(product.getAlcohol())):"");
        organizeTextView((TextView) formView.findViewById(R.id.product_volume), Utils.organizeProductLabel(FORMAT_VOLUME, trimTrailingZeros(product.getVolume() + "")));
        organizeTextView((TextView) formView.findViewById(R.id.product_year), product.getYear()!=0 ?  String.valueOf(product.getYear()) : "");
        String strPriceLabel = takeRetailPrice(product) != 0 ? takeRetailPrice(product).toString() : "";
        if (!TextUtils.isEmpty(strPriceLabel)) {
            ((TextView) formView.findViewById(R.id.retail_price)).setText(Utils.organizePriceLabel(getResources().getString(R.string.text_for_retail_price, strPriceLabel)));
        } else {
            ((TextView) formView.findViewById(R.id.retail_price)).setText(strPriceLabel);
        }
    }

    private Integer takeRetailPrice(Item product) {
        int retailPrice;
        String strPriceLabel = product.getPrice() != null ? String.valueOf(product.getPrice()) : null;
        if(strPriceLabel != null){
        String priceLabel = Utils.organizePriceLabel(strPriceLabel);
        if (!TextUtils.isEmpty(priceLabel)) {
            Scanner in = new Scanner(priceLabel).useDelimiter("[^0-9]+");
            int integerPriceLabel = in.nextInt();
            String priceMarkup = Utils.organizePriceLabel(product.getPriceMarkup() + "");
            if (priceMarkup != null) {
                Scanner intMarkup = new Scanner(priceMarkup).useDelimiter("[^0-9]+");
                int integerPriceMarkup = intMarkup.nextInt();
                retailPrice = integerPriceLabel * (integerPriceMarkup + 100) / 100;
            } else {
                retailPrice = integerPriceLabel;
            }
            return roundToTheTens(retailPrice);
        } else {
            return null;
        }
    }else{
            return null;
        }
    }
    private int roundToTheTens(int price) {
        int newPrice = price;
        if ((price % 10) != 0) {
            newPrice = price + (10 - (price % 10));
        }
        return newPrice;
    }

    private void organizeTextView(TextView textView, String text) {
        if (!TextUtils.isEmpty(text)) {
            textView.setText(String.format(FORMAT_FIELDS, text));
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private String trimTrailingZeros(String number) {
        return number != null ? Utils.removeZeros(number) : null;
    }
}