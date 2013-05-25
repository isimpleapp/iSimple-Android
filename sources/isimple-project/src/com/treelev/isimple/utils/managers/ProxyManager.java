package com.treelev.isimple.utils.managers;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import com.treelev.isimple.R;
import com.treelev.isimple.data.*;
import com.treelev.isimple.domain.comparators.ItemCompareName;
import com.treelev.isimple.domain.comparators.ItemComparePrice;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.DistanceShop;
import com.treelev.isimple.utils.Utils;

import java.util.*;

public class ProxyManager {

    private Context context;

    public static final int SORT_NAME_AZ = 1;
    public static final int SORT_PRICE_UP = 2;
    private final static String FORMAT_TEXT_LABEL = "%s...";
    private final static int FORMAT_NAME_MAX_LENGTH = 41;
    private final static int FORMAT_LOC_NAME_MAX_LENGTH = 30;

    private HashMap<Integer, BaseDAO> mdao = new HashMap<Integer, BaseDAO>();

    public ProxyManager(Context context) {
        this.context = context;
    }

    public Item getItemById(String itemId) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemById(itemId);
    }

    public Cursor getRandomItems() {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getRandomItems();
    }

    public List<String> getYearsByCategory(int categoryId) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getYearsByCategory(categoryId);
    }

    public Map<String, List<String>> getRegionsByCategory(int categoryId) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getRegionsByCategory(categoryId);
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

    public Cursor getItemsByCategory(int categoryId, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemsByCategory(categoryId, orderByField);
    }

    public Cursor getItemsByDrinkId(String drinkId, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemsByDrinkId(drinkId, orderByField);
    }

    public Cursor getSearchItemsByCategory(Integer categoryId, String query, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getSearchItemsByCategory(categoryId, query, orderByField);
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
            uiDataItem.put(Item.UI_TAG_DRINK_ID, item.getDrinkID());
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

    public Cursor getChains() {
        return ((ChainDAO) getObjectDAO(ChainDAO.ID)).getChains();
    }

    public List<AbsDistanceShop> getNearestShops(Location location) {
        return ((ShopDAO) getObjectDAO(ShopDAO.ID)).getNearestShops(location);
    }

    private BaseDAO getObjectDAO(int id) {
        if (mdao.containsKey(id)) {
            return mdao.get(id);
        } else {
            BaseDAO dao =
                    (id == ItemDAO.ID) ? new ItemDAO(context) :
                            (id == ItemAvailabilityDAO.ID) ? new ItemAvailabilityDAO(context) :
                                    (id == ShopDAO.ID) ? new ShopDAO(context) :
                                            (id == ChainDAO.ID) ? new ChainDAO(context) : null;
            if (dao != null) {
                mdao.put(id, dao);
                return dao;
            } else {
                return null;
            }
        }
    }

    public void release() {
        if (mdao.size() > 0) {
            for (BaseDAO dao : mdao.values()) {
                dao.close();
            }
            mdao.clear();
        }
    }
}
