package com.treelev.isimple.domain.comparators;

import com.treelev.isimple.domain.db.Item;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 20.04.13
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */
public class ItemComparePrice implements Comparator<Item> {
    @Override
    public int compare(Item prev, Item next) {
        Float prevPrice = 0F;
        if(prev.getPrice() != null && prev.getPrice().trim().length() != 0 ) {
            prevPrice = Float.valueOf(prev.getPrice());
        }
        Float nextIPrice = 0F;
        if(next.getPrice() != null && next.getPrice().trim().length() != 0 ) {
            nextIPrice = Float.valueOf(next.getPrice());
        }
        return prevPrice.compareTo(nextIPrice);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
