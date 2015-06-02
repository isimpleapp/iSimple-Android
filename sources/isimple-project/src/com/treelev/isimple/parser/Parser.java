package com.treelev.isimple.parser;

import java.util.List;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.domain.db.Item;

import org.xmlpull.v1.XmlPullParser;

public interface Parser {
    public final static int MAX_SIZE_DATA_TO_ITEM_AVAILABILITY_LIST = 10000;
    public final static int MAX_SIZE_DATA_TO_ITEM_LIST = 1000;
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO...daoList);
}
