package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.domain.db.ItemPrice;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemPricesParser implements Parser {

    public final static int ITEM_PRICES_PARSER_ID = 1;
    private final static String ITEM_PRICE_OBJECT_TAG = "ItemPrice";
    private final static String ITEM_ID_VALUE_TAG = "ItemID";
    private final static String ITEM_PRICE_VALUE_TAG = "Price";
    private final static String ITEM_PRICE_MARKUP_VALUE_TAG = "PriceMarkup";

    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO...daoList) {
        try {
            ItemPrice itemPrice;
            List<ItemPrice> itemPriceList = new ArrayList<ItemPrice>();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG &&
                        xmlPullParser.getName().equals(ITEM_PRICE_OBJECT_TAG)) {
                    itemPrice = new ItemPrice();
                    xmlPullParser.next();
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG &&
                            !xmlPullParser.getName().equals(ITEM_PRICE_OBJECT_TAG)) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals(ITEM_ID_VALUE_TAG)) {
                                itemPrice.setItemId(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(ITEM_PRICE_VALUE_TAG)) {
                                itemPrice.setPrice(Integer.parseInt(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(ITEM_PRICE_MARKUP_VALUE_TAG)) {
                                itemPrice.setPriceMarkup(Integer.parseInt(xmlPullParser.nextText()));
                            } else {
                                xmlPullParser.nextText();
                            }
                        }
                        xmlPullParser.next();
                    }
                    itemPriceList.add(itemPrice);
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
