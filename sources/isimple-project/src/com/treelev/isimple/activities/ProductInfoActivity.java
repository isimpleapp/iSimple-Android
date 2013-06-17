package com.treelev.isimple.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ProductContentAdapter;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.ui.ProductContent;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.widget.*;

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
    private boolean mIsFavourite;
    private MenuItem mItemFavourite;
    private View headerView;
    private ProxyManager proxyManager;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    @Override
    protected void onResume() {
        super.onResume();
        ProxyManager proxyManager = new ProxyManager(this);
        mIsFavourite = proxyManager.isFavourites(itemId);
        setFavouritesImage(mIsFavourite);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = Utils.getImageLoader(getApplicationContext());
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.product_default_image)
                .showImageForEmptyUri(R.drawable.product_default_image)
                .showImageOnFail(R.drawable.product_default_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        mContext = this;
        mLocationId = getIntent().getStringExtra(ShopInfoActivity.LOCATION_ID);
        if (mLocationId == null) {
            setCurrentCategory(0);
        } else if (getIntent().getBooleanExtra(FavoritesActivity.FAVORITES, false)) {
            setCurrentCategory(2);
        } else {
            setCurrentCategory(1);
        }
        createNavigationMenuBar();
//        String itemId = getIntent().getStringExtra(ITEM_ID_TAG);
        setContentView(R.layout.product_layout);
        proxyManager = new ProxyManager(this);

        itemId = getIntent().getStringExtra(ITEM_ID_TAG);
        mBarcode = getIntent().getStringExtra(BaseListActivity.BARCODE);

        if (itemId != null && mBarcode == null) {
            mProduct = proxyManager.getItemById(itemId);
            mIsFavourite = proxyManager.isFavourites(itemId);
        } else {

            if (proxyManager.getItemByBarcodeTypeItem(mBarcode) == null) {
                mProduct = proxyManager.getItemDeprecatedByBarcodeTypeItem(mBarcode);
            } else {
                mProduct = proxyManager.getItemByBarcodeTypeItem(mBarcode);
            }
        }


        ExpandableListView listView = getExpandableListView();
        headerView = getLayoutInflater().inflate(R.layout.product_header_view, listView, false);
//TODO: replace
        TextView itemTitle = (TextView) findViewById(R.id.title_item);

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
                Intent newIntent = new Intent(mContext, ShopsFragmentActivity.class);
                newIntent.putExtra(ShopsFragmentActivity.ITEM_PRODUCT_ID, itemId);
                startActivity(newIntent);
                overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
            }
        });

        Button btAddToBasket = (Button) headerView.findViewById(R.id.add_to_basket_butt);
        btAddToBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isProductExistShoppingCart = proxyManager.isProductExistShoppingCart(itemId);
                if (isProductExistShoppingCart) {
                    proxyManager.addItemCount(itemId);
                } else {
                    proxyManager.insertProductInShoppingCart(mProduct);
                }
                Toast.makeText(ProductInfoActivity.this, "Товар добавлен в корзину", android.widget.Toast.LENGTH_LONG).show();
            }
        });
// Hide buttons
//        if( !proxyManager.availibilityItem(mProduct.getDrinkID()) ){
//            btAddToBasket.setEnabled(false);
//            btWhereToBuy.setEnabled(false);
//            btWhereToBuy.setVisibility(View.GONE);
//            btAddToBasket.setVisibility(View.GONE);
//            headerView.findViewById(R.id.retail_price).setVisibility(View.GONE);
//        }
        setFavouritesImage(mIsFavourite);
    }

    private void setFavouritesImage(boolean isFavourite) {
        ImageView image = (ImageView) headerView.findViewById(R.id.favourite_image);
        TextView tv = (TextView) headerView.findViewById(R.id.product_manufacturer);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (isFavourite) {
            image.setVisibility(View.VISIBLE);
            params.setMargins(0, 0, 0, 0);
            tv.setLayoutParams(params);
        } else {
            image.setVisibility(View.GONE);
            int marginLeft = (int)getResources().getDimension(R.dimen.marginLeft);
            params.setMargins(marginLeft, 0, 0, 0);
            tv.setLayoutParams(params);
        }
    }

    @Override
    public void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        if (mLocationId == null) {
            getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
        } else if (getIntent().getBooleanExtra(FavoritesActivity.FAVORITES, false)) {
            getSupportActionBar().setIcon(R.drawable.menu_ico_fav);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_shared, menu);
        mItemFavourite = menu.findItem(R.id.menu_item_favorite);
        if (mIsFavourite) {
            mItemFavourite.setIcon(R.drawable.product_icon_not_favorite);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
                return true;
            case R.id.menu_item_favorite:
                ProxyManager proxyManager = new ProxyManager(this);
                ArrayList listProduct = new ArrayList<String>();
                listProduct.add(mProduct.getItemID());
                if (mIsFavourite) {
                    proxyManager.delFavourites(listProduct);
                    mItemFavourite.setIcon(R.drawable.product_icon_favorite);
                    mIsFavourite = false;
                } else {
                    proxyManager.addFavourites(mProduct);
                    mItemFavourite.setIcon(R.drawable.product_icon_not_favorite);
                    mIsFavourite = true;
                }
                proxyManager.setFavouriteItemTable(listProduct, mIsFavourite);
                setFavouritesImage(mIsFavourite);
                return true;
            case R.id.menu_item_send_mail:
                 initShareIntent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initShareIntent() {
        String type = "mail";
        boolean found = false;
        Intent sendMail = new Intent(Intent.ACTION_SEND);
        sendMail.setType("text/html");
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(sendMail, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type) ) {
                    sendMail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_mail));
                    String mailText = getMailText();
                    sendMail.putExtra(Intent.EXTRA_TEXT, mailText);
                    sendMail.putExtra(Intent.EXTRA_HTML_TEXT, mailText);
                    sendMail.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (found){
                startActivity(Intent.createChooser(sendMail, getString(R.string.title_dialog_send_mail)));
            } else {
                Toast.makeText(this, this.getString(R.string.not_found_mail_cleint), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getMailText() {
        String bottleRes = mProduct.getBottleHiResolutionImageFilename();
        String name = mProduct.getName();
        String localizedName = mProduct.getLocalizedName();
        String typeProduct = mProduct.getProductType() != null ? mProduct.getProductType().getLabel() : "";
        String country = mProduct.getCountry();
        String region = TextUtils.isEmpty(mProduct.getRegion()) ? "-" : mProduct.getRegion();
        String volume = Utils.organizeProductLabel(FORMAT_VOLUME, trimTrailingZeros(mProduct.getVolume() + ""));
        String alcohol = !trimTrailingZeros(mProduct.getAlcohol()).equals("0") ? Utils.organizeProductLabel(FORMAT_ALCOHOL, trimTrailingZeros(mProduct.getAlcohol())) : "";
        String manufacturer = mProduct.getManufacturer();
        String itemId = mProduct.getItemID();
        String str = "<html><body><div><p style=\"margin: 20px;; width: 80%%;\">Мне понравился напиток в приложении iSimple." +
                "</p><div style=\"float: left;;\"><img src=\"http://s1.isimpleapp.ru/img/ver0/%1$s_product.jpg\" style=\"margin-top: 30px;; margin-right: 20px;;\">" +
                "</div><table style=\"width: 50%%;;\"><tbody><tr><td width=\"30%%\" height=\"230px\" align=\"center\" vertical-align=\"top\" style=\"vertical-align: top\"></td>" +
                "<td width=\"70%%\"><h2 style=\"margin-bottom: 0px; padding-bottom: 0px; font-weight: normal\">%2$s</h2>" +
                "<span style=\"color: #6f6f6f\">%3$s</span><br><br><span style=\"color: #ec068d\">%4$s</span><br><br>%5$s, %6$s<br><br>" +
                "<span style=\"color: #6f6f6f\">Объем:</span> %7$s л.<br><br><span style=\"color: #6f6f6f\">Крепость:</span> %8$s об.<br><br>" +
                "<span style=\"color: #6f6f6f\">Производитель:</span> %9$s<br><br><span style=\"color: #6f6f6f\">Артикул:</span> %10$s<br><br><" +
                "br>Подробнее об этом напитке можно узнать на " +
                "<a href=\"http://simplewine.ru/product_xml_id/%10$s/?doc_name=ISIMPLE\" style=\"color: #ec068d\">сайте Simple</a>.</td></tr></tbody></table></div></body></html>";
        return String.format(str, bottleRes, name, localizedName, typeProduct, country, region, volume, alcohol, manufacturer, itemId);
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
        organizeTextView((TextView) formView.findViewById(R.id.product_region), !product.getRegion().equals("-") ? product.getRegion() : "");
        organizeTextView((TextView) formView.findViewById(R.id.product_sweetness), !product.getSweetness().getDescription().isEmpty() ? product.getSweetness().getDescription() : "");
        organizeTextView((TextView) formView.findViewById(R.id.product_style), product.getStyle());
        organizeTextView((TextView) formView.findViewById(R.id.product_grapes), product.getGrapesUsed());
        organizeTextView((TextView) formView.findViewById(R.id.product_alcohol), !trimTrailingZeros(product.getAlcohol()).equals("0") ? Utils.organizeProductLabel(FORMAT_ALCOHOL, trimTrailingZeros(product.getAlcohol())) : "");
        organizeTextView((TextView) formView.findViewById(R.id.product_volume), Utils.organizeProductLabel(FORMAT_VOLUME, trimTrailingZeros(product.getVolume() + "")));
        organizeTextView((TextView) formView.findViewById(R.id.product_year), product.getYear() != 0 ? String.valueOf(product.getYear()) : "");

        if (!TextUtils.isEmpty(product.getBottleHiResolutionImageFilename())) {
            ImageView productImage = (ImageView) formView.findViewById(R.id.product_image);
            int screenMask = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
            String sizePrefix =
                screenMask == Configuration.SCREENLAYOUT_SIZE_LARGE ? "_hdpi" :
                screenMask == Configuration.SCREENLAYOUT_SIZE_XLARGE ? "_xhdpi" : "";

            imageLoader.displayImage(
                String.format("http://s1.isimpleapp.ru/img/ver0/%1$s%2$s_product.jpg", product.getBottleHiResolutionImageFilename().replace('\\', '/'), sizePrefix),
                productImage, options);
        }

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
        if (strPriceLabel != null) {
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
        } else {
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