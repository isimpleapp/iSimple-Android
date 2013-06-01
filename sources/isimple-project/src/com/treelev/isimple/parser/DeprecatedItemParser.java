package com.treelev.isimple.parser;

import android.text.TextUtils;
import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.DeprecatedItemDAO;
import com.treelev.isimple.domain.db.DeprecatedItem;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeprecatedItemParser implements Parser {

    public final static int DEPRECATED_ITEMS_PARSER_ID = 6;

    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            String tempStr;
            int nestingLevel = 0;
            DeprecatedItem deprecatedItem;
            List<DeprecatedItem> deprecatedItemList = new ArrayList<DeprecatedItem>();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals("Deprecated")) {
                    nestingLevel++;
                    if (nestingLevel == 2) {
                        deprecatedItem = new DeprecatedItem();
                        xmlPullParser.next();
                        while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals("Deprecated")) {
                            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                if (xmlPullParser.getName().equals("ItemID")) {
                                    deprecatedItem.setItemID(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("DrinkID")) {
                                    deprecatedItem.setDrinkID(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("Name")) {
                                    deprecatedItem.setName(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("LocalizedName")) {
                                    deprecatedItem.setLocalizedName(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("Manufacturer")) {
                                    deprecatedItem.setManufacturer(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("LocalizedManufacturer")) {
                                    deprecatedItem.setLocalizedManufacturer(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("Country")) {
                                    deprecatedItem.setCountry(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("Region")) {
                                    deprecatedItem.setRegion(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("Barcode")) {
                                    deprecatedItem.setBarcode(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("DrinkCategory")) {
                                    deprecatedItem.setDrinkCategory(DrinkCategory.getDrinkCategory(xmlPullParser.nextText()));
                                } else if (xmlPullParser.getName().equals("ProductType")) {
                                    deprecatedItem.setProductType(ProductType.getProductType(xmlPullParser.nextText()));
                                } else if (xmlPullParser.getName().equals("DrinkType")) {
                                    tempStr = xmlPullParser.nextText();
                                    if (!TextUtils.isEmpty(tempStr)) {
                                        deprecatedItem.setDrinkType(tempStr);
                                    }
                                } else if (xmlPullParser.getName().equals("Classification")) {
                                    deprecatedItem.setClassification(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals("Volume")) {
                                    deprecatedItem.setVolume(Utils.parseFloat(xmlPullParser.nextText()));
                                }
                            }
                            xmlPullParser.next();
                        }
                        deprecatedItemList.add(deprecatedItem);
                        nestingLevel--;
                    }
                }
                xmlPullParser.next();
            }
            ((DeprecatedItemDAO) daoList[0]).insertListData(deprecatedItemList);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}