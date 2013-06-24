package com.treelev.isimple.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ShoppingCartCursorAdapter;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.fragments.OrderDialogFragment;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

public class ShoppingCartActivity extends BaseListActivity implements View.OnClickListener {

    public final static int NAVIGATE_CATEGORY_ID = 3;
    public final static String PRICE_LABEL_FORMAT = "%s р.";
    private ProxyManager proxyManager;
    private View footerView;
    private String[] countries;
    public final static String COUNTRY_LABEL = "country";
    private boolean mResultSendOrders;
    private boolean mIsSaveInstancceState;
    private boolean mSendOrders;


    private OrderDialogFragment dlgMakeOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_layout);
        setCurrentCategory(NAVIGATE_CATEGORY_ID);
        createNavigationMenuBar();
        proxyManager = new ProxyManager(this);
        getListView().addFooterView(organizeFooterView());
        dlgMakeOrder = new OrderDialogFragment(OrderDialogFragment.SELECT_TYPE);

        new SelectDataShoppingCartTask(this).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mIsSaveInstancceState){
            if(mResultSendOrders){
                OrderDialogFragment dialog = new OrderDialogFragment(OrderDialogFragment.SUCCESS_TYPE);
                dialog.setSuccess(mResultSendOrders);
                dialog.show(getSupportFragmentManager(), "SUCCESS_TYPE");
            }
        }
        mIsSaveInstancceState = false;
    }

    public void setResultSendOrders(boolean resultSendOrders){
        mResultSendOrders = resultSendOrders;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    @Override
    protected void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shopping_cart);
    }

    public void clickCatalogButton(View view) {
        getSupportActionBar().setSelectedNavigationItem(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_all_btn:
                proxyManager.deleteAllShoppingCartData();
                Cursor cursor = ((CursorAdapter) getListView().getAdapterSource()).getCursor();
                if (cursor != null) {
                    cursor.requery();
                    if (cursor.getCount() == 0) {
                        findViewById(R.id.content_layout).setVisibility(View.GONE);
                        findViewById(R.id.empty_shopping_list_view).setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.delivery_btn:
                org.holoeverywhere.app.AlertDialog.Builder builder = new org.holoeverywhere.app.AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.delivery_dialog_title));
                countries = proxyManager.getCountries();
                builder.setItems(proxyManager.getCountries(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String country = countries[which];
                        putCountryInPref(country);
                        TextView shoppingCartFooterTextView = (TextView) footerView.findViewById(R.id.footer_view_label);
                        int shoppingCartPrice = proxyManager.getShoppingCartPrice();
                        shoppingCartFooterTextView.setText(proxyManager.getDeliveryMessage(country, shoppingCartPrice));
                        organizeCreateOrderButton(shoppingCartPrice);
                        Button button = (Button) footerView.findViewById(R.id.delivery_btn);
                        button.setText(country);
                    }
                });
                builder.show();
                break;
        }
    }

    private View organizeFooterView() {
        footerView = getLayoutInflater().inflate(R.layout.shopping_cart_list_footer_layout, null, false);
        Button button = (Button) footerView.findViewById(R.id.delete_all_btn);
        button.setOnClickListener(this);
        button = (Button) footerView.findViewById(R.id.delivery_btn);
        String country = getPreferences(MODE_PRIVATE).getString(COUNTRY_LABEL, null);
        if (country == null) {
            country = proxyManager.getDeliveryFirstCountry();
            putCountryInPref(country);
        }
        button.setText(country);
        button.setOnClickListener(this);
        return footerView;
    }

    private void putCountryInPref(String country) {
        SharedPreferences sharedPreference = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(COUNTRY_LABEL, country);
        editor.commit();
    }

    private class SelectDataShoppingCartTask extends AsyncTask<Void, Void, Cursor> {

        private Dialog mDialog;
        private Context mContext;
        private ProxyManager proxyManager;

        private SelectDataShoppingCartTask(Context context) {
            mContext = context;
            proxyManager = new ProxyManager(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.dialog_select_data_message), false, false);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return proxyManager.getShoppingCartItems();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            Cursor cItems = cursor;
            startManagingCursor(cItems);
            TextView shoppingCartPriceTextView = (TextView) findViewById(R.id.shopping_cart_price);
            TextView shoppingCartFooterTextView = (TextView) footerView.findViewById(R.id.footer_view_label);
            String country = getPreferences(MODE_PRIVATE).getString(COUNTRY_LABEL, "");
            CursorAdapter mListCategoriesAdapter = new ShoppingCartCursorAdapter(mContext, cItems, shoppingCartPriceTextView, shoppingCartFooterTextView);
            int shoppingCartPrice = 0;
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        int count = cursor.getInt(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_SHOPPING_CART_COUNT));
                        int itemPrice = cursor.getInt(cursor.getColumnIndex(DatabaseSqlHelper.ITEM_PRICE));
                        shoppingCartPrice += itemPrice * count;
                    } while (cursor.moveToNext());
                }
                cursor.moveToFirst();
            }
            shoppingCartFooterTextView.setText(proxyManager.getDeliveryMessage(country, shoppingCartPrice));
            shoppingCartPriceTextView.setText(String.format(PRICE_LABEL_FORMAT, shoppingCartPrice));
            organizeCreateOrderButton(shoppingCartPrice);
            getListView().setAdapter(mListCategoriesAdapter);
            if (cursor != null && cursor.getCount() == 0) {
                findViewById(R.id.content_layout).setVisibility(View.GONE);
                findViewById(R.id.empty_shopping_list_view).setVisibility(View.VISIBLE);
            }
            mDialog.dismiss();
        }
    }

    public void organizeCreateOrderButton(int shoppingCartPrice) {
        int minPrice = proxyManager.getMinPriceByCountry(getPreferences(MODE_PRIVATE).getString(COUNTRY_LABEL, ""));
        Button button = (Button) findViewById(R.id.create_order_btn);
        if (shoppingCartPrice >= minPrice) {
            button.setBackgroundColor(getResources().getColor(R.color.product_price_color));
            button.setClickable(true);
        } else {
            button.setBackgroundColor(Color.GRAY);
            button.setClickable(false);
        }
    }

    public void updateList(){
        new SelectDataShoppingCartTask(this).execute();
    }

    public void onMakeOrder(View v) {
        int count = getListView().getCount();
        if( count > 0){
            String cs = Context.CONNECTIVITY_SERVICE;
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService(cs);
            NetworkInfo nInfoMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo nIfoWIFI = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(nInfoMobile.getState() == NetworkInfo.State.CONNECTED || nIfoWIFI.getState() == NetworkInfo.State.CONNECTED) {
                dlgMakeOrder.setArguments(new Bundle());
                dlgMakeOrder.show(getSupportFragmentManager(), "SELECT_TYPE");
            } else {
                Toast.makeText(this, getString(R.string.message_offline), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.message_empty_orders), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mIsSaveInstancceState = true;
    }

    public void sendOrderSetFlag(boolean flag){
        mSendOrders = flag;
    }
    public boolean isIsSaveInstancceState(){
        return  mIsSaveInstancceState;
    }
}