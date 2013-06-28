package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ItemAvailabilityDAO;
import com.treelev.isimple.domain.db.ItemAvailability;
import com.treelev.isimple.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemAvailabilityParser implements Parser {

    public final static int ITEM_AVAILABILITY_PARSER_ID = 4;
    public final static String FILE_NAME = "Item-Availability.xml";

    private final static String ITEM_AVAILABILITY_OBJECT_TAG = "Item";
    private final static String ITEM_AVAILABILITY_ID_VALUE_TAG = "ItemID";
    private final static String ITEM_AVAILABILITY_LOCATION_ID_VALUE_TAG = "LocationID";
    private final static String ITEM_AVAILABILITY_CUSTOMER_ID_VALUE_TAG = "CustomerID";
    private final static String ITEM_AVAILABILITY_SHIPTO_CODE_ID_VALUE_TAG = "ShiptoCodeID";
    private final static String ITEM_AVAILABILITY_PRICE_VALUE_TAG = "Price";

    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            ItemAvailability itemAvailability;
            List<ItemAvailability> itemAvailabilityList = new ArrayList<ItemAvailability>();
            ItemAvailabilityDAO itemAvailabilityDAO = (ItemAvailabilityDAO) daoList[0];
            //itemAvailabilityDAO.deleteAllData();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(ITEM_AVAILABILITY_OBJECT_TAG)) {
                    itemAvailability = new ItemAvailability();
                    xmlPullParser.next();
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !ITEM_AVAILABILITY_OBJECT_TAG.equals(xmlPullParser.getName())) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals(ITEM_AVAILABILITY_ID_VALUE_TAG)) {
                                itemAvailability.setItemID(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(ITEM_AVAILABILITY_LOCATION_ID_VALUE_TAG)) {
                                itemAvailability.setLocationID(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(ITEM_AVAILABILITY_CUSTOMER_ID_VALUE_TAG)) {
                                itemAvailability.setCustomerID(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(ITEM_AVAILABILITY_SHIPTO_CODE_ID_VALUE_TAG)) {
                                itemAvailability.setShiptoCodeID(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(ITEM_AVAILABILITY_PRICE_VALUE_TAG)) {
                                itemAvailability.setPrice(Utils.parseFloat(xmlPullParser.nextText()));
                            } else {
                                xmlPullParser.nextText();
                            }
                        }
                        xmlPullParser.next();
                    }
                    itemAvailabilityList.add(itemAvailability);
                }
                xmlPullParser.next();
            }
            itemAvailabilityDAO.deleteAllData();
            itemAvailabilityDAO.insertListData(itemAvailabilityList);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
