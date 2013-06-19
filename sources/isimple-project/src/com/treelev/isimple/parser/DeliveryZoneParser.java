package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.DeliveryZoneDAO;
import com.treelev.isimple.domain.db.DeliveryZone;
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
                            }
                        }
                        xmlPullParser.next();
                    }
                    deliveryZones.add(deliveryZone);
                }
                xmlPullParser.next();
            }
            ((DeliveryZoneDAO) daoList[0]).insertListData(deliveryZones);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
