package com.treelev.isimple.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.db.Offer;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ItemColor;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.enumerable.item.Sweetness;
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
    
    //TODO lower case
    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            Offer offer;
            List<Offer> offersList = new ArrayList<Offer>();
            String tempStr;
            ItemDAO itemDAO = (ItemDAO) daoList[0];
            itemDAO.deleteAllData();
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(CATALOG_OBJECT_TAG)) {
                    offer = new Item();
                    xmlPullParser.next();
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(CATALOG_OBJECT_TAG)) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals(CATALOG_ITEM_ID_VALUE_TAG)) {
                                offer.setItemID(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_DRINK_ID_VALUE_TAG)) {
                                offer.setDrinkID(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_NAME_VALUE_TAG)) {
                                offer.setName(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_LOCALIZATED_NAME_VALUE_TAG)) {
                                offer.setLocalizedName(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_MANUFACTURER_VALUE_TAG)) {
                                offer.setManufacturer(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_LOCALIZATED_MANUFACTURER_VALUE_TAG)) {
                                offer.setLocalizedManufacturer(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_PRICE_VALUE_TAG)) {
                                offer.setPrice(Utils.parseFloat(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_COUNTRY_VALUE_TAG)) {
                                offer.setCountry(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_REGION_VALUE_TAG)) {
                                offer.setRegion(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_BARCODE_VALUE_TAG)) {
                                offer.setBarcode(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_PRODUCT_TYPE_VALUE_TAG)) {
                                offer.setProductType(ProductType.getProductType(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_CLASSIFICATION_VALUE_TAG)) {
                                offer.setClassification(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_DRINK_CATEGORY_VALUE_TAG)) {
                                offer.setDrinkCategory(DrinkCategory.getDrinkCategory(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_COLOR_VALUE_TAG)) {
                                offer.setColor(ItemColor.getColor(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_STYLE_VALUE_TAG)) {
                                offer.setStyle(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_SWEETNESS_VALUE_TAG)) {
                                offer.setSweetness(Sweetness.getSweetness(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_YEAR_VALUE_TAG)) {
                                offer.setYear(Utils.parseInteger(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_VOLUME_VALUE_TAG)) {
                                offer.setVolume(Utils.parseFloat(xmlPullParser.nextText()));
                            } else if (xmlPullParser.getName().equals(CATALOG_DRINK_TYPE_VALUE_TAG)) {
                                tempStr = xmlPullParser.nextText();
                                if (!TextUtils.isEmpty(tempStr)) {
                                    offer.setDrinkType(tempStr);
                                }
                            } else if (xmlPullParser.getName().equals(CATALOG_ALCOHOL_VALUE_TAG)) {
                                offer.setAlcohol(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_BOTTLE_IMG_HIGR_RES_VALUE_TAG)) {
                                offer.setBottleHiResolutionImageFilename(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_BOTTLE_IMG_LOW_RES_VALUE_TAG)) {
                                tempStr = xmlPullParser.nextText();
                                if (!TextUtils.isEmpty(tempStr)) {
                                    offer.setBottleLowResolutionImageFilename(tempStr);
                                }
                            } else if (xmlPullParser.getName().equals(CATALOG_STYLE_DESCRIPTION_VALUE_TAG)) {
                                offer.setStyleDescription(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_APPELATION_VALUE_TAG)) {
                                offer.setAppelation(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_SERVING_TEMP_MIN_VALUE_TAG)) {
                                offer.setServingTempMin(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_SERVING_TEMP_MAX_VALUE_TAG)) {
                                offer.setServingTempMax(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_TASTE_QUALITIES_VALUE_TAG)) {
                                offer.setTasteQualities(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_VINTAGE_REPORT_VALUE_TAG)) {
                                tempStr = xmlPullParser.nextText();
                                if (!TextUtils.isEmpty(tempStr)) {
                                    offer.setVintageReport(tempStr);
                                }
                            } else if (xmlPullParser.getName().equals(CATALOG_AGING_PROCESS_VALUE_TAG)) {
                                offer.setAgingProcess(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_PRODUCTION_PROCESS_VALUE_TAG)) {
                                offer.setProductionProcess(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_INTERESTING_FACTS_VALUE_TAG)) {
                                offer.setInterestingFacts(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_LABEL_HISTORY_VALUE_TAG)) {
                                tempStr = xmlPullParser.nextText();
                                if (!TextUtils.isEmpty(tempStr)) {
                                    offer.setLabelHistory(tempStr);
                                }
                            } else if (xmlPullParser.getName().equals(CATALOG_GASTRONOMY_VALUE_TAG)) {
                                offer.setGastronomy(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_VINEYARD_VALUE_TAG)) {
                                offer.setVineyard(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_GRAPES_USED_VALUE_TAG)) {
                                offer.setGrapesUsed(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_RATING_VALUE_TAG)) {
                                offer.setRating(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals(CATALOG_QUANTITY_VALUE_TAG)) {
                                offer.setQuantity(Utils.parseFloat(xmlPullParser.nextText()));
                            } else {
                                xmlPullParser.nextText();
                            }
                        }
                        xmlPullParser.next();
                    }
                    offersList.add(offer);
                    if (offersList.size() == MAX_SIZE_DATA_TO_ITEM_LIST) {
                        itemDAO.insertListData(offersList);
                        offersList.clear();
                    }
                }
                xmlPullParser.next();
            }
            itemDAO.insertListData(offersList);
            offersList.clear();
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
