package com.treelev.isimple.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.data.ChainDAO;
import com.treelev.isimple.data.DeliveryZoneDAO;
import com.treelev.isimple.data.DeprecatedItemDAO;
import com.treelev.isimple.data.ItemAvailabilityDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.parser.CatalogParser;
import com.treelev.isimple.parser.DeliveryZoneParser;
import com.treelev.isimple.parser.DeprecatedItemParser;
import com.treelev.isimple.parser.FeaturedItemsParser;
import com.treelev.isimple.parser.ItemAvailabilityParser;
import com.treelev.isimple.parser.ItemPriceDiscountParser;
import com.treelev.isimple.parser.ItemPricesParser;
import com.treelev.isimple.parser.Parser;
import com.treelev.isimple.parser.ShopAndChainsParser;

public class FileParseObject implements Comparable<FileParseObject>, Serializable {

    private File file;
    private FilePriority filePriority;
    private Parser parser;
    private BaseDAO[] daoList;
    private static List<FilePriority> filePriorityList;

    static {
        if (filePriorityList == null) {
            filePriorityList = new ArrayList<FilePriority>();
            logFileNameCompareProcess("static", "");
            filePriorityList.add(new FilePriority(CatalogParser.getFileName(), 1));
            filePriorityList.add(new FilePriority(ShopAndChainsParser.getFileName(), 2));
            filePriorityList.add(new FilePriority(ItemAvailabilityParser.getFileName(), 3));
            filePriorityList.add(new FilePriority(ItemPricesParser.getFileName(), 4));
            filePriorityList.add(new FilePriority(ItemPriceDiscountParser.getFileName(), 5));
            filePriorityList.add(new FilePriority(FeaturedItemsParser.getFileName(), 6));
            filePriorityList.add(new FilePriority(DeprecatedItemParser.getFileName(), 7));
            filePriorityList.add(new FilePriority(DeliveryZoneParser.getFileName(), 8));
        }
    }

    public FileParseObject(File file, Context context) {
        this.file = file;
        this.filePriority = getFilePriorityByName(file.getName());
        this.parser = getParserByName(file.getName());
        this.daoList = getDaoListByName(file.getName(), context);
    }

    public void parseObjectDataToDB() {
        try {
            if(parser != null){
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new FileInputStream(file), null);
                parser.parseXmlToDB(xmlPullParser, daoList);
                file.delete();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getFileName(){
        return file != null ? file.getName() : "";
    }

    private BaseDAO[] getDaoListByName(String fileName, Context context) {
    	logFileNameCompareProcess("getDaoListByName", fileName);
        List<BaseDAO> baseDAOList = new ArrayList<BaseDAO>();
        if (fileName.equals(ItemPricesParser.getFileName())) {
            baseDAOList.add(new ItemDAO(context));
        } if (fileName.equals(ItemPriceDiscountParser.getFileName())) {
            baseDAOList.add(new ItemDAO(context));
        } else if (fileName.equals(ShopAndChainsParser.getFileName())) {
            baseDAOList.add(new ChainDAO(context));
            baseDAOList.add(new ShopDAO(context));
        } else if (fileName.equals(CatalogParser.getFileName())) {
            baseDAOList.add(new ItemDAO(context));
        } else if (fileName.equals(ItemAvailabilityParser.getFileName())) {
            baseDAOList.add(new ItemAvailabilityDAO(context));
        } else if (fileName.equals(FeaturedItemsParser.getFileName())) {
            baseDAOList.add(new ItemDAO(context));
        } else if (fileName.equals(DeprecatedItemParser.getFileName())) {
            baseDAOList.add(new DeprecatedItemDAO(context));
        } else if (fileName.equals(DeliveryZoneParser.getFileName())) {
            baseDAOList.add(new DeliveryZoneDAO(context));
        }
        return baseDAOList.toArray(new BaseDAO[baseDAOList.size()]);
    }

    private Parser getParserByName(String fileName) {
    	logFileNameCompareProcess("getParserByName", fileName);
        if (fileName.equals(ItemPricesParser.getFileName())) {
            return new ItemPricesParser();
        } else if (fileName.equals(ShopAndChainsParser.getFileName())) {
            return new ShopAndChainsParser();
        } else if (fileName.equals(CatalogParser.getFileName())) {
            return new CatalogParser();
        } else if (fileName.equals(ItemAvailabilityParser.getFileName())) {
            return new ItemAvailabilityParser();
        } else if (fileName.equals(FeaturedItemsParser.getFileName())) {
            return new FeaturedItemsParser();
        } else if (fileName.equals(DeprecatedItemParser.getFileName())) {
            return new DeprecatedItemParser();
        } else if (fileName.equals(DeliveryZoneParser.getFileName())) {
            return new DeliveryZoneParser();
        } else if (fileName.equals(ItemPriceDiscountParser.getFileName())) {
        	return new ItemPriceDiscountParser();
        } else {
            return null;
        }
    }

    private FilePriority getFilePriorityByName(String fileName) {
    	logFileNameCompareProcess("getFilePriorityByName", fileName);
        for (FilePriority filePriority : filePriorityList) {
            if (filePriority.getFileName().equals(fileName)) {
                return filePriority;
            }
        }
        return null;
    }
    
    @Override
    public int compareTo(FileParseObject another) {
        Integer compare  = another != null ? another.filePriority.getPriority() : 0;
        return filePriority.getPriority().compareTo(compare);
    }
    
    private static void logFileNameCompareProcess(String method, String fileName) {
//    	Log.i("", method + ", fileName = " + fileName);
//    	Log.i("", ItemPricesParser.getFileName());
//    	Log.i("", ShopAndChainsParser.getFileName());
//    	Log.i("", CatalogParser.getFileName());
//    	Log.i("", ItemAvailabilityParser.getFileName());
//    	Log.i("", FeaturedItemsParser.getFileName());
//    	Log.i("", DeprecatedItemParser.getFileName());
//    	Log.i("", DeliveryZoneParser.getFileName());
//    	Log.i("", ItemPriceDiscountParser.getFileName());
    }
}
