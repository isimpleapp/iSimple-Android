
package com.treelev.isimple.analytics;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.treelev.isimple.app.ISimpleApp;

/**
 * Created by grigorij on 16/12/13.
 */
public class Analytics {

    // ****************************** Contract ******************************

    private static final String SCREEN_CATALOG = "Catalog";
    private static final String SCREEN_WINE = "Вино";
    private static final String SCREEN_SPIRITS = "Крепкий Алкоголь";
    private static final String SCREEN_CHAMPAGNE = "Шампанское";
    private static final String SCREEN_PORTO_JEREZ = "Порто-Херес";
    private static final String SCREEN_SAKE = "Саке";
    private static final String SCREEN_WATER = "Вода";

    private static final String SCREEN_SEARCH = "Search";
    private static final String SCREEN_PRODUCT_CARD = "Product card";
    private static final String SCREEN_STORE_LIST = "Store list";
    private static final String SCREEN_STORE_MAP = "Store map";
    private static final String SCREEN_STORE_CARD = "Store card";
    private static final String SCREEN_FAVOURITES = "Favourites";
    private static final String SCREEN_BASKET = "Basket";
    private static final String SCREEN_BASKET_HISTORY = "Basket history";
    private static final String SCREEN_ORDER_INFO = "Order info";
    private static final String SCREEN_ORDER_DONE = "Order done";
    private static final String SCREEN_SCAN = "Scan";
    private static final String SCREEN_ORDER_DETAILS = "Order details";
    private static final String SCREEN_FILTR_SETTINGS = "Filtr settings";

    private static final String EVENT_CATEGORY_UI_ACTION = "ui_action";
    private static final String EVENT_CATEGORY_FILTER = "filter";
    private static final String EVENT_CATEGORY_SEARCH = "search";

    private static final String EVENT_FIRST_RUN = "First run";
    private static final String EVENT_ADDED_TO_BASKET = "added to basket";
    private static final String EVENT_ADDED_TO_FAVORITES = "added to favorites";
    private static final String EVENT_DELETED_FROM_FAVORITES = "deleted from favorites";
    private static final String EVENT_STARTED_TO_ORDER = "started to order";
    private static final String EVENT_ORDER_DONE = "order done";
    private static final String EVENT_BASKET_RESTORE = "basket restore";
    private static final String EVENT_SEARCH_TOPMOST = "search";
    private static final String EVENT_SEARCH_IN_CATEGORY = "search";
    private static final String EVENT_SEARCH_IN_SHOP = "store category search";
    private static final String EVENT_BARCODE_SCANNED = "barcode scanned";
    private static final String EVENT_SEARCH_PREDICATE_IN_CATALOG = "search predicate in catalog";
    private static final String EVENT_OPENED_STORE = "opened store";
    private static final String EVENT_OPEN_PRODUCT_CARD = "open product card";
    private static final String EVENT_UPD_CAT = "upd cat";
    private static final String EVENT_UPD_PRICE = "upd price";
    private static final String EVENT_SHARING = "sharing";
    private static final String EVENT_NOTHING_FOUND = "sharing";
    private static final String EVENT_TEXT = "text";
    private static final String EVENT_ADVANCED = "advanced";

    // ****************************** Screens ******************************

    static private void sendScreen(Context c, String screen) {
        Tracker easyTracker = ISimpleApp.getAnalyticsTracker();
        easyTracker.setScreenName(screen);
        easyTracker.send(new HitBuilders.EventBuilder()
                .build());
    }

    public static void screen_Catalog(Context c) {
        sendScreen(c, SCREEN_CATALOG);
    }

    public static void screen_Wine(Context c) {
        sendScreen(c, SCREEN_WINE);
    }

    public static void screen_Spirits(Context c) {
        sendScreen(c, SCREEN_SPIRITS);
    }

    public static void screen_Champagne(Context c) {
        sendScreen(c, SCREEN_CHAMPAGNE);
    }

    public static void screen_PortoJerez(Context c) {
        sendScreen(c, SCREEN_PORTO_JEREZ);
    }

    public static void screen_Sake(Context c) {
        sendScreen(c, SCREEN_SAKE);
    }

    public static void screen_Water(Context c) {
        sendScreen(c, SCREEN_WATER);
    }

    public static void screen_Search(Context c) {
        sendScreen(c, SCREEN_SEARCH);
    }

    public static void screen_ProductCard(Context c) {
        sendScreen(c, SCREEN_PRODUCT_CARD);
    }

    public static void screen_StoreList(Context c) {
        sendScreen(c, SCREEN_STORE_LIST);
    }

    public static void screen_StoreMap(Context c) {
        sendScreen(c, SCREEN_STORE_MAP);
    }

    public static void screen_StoreCard(Context c) {
        sendScreen(c, SCREEN_STORE_CARD);
    }

    public static void screen_Favourites(Context c) {
        sendScreen(c, SCREEN_FAVOURITES);
    }

    public static void screen_Basket(Context c) {
        sendScreen(c, SCREEN_BASKET);
    }

    public static void screen_BasketHistory(Context c) {
        sendScreen(c, SCREEN_BASKET_HISTORY);
    }

    public static void screen_OrderInfo(Context c) {
        sendScreen(c, SCREEN_ORDER_INFO);
    }

    public static void screen_OrderDone(Context c) {
        sendScreen(c, SCREEN_ORDER_DONE);
    }

    public static void screen_Scan(Context c) {
        sendScreen(c, SCREEN_SCAN);
    }

    public static void screen_OrderDetails(Context c) {
        sendScreen(c, SCREEN_ORDER_DETAILS);
    }

    public static void screen_FiltrSettings(Context c) {
        sendScreen(c, SCREEN_FILTR_SETTINGS);
    }

    // ****************************** Events ******************************

    private static void sendEvent(
            Context c,
            String category,
            String action,
            String label /* nullable */,
            Long value /* nullable */) {

        Tracker easyTracker = ISimpleApp.getAnalyticsTracker();

        easyTracker.send(
                new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }

    public static void event_FirstRun(Context c) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_FIRST_RUN, null, null);
    }

    public static void event_OpenProductCard(Context c, Long art, String cat) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_OPEN_PRODUCT_CARD, null, null);
    }

    public static void event_UpdPrice(Context c, boolean success) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_UPD_PRICE, null, null);
    }

    public static void event_BarcodeScanned(Context c, boolean success, Long art, String cat) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_BARCODE_SCANNED, null, null);
    }

    public static void event_AddedToBasket(Context c, Long art, String cat) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_ADDED_TO_BASKET, null, null);
    }

    public static void event_OpenedStore(Context c, String name, String adress) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_OPENED_STORE, null, null);
    }

    public static void event_AddedToFavorites(Context c, Long art, String cat) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_ADDED_TO_FAVORITES, null, null);
    }

    public static void event_StartedToOrder(Context c, int amount, int summ) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_STARTED_TO_ORDER, null, null);
    }

    public static void event_OrderDone(Context c, int summ, int amount, String region,
            String transport, String preferred) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_ORDER_DONE, null, null);
    }

    public static void event_BasketRestore(Context c, Long art, String cat) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_BASKET_RESTORE, null, null);
    }

    public static void event_Sharing(Context c, Long id, String socialNetwork) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_SHARING, null, null);
    }

    public static void event_DeletedFromFavorites(Context c, Long art, String cat) {
        sendEvent(c, EVENT_CATEGORY_UI_ACTION, EVENT_DELETED_FROM_FAVORITES, null, null);
    }

    public static void event_Filter(Context c) { // <<
        sendEvent(c, EVENT_CATEGORY_FILTER, EVENT_CATEGORY_FILTER, null, null);
    }

    public static void event_NothingFoundAfterFilter(Context c) { // <<
        sendEvent(c, EVENT_CATEGORY_FILTER, EVENT_NOTHING_FOUND, null, null);
    }

    public static void event_SearchText(Context c, String text) {
        sendEvent(c, EVENT_CATEGORY_SEARCH, EVENT_TEXT, null, null);
    }

    public static void event_SearchNothingFound(Context c, String text) {
        sendEvent(c, EVENT_CATEGORY_SEARCH, EVENT_NOTHING_FOUND, null, null);
    }

    public static void event_event_SearchAdvanced(Context c, String text) {
        sendEvent(c, EVENT_CATEGORY_SEARCH, EVENT_ADVANCED, null, null);
    }
}
