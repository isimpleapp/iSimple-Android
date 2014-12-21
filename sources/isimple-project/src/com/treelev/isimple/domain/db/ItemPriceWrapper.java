package com.treelev.isimple.domain.db;

import java.util.List;

public class ItemPriceWrapper {
    
    private List<ItemPrice> itemsPricesList;
    private List<String> itemsIds;
    
    public ItemPriceWrapper(List<ItemPrice> itemsPricesList, List<String> itemsIds) {
        super();
        this.itemsPricesList = itemsPricesList;
        this.itemsIds = itemsIds;
    }
    
    public List<ItemPrice> getItemsPricesList() {
        return itemsPricesList;
    }
    public List<String> getItemsIds() {
        return itemsIds;
    }

}
