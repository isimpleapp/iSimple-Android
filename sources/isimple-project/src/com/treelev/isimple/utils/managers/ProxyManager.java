package com.treelev.isimple.utils.managers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;

import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ChainDAO;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.data.DeliveryZoneDAO;
import com.treelev.isimple.data.FavouriteItemDAO;
import com.treelev.isimple.data.ItemAvailabilityDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.data.OfferDAO;
import com.treelev.isimple.data.SectionsItemsDAO;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.data.ShoppingCartDAO;
import com.treelev.isimple.domain.db.DeliveryZone;
import com.treelev.isimple.domain.db.Item;
import com.treelev.isimple.domain.db.Offer;
import com.treelev.isimple.domain.db.Order;
import com.treelev.isimple.domain.ui.AbsDistanceShop;
import com.treelev.isimple.domain.ui.filter.FilterItemData;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.utils.LogUtils;

public class ProxyManager {

    private Context context;

    public static final int SORT_NAME_AZ = 1;
    public static final int SORT_PRICE_UP = 2;
    public static final int SORT_DEFAULT = 3;
    public static final int TYPE_SECTION_MAIN = 10;
    public static final int TYPE_SECTION_SHOP_MAIN = 11;
    public static final int TYPE_SECTION_FILTRATION_SEARCH = 13;
    public static final int TYPE_SECTION_SUB_CATEGORY = 14;

    private static ProxyManager instanse;

    private HashMap<Integer, BaseDAO> mdao = new HashMap<Integer, BaseDAO>();

    public static ProxyManager getInstanse() {
        if (instanse == null) {
            instanse = new ProxyManager();
        }
        return instanse;
    }
    
    private ProxyManager() {
        this.context = ISimpleApp.getInstantce();
    }

    public Item getItemById(String itemId) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemById(itemId);
    }
    
    public Cursor getItemsByIds(List<Long> itemsIds) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemsByIds(itemsIds);
    }

    public Cursor getItemByBarcode(String barcode, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemByBarcode(barcode, orderByField);
    }

    public Cursor getItemDeprecatedByBarcode(String barcode, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemDeprecatedByBarcode(barcode, orderByField);
    }

    public Item getItemByBarcodeTypeItem(String barcode) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemByBarcodeTypeItem(barcode);
    }

    public Item getItemDeprecatedByBarcodeTypeItem(String barcode) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemDeprecatedByBarcodeTypeItem(barcode);
    }

    public int getCountBarcode(String barcode) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getCountBarcode(barcode);
    }

    public int getCountBarcodeInDeprecatedTable(String barcode) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getCountBarcodeInDeprecatedTable(barcode);
    }

    public Cursor getFeaturedMainItems() {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getFeaturedMainItems();
    }

    public List<Integer> getYearsByCategory(DrinkCategory category) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getYearsByCategory(category.ordinal());
    }

    public List<String> getManufactureByCategory(DrinkCategory category) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getManufactureByCategory(category.ordinal());
    }

    public Map<String, FilterItemData[]> getRegionsByCategory(DrinkCategory category) {
        Map<String, List<String>> regionsByCategory = ((ItemDAO) getObjectDAO(ItemDAO.ID)).getRegionsByCategory(category.ordinal());
        Map<String, Integer> productCountByCountriesBy = ((ItemDAO) getObjectDAO(ItemDAO.ID)).getProductCountsByCountries(category.ordinal());
        CountriesComparator countriesComparator = new CountriesComparator(productCountByCountriesBy);
        Map<String, FilterItemData[]> result = new TreeMap<String, FilterItemData[]>(countriesComparator);
        for (String country : regionsByCategory.keySet()) {
            List<String> r = regionsByCategory.get(country);
            FilterItemData[] regionsData;
            if (r != null) {
                regionsData = new FilterItemData[r.size()];
                for (int i = 0; i < r.size(); i++) {
                    regionsData[i] = new FilterItemData(r.get(i));
                }
            } else {
                regionsData = new FilterItemData[0];
            }
            result.put(country, regionsData);
        }
        return result;
    }

    private class CountriesComparator implements Comparator<String> {

        private Map<String, Integer> howSort;

        private CountriesComparator(Map<String, Integer> howSort) {
            this.howSort = howSort;
        }

        @Override
        public int compare(String a, String b) {
            Integer indexA = null;
            Integer indexB = null;
            Set<String> values = howSort.keySet();
            int index = 0;
            for (String country : values) {
                if (country.equals(a)) {
                    indexA = index;
                } else if (country.equals(b)) {
                    indexB = index;
                }
                index++;
            }
            if (indexA != null && indexB != null) {
                int compareValue = indexA.compareTo(indexB);
                return compareValue == 0 ? a.compareTo(b) : compareValue;
            }
            return 0;
        }
    }

    public List<String> getRegionsByCountryCategory(String country, DrinkCategory category) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getRegionsByCountriesCategory(country, category.ordinal());
    }

    public Map<ProductType, List<String>> getClassificationsByCategory(DrinkCategory category) {
        HashMap<ProductType, List<String>> result = new HashMap<ProductType, List<String>>();
        Map<Integer, List<String>> classifications = ((ItemDAO) getObjectDAO(ItemDAO.ID))
                .getClassificationsByCategory(category.ordinal());
        for (Integer key : classifications.keySet()) {
            result.put(ProductType.getProductType(key), classifications.get(key));
        }
        return result;
    }

    public Cursor getFeaturedItemsByCategory(int categoryId, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getFeaturedItemsByCategory(categoryId, orderByField);
    }

    public Cursor getAllItemsByCategory(int categoryId, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getAllItemsByCategory(categoryId, orderByField);
    }

    public Cursor getFeaturedItemsByCategory(int categoryId, String locationId, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getFeaturedItemsByCategory(categoryId, locationId, orderByField);
    }

    public Cursor getItemsByDrinkId(String drinkId, String locationID, String query, boolean search, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemsByDrinkId(drinkId, locationID, query, search, orderByField);
    }

    public Cursor getItemsByDrinkIdPreOrder(String drinkId, String locationID, String query, boolean search, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemsByDrinkIdPreOrder(drinkId, locationID, query, search, orderByField);
    }

    public Cursor getItemsByDrinkId(String drinkId, String filterQuery, String locationID, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemsByDrinkId(drinkId, filterQuery, locationID, orderByField);
    }

    public Cursor getItemsByDrinkIdPreOrder(String drinkId, String filterQuery, String locationID, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemsByDrinkIdPreOrder(drinkId, filterQuery, locationID, orderByField);
    }

    public Cursor getFilteredItemsByCategory(Integer categoryId, String query, int sortType, boolean group) {
        return getFilteredItemsByCategory(categoryId, null, query, sortType, group);
    }

    public Cursor getFilteredItemsByCategory(Integer categoryId, String locationId, String query, int sortType, boolean group) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getFilteredItemsByCategory(categoryId, locationId, query, orderByField, group);
    }

    public Cursor getFilteredItemsByCategoryPreOrder(Integer categoryId, String query, int sortType, boolean group) {
        return getFilteredItemsByCategoryPreOrder(categoryId, null, query, sortType, group);
    }

    public Cursor getFilteredItemsByCategoryPreOrder(Integer categoryId, String locationId, String query, int sortType, boolean group) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getFilteredItemsByCategoryPreOrder(categoryId, locationId, query, orderByField, group);
    }

    public Cursor getSearchItemsByCategory(Integer categoryId, String query, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getSearchItemsByCategory(categoryId, query, orderByField);
    }

    public Cursor getSearchItemsByCategory(Integer categoryId, String locationId, String query, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getSearchItemsByCategory(categoryId, locationId, query, orderByField);
    }

    public Cursor getSearchItemsByCategoryPreOrder(Integer categoryId, String query, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getSearchItemsByCategoryPreOrder(categoryId, query, orderByField);
    }

    public Cursor getSearchItemsByCategoryPreOrder(Integer categoryId, String locationId, String query, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getSearchItemsByCategoryPreOrder(categoryId, locationId, query, orderByField);
    }

    public Cursor getAllItems(int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getAllItems(orderByField);
    }

    public Cursor getItems() {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItems();
    }

    public Cursor getAllItemsByCategory(Integer categoryId, int sortType) {
        String orderByField =
                (sortType == SORT_NAME_AZ) ? DatabaseSqlHelper.ITEM_NAME :
                        (sortType == SORT_PRICE_UP) ? DatabaseSqlHelper.ITEM_PRICE : null;
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getAllItemsByCategory(categoryId, orderByField);
    }

    public Cursor getChains() {
        return ((ChainDAO) getObjectDAO(ChainDAO.ID)).getChains();
    }

    public Cursor getChainsByItemId(String itemId) {
        return ((ChainDAO) getObjectDAO(ChainDAO.ID)).getChains(itemId);
    }

    public List<AbsDistanceShop> getNearestShopsByItemId(String drinkId, Location currentLocation) {
        return ((ShopDAO) getObjectDAO(ShopDAO.ID)).getShopsByDrinkId(drinkId, currentLocation);
    }

    public List<AbsDistanceShop> getShopByChain(Location currentLocation, String chainID) {
        return ((ShopDAO) getObjectDAO(ShopDAO.ID)).getShopByChain(currentLocation, chainID);
    }

    public List<AbsDistanceShop> getNearestShops(Location location) {
        return ((ShopDAO) getObjectDAO(ShopDAO.ID)).getNearestShops(location);
    }

    public Integer getMaxValuePriceByCategoryId(Integer categoryId) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemMaxPriceByCategory(categoryId);
    }

    public Integer getMinValuePriceByCategoryId(Integer categoryId) {
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getItemMinPriceByCategory(categoryId);
    }

    public void setFavouriteItemTable(List<String> itemsId, boolean state) {
        ((ItemDAO) getObjectDAO(ItemDAO.ID)).setFavourite(itemsId, state);
    }

    public List<Boolean> getCountItemsByCategoryByShop( String locationID){
        return ((ItemDAO) getObjectDAO(ItemDAO.ID)).getCountItemsByCategoryByShop(locationID);
    }

    public boolean addFavourites(Item item) {
        return ((FavouriteItemDAO) getObjectDAO(FavouriteItemDAO.ID)).addFavoutites(item);
    }

    public boolean delFavourites(List<String> listItemsId) {
        return ((FavouriteItemDAO) getObjectDAO(FavouriteItemDAO.ID)).delFavouriteItems(listItemsId);
    }

    public boolean isFavourites(String itemId) {
        return ((FavouriteItemDAO) getObjectDAO(FavouriteItemDAO.ID)).isFavourites(itemId);
    }

    public Cursor getFavouriteItems() {
        return ((FavouriteItemDAO) getObjectDAO(FavouriteItemDAO.ID)).getFavouriteItems();
    }

    public Cursor getSectionsItems(int typeSections) {
        LogUtils.i("!!!!!!!!!!!!!!!!", "ProxyManager getSectionsItems typeSections = " + typeSections + ", daoId = " + SectionsItemsDAO.ID);
        return ((SectionsItemsDAO) getObjectDAO(SectionsItemsDAO.ID)).getSectionsItems(typeSections);
    }


    private BaseDAO getObjectDAO(int id) {
        if (mdao.containsKey(id)) {
            return mdao.get(id);
        } else {
            BaseDAO dao =
                    (id == ItemDAO.ID) ? new ItemDAO(context) :
                    (id == ItemAvailabilityDAO.ID) ? new ItemAvailabilityDAO(context) :
                    (id == ShopDAO.ID) ? new ShopDAO(context) :
                    (id == ChainDAO.ID) ? new ChainDAO(context) :
                    (id == FavouriteItemDAO.ID) ? new FavouriteItemDAO(context) :
                    (id == ShoppingCartDAO.ID) ? new ShoppingCartDAO(context) :
                    (id == DeliveryZoneDAO.ID) ? new DeliveryZoneDAO(context) :
                    (id == SectionsItemsDAO.ID) ? new SectionsItemsDAO(context) :
                    (id == OfferDAO.ID) ? new OfferDAO(context) : null;
            if (dao != null) {
                mdao.put(id, dao);
                return dao;
            } else {
                return null;
            }
        }
    }

    public boolean isProductExistShoppingCart(String itemId) {
        return ((ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID)).isProductExistShoppingCart(itemId);
    }

    public long insertProductInShoppingCart(Item product) {
        return ((ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID)).insertItem(product);
    }

    public Cursor getShoppingCartItems() {
        return ((ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID)).getShoppingCartItems();
    }

    public int getCountOrders() {
        return ((ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID)).getCountOrder();
    }

    public int increaseShopCardItemCount(String itemId) {
        ShoppingCartDAO dao = (ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID);
        dao.increaseItemCount(itemId);
        return dao.getItemCount(itemId);
    }

    public int decreaseShopCardItemCount(String itemId) {
        ShoppingCartDAO dao = (ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID);
        dao.decreaseItemCount(itemId);
        return dao.getItemCount(itemId);
    }

    public void removeShopCardItem(String itemId) {
        ((ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID)).deleteItem(itemId);
    }

    public int getShoppingCartPrice() {
        return ((ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID)).getShoppingCartPrice();
    }

    public void deleteAllShoppingCartData() {
        ((ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID)).deleteAllData();
    }

    public int getItemCount(String itemId) {
        return ((ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID)).getItemCount(itemId);
    }

    public List<Order> getOrders() {
        return ((ShoppingCartDAO) getObjectDAO(ShoppingCartDAO.ID)).getOrders();
    }

    public String getDeliveryFirstCountry() {
        return ((DeliveryZoneDAO) getObjectDAO(DeliveryZoneDAO.ID)).getFirstCountryLabel();
    }

    public String[] getCountries() {
        return ((DeliveryZoneDAO) getObjectDAO(DeliveryZoneDAO.ID)).getCountries();
    }

    public String getDeliveryMessage(String country, int price) {
        return ((DeliveryZoneDAO) getObjectDAO(DeliveryZoneDAO.ID)).getMessage(country, price);
    }

    public int getMinPriceByCountry(String country) {
        return ((DeliveryZoneDAO) getObjectDAO(DeliveryZoneDAO.ID)).getMinPriceByCountry(country);
    }

    public DeliveryZone getDeliveryZone(String name) {
        return ((DeliveryZoneDAO) getObjectDAO(DeliveryZoneDAO.ID)).getDeliveryZone(name);
    }
    
    public Offer getOfferById(long offerId) {
        return ((OfferDAO) getObjectDAO(OfferDAO.ID)).getOfferById(offerId);
    }

    public void release() {
        if (mdao.size() > 0) {
            mdao.clear();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }
}
