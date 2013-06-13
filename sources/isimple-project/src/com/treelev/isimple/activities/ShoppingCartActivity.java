package com.treelev.isimple.activities;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CursorAdapter;
import com.actionbarsherlock.view.MenuItem;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.ShoppingCartCursorAdapter;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;

public class ShoppingCartActivity extends BaseListActivity {

    public final static int NAVIGATE_CATEGORY_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_layout);
        setCurrentCategory(NAVIGATE_CATEGORY_ID);
        createNavigationMenuBar();
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
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.finish_show_anim, R.anim.finish_back_anim);
    }

    @Override
    protected void createNavigationMenuBar() {
        super.createNavigationMenuBar();
        getSupportActionBar().setIcon(R.drawable.menu_ico_shopping_cart);
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
            CursorAdapter mListCategoriesAdapter = new ShoppingCartCursorAdapter(mContext, cItems);
            getListView().setAdapter(mListCategoriesAdapter);
            mDialog.dismiss();
        }
    }
}