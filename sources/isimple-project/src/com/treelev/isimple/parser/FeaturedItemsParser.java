package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.domain.db.FeaturedItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeaturedItemsParser implements Parser {

    public final static int FEATURED_ITEMS_PARSER_ID = 5;
    public final static String FILE_NAME = "Featured.xml";
    public final static String FILE_SECOND_NAME = "Featured.xmlz";

    public final static String ITEM_ID_TAG = "ItemID";
    private final static String FEATURES_ROOT_TAG = "Features";

    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            List<FeaturedItem> featuredList = new ArrayList<FeaturedItem>();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(FEATURES_ROOT_TAG)) {
                    xmlPullParser.next();
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !FEATURES_ROOT_TAG.equals(xmlPullParser.getName())) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            String tagName = xmlPullParser.getName();
                            if (validateTagName(tagName)) {
                                xmlPullParser.next();
                                while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !tagName.equals(xmlPullParser.getName())) {
                                    if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(ITEM_ID_TAG)) {
                                        featuredList.add(new FeaturedItem(xmlPullParser.nextText(), tagName));
                                    }
                                    xmlPullParser.next();
                                }
                            }
                        }
                        xmlPullParser.next();
                    }
                }
                xmlPullParser.next();
            }
            ItemDAO itemDAO = (ItemDAO) daoList[0];
            itemDAO.deleteAllFeaturedItemData();
            itemDAO.insertListFeaturedData(featuredList);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateTagName(String tagName) {
        return tagName.equalsIgnoreCase(FeaturedItem.MAIN_CATEGORY_TAG) ||
                tagName.equalsIgnoreCase(FeaturedItem.WINE_CATEGORY_TAG) ||
                tagName.equalsIgnoreCase(FeaturedItem.ALCO_CATEGORY_TAG) ||
                tagName.equalsIgnoreCase(FeaturedItem.CHAMPA_CATEGORY_TAG) ||
                tagName.equalsIgnoreCase(FeaturedItem.PORTO_CATEGORY_TAG) ||
                tagName.equalsIgnoreCase(FeaturedItem.SAKE_CATEGORY_TAG) ||
                tagName.equalsIgnoreCase(FeaturedItem.WATER_CATEGORY_TAG);
    }

}
