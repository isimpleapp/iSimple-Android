package com.treelev.isimple.parser;

import android.text.TextUtils;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.DeliveryZoneDAO;
import com.treelev.isimple.domain.db.DeliveryZone;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeliveryZoneParser implements Parser {

    public final static int DELIVERY_ZONE_PARSER_ID = 7;

    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            DeliveryZone deliveryZone;
            List<DeliveryZone> deliveryZones = new ArrayList<DeliveryZone>();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals("delivery_zone")) {
                    deliveryZone = new DeliveryZone();
                    xmlPullParser.next();
                    while (!"delivery_zone".equals(xmlPullParser.getName())) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals("name")) {
                                if (deliveryZone.getName() == null) {
                                    deliveryZone.setName(xmlPullParser.nextText());
                                }
                            } else if (xmlPullParser.getName().equals("pickup_condition")) {
                                deliveryZone.setPickupCondition(Integer.parseInt(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals("pickup_desc")) {
                                deliveryZone.setPickupDesc(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals("delivery_condition")) {
                                deliveryZone.setDeliveryCondition(Integer.parseInt(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals("delivery_desc")) {
                                deliveryZone.setDeliveryDesc(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals("special_condition")) {
                                deliveryZone.setSpecialCondition(Integer.parseInt(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals("special_desc")) {
                                deliveryZone.setSpecialDesc(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals("pickup_location")) {
                                xmlPullParser.next();
                                while(!"pickup_location".equals(xmlPullParser.getName())){
                                    if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                        if(xmlPullParser.getName().equals("address")){
                                            deliveryZone.setAddress(xmlPullParser.nextText());
                                        } else if(xmlPullParser.getName().equals("location")) {
                                            String locationStr = xmlPullParser.nextText();
                                            String locationArray[] = locationStr.split(",");
                                            deliveryZone.setLatitude(Float.valueOf(locationArray[0]));
                                            deliveryZone.setLongitude(Float.valueOf(locationArray[1]));
                                        }
                                    }
                                    xmlPullParser.next();
                                }
                            }
                        }
                        xmlPullParser.next();
                    }
                    deliveryZones.add(deliveryZone);
                }
                xmlPullParser.next();
            }
            DeliveryZoneDAO deliveryZoneDAO = (DeliveryZoneDAO) daoList[0];
            deliveryZoneDAO.deleteAllData();
            deliveryZoneDAO.insertListData(deliveryZones);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static String getFileName() {
		String name = SharedPreferencesManager.getUpdateFileName(UpdateFile.DELIVERY.getUpdateFileTag()); 
		return TextUtils.isEmpty(name) ? "Delivery.xml" : name;
	}
}
