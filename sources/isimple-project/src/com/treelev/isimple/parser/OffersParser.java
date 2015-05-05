
package com.treelev.isimple.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.OfferDAO;
import com.treelev.isimple.domain.db.Offer;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

public class OffersParser implements Parser {

    private final static String OFFER_OBJECT_TAG = "Offer";
    private final static String OFFER_ID_VALUE_TAG = "OfferID";
    private final static String OFFER_NAME_VALUE_TAG = "Name";
    private final static String OFFER_URL_VALUE_TAG = "Url";
    private final static String OFFER_EXPIRED_VALUE_TAG = "Expired";
    private final static String OFFER_IMAGE_VALUE_TAG = "Image";
    private final static String OFFER_IMAGE1200_VALUE_TAG = "Image1200";
    private final static String OFFER_IMAGEHDPI_VALUE_TAG = "ImageHdpi";
    private final static String OFFER_IMAGE2X_VALUE_TAG = "Image2x";
    private final static String OFFER_IMAGEIPAD_VALUE_TAG = "ImageIpad";
    private final static String OFFER_IMAGEIPAD2X_VALUE_TAG = "ImageIpad2x";
    private final static String OFFER_DESCRIPTION_VALUE_TAG = "Description";
    private final static String OFFER_PRIORITIZED_VALUE_TAG = "Prioritized";
    private final static String OFFER_ITEMS_LIST_TAG = "Items";
    private final static String OFFER_ITEMID_VALUE_TAG = "ItemID";

    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            Offer offer;
            List<Offer> offersList = new ArrayList<Offer>();
            OfferDAO offerDAO = (OfferDAO) daoList[0];
            offerDAO.deleteAllData();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG
                        && xmlPullParser.getName().equals(OFFER_OBJECT_TAG)) {
                    offer = new Offer();
                    xmlPullParser.next();
                    while (xmlPullParser.getEventType() == XmlPullParser.TEXT) {
                        // Looks like we have new lines in the xml
                        xmlPullParser.next();
                    }
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG
                            && !xmlPullParser.getName().equals(OFFER_OBJECT_TAG)) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals(OFFER_ID_VALUE_TAG)) {
                                offer.setId(Long.valueOf(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(OFFER_NAME_VALUE_TAG)) {
                                offer.setName(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(OFFER_URL_VALUE_TAG)) {
                                offer.setUrl(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(OFFER_EXPIRED_VALUE_TAG)) {
                                offer.setExpired(Utils.parseInteger(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(OFFER_IMAGE_VALUE_TAG)) {
                                offer.setImage(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(OFFER_IMAGE1200_VALUE_TAG)) {
                                offer.setImage1200(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(OFFER_IMAGEHDPI_VALUE_TAG)) {
                                offer.setImagehdpi(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(OFFER_IMAGE2X_VALUE_TAG)) {
                                offer.setImage2x(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(OFFER_IMAGEIPAD_VALUE_TAG)) {
                                offer.setImageipad(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(OFFER_IMAGEIPAD2X_VALUE_TAG)) {
                                offer.setImageipad2x(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(OFFER_DESCRIPTION_VALUE_TAG)) {
                                offer.setDescription(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(OFFER_PRIORITIZED_VALUE_TAG)) {
                                offer.setPrioritized(Utils.parseInteger(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(OFFER_ITEMS_LIST_TAG)) {
                                List<Long> itemsIds = new ArrayList<Long>();
                                while (!(xmlPullParser.getEventType() == XmlPullParser.END_TAG
                                && xmlPullParser.getName().equals(OFFER_ITEMS_LIST_TAG))) {
                                    if (xmlPullParser.getEventType() == XmlPullParser.START_TAG
                                            && xmlPullParser.getName().equals(
                                                    OFFER_ITEMID_VALUE_TAG)) {
                                        itemsIds.add(Long.valueOf(xmlPullParser.nextText()));
                                    }
                                    xmlPullParser.next();
                                    while (xmlPullParser.getEventType() == XmlPullParser.TEXT) {
                                        // Looks like we have new lines in the xml
                                        xmlPullParser.next();
                                    }
                                }
                                offer.setItemsList(itemsIds);
                            }
                        }
                        xmlPullParser.next();
                        while (xmlPullParser.getEventType() == XmlPullParser.TEXT) {
                            // Looks like we have new lines in the xml
                            xmlPullParser.next();
                        }
                    }
                    offersList.add(offer);
                    if (offersList.size() == MAX_SIZE_DATA_TO_ITEM_LIST) {
                        offerDAO.insertOffers(offersList);
                        offersList.clear();
                    }
                }
                xmlPullParser.next();
                while (xmlPullParser.getEventType() == XmlPullParser.TEXT) {
                    // Looks like we have new lines in the xml
                    xmlPullParser.next();
                }
            }
            offerDAO.insertOffers(offersList);
            offersList.clear();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileName() {
        String name = SharedPreferencesManager.getUpdateFileName(UpdateFile.CATALOG_UPDATES
                .getUpdateFileTag());
        return TextUtils.isEmpty(name) ? "Catalog-Update.xml" : name;
    }
}
