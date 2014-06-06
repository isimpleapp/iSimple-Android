package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ChainDAO;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.domain.db.Chain;
import com.treelev.isimple.domain.db.Shop;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.enumerable.chain.ChainType;
import com.treelev.isimple.enumerable.location.LocationType;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopAndChainsParser implements Parser {

    public final static int SHOP_AND_CHAINS_PARSER_ID = 2;

    private final static String CHAIN_OBJECT_TAG = "Chain";
    private final static String CHAIN_ID_VALUE_TAG = "ChainID";
    private final static String CHAIN_NAME_VALUE_TAG = "Name";
    private final static String CHAIN_TYPE_VALUE_TAG = "ChainType";

    private final static String LOCATION_OBJECT_TAG = "Location";
    private final static String LOCATION_ID_VALUE_TAG = "LocationID";
    private final static String LOCATION_CUSTOMER_ID_VALUE_TAG = "CustomerID";
    private final static String LOCATION_SHIP_TO_CODE_ID_VALUE_TAG = "ShiptoCodeID";
    private final static String LOCATION_NAME_VALUE_TAG = "Name";
    private final static String LOCATION_ADDRESS_VALUE_TAG = "Address";
    private final static String LOCATION_LONGITUDE_VALUE_TAG = "Longitude";
    private final static String LOCATION_LATITUDE_VALUE_TAG = "Latitude";
    private final static String LOCATION_WORKING_HOURS_VALUE_TAG = "WorkingHours";
    private final static String LOCATION_PHONE_NUMBER_VALUE_TAG = "PhoneNumber";
    private final static String LOCATION_CHAIN_ID_VALUE_TAG = "ChainID";
    private final static String LOCATION_TYPE_VALUE_TAG = "LocationType";
    private final static String LOCATION_PRESENCE_PERCENTAGE_VALUE_TAG = "PresencePercentage";

    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO...daoList) {
        try {
            Shop shop;
            Chain chain;
            String tempStr;
            List<Chain> chainList = new ArrayList<Chain>();
            List<Shop> locationList = new ArrayList<Shop>();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                    if (xmlPullParser.getName().equals(CHAIN_OBJECT_TAG)) {
                        chain = new Chain();
                        xmlPullParser.next();
                        while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(CHAIN_OBJECT_TAG)) {
                            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                if (xmlPullParser.getName().equals(CHAIN_ID_VALUE_TAG)) {
                                    chain.setChainID(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(CHAIN_NAME_VALUE_TAG)) {
                                    chain.setChainName(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(CHAIN_TYPE_VALUE_TAG)) {
                                    chain.setChainType(ChainType.getChainType(Utils.parseInteger(xmlPullParser.nextText())));
                                } else {
                                    xmlPullParser.nextText();
                                }
                            }
                            xmlPullParser.next();
                        }
                        chainList.add(chain);
                    } else if (xmlPullParser.getName().equals(LOCATION_OBJECT_TAG)) {
                        shop = new Shop();
                        xmlPullParser.next();
                        while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !LOCATION_OBJECT_TAG.equals(xmlPullParser.getName())) {
                            int eventType = xmlPullParser.getEventType();
                            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                if (xmlPullParser.getName().equals(LOCATION_ID_VALUE_TAG)) {
                                    shop.setLocationID(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(LOCATION_NAME_VALUE_TAG)) {
                                    shop.setLocationName(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(LOCATION_ADDRESS_VALUE_TAG)) {
                                    shop.setLocationAddress(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(LOCATION_LONGITUDE_VALUE_TAG)) {
                                    shop.setLongitude(Float.parseFloat(xmlPullParser.nextText()));
                                } else if (xmlPullParser.getName().equals(LOCATION_LATITUDE_VALUE_TAG)) {
                                    shop.setLatitude(Float.parseFloat(xmlPullParser.nextText()));
                                } else if (xmlPullParser.getName().equals(LOCATION_WORKING_HOURS_VALUE_TAG)) {
                                    shop.setWorkingHours(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(LOCATION_PHONE_NUMBER_VALUE_TAG)) {
                                    shop.setPhoneNumber(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(LOCATION_CHAIN_ID_VALUE_TAG)) {
                                    shop.setChainID(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(LOCATION_TYPE_VALUE_TAG)) {
                                    shop.setLocationType(LocationType.getLocationType(Utils.parseInteger(xmlPullParser.nextText())));
                                } else if (xmlPullParser.getName().equals(LOCATION_PRESENCE_PERCENTAGE_VALUE_TAG)) {
                                    shop.setPresencePercentage(Utils.parseFloat(xmlPullParser.nextText()));
                                } else {
                                    xmlPullParser.nextText();
                                }
                            }
                            xmlPullParser.next();
                        }
                        locationList.add(shop);
                    }
                }
                xmlPullParser.next();
            }
            ChainDAO chainDAO = (ChainDAO) daoList[0];
            chainDAO.deleteAllData();
            chainDAO.insertListData(chainList);
            ShopDAO shopDAO = (ShopDAO) daoList[1];
            shopDAO.deleteAllData();
            shopDAO.insertListData(locationList);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	public static String getFileName() {
		return SharedPreferencesManager.getUpdateFileName(UpdateFile.LOCATIONS_AND_CHAINS_UPDATES.getUpdateFileTag());
	}
}
