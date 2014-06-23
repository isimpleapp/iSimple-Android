package com.treelev.isimple.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Log;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.domain.db.ItemPriceDiscount;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

public class ItemPriceDiscountParser implements Parser {

	public final static int ITEM_PRICES_PARSER_ID = 8;

	private final static String ITEM_DISCOUNT_OBJECT_TAG = "ItemDiscount";
	private final static String ITEM_ID_VALUE_TAG = "ItemID";
	private final static String ITEM_PRICE_DISCOUNT_VALUE_TAG = "PriceDiscount";

	public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
		Log.i("", "ItemPriceDiscountParser parseXmlToDB ");
		try {
			List<ItemPriceDiscount> itemPriceDiscountsList = new ArrayList<ItemPriceDiscount>();
			while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (xmlPullParser.getEventType() == XmlPullParser.START_TAG
						&& xmlPullParser.getName().equals(ITEM_DISCOUNT_OBJECT_TAG)) {
					xmlPullParser.nextTag();
					String itemID = null;
					float priceDiscount = -1f;
					Log.i("", "xmlPullParser.getName() = " + xmlPullParser.getName());
					while (xmlPullParser.getEventType() != XmlPullParser.END_TAG
							&& !xmlPullParser.getName().equals(ITEM_DISCOUNT_OBJECT_TAG)) {
						Log.i("", "xmlPullParser.getName() = " + xmlPullParser.getName());
						if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
							if (xmlPullParser.getName().equals(ITEM_ID_VALUE_TAG)) {
								itemID = xmlPullParser.nextText();
							} else if (xmlPullParser.getName().equals(ITEM_PRICE_DISCOUNT_VALUE_TAG)) {
								priceDiscount = Utils.parseFloat(xmlPullParser.nextText());
							} else {
								xmlPullParser.nextText();
							}
						}
						xmlPullParser.nextTag();
					}
					if (itemID != null) {
						itemPriceDiscountsList.add(new ItemPriceDiscount(itemID, priceDiscount));
					}
				}
				try {
					xmlPullParser.nextTag();
				} catch (Exception e) {
					
				}
			}
			Log.i("", "itemPriceDiscountsList size = " + itemPriceDiscountsList.size());
			((ItemDAO) daoList[0]).updatePriceDiscountList(itemPriceDiscountsList);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getFileName() {
		String name = SharedPreferencesManager.getUpdateFileName(UpdateFile.DISCOUNT.getUpdateFileTag()); 
		return TextUtils.isEmpty(name) ? "Item-Price-Discount.xml" : name;
	}
}
