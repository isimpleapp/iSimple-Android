package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import org.xmlpull.v1.XmlPullParser;

public interface Parser {
    public final static int MAX_SIZE_DATA_TO_ITEM_AVAILABILITY_LIST = 10000;
    public final static int MAX_SIZE_DATA_TO_ITEM_LIST = 1000;
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO...daoList);
}
