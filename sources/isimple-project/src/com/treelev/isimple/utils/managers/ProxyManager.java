package com.treelev.isimple.utils.managers;

import android.content.Context;
import com.treelev.isimple.R;
import com.treelev.isimple.data.*;
import com.treelev.isimple.domain.comparators.ItemCompareName;
import com.treelev.isimple.domain.comparators.ItemComparePrice;
import com.treelev.isimple.domain.db.Item;

import java.util.*;

public class ProxyManager {

    private Context context;

    public static final int SORT_NAME_AZ = 1;
    public static final int SORT_PRICE_UP = 2;

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
        switch(sortBy) {
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

    public List<Item>getSerchItemsByCategory(int categoryId, String query){
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getSearchItemsByCategory(categoryId, query);
    }

    private List<Map<String, ?>> convertItemsToUI(List<Item> itemList) {
        List<Map<String, ?>> uiDataList = new ArrayList<Map<String, ?>>();
        for (Item item : itemList) {
            Map<String, Object> uiDataItem = new HashMap<String, Object>();
            uiDataItem.put(Item.UI_TAG_ID, item.getItemID());
            uiDataItem.put(Item.UI_TAG_IMAGE, R.drawable.image_not_found);
            uiDataItem.put(Item.UI_TAG_NAME, item.getName());
            uiDataItem.put(Item.UI_TAG_LOCALIZATION_NAME, item.getLocalizedName());
            uiDataItem.put(Item.UI_TAG_DRINK_TYPE, item.getDrinkType());
            uiDataItem.put(Item.UI_TAG_VOLUME, item.getVolume());
            uiDataItem.put(Item.UI_TAG_PRICE, item.getPrice());
            uiDataList.add(uiDataItem);
        }
        return uiDataList;
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
