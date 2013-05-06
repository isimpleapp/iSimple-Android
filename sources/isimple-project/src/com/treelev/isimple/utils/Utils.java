package com.treelev.isimple.utils;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import com.treelev.isimple.parser.*;
import com.treelev.isimple.utils.managers.LocationTrackingManager;

public class Utils {

    private final static String FORMAT_PRICE_LABEL = "%s р.";
    private final static String FORMAT_VOLUME_LABEL = "%sл";
    private final static String REPLACE_STRING_ZEROS = "\\.?0*$";

    public static Parser getXmlParser(int parserId) {
        switch (parserId) {
            case ItemPricesParser.ITEM_PRICES_PARSER_ID:
                return new ItemPricesParser();
            case ShopAndChainsParser.SHOP_AND_CHAINS_PARSER_ID:
                return new ShopAndChainsParser();
            case CatalogParser.CATALOG_PARSER_ID:
                return new CatalogParser();
            case ItemAvailabilityParser.ITEM_AVAILABILITY_PARSER_ID:
                return new ItemAvailabilityParser();
            default:
                return null;
        }
    }

    public static Location getCurrentLocation(Context context) {
        return new LocationTrackingManager(context).getCurrentLocation();
    }

    public static Location getMoscowLocation() {
        Location location = new Location("moscow");
        location.setLatitude(55.755713);
        location.setLongitude(37.628174);
        return location;
    }

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static String organizePriceLabel(String price) {
        String result = null;
        if (price != null) {
            String[] realPrice = price.split("\\.");
            result = String.format(FORMAT_PRICE_LABEL, realPrice[0]);
        }
        return result;
    }

    public static String removeZeros(String number) {
        String result = number;
        if (number.contains(".")) {
            result = number.replaceAll(REPLACE_STRING_ZEROS, "");
        }
        return result;
    }

    public static String organizeProductLabel(String volume) {
        return organizeProductLabel(FORMAT_VOLUME_LABEL, volume);
    }

    public static String organizeProductLabel(String format, String volume) {
        String result = null;
        if (volume != null) {
            result = volume.replace('.', ',');
            result = String.format(format, result);
        }
        return result;
    }
}
