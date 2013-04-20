package com.treelev.isimple.domain.comparators;

import com.treelev.isimple.domain.db.Item;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 20.04.13
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public class ItemCompareName implements Comparator<Item> {
    @Override
    public int compare(Item prev, Item next) {
        return prev.getName().compareTo(next.getName());  //To change body of implemented methods use File | Settings | File Templates.
    }
}
