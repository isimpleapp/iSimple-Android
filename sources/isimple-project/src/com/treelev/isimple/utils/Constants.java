package com.treelev.isimple.utils;

public class Constants {
    
    public static final String URL_ITEMS_PRICE = "http://s1.isimpleapp.ru/xml/ver0/Item-Prices.xmlz";
    public static final String URL_CATALOG_ITEM_BASE_URL = "http://s1.isimpleapp.ru/xml/ver0/Catalog-Update/";
    public static final String URL_PRICE_DISCOUNTS = "http://s1.isimpleapp.ru/xml/ver0/Item-Price-Discount.xmlz";
    public static final String URL_OFFERS = "http://s1.isimpleapp.ru/xml/ver0/OffersList.xmlz";
    public static final String URL_FEATURED = "http://s1.isimpleapp.ru/xml/ver0/Featured.xmlz";
    public static final String URL_NEW_ITEMS = "http://s1.isimpleapp.ru/get_items_descr.php?type=xml&ids=";
    
    public static final String INTENT_ACTION_SYNC_TRIGGER = "com.treelev.isimple.action.SYNC_DATA";
    public static final String INTENT_ACTION_SYNC_FINISHED = "com.treelev.isimple.action.SYNC_FINISHED";
    public static final String INTENT_ACTION_SYNC_SUCCESSFULL = "com.treelev.isimple.action.SYNC_SUCCESSFULL";
    public static final String INTENT_ACTION_SYNC_FAILED_PHASE = "com.treelev.isimple.action.SYNC_FAILED_PHASE";
    public static final String INTENT_ACTION_SYNC_STATE_UPDATE = "com.treelev.isimple.action.SYNC_STATE_UPDATE";
    public static final String INTENT_EXTRA_SYNC_STATE = "sync_state";
    
    public static long SYNC_LONG_PERIOD = 30 * 24 * 60 * 60 * 1000; // 30 days
    
    public static final String SYNC_LOG_FILE_NAME = "sync_log";
    
    public static final int NEW_ITEMS_BLOCK_SIZE = 50;

}
