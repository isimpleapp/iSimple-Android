package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.domain.db.FeaturedItem;
import com.treelev.isimple.domain.db.ItemPrice;
import com.treelev.isimple.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeaturedItemsParser implements Parser {

    public final static int FEATURED_ITEMS_PARSER_ID = 5;
    public final static String ITEM_ID_TAG = "ItemID";

    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO...daoList) {
        try {
            List<FeaturedItem> featuredList = new ArrayList<FeaturedItem>();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                    String tagName = xmlPullParser.getName();
                    if (tagName.equalsIgnoreCase(FeaturedItem.MAIN_CATEGORY_TAG) ||
                        tagName.equalsIgnoreCase(FeaturedItem.WINE_CATEGORY_TAG) ||
                        tagName.equalsIgnoreCase(FeaturedItem.ALCO_CATEGORY_TAG) ||
                        tagName.equalsIgnoreCase(FeaturedItem.CHAMPA_CATEGORY_TAG) ||
                        tagName.equalsIgnoreCase(FeaturedItem.PORTO_CATEGORY_TAG) ||
                        tagName.equalsIgnoreCase(FeaturedItem.SAKE_CATEGORY_TAG) ||
                        tagName.equalsIgnoreCase(FeaturedItem.WATER_CATEGORY_TAG)) {
                        xmlPullParser.next();
                        while (xmlPullParser.getEventType() != XmlPullParser.END_TAG &&
                                !xmlPullParser.getName().equals(ITEM_ID_TAG)) {
                            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                if (xmlPullParser.getName().equals(ITEM_ID_TAG)) {
                                    featuredList.add(new FeaturedItem(xmlPullParser.nextText(), tagName));
                                } else {
                                    xmlPullParser.nextText();
                                }
                            }
                            xmlPullParser.next();
                        }
                    }
                    xmlPullParser.next();
                }
            }
            ((ItemDAO) daoList[0]).insertListFeaturedData(featuredList);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
