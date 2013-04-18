package com.treelev.isimple.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.parser.CatalogParser;
import com.treelev.isimple.parser.ItemAvailabilityParser;
import com.treelev.isimple.parser.ItemPricesParser;
import com.treelev.isimple.parser.ShopAndChainsParser;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.UnzipManager;
import com.treelev.isimple.utils.managers.WebServiceManager;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FirstDataLoadTask extends AsyncTask<String, Integer, Void> {

    private BaseDAO[] daoList;
    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private Button button;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public FirstDataLoadTask(BaseDAO[] daoList, TextView textView, TextView textView1, TextView textView2, TextView textView3, Button button) {
        this.daoList = daoList;
        this.textView = textView;
        this.textView1 = textView1;
        this.textView2 = textView2;
        this.textView3 = textView3;
        this.button = button;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        textView.setText("Начало: " + dateFormat.format(Calendar.getInstance().getTime()));
    }

    @Override
    protected Void doInBackground(String... params) {
        /*for (int i = 0; i < params.length; i++) {
            File downloadFile = new WebServiceManager().downloadFile(params[i]);
            XmlPullParser xmlPullParser = new UnzipManager().unZipFile(downloadFile);
            Utils.getXmlParser(getParserIdByIndex(i)).parseXmlToDB(xmlPullParser, getDAOListByIndex(i));
        }*/
        File downloadFile = new WebServiceManager().downloadFile(params[0]);
        XmlPullParser xmlPullParser = new UnzipManager().unZipFile(downloadFile);
        Utils.getXmlParser(getParserIdByIndex(2)).parseXmlToDB(xmlPullParser, getDAOListByIndex(2));
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
            default:
                return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] == 1) {
            textView1.setText("Окончание загрузки: " + dateFormat.format(Calendar.getInstance().getTime()));
        } else if (values[0] == 2) {
            textView2.setText("Окончание распаковки: " + dateFormat.format(Calendar.getInstance().getTime()));
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        textView3.setText("Окончание: " + dateFormat.format(Calendar.getInstance().getTime()));
        button.setVisibility(View.VISIBLE);
    }
}
