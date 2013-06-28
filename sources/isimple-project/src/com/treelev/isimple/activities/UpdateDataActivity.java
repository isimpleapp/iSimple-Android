package com.treelev.isimple.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.domain.FileParseObject;
import com.treelev.isimple.service.UpdateDataService;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateDataActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new View(this);
        view.setBackgroundColor(Color.WHITE);
        view.getBackground().setAlpha(150);
        setContentView(view);
        File directory = new File(String.format(UpdateDataService.FILE_URL_FORMAT, Environment.getExternalStorageDirectory()));
        List<FileParseObject> fileParseObjectList = createFileList(directory.listFiles());
        new UpdateDataTask().execute(fileParseObjectList.toArray(new FileParseObject[fileParseObjectList.size()]));
    }

    private List<FileParseObject> createFileList(File[] fileList) {
        List<FileParseObject> fileParseObjectList = new ArrayList<FileParseObject>();
        for (File file : fileList) {
            fileParseObjectList.add(new FileParseObject(file, this));
        }
        Collections.sort(fileParseObjectList);
        return fileParseObjectList;
    }

    private class UpdateDataTask extends AsyncTask<FileParseObject, Void, Void> {

        private Dialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UpdateDataActivity.this, "Обновление данных iSimple", "Пожалуйста, подождите..", false, false);
        }

        @Override
        protected Void doInBackground(FileParseObject... fileParseObjects) {
            for (FileParseObject fileParseObject : fileParseObjects) {
                fileParseObject.parseObjectDataToDB();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Intent newIntent = new Intent(UpdateDataActivity.this, CatalogListActivity.class);
            newIntent.putExtra(UpdateDataService.NEED_DATA_UPDATE, false);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
        }

        private BaseDAO[] getDAOListByIndex(int index) {
            BaseDAO[] customDAOList = null;
            /*switch (index) {
                case 0:
                case 3:
                case 4:
                    customDAOList = new BaseDAO[]{daoList[0]};
                    break;
                case 1:
                    customDAOList = new BaseDAO[]{daoList[1], daoList[2]};
                    break;
                case 2:
                    customDAOList = new BaseDAO[]{daoList[3]};
                    break;
                case 5:
                    customDAOList = new BaseDAO[]{daoList[4]};
                    break;
                case 6:
                    customDAOList = new BaseDAO[]{daoList[5]};
                    break;
            }*/
            return customDAOList;
        }

        private int getParserIdByIndex(String fileName) {
            /*switch (index) {
                case 0:
                    return CatalogParser.CATALOG_PARSER_ID;
                case 1:
                    return ShopAndChainsParser.SHOP_AND_CHAINS_PARSER_ID;
                case 2:
                    return ItemAvailabilityParser.ITEM_AVAILABILITY_PARSER_ID;
                case 3:
                    return ItemPricesParser.ITEM_PRICES_PARSER_ID;
                case 4:
                    return FeaturedItemsParser.FEATURED_ITEMS_PARSER_ID;
                case 5:
                    return DeprecatedItemParser.DEPRECATED_ITEMS_PARSER_ID;
                case 6:
                    return DeliveryZoneParser.DELIVERY_ZONE_PARSER_ID;
                default:
                    return -1;
            }*/
            return -1;
        }
    }
}