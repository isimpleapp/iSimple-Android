package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.DeliveryZoneDAO;
import com.treelev.isimple.domain.PickupLocation;
import com.treelev.isimple.domain.db.DeliveryZone;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeliveryZoneParser implements Parser {

    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            DeliveryZone deliveryZone;
            PickupLocation pickupLocation;
            List<DeliveryZone> deliveryZones = new ArrayList<DeliveryZone>();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals("delivery_zone")) {
                    deliveryZone = new DeliveryZone();
                    xmlPullParser.next();
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !"delivery_zone".equals(xmlPullParser.getName())) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals("name")) {
                                deliveryZone.setName(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals("pickup_cost")) {
                                deliveryZone.setPickupCost(Integer.parseInt(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals("pickup_condition")) {
                                deliveryZone.setPickupCondition(Integer.parseInt(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals("pickup_location")) {
                                pickupLocation = new PickupLocation();
                                xmlPullParser.next();
                                while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !"pickup_location".equals(xmlPullParser.getName())) {
                                    if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                        if (xmlPullParser.getName().equals("name")) {
                                            pickupLocation.setName(xmlPullParser.getName());
                                        } else if (xmlPullParser.getName().equals("address")) {
                                            pickupLocation.setAddress(xmlPullParser.getName());
                                        } else if (xmlPullParser.getName().equals("location")) {
                                            pickupLocation.setCoordinates(xmlPullParser.getName());
                                        }
                                    }
                                    xmlPullParser.next();
                                }
                                deliveryZone.setPickupLocation(pickupLocation);
                            } else if (xmlPullParser.getName().equals("pickup_desc")) {
                                deliveryZone.setPickupDesc(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals("delivery_cost")) {
                                deliveryZone.setDeliveryCost(Integer.parseInt(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals("delivery_condition")) {
                                deliveryZone.setDeliveryCondition(Integer.parseInt(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals("delivery_desc")) {
                                deliveryZone.setDeliveryDesc(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals("special_cost")) {
                                deliveryZone.setSpecialCost(Integer.parseInt(xmlPullParser.nextText()));
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
