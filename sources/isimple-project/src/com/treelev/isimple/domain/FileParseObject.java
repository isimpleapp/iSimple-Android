package com.treelev.isimple.domain;

import android.content.Context;
import com.treelev.isimple.data.*;
import com.treelev.isimple.parser.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileParseObject implements Comparable<FileParseObject>, Serializable {

    private File file;
    private FilePriority filePriority;
    private Parser parser;
    private BaseDAO[] daoList;
    private static List<FilePriority> filePriorityList;

    static {
        if (filePriorityList == null) {
            filePriorityList = new ArrayList<FilePriority>();
            filePriorityList.add(new FilePriority(CatalogParser.FILE_NAME, CatalogParser.FILE_SECOND_NAME, 1));
            filePriorityList.add(new FilePriority(ShopAndChainsParser.FILE_NAME, ShopAndChainsParser.FILE_SECOND_NAME, 2));
            filePriorityList.add(new FilePriority(ItemAvailabilityParser.FILE_NAME, ItemAvailabilityParser.FILE_SECOND_NAME, 3));
            filePriorityList.add(new FilePriority(ItemPricesParser.FILE_NAME, ItemPricesParser.FILE_SECOND_NAME, 4));
            filePriorityList.add(new FilePriority(ItemPriceDiscountParser.FILE_NAME, ItemPriceDiscountParser.FILE_SECOND_NAME, 5));
            filePriorityList.add(new FilePriority(FeaturedItemsParser.FILE_NAME, FeaturedItemsParser.FILE_SECOND_NAME, 6));
            filePriorityList.add(new FilePriority(DeprecatedItemParser.FILE_NAME, DeprecatedItemParser.FILE_SECOND_NAME, 7));
            filePriorityList.add(new FilePriority(DeliveryZoneParser.FILE_NAME, DeliveryZoneParser.FILE_SECOND_NAME, 8));
        }
    }

    public FileParseObject(File file, Context context) {
        this.file = file;
        this.filePriority = getFilePriorityByName(file.getName());
        if(this.filePriority == null){
        	this.filePriority = getFilePriorityBySecondName(file.getName());
        }
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
        List<BaseDAO> baseDAOList = new ArrayList<BaseDAO>();
        if (fileName.equals(ItemPricesParser.FILE_NAME)) {
            baseDAOList.add(new ItemDAO(context));
        } if (fileName.equals(ItemPriceDiscountParser.FILE_NAME)) {
            baseDAOList.add(new ItemDAO(context));
        } else if (fileName.equals(ShopAndChainsParser.FILE_NAME)) {
            baseDAOList.add(new ChainDAO(context));
            baseDAOList.add(new ShopDAO(context));
        } else if (fileName.equals(CatalogParser.FILE_NAME)) {
            baseDAOList.add(new ItemDAO(context));
        } else if (fileName.equals(ItemAvailabilityParser.FILE_NAME)) {
            baseDAOList.add(new ItemAvailabilityDAO(context));
        } else if (fileName.equals(FeaturedItemsParser.FILE_NAME)) {
            baseDAOList.add(new ItemDAO(context));
        } else if (fileName.equals(DeprecatedItemParser.FILE_NAME)) {
            baseDAOList.add(new DeprecatedItemDAO(context));
        } else if (fileName.equals(DeliveryZoneParser.FILE_NAME)) {
            baseDAOList.add(new DeliveryZoneDAO(context));
        }
        return baseDAOList.toArray(new BaseDAO[baseDAOList.size()]);
    }

    private Parser getParserByName(String fileName) {
        if (fileName.equals(ItemPricesParser.FILE_NAME)) {
            return new ItemPricesParser();
        } else if (fileName.equals(ShopAndChainsParser.FILE_NAME)) {
            return new ShopAndChainsParser();
        } else if (fileName.equals(CatalogParser.FILE_NAME)) {
            return new CatalogParser();
        } else if (fileName.equals(ItemAvailabilityParser.FILE_NAME)) {
            return new ItemAvailabilityParser();
        } else if (fileName.equals(FeaturedItemsParser.FILE_NAME)) {
            return new FeaturedItemsParser();
        } else if (fileName.equals(DeprecatedItemParser.FILE_NAME)) {
            return new DeprecatedItemParser();
        } else if (fileName.equals(DeliveryZoneParser.FILE_NAME)) {
            return new DeliveryZoneParser();
        } else if (fileName.equals(ItemPriceDiscountParser.FILE_NAME)) {
        	return new ItemPriceDiscountParser();
        } else {
            return null;
        }
    }

    private FilePriority getFilePriorityByName(String fileName) {
        for (FilePriority filePriority : filePriorityList) {
            if (filePriority.getFileName().equals(fileName)) {
                return filePriority;
            }
        }
        return null;
    }
    
    private FilePriority getFilePriorityBySecondName(String fileName) {
        for (FilePriority filePriority : filePriorityList) {
            if (filePriority.getFileSecondName().equals(fileName)) {
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
}
