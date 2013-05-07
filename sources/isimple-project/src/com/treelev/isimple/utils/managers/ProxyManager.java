package com.treelev.isimple.utils.managers;

import android.content.Context;
import com.treelev.isimple.R;
import com.treelev.isimple.data.*;
import com.treelev.isimple.domain.comparators.ItemCompareName;
import com.treelev.isimple.domain.comparators.ItemComparePrice;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.utils.Utils;

import java.util.*;

public class ProxyManager {

    private Context context;

    public static final int SORT_NAME_AZ = 1;
    public static final int SORT_PRICE_UP = 2;
    private final static String FORMAT_TEXT_LABEL = "%s...";
    private final static int FORMAT_NAME_MAX_LENGTH = 41;
    private final static int FORMAT_LOC_NAME_MAX_LENGTH = 30;

    public ProxyManager(Context context) {
        this.context = context;
    }

    public Item getItemById(String itemId) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemById(itemId);
    }

    public List<Map<String, ?>> getRandomItems() {
        List<Item> itemList = ((ItemDAO) getObjectDAO(ItemDAO.ID)).getRandomItems();
        return convertItemsToUI(itemList);
    }

    public List<Map<String, ?>> convertItemsToUI(List<Item> itemList, int sortBy) {
        Comparator<Item> compare = null;
        switch (sortBy) {
            case SORT_NAME_AZ:
                compare = new ItemCompareName();
                break;
            case SORT_PRICE_UP:
                compare = new ItemComparePrice();
                break;
        }
        Collections.sort(itemList, compare);
        return convertItemsToUI(itemList);
    }

    public List<Item> getItemsByCategory(int categoryId) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemsByCategory(categoryId);
    }

    public List<Item> getSearchItemsByCategory(Integer categoryId, String query) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getSearchItemsByCategory(categoryId, query);
    }

    private List<Map<String, ?>> convertItemsToUI(List<Item> itemList) {
        List<Map<String, ?>> uiDataList = new ArrayList<Map<String, ?>>();
        for (Item item : itemList) {
            Map<String, Object> uiDataItem = new HashMap<String, Object>();
            uiDataItem.put(Item.UI_TAG_ID, item.getItemID());
            uiDataItem.put(Item.UI_TAG_IMAGE, R.drawable.bottle_list_image_default);
            uiDataItem.put(Item.UI_TAG_NAME, organizeItemNameLabel(item.getName()));
            uiDataItem.put(Item.UI_TAG_LOCALIZATION_NAME, organizeLocItemNameLabel(item.getLocalizedName()));
            String volumeLabel = Utils.organizeProductLabel(Utils.removeZeros(item.getVolume()));
            uiDataItem.put(Item.UI_TAG_VOLUME, volumeLabel != null ? volumeLabel : "");
            String priceLabel = Utils.organizePriceLabel(item.getPrice());
            uiDataItem.put(Item.UI_TAG_PRICE, priceLabel != null ? priceLabel : "");
            uiDataItem.put(Item.UI_TAG_DRINK_CATEGORY, item.getDrinkCategory().getDescription());
            uiDataList.add(uiDataItem);
        }
        return uiDataList;
    }

    private String organizeItemNameLabel(String itemName) {
        return organizeTextLabel(itemName, FORMAT_NAME_MAX_LENGTH);
    }

    private String organizeLocItemNameLabel(String locItemName) {
        return organizeTextLabel(locItemName, FORMAT_LOC_NAME_MAX_LENGTH);
    }

    private String organizeTextLabel(String itemName, int maxLength) {
        String result = itemName;
        if (result.length() > maxLength) {
            result = String.format(FORMAT_TEXT_LABEL, result.substring(0, maxLength));
        }
        return result;
    }

    private BaseDAO getObjectDAO(int id) {
        switch (id) {
            case ItemDAO.ID:
                return new ItemDAO(context);
            case ItemAvailabilityDAO.ID:
                return new ItemAvailabilityDAO(context);
            case ShopDAO.ID:
                return new ShopDAO(context);
            case ChainDAO.ID:
                return new ChainDAO(context);
            default:
                return null;
        }
    }
}
