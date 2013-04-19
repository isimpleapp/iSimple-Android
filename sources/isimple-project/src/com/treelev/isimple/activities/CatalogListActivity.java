package com.treelev.isimple.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.apache.http.util.ByteArrayBuffer;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class CatalogListActivity extends ListActivity {

    public final static String CATEGORY_NAME_EXTRA_ID = "category_name";

    @Override
    protected void onCreate(Bundle sSavedInstanceState) {
        super.onCreate(sSavedInstanceState);
        setContentView(R.layout.catalog_list_layout);
        ListView listView = getListView();
        View headerView = getLayoutInflater().inflate(R.layout.catalog_list_header_view, listView, false);
        listView.addHeaderView(headerView, null, false);
        ProxyManager proxyManager = new ProxyManager(this);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, proxyManager.getRandomItems(), R.layout.catalog_item_layout,
                Item.getUITags(), new int[]{R.id.item_image, R.id.item_name, R.id.item_loc_name, R.id.item_drink_type,
                R.id.item_volume, R.id.item_price});
        //simpleAdapter.setViewBinder(new ImageBinder(this));
        listView.setAdapter(simpleAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HashMap product = (HashMap) l.getAdapter().getItem(position);
        Intent startIntent = new Intent(this, ProductInfoActivity.class);
        startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG, (String) product.get(Item.UI_TAG_ID));
        startActivity(startIntent);
    }

    public void onClickCategoryButt(View v) {
        Intent startIntent = new Intent(getApplicationContext(), CatalogByCategoryActivity.class);
        startIntent.putExtra(CATEGORY_NAME_EXTRA_ID, v.getId());
        startActivity(startIntent);
    }

    private static class ImageBinder implements SimpleAdapter.ViewBinder {

        private Context context;

        private ImageBinder(Context context) {
            this.context = context;
        }

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            boolean result = false;
            if (view.getId() == R.id.item_image) {
                Bitmap bitmap;
                File tempFile = null;
                try {
                    tempFile = new File("/sdcard/temp.jpg");
                    URL imageUrl = new URL((String) data);
                    URLConnection conn = imageUrl.openConnection();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(50);
                    int current;
                    while ((current = bis.read()) != -1) {
                        byteArrayBuffer.append((byte) current);
                    }
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(byteArrayBuffer.toByteArray());
                    fos.close();
                    ((ImageView) view).setImageBitmap(BitmapFactory.decodeFile(tempFile.getPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                    ((ImageView) view).setImageDrawable(context.getResources().getDrawable(R.drawable.image_not_found));
                } finally {
                    if (tempFile != null) {
                        tempFile.delete();
                    }
                }
                result = true;
            }
            return result;
        }
    }
}