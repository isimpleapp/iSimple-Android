package com.treelev.isimple.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.SearchResult;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 24.04.13
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
public class Search extends AsyncTask<String, Void, List<Item>> {

    private Dialog mDialog;
    private Context mContext;
    private ProxyManager mProxyManager;
    private Integer mCategoryId;

    public Search(Context context, Integer categoryId) {
        mContext = context;
        mCategoryId = categoryId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_search_title),
                mContext.getString(R.string.dialog_search_message), false, false);
    }

    @Override
    protected List<Item> doInBackground(String... strings) {
        mProxyManager = new ProxyManager(mContext);
        return mProxyManager.getSearchItemsByCategory(mCategoryId, strings[0]);
    }

    @Override
    protected void onPostExecute(List<Item> result) {
        super.onPostExecute(result);
        mDialog.dismiss();
        Intent newIntent = new Intent(mContext, SearchResult.class);
        newIntent.putExtra(SearchResult.LIST_RESULT, (ArrayList<Item>)result);
        mContext.startActivity(newIntent);
    }
}
