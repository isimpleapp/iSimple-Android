package com.treelev.isimple.domain.comparators;

import com.treelev.isimple.domain.db.Item;

import java.util.Comparator;

public class ItemCompareName implements Comparator<Item> {

    @Override
    public int compare(Item prev, Item next) {
        return prev.getName().compareTo(next.getName());
    }
}
