package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.domain.db.ItemPrice;
import com.treelev.isimple.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemPricesParser implements Parser {

    public final static int ITEM_PRICES_PARSER_ID = 1;
    public final static String FILE_NAME = "Item-Prices.xml";

    private final static String ITEM_PRICE_OBJECT_TAG = "ItemPrice";
    private final static String ITEM_ID_VALUE_TAG = "ItemID";
    private final static String ITEM_PRICE_VALUE_TAG = "Price";
    private final static String ITEM_PRICE_MARKUP_VALUE_TAG = "PriceMarkup";

    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO...daoList) {
        try {
            List<ItemPrice> itemPriceList = new ArrayList<ItemPrice>();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG &&
                        xmlPullParser.getName().equals(ITEM_PRICE_OBJECT_TAG)) {
                    xmlPullParser.next();
                    String itemID = null;
                    float price = -1f;
                    float priceMarkup = -1f;
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG &&
                            !xmlPullParser.getName().equals(ITEM_PRICE_OBJECT_TAG)) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals(ITEM_ID_VALUE_TAG)) {
                                itemID = xmlPullParser.nextText();
                            } else if (xmlPullParser.getName().equals(ITEM_PRICE_VALUE_TAG)) {
                                price = Utils.parseFloat(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(ITEM_PRICE_MARKUP_VALUE_TAG)) {
                                priceMarkup = Utils.parseFloat(xmlPullParser.nextText());
                            } else {
                                xmlPullParser.nextText();
                            }
                        }
                        xmlPullParser.next();
                    }
                    if (itemID != null) {
                        itemPriceList.add(new ItemPrice(itemID, price, priceMarkup));
                    }
                }
                xmlPullParser.next();
            }
            ((ItemDAO) daoList[0]).updatePriceList(itemPriceList);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
