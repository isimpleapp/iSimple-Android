package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ProductContentAdapter;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.ui.ProductContent;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;
import com.treelev.isimple.utils.observer.ObserverDataChanged;
import com.treelev.isimple.views.ProportionalImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductInfoActivity extends BaseExpandableListActivity {

    public final static String CHANGE_FAVOURITE = "CHANGE_FAVOURITE";

    public final static String ITEM_ID_TAG = "id";
    private final static String FORMAT_FIELDS = "\u2013 %s";
    private final static String EMPTY_PRICE_LABEL = "\u2013";
    private final static String FORMAT_ALCOHOL = "%s%% алк.";
    private final static String FORMAT_VOLUME = "%s л.";
    private final static String PATH_TMP_IMAGE = "/sdcard/Android/data/com.treelev.isimple/cache";
    private final static String TMP_IMAGE = "/sdcard/Android/data/com.treelev.isimple/cache/%s.png";
    private String itemId;
    private Item mProduct;
    private boolean mIsFavourite;
    private boolean mLastFavourite;
    private MenuItem mItemFavourite;
    private View headerView;
    private ProxyManager proxyManager;
    private TextView animateText;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private boolean mIsProductExistShoppingCart;
    private Bitmap mBitMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageLoader();
        String locationId = getIntent().getStringExtra(ShopInfoActivity.LOCATION_ID);
        initCurrentCategory(locationId);
        setContentView(R.layout.product_layout);
        createDrawableMenu();
        proxyManager = ProxyManager.getInstanse();
        itemId = getIntent().getStringExtra(ITEM_ID_TAG);
        String mBarcode = getIntent().getStringExtra(BaseListActivity.BARCODE);
        initProduct(mBarcode);
        organizeHeaderView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Analytics.screen_ProductCard(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProxyManager proxyManager = ProxyManager.getInstanse();
        mIsFavourite = proxyManager.isFavourites(itemId);
        mLastFavourite = mIsFavourite;
        setFavouritesImage(mIsFavourite);
        mIsProductExistShoppingCart = proxyManager.isProductExistShoppingCart(itemId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteTmpImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shared, menu);
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
                onBackPressed();
                // overridePendingTransition(R.anim.finish_show_anim,
                // R.anim.finish_back_anim);
                return true;
            case R.id.menu_item_favorite:
                ProxyManager proxyManager = ProxyManager.getInstanse();
                ArrayList<String> listProduct = new ArrayList<String>();
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

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(CHANGE_FAVOURITE, mLastFavourite != mIsFavourite);
        setResult(RESULT_OK, data);
        if (mLastFavourite != mIsFavourite) {
            ObserverDataChanged.getInstant().sendEvent();
        }
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    private void initImageLoader() {
        imageLoader = Utils.getImageLoader(getApplicationContext());
        options = new DisplayImageOptions.Builder().showStubImage(R.drawable.product_default_image)
                .showImageForEmptyUri(R.drawable.product_default_image)
                .showImageOnFail(R.drawable.product_default_image).cacheInMemory(true).cacheOnDisc(true).build();
    }

    private void initCurrentCategory(String locationId) {
        if (locationId == null) {
            setCurrentCategory(0);
        } else if (getIntent().getBooleanExtra(FavoritesActivity.FAVORITES, false)) {
            setCurrentCategory(2);
        } else {
            setCurrentCategory(1);
        }
    }

    private void initProduct(String mBarcode) {
        if (itemId != null && mBarcode == null) {
            mProduct = proxyManager.getItemById(itemId);
            mIsFavourite = proxyManager.isFavourites(itemId);
        } else {
            if (proxyManager.getItemByBarcodeTypeItem(mBarcode) == null) {
                mProduct = proxyManager.getItemDeprecatedByBarcodeTypeItem(mBarcode);
            } else {
                if (itemId == null) {
                    mProduct = proxyManager.getItemByBarcodeTypeItem(mBarcode);
                } else {
                    mProduct = proxyManager.getItemById(itemId);
                }
            }
        }
    }

    private void organizeHeaderView() {
        headerView = getLayoutInflater().inflate(R.layout.product_header_view, getExpandableListView(), false);
        organizeHeaderTitle(headerView);
        getExpandableListView().addHeaderView(headerView, null, false);
        List<ProductContent> productContentList = createExpandableItems(mProduct);
        ProductContentAdapter mListAdapter = new ProductContentAdapter(this, productContentList);
        getExpandableListView().setAdapter(mListAdapter);
        populateFormsFields(headerView, mProduct);
        LinearLayout list_layout = (LinearLayout) headerView.findViewById(R.id.list_layout);
        ((RelativeLayout.LayoutParams) list_layout.getLayoutParams()).width = widthDisplay() - (widthDisplay() / 3);
        final LinearLayout lin_for_two_button = (LinearLayout) headerView.findViewById(R.id.linear_for_two_button);
        ((LinearLayout.LayoutParams) lin_for_two_button.getLayoutParams()).width = widthDisplay();
        lin_for_two_button.requestLayout();
        Float price = mProduct.getPrice();
        Button btWhereToBuy = (Button) headerView.findViewById(R.id.shops_butt);
        Button btAddToShoppingCart = (Button) headerView.findViewById(R.id.add_to_shopping_cart_butt);
        if (mProduct.getProductType() == ProductType.WATER ||
                mProduct.getProductType() == ProductType.ENERGY ||
                mProduct.getProductType() == ProductType.JUICE ||
                mProduct.getProductType() == ProductType.SYRUP) {
            btAddToShoppingCart.setTextColor(getResources().getColor(R.color.filter_blue));
            btAddToShoppingCart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.add_to_shopping_cart_blue, 0, 0, 0);
        }
        if (price != null && price != 0.0f) {
            btWhereToBuy.setOnClickListener(whereToBuyBtnClick);
            btAddToShoppingCart.setOnClickListener(addToShoppingCartBtnClick);
        } else {
            btWhereToBuy.setVisibility(View.GONE);
            btWhereToBuy.setOnClickListener(null);
            btAddToShoppingCart.setVisibility(View.GONE);
            btAddToShoppingCart.setOnClickListener(null);
        }
        setFavouritesImage(mIsFavourite);
    }

    private void organizeHeaderTitle(View headerView) {
        TextView itemTitle = (TextView) headerView.findViewById(R.id.title_item);
        itemTitle.setText(mProduct.getProductType().getLabel());

        if (mProduct.getColor() == ItemColor.WHITE) {
            itemTitle.setTextColor(getResources().getColor(android.R.color.black));
        }
        itemTitle.setBackgroundColor(Color.parseColor(getColorTitleString()));
    }

    private String getColorTitleString() {
        String colorStr = mProduct.getProductType().getColor();
        if (colorStr == null) {
            colorStr = mProduct.getColor().getCode();
        }
        return colorStr;
    }

//	private void createNavigationMenuBar(String locationId) {
//		super.createNavigationMenuBar();
//		if (locationId == null) {
//			getSupportActionBar().setIcon(R.drawable.menu_ico_catalog);
//		} else if (getIntent().getBooleanExtra(FavoritesActivity.FAVORITES, false)) {
//			getSupportActionBar().setIcon(R.drawable.menu_ico_fav);
//		}
//	}

    private void setFavouritesImage(boolean isFavourite) {
        ImageView image = (ImageView) headerView.findViewById(R.id.favourite_image);
        TextView tv = (TextView) headerView.findViewById(R.id.product_name);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        if (isFavourite) {
            image.setVisibility(View.VISIBLE);
            params.setMargins(0, 0, 0, 0);
            tv.setLayoutParams(params);
        } else {
            image.setVisibility(View.GONE);
            int marginLeft = (int) getResources().getDimension(R.dimen.marginLeft);
            params.setMargins(marginLeft, 0, 0, 0);
            tv.setLayoutParams(params);
        }
    }

    private void initShareIntent() {
        Intent sendMail = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sendMail.setType("text/html");
        sendMail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_mail));
        sendMail.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getMailText()));
        ArrayList<Uri> attachmentUri = getAttachmentImageUri();
        if (attachmentUri != null) {
            sendMail.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getAttachmentImageUri());
        }
        startActivity(Intent.createChooser(sendMail, getString(R.string.title_dialog_send_mail)));
        // List<ResolveInfo> resInfo =
        // getPackageManager().queryIntentActivities(sendMail, 0);
        // if (!resInfo.isEmpty()) {
        // for (ResolveInfo info : resInfo) {
        // if (info.activityInfo.packageName.toLowerCase().contains(type) ||
        // info.activityInfo.name.toLowerCase().contains(type)) {
        // sendMail.putExtra(Intent.EXTRA_SUBJECT,
        // getString(R.string.subject_mail));
        // sendMail.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getMailText()));
        // sendMail.setPackage(info.activityInfo.packageName);
        // found = true;
        // break;
        // }
        // }
        // if (found) {
        // startActivity(Intent.createChooser(sendMail,
        // getString(R.string.title_dialog_send_mail)));
        // } else {
        // Toast.makeText(this, this.getString(R.string.not_found_mail_cleint),
        // Toast.LENGTH_SHORT).show();
        // }
        // }
    }

    private ArrayList<Uri> getAttachmentImageUri() {
        ArrayList<Uri> attachmentUri = null;
        Uri image = getUriImage();
        if (image != null) {
            attachmentUri = new ArrayList<Uri>();
            attachmentUri.add(image);
        }
        return attachmentUri;
    }

    private Uri getUriImage() {
        Uri result = null;
        try {
            if (mBitMap != null) {
                File tmpDir = new File(PATH_TMP_IMAGE);
                if (!tmpDir.isDirectory()) {
                    tmpDir.mkdir();
                }
                File tmpImage = new File(String.format(TMP_IMAGE, mProduct.getItemID()));
                OutputStream fOut = new FileOutputStream(tmpImage);
                mBitMap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
                result = Uri.fromFile(tmpImage);
            }
        } catch (IOException e) {

        }
        return result;

    }

    private void deleteTmpImage() {
        if (mBitMap != null) {
            File tmpImage = new File(String.format(TMP_IMAGE, mProduct.getItemID()));
            if (tmpImage.exists()) {
                tmpImage.delete();
            }
        }
    }

    private String getMailText() {
        String name = mProduct.getName();
        String localizedName = mProduct.getLocalizedName();
        String typeProduct = mProduct.getProductType() != null ? mProduct.getProductType().getLabel() : "";
        String country = mProduct.getCountry();
        String region = TextUtils.isEmpty(mProduct.getRegion()) ? "\u2013" : mProduct.getRegion();
        String volume = Utils.organizeProductLabel(FORMAT_VOLUME, trimTrailingZeros(mProduct.getVolume() + ""));
        String alcohol = !trimTrailingZeros(mProduct.getAlcohol()).equals("0") ? trimTrailingZeros(mProduct
                .getAlcohol()) : "";
        String manufacturer = mProduct.getManufacturer();
        String itemId = mProduct.getItemID();
        String str = getResources().getString(R.string.mail_template);
        return String.format(str, name, localizedName, typeProduct, country, region, volume, alcohol, manufacturer,
                itemId, itemId);
    }

    private int widthDisplay() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    private List<ProductContent> createExpandableItems(Item product) {
        String[] itemsNames = getResources().getStringArray(R.array.expandable_groups_names);
        List<ProductContent> items = new ArrayList<ProductContent>();
        addExpandableItem(items, itemsNames[0], product.getStyleDescription());
        addExpandableItem(items, itemsNames[1], product.getGastronomy());
        addExpandableItem(items, itemsNames[2], product.getTasteQualities());
        addExpandableItem(items, itemsNames[3], product.getInterestingFacts());
        addExpandableItem(items, itemsNames[4], product.getProductionProcess());
//		addExpandableItem(items, itemsNames[5], product.getDrinkCategory().getDescription());
        addExpandableItem(items, itemsNames[5], product.getVineyard());
        return items;
    }

    private void addExpandableItem(List<ProductContent> listItems, String itemName, String itemContent) {
        if (!TextUtils.isEmpty(itemContent)) {
            listItems.add(new ProductContent(itemName, itemContent));
        }
    }

    private void populateFormsFields(View formView, final Item product) {
        String priceLabel = Utils.organizePriceLabel(String.valueOf(product.getPrice()));
        ((Button) formView.findViewById(R.id.add_to_shopping_cart_butt)).setText(product.hasPrice() ? priceLabel
                : EMPTY_PRICE_LABEL);
        animateText = (TextView) formView.findViewById(R.id.add_to_shopping_cart_animate);
        animateText.setText(product.hasPrice() ? priceLabel : EMPTY_PRICE_LABEL);
        animateText.setVisibility(View.INVISIBLE);
        ((TextView) formView.findViewById(R.id.product_manufacturer)).setText(product.getManufacturer());
        ((TextView) formView.findViewById(R.id.product_name)).setText(product.getName());
        ((TextView) formView.findViewById(R.id.product_localizated_manufacturer)).setText(product
                .getLocalizedManufacturer());
        if (product.getDrinkCategory() == DrinkCategory.WATER) {
            if ("ГАЗ".equals(product.getClassification())) {
                ((TextView) formView.findViewById(R.id.product_localizated_name)).setText(getString(R.string.aerated));
            } else if ("НЕ_ГАЗ".equals(product.getClassification())) {
                ((TextView) formView.findViewById(R.id.product_localizated_name)).setText(getString(R.string.non_aerated));
            }

            String singleItemPriceLabel = Utils.organizePriceLabel(String.valueOf(product.getPrice() / product.getQuantity()));
            TextView singleItemPriceTextView = (TextView) formView.findViewById(R.id.product_single_itme_price);
            singleItemPriceTextView.setVisibility(View.VISIBLE);
            singleItemPriceTextView.setText(String.format(getString(R.string.single_item_price), singleItemPriceLabel));
        } else {
            ((TextView) formView.findViewById(R.id.product_localizated_name)).setText(product
                    .getLocalizedName());
        }

        ((TextView) formView.findViewById(R.id.product_item_id)).setText(product.getItemID());
        organizeTextView((TextView) formView.findViewById(R.id.product_region), !product.getRegion().equals("-") ? product.getRegion() : "");
        organizeTextView((TextView) formView.findViewById(R.id.product_sweetness), !product.getSweetness()
                .getDescription().isEmpty() ? product.getSweetness().getDescription() : "");
        organizeTextView((TextView) formView.findViewById(R.id.product_style), product.getStyle());
        organizeTextView((TextView) formView.findViewById(R.id.product_grapes), product.getGrapesUsed());
        organizeTextView(
                (TextView) formView.findViewById(R.id.product_alcohol),
                product.hasAlcohol() ? Utils.organizeProductLabel(FORMAT_ALCOHOL,
                        trimTrailingZeros(product.getAlcohol())) : "");
        if (product.getDrinkCategory() == DrinkCategory.WATER) {
            String volumeLabel = String.format(getString(R.string.water_volume), trimTrailingZeros(String.valueOf(product.getQuantity())));
            organizeTextView((TextView) formView.findViewById(R.id.product_volume), volumeLabel);
        } else {
            String volumeLabel = "";
            String formatVolume = "%.0f x %s л.";
            if (product.getQuantity() != null && product.getQuantity() > 1) {
                volumeLabel = String.format(formatVolume, product.getQuantity(),
                        trimTrailingZeros(product.getVolume() + ""));
            } else {
                volumeLabel = Utils.organizeProductLabel(FORMAT_VOLUME,
                        trimTrailingZeros(product.getVolume() + ""));
            }
            organizeTextView((TextView) formView.findViewById(R.id.product_volume), volumeLabel);
        }
        organizeTextView((TextView) formView.findViewById(R.id.product_year), product.hasYear() ? String.valueOf(product.getYear()) : "");

        String strPriceLabel = takeRetailPrice(product) != null ? takeRetailPrice(product).toString() : "";
        TextView retailPrice = (TextView) formView.findViewById(R.id.retail_price);
        if (!TextUtils.isEmpty(strPriceLabel)) {
            retailPrice.setText(Utils.organizePriceLabel(getResources().getString(R.string.text_for_retail_price, strPriceLabel)));
        } else {
            retailPrice.setText(strPriceLabel);
        }
        TextView oldRetailPrice = (TextView) formView.findViewById(R.id.old_price_value);
        oldRetailPrice.setPaintFlags(oldRetailPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        LinearLayout oldPriceContainer = (LinearLayout) formView.findViewById(R.id.old_price_container);
        String originPriceLabel = Utils.organizePriceLabel(String.valueOf(product.getOrigin_price()));
        if (product.getDiscount() != null && !TextUtils.isEmpty(originPriceLabel) && product.getDiscount() != 0f) {
            oldPriceContainer.setVisibility(View.VISIBLE);
            oldRetailPrice.setText(originPriceLabel);
        } else {
            oldPriceContainer.setVisibility(View.GONE);
        }
        if (mProduct.getPrice() != null && mProduct.getPrice() != 0.0f) {
            retailPrice.setVisibility(View.VISIBLE);
        } else {
            retailPrice.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(product.getBottleHiResolutionImageFilename())) {
            ImageView mProductImage = (ProportionalImageView) formView.findViewById(R.id.product_image);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            String sizePrefix = null;
//            Toast.makeText(this, "dpi:" + metrics.densityDpi, Toast.LENGTH_LONG).show();
            switch (metrics.densityDpi) {
                case DisplayMetrics.DENSITY_MEDIUM: {
                    sizePrefix = "_mdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_TV: {
                    sizePrefix = "_mdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_HIGH: {
                    sizePrefix = "_hdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_280: {
                    sizePrefix = "_hdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_XHIGH: {
                    sizePrefix = "_xhdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_360: {
                    sizePrefix = "_xhdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_400: {
                    sizePrefix = "_xhdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_420: {
                    sizePrefix = "_xhdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_XXHIGH: {
                    sizePrefix = "_xxhdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_560: {
                    sizePrefix = "_xxhdpi";
                    break;
                }
                case DisplayMetrics.DENSITY_XXXHIGH: {
                    sizePrefix = "_xxhdpi";
                    break;
                }
                default: {
                    sizePrefix = "_hdpi";
                    break;
                }
            }
            imageLoader.displayImage(String.format("http://s1.isimpleapp.ru/img/ver0/%1$s%2$s_product.jpg",
                    product.getBottleHiResolutionImageFilename().replace('\\', '/'), sizePrefix), mProductImage, options, mImageLoadingListener);
            mProductImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent imageIntent = new Intent(ProductInfoActivity.this, ProductImageActivity.class);
                    imageIntent.putExtra(ProductImageActivity.HI_RESOLUTION_IMAGE_FILE_NAME, product.getBottleHiResolutionImageFilename());
                    startActivity(imageIntent);
                    overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
                }
            });
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

    private View.OnClickListener whereToBuyBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            whereToBuyClick();
        }

        private void whereToBuyClick() {
            Intent newIntent = new Intent(ProductInfoActivity.this, ShopsFragmentActivity.class);
            newIntent.putExtra(ShopsFragmentActivity.ITEM_PRODUCT_ID, mProduct.getItemID());
            startActivity(newIntent);
            overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        }
    };

    private View.OnClickListener addToShoppingCartBtnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            addToShoppingCartClick();
        }

        private void addToShoppingCartClick() {
            // boolean isProductExistShoppingCart =
            // proxyManager.isProductExistShoppingCart(itemId);
            if (mIsProductExistShoppingCart) {
                proxyManager.increaseShopCardItemCount(itemId);
            } else {
                proxyManager.insertProductInShoppingCart(mProduct);
                mIsProductExistShoppingCart = true;
            }
            ((ISimpleApp) getApplication()).setActiveCartState();
            animateText.setVisibility(View.VISIBLE);
            animateText.startAnimation(createTranslateAnimation());
        }

        private Animation.AnimationListener translateAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animateText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        private Animation createTranslateAnimation() {
            Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -10000.0f);
            translateAnimation.setDuration(5000);
            translateAnimation.setAnimationListener(translateAnimationListener);
            return translateAnimation;
        }
    };

    private SimpleImageLoadingListener mImageLoadingListener = new SimpleImageLoadingListener() {

        @Override
        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            mBitMap = bitmap;
//            int viewW = view.getWidth();
//            int viewH = view.getHeight();
//            if (viewH != 0 && viewW != 0) {
//                double coef = (double) viewH / bitmap.getHeight();
//                view.setMinimumWidth((int) (bitmap.getWidth() * coef));
//            }
        }
    };

}