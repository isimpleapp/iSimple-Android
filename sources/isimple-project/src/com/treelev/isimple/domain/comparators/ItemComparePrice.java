package com.treelev.isimple.domain.comparators;

import com.treelev.isimple.domain.db.Item;

import java.util.Comparator;

public class ItemComparePrice implements Comparator<Item> {
    @Override
    public int compare(Item prev, Item next) {
        Float prevPrice = getPriceValue(prev);
        Float nextPrice = getPriceValue(next);
        return prevPrice.compareTo(nextPrice);
    }

    private Float getPriceValue(Item item) {
        Float price = 0F;
        if (item.getPrice() != null && item.getPrice().trim().length() != 0) {
            price = Float.valueOf(item.getPrice());
        }
        return price;
    }
}
