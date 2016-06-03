package com.treelev.isimple.parser;

import android.text.TextUtils;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.DeprecatedItemDAO;
import com.treelev.isimple.domain.db.DeprecatedItem;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeprecatedItemParser implements Parser {

    public final static int DEPRECATED_ITEMS_PARSER_ID = 6;

    private final static String DEPRECATED_ITEM_TAG = "Deprecated";
    private final static String DEPRECATED_ITEM_ID_TAG = "ItemID";
    private final static String DEPRECATED_DRINK_ID_TAG = "DrinkID";
    private final static String DEPRECATED_NAME_TAG = "Name";
    private final static String DEPRECATED_LOC_NAME_TAG = "LocalizedName";
    private final static String DEPRECATED_MANUFACTURER_TAG = "Manufacturer";
    private final static String DEPRECATED_LOC_MANUFACTURER_TAG = "LocalizedManufacturer";
    private final static String DEPRECATED_COUNTRY_TAG = "Country";
    private final static String DEPRECATED_REGION_TAG = "Region";
    private final static String DEPRECATED_BARCODE_TAG = "Barcode";
    private final static String DEPRECATED_DRINK_CATEGORY_TAG = "DrinkCategory";
    private final static String DEPRECATED_PRODUCT_TYPE_TAG = "ProductType";
    private final static String DEPRECATED_DRINK_TYPE_TAG = "DrinkType";
    private final static String DEPRECATED_CLASSIFICATION_TAG = "Classification";
    private final static String DEPRECATED_VOLUME_TAG = "Volume";

    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            String tempStr;
            int nestingLevel = 0;
            DeprecatedItem deprecatedItem;
            List<DeprecatedItem> deprecatedItemList = new ArrayList<DeprecatedItem>();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(DEPRECATED_ITEM_TAG)) {
                    nestingLevel++;
                    if (nestingLevel == 2) {
                        deprecatedItem = new DeprecatedItem();
                        xmlPullParser.next();
                        while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(DEPRECATED_ITEM_TAG)) {
                            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                if (xmlPullParser.getName().equals(DEPRECATED_ITEM_ID_TAG)) {
                                    deprecatedItem.setItemID(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_DRINK_ID_TAG)) {
                                    deprecatedItem.setDrinkID(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_NAME_TAG)) {
                                    deprecatedItem.setName(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_LOC_NAME_TAG)) {
                                    deprecatedItem.setLocalizedName(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_MANUFACTURER_TAG)) {
                                    deprecatedItem.setManufacturer(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_LOC_MANUFACTURER_TAG)) {
                                    deprecatedItem.setLocalizedManufacturer(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_COUNTRY_TAG)) {
                                    deprecatedItem.setCountry(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_REGION_TAG)) {
                                    deprecatedItem.setRegion(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_BARCODE_TAG)) {
                                    deprecatedItem.setBarcode(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_DRINK_CATEGORY_TAG)) {
                                    deprecatedItem.setDrinkCategory(DrinkCategory.getDrinkCategory(xmlPullParser.nextText()));
                                } else if (xmlPullParser.getName().equals(DEPRECATED_PRODUCT_TYPE_TAG)) {
                                    deprecatedItem.setProductType(ProductType.getProductType(xmlPullParser.nextText()));
                                } else if (xmlPullParser.getName().equals(DEPRECATED_DRINK_TYPE_TAG)) {
                                    tempStr = xmlPullParser.nextText();
                                    if (!TextUtils.isEmpty(tempStr)) {
                                        deprecatedItem.setDrinkType(tempStr);
                                    }
                                } else if (xmlPullParser.getName().equals(DEPRECATED_CLASSIFICATION_TAG)) {
                                    deprecatedItem.setClassification(xmlPullParser.nextText());
                                } else if (xmlPullParser.getName().equals(DEPRECATED_VOLUME_TAG)) {
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
            DeprecatedItemDAO deprecatedItemDAO = (DeprecatedItemDAO) daoList[0];
            deprecatedItemDAO.deleteAllData();
            deprecatedItemDAO.insertListData(deprecatedItemList);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	public static String getFileName() {
		String name = SharedPreferencesManager.getUpdateFileName(UpdateFile.DEPRECATED.getUpdateFileTag()); 
		return TextUtils.isEmpty(name) ? "Deprecated.xml" : name;
	}
}