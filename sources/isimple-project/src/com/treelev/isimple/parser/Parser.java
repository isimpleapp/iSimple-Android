package com.treelev.isimple.parser;

import com.treelev.isimple.data.BaseDAO;
import org.xmlpull.v1.XmlPullParser;

public interface Parser {
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO...daoList);
}
