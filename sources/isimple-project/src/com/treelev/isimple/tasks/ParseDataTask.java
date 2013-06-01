package com.treelev.isimple.tasks;

import android.os.AsyncTask;
import android.widget.TextView;
import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.parser.*;
import com.treelev.isimple.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ParseDataTask extends AsyncTask<File, Void, Void> {

    private TextView startLoad;
    private TextView endLoad;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private BaseDAO[] daoList;

    public ParseDataTask(TextView startLoad, TextView endLoad, BaseDAO[] daoList) {
        this.startLoad = startLoad;
        this.endLoad = endLoad;
        this.daoList = daoList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startLoad.setText("Начало загрузки данных: " + dateFormat.format(Calendar.getInstance().getTime()));
    }

    @Override
    protected Void doInBackground(File... files) {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new FileInputStream(file), null);
                Utils.getXmlParser(getParserIdByIndex(i)).parseXmlToDB(xmlPullParser, getDAOListByIndex(i));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private BaseDAO[] getDAOListByIndex(int index) {
        BaseDAO[] customDAOList = null;
        switch (index) {
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
        }
        return customDAOList;
    }

    private int getParserIdByIndex(int index) {
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
            case 5:
                return DeprecatedItemParser.DEPRECATED_ITEMS_PARSER_ID;
            default:
                return -1;
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        endLoad.setText("Данные загружены в базу: " + dateFormat.format(Calendar.getInstance().getTime()));
    }
}
