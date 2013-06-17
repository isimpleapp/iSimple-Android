package com.treelev.isimple.utils;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.treelev.isimple.parser.*;

import java.io.File;

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
            case FeaturedItemsParser.FEATURED_ITEMS_PARSER_ID:
                return new FeaturedItemsParser();
            case DeprecatedItemParser.DEPRECATED_ITEMS_PARSER_ID:
                return new DeprecatedItemParser();
            default:
                return null;
        }
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
        String result = "";
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

    public static Float parseFloat(String floatValue) {
        if (floatValue != null) {
            if (floatValue.startsWith(".")) {
                floatValue = "0" + floatValue;
            }
            try {
                return Float.parseFloat(floatValue);
            } catch (NumberFormatException e) {
                return -1f;
            }
        } else {
            return -1f;
        }
    }

    public static Integer parseInteger(String integerValue) {
        if (integerValue != null) {
            try {
                return Integer.parseInt(integerValue);
            } catch (NumberFormatException e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static String ellipseString(String input, int maxCharactersNumber) {
        return (input.length() > maxCharactersNumber) ?
            input.substring(0, maxCharactersNumber - 2) + "..." : input;
    }

    private  static ImageLoader imageLoader;

    public static ImageLoader getImageLoader(Context context) {
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            File cacheDir = StorageUtils.getCacheDirectory(context);
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .threadPoolSize(5)
                    .threadPriority(Thread.MIN_PRIORITY + 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
                    .discCache(new TotalSizeLimitedDiscCache(cacheDir, new HashCodeFileNameGenerator(), 40 * 1024 * 1024))  // 40 Mb
                    .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    .build();
            imageLoader.init(config);
        }
        return imageLoader;
    }
}
