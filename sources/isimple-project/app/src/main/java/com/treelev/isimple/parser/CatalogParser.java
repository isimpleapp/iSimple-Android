package com.treelev.isimple.parser;

import android.text.TextUtils;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.enumerable.item.Sweetness;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CatalogParser implements Parser {

    public final static int CATALOG_PARSER_ID = 3;

    private final static String CATALOG_OBJECT_TAG = "Item";
    private final static String CATALOG_ITEM_ID_VALUE_TAG = "ItemID";
    private final static String CATALOG_DRINK_ID_VALUE_TAG = "DrinkID";
    private final static String CATALOG_NAME_VALUE_TAG = "Name";
    private final static String CATALOG_LOCALIZATED_NAME_VALUE_TAG = "LocalizedName";
    private final static String CATALOG_MANUFACTURER_VALUE_TAG = "Manufacturer";
    private final static String CATALOG_LOCALIZATED_MANUFACTURER_VALUE_TAG = "LocalizedManufacturer";
    private final static String CATALOG_PRICE_VALUE_TAG = "Price";
    private final static String CATALOG_COUNTRY_VALUE_TAG = "Country";
    private final static String CATALOG_REGION_VALUE_TAG = "Region";
    private final static String CATALOG_BARCODE_VALUE_TAG = "Barcode";
    private final static String CATALOG_DRINK_CATEGORY_VALUE_TAG = "DrinkCategory";
    private final static String CATALOG_COLOR_VALUE_TAG = "Color";
    private final static String CATALOG_STYLE_VALUE_TAG = "Style";
    private final static String CATALOG_SWEETNESS_VALUE_TAG = "Sweetness";
    private final static String CATALOG_YEAR_VALUE_TAG = "Year";
    private final static String CATALOG_VOLUME_VALUE_TAG = "Volume";
    private final static String CATALOG_DRINK_TYPE_VALUE_TAG = "DrinkType";
    private final static String CATALOG_PRODUCT_TYPE_VALUE_TAG = "ProductType";
    private final static String CATALOG_CLASSIFICATION_VALUE_TAG = "Classification";
    private final static String CATALOG_ALCOHOL_VALUE_TAG = "Alcohol";
    private final static String CATALOG_BOTTLE_IMG_HIGR_RES_VALUE_TAG = "BottleHiResolutionImageFilename";
    private final static String CATALOG_BOTTLE_IMG_LOW_RES_VALUE_TAG = "BottleLowResolutionImageFilename";
    private final static String CATALOG_STYLE_DESCRIPTION_VALUE_TAG = "StyleDescription";
    private final static String CATALOG_APPELATION_VALUE_TAG = "Appelation";
    private final static String CATALOG_SERVING_TEMP_MIN_VALUE_TAG = "ServingTempMin";
    private final static String CATALOG_SERVING_TEMP_MAX_VALUE_TAG = "ServingTempMax";
    private final static String CATALOG_TASTE_QUALITIES_VALUE_TAG = "TasteQualities";
    private final static String CATALOG_VINTAGE_REPORT_VALUE_TAG = "VintageReport";
    private final static String CATALOG_AGING_PROCESS_VALUE_TAG = "AgingProcess";
    private final static String CATALOG_PRODUCTION_PROCESS_VALUE_TAG = "ProductionProcess";
    private final static String CATALOG_INTERESTING_FACTS_VALUE_TAG = "InterestingFacts";
    private final static String CATALOG_LABEL_HISTORY_VALUE_TAG = "LabelHistory";
    private final static String CATALOG_GASTRONOMY_VALUE_TAG = "Gastronomy";
    private final static String CATALOG_VINEYARD_VALUE_TAG = "Vineyard";
    private final static String CATALOG_GRAPES_USED_VALUE_TAG = "GrapesUsed";
    private final static String CATALOG_RATING_VALUE_TAG = "Rating";
    private final static String CATALOG_QUANTITY_VALUE_TAG = "Quantity";
    
    //TODO lower case
    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            Item item;
            List<Item> itemList = new ArrayList<Item>();
            String tempStr;
            ItemDAO itemDAO = (ItemDAO) daoList[0];
            itemDAO.deleteAllData();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(CATALOG_OBJECT_TAG)) {
                    item = new Item();
                    xmlPullParser.next();
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(CATALOG_OBJECT_TAG)) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals(CATALOG_ITEM_ID_VALUE_TAG)) {
                                item.setItemID(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_DRINK_ID_VALUE_TAG)) {
                                item.setDrinkID(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_NAME_VALUE_TAG)) {
                                item.setName(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_LOCALIZATED_NAME_VALUE_TAG)) {
                                item.setLocalizedName(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_MANUFACTURER_VALUE_TAG)) {
                                item.setManufacturer(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_LOCALIZATED_MANUFACTURER_VALUE_TAG)) {
                                item.setLocalizedManufacturer(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_PRICE_VALUE_TAG)) {
                                item.setPrice(Utils.parseFloat(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_COUNTRY_VALUE_TAG)) {
                                item.setCountry(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_REGION_VALUE_TAG)) {
                                item.setRegion(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_BARCODE_VALUE_TAG)) {
                                item.setBarcode(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_PRODUCT_TYPE_VALUE_TAG)) {
                                item.setProductType(ProductType.getProductType(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_CLASSIFICATION_VALUE_TAG)) {
                                item.setClassification(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_DRINK_CATEGORY_VALUE_TAG)) {
                                item.setDrinkCategory(DrinkCategory.getDrinkCategory(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_COLOR_VALUE_TAG)) {
                                item.setColor(ItemColor.getColor(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_STYLE_VALUE_TAG)) {
                                item.setStyle(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_SWEETNESS_VALUE_TAG)) {
                                item.setSweetness(Sweetness.getSweetness(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_YEAR_VALUE_TAG)) {
                                item.setYear(Utils.parseInteger(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_VOLUME_VALUE_TAG)) {
                                item.setVolume(Utils.parseFloat(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_DRINK_TYPE_VALUE_TAG)) {
                                tempStr = xmlPullParser.nextText();
                                if (!TextUtils.isEmpty(tempStr)) {
                                    item.setDrinkType(tempStr);
                                }
                            } else if (xmlPullParser.getName().equals(CATALOG_ALCOHOL_VALUE_TAG)) {
                                item.setAlcohol(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_BOTTLE_IMG_HIGR_RES_VALUE_TAG)) {
                                item.setBottleHiResolutionImageFilename(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_BOTTLE_IMG_LOW_RES_VALUE_TAG)) {
                                tempStr = xmlPullParser.nextText();
                                if (!TextUtils.isEmpty(tempStr)) {
                                    item.setBottleLowResolutionImageFilename(tempStr);
                                }
                            } else if (xmlPullParser.getName().equals(CATALOG_STYLE_DESCRIPTION_VALUE_TAG)) {
                                item.setStyleDescription(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_APPELATION_VALUE_TAG)) {
                                item.setAppelation(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_SERVING_TEMP_MIN_VALUE_TAG)) {
                                item.setServingTempMin(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_SERVING_TEMP_MAX_VALUE_TAG)) {
                                item.setServingTempMax(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_TASTE_QUALITIES_VALUE_TAG)) {
                                item.setTasteQualities(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_VINTAGE_REPORT_VALUE_TAG)) {
                                tempStr = xmlPullParser.nextText();
                                if (!TextUtils.isEmpty(tempStr)) {
                                    item.setVintageReport(tempStr);
                                }
                            } else if (xmlPullParser.getName().equals(CATALOG_AGING_PROCESS_VALUE_TAG)) {
                                item.setAgingProcess(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_PRODUCTION_PROCESS_VALUE_TAG)) {
                                item.setProductionProcess(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_INTERESTING_FACTS_VALUE_TAG)) {
                                item.setInterestingFacts(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_LABEL_HISTORY_VALUE_TAG)) {
                                tempStr = xmlPullParser.nextText();
                                if (!TextUtils.isEmpty(tempStr)) {
                                    item.setLabelHistory(tempStr);
                                }
                            } else if (xmlPullParser.getName().equals(CATALOG_GASTRONOMY_VALUE_TAG)) {
                                item.setGastronomy(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_VINEYARD_VALUE_TAG)) {
                                item.setVineyard(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_GRAPES_USED_VALUE_TAG)) {
                                item.setGrapesUsed(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_RATING_VALUE_TAG)) {
                                item.setRating(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_QUANTITY_VALUE_TAG)) {
                                item.setQuantity(Utils.parseFloat(xmlPullParser.nextText()));
                            } else {
                                xmlPullParser.nextText();
                            }
                        }
                        xmlPullParser.next();
                    }
                    itemList.add(item);
                    if (itemList.size() == MAX_SIZE_DATA_TO_ITEM_LIST) {
                        itemDAO.insertListData(itemList);
                        itemList.clear();
                    }
                }
                xmlPullParser.next();
            }
            itemDAO.insertListData(itemList);
            itemList.clear();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static String getFileName() {
		String name = SharedPreferencesManager.getUpdateFileName(UpdateFile.CATALOG_UPDATES.getUpdateFileTag()); 
		return TextUtils.isEmpty(name) ? "Catalog-Update.xml" : name;
	}
}
