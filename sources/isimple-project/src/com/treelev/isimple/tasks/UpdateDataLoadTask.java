package com.treelev.isimple.tasks;

import android.os.AsyncTask;
import android.widget.TextView;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.parser.*;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.UnzipManager;
import com.treelev.isimple.utils.managers.WebServiceManager;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UpdateDataLoadTask extends AsyncTask<String, Void, Void> {

    private BaseDAO[] daoList;
    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public UpdateDataLoadTask(BaseDAO[] daoList, TextView textView, TextView textView1, TextView textView2, TextView textView3) {
        this.daoList = daoList;
        this.textView = textView;
        this.textView1 = textView1;
        this.textView2 = textView2;
        this.textView3 = textView3;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        textView.setText("Начало: " + dateFormat.format(Calendar.getInstance().getTime()));
        textView1.setText(calculateDataTableTasks());
    }

    @Override
    protected Void doInBackground(String... params) {
        for (int i = 0; i < params.length; i++) {
            File downloadFile;
            try {
                downloadFile = new WebServiceManager().downloadFile(params[i]).getDownloadedFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            XmlPullParser xmlPullParser = new UnzipManager().unZipFile(downloadFile);
            Utils.getXmlParser(getParserIdByIndex(i)).parseXmlToDB(xmlPullParser, getDAOListByIndex(i));
        }
        return null;
    }

    private BaseDAO[] getDAOListByIndex(int index) {
        BaseDAO[] customDAOList = null;
        switch (index) {
            case 0:
            case 3:
                customDAOList = new BaseDAO[]{daoList[0]};
                break;
            case 1:
                customDAOList = new BaseDAO[]{daoList[1], daoList[2]};
                break;
            case 2:
                customDAOList = new BaseDAO[]{daoList[3]};
                break;
        }
        return customDAOList;
    }

    private Integer getParserIdByIndex(int index) {
        switch (index) {
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
            default:
                return null;
        }
    }

    private String calculateDataTableTasks() {
        StringBuilder stringBuilder = new StringBuilder();
        /*for (BaseDAO dao : daoList) {
            stringBuilder.append(dao.getClassName()).append(" ").append(dao.getTableDataCount()).append(" ");
        }*/
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        textView2.setText("Окончание: " + dateFormat.format(Calendar.getInstance().getTime()));
        textView3.setText(calculateDataTableTasks());
    }
}
