
package com.treelev.isimple.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.data.ShoppingCartDAO;
import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.domain.db.ItemPriceWrapper;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.parser.CatalogItemParser;
import com.treelev.isimple.parser.FeaturedItemsParser;
import com.treelev.isimple.parser.ItemPriceDiscountParser;
import com.treelev.isimple.parser.ItemPricesParser;
import com.treelev.isimple.utils.LogUtils;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.managers.WebServiceManager;

public class SyncServcie extends Service {

    public final static String LAST_UPDATED_DATE = "date_for_last_update";
    private static SyncDataTask syncDataTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i("", "Started sync service");
        if (isExternalStorageAvailable()) {
            Context context = getApplication();
            if (!SharedPreferencesManager.isPreparationUpdate(context)) {
                Log.v("Test log start update", "start update");
                SharedPreferencesManager.setUpdateReady(context, false);
                SharedPreferencesManager.setPreparationUpdate(context, true);
                syncDataTask = new SyncDataTask(context);
                syncDataTask.execute();
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    public static boolean isSyncDataTaskRunning() {
        if (syncDataTask == null || syncDataTask.getStatus() != AsyncTask.Status.RUNNING) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            externalStorageAvailable = externalStorageWriteable = false;
        }
        return externalStorageAvailable && externalStorageWriteable;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class SyncDataTask extends AsyncTask<Void, Void, Void> {

        private WebServiceManager webServiceManager;

        private boolean error;

        public SyncDataTask(Context context) {
            this.webServiceManager = new WebServiceManager();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            error = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            
            // TODO Clean temp folder

            ItemDAO itemDAO = new ItemDAO(ISimpleApp.getInstantce());
            ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO(ISimpleApp.getInstantce());

            LogUtils.i("", "Downloading update urls file");
            Map<String, LoadFileData> loadFileDataList = null;
            try {
                loadFileDataList = webServiceManager.getLoadFileDataMap(SharedPreferencesManager
                        .getUpdateFileUrl());
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (loadFileDataList == null) {
                // TODO Handle download exception
                return null;
            }

            LogUtils.i("", "DONE Downloading update urls file, size = " + loadFileDataList.size()
                    + " list = " + loadFileDataList);

            // 1. Download ITEMS_PRICE
            LogUtils.i("", "Downloading ITEMS_PRICE file");
            File itemsPriceArchive = webServiceManager.downloadFile(loadFileDataList.get(
                    UpdateFile.ITEM_PRICES.getUpdateFileTag()).getFileUrl());

            // 2. Unzip itemsPriceArchive
            LogUtils.i("", "Unzipping ITEMS_PRICE file");
            File unzippedItemsPrice = unzipFile(itemsPriceArchive);

            // 3. Parse itemsPrice xml. Get items to be updated
            LogUtils.i("", "Parsing ITEMS_PRICE file");
            ItemPriceWrapper itemPriceWrapper = null;
            ItemPricesParser itemPricesParser = new ItemPricesParser();
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new FileInputStream(unzippedItemsPrice), null);
                itemPriceWrapper = itemPricesParser.parseListItems(xmlPullParser);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                error = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                error = true;
            }
            LogUtils.i("", "DONE Parsing ITEMS_PRICE file, ids list size = "
                    + itemPriceWrapper.getItemsIds().size() + ", items list size = "
                    + itemPriceWrapper.getItemsPricesList().size());

            // 3.1 Get items ids from db
            LogUtils.i("", "Getting itemsIds from db");
            List<String> itemsIds = itemDAO.getItemsIds();
            LogUtils.i("", "DONE Getting itemsIds from db, list size = " + itemsIds.size());

            // 3.2 Get new items list
            LogUtils.i("", "Getting new items ids list");
            List<String> newItemsIds = null;
            if (itemPriceWrapper != null && itemPriceWrapper.getItemsIds() != null) {
                newItemsIds = new ArrayList<String>(itemPriceWrapper.getItemsIds());
                newItemsIds.removeAll(itemsIds);
            }
            LogUtils.i("", "DONE Getting new items ids list, size = " + newItemsIds.size());

            // 3.3 Delete missing items
            LogUtils.i("", "Deleting missing items");
            List<String> deletedItemsIds = null;
            if (itemPriceWrapper != null && itemPriceWrapper.getItemsIds() != null) {
                itemsIds.removeAll(itemPriceWrapper.getItemsIds());
                deletedItemsIds = new ArrayList<String>(itemsIds);
            }
            LogUtils.i("", "Missing items list size = " + deletedItemsIds.size());
            LogUtils.i("", "Deleting missing items from db items table");
            itemDAO.deleteItems(deletedItemsIds);
            LogUtils.i("", "Deleting missing items from db shopping cart table");
            shoppingCartDAO.deleteItems(deletedItemsIds);

            // 4. Delete unzippedItemsPrize file
            LogUtils.i("", "Deleting missing items file");
            unzippedItemsPrice.delete();

            // 5. Download new items
            LogUtils.i("", "Downloading new items xml files");
            List<File> catalogItemXmlFilesList = new ArrayList<File>();
            if (newItemsIds != null && !newItemsIds.isEmpty()) {
                for (String itemId : newItemsIds) {
                    LogUtils.i("", "file url = " + loadFileDataList
                            .get(UpdateFile.CATALOG_UPDATES.getUpdateFileTag())
                            .getFileUrl()
                            .replace(".xmlz", "/")
                            + itemId.substring(0, 2) + "/"
                            + itemId + ".xmlz");
                    File catalogItemXml = webServiceManager
                            .downloadFile(loadFileDataList
                                    .get(UpdateFile.CATALOG_UPDATES.getUpdateFileTag())
                                    .getFileUrl()
                                    .replace(".xmlz", "/")
                                    + itemId.substring(0, 2) + "/"
                                    + itemId + ".xmlz");
                    if (catalogItemXml != null) {
                        catalogItemXmlFilesList.add(catalogItemXml);
                    } else {
                        error = true;
                    }
                }
            }
            LogUtils.i("", "DONE Downloading new items xml files, files list size = " + catalogItemXmlFilesList.size());
            
            // 5.1 Unzip new items files

            // 6. Parse and update/add items
            LogUtils.i("", "Parsing new items xml files to dp");
            for (File itemPriceXmlArchive : catalogItemXmlFilesList) {
                LogUtils.i("", "Unzipping file " + itemPriceXmlArchive.getName());
                File uzippedFile = unzipFile(itemPriceXmlArchive);
                itemPriceXmlArchive.delete();
                
                LogUtils.i("", "Parsing file " + itemPriceXmlArchive.getName());
                CatalogItemParser catalogParser = new CatalogItemParser();

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xmlPullParser = factory.newPullParser();
                    xmlPullParser.setInput(new FileInputStream(uzippedFile), null);
                    catalogParser.parseXmlToDB(xmlPullParser, itemDAO);

                    itemPriceXmlArchive.delete();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    error = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    error = true;
                }
            }
            LogUtils.i("", "Done new items xml files to dp");

            // 7. Update items prices
            LogUtils.i("", "Updating items prices in dp");
            if (itemPriceWrapper.getItemsPricesList() != null
                    && !itemPriceWrapper.getItemsPricesList().isEmpty()) {
                itemDAO.updatePriceList(itemPriceWrapper.getItemsPricesList());
            }

            // 8. Update discounts
            // 8.1. Download price discounts
            LogUtils.i("", "Downloading price discounts");
            File priceDiscountsArchive = webServiceManager
                    .downloadFile(loadFileDataList.get(UpdateFile.DISCOUNT.getUpdateFileTag())
                            .getFileUrl());

            // 8.2. Unzip itemsPriceArchive
            LogUtils.i("", "Unzipping price discounts");
            File unzippedPriceDiscounts = unzipFile(priceDiscountsArchive);

            // 8.3 Parse and save price discounts to DB
            LogUtils.i("", "Parsing price discounts to db");
            ItemPriceDiscountParser discountParser = new ItemPriceDiscountParser();

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new FileInputStream(unzippedPriceDiscounts), null);
                discountParser.parseXmlToDB(xmlPullParser, itemDAO);

                unzippedPriceDiscounts.delete();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                error = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                error = true;
            }

            // // 9. Update offers list
            // // 9.1. Download offers list
            // File offersArchive =
            // webServiceManager.downloadFile(Constants.URL_OFFERS);
            //
            // // 9.2. Unzip offers list
            // File unzippedOffers = null;
            // if (offersArchive != null) {
            // try {
            // ZipInputStream zipInputStream = new ZipInputStream(new
            // FileInputStream(
            // offersArchive));
            // ZipEntry zipEntry = zipInputStream.getNextEntry();
            // byte[] buffer = new byte[4096];
            // FileOutputStream fileOutputStream = null;
            // if (zipEntry == null) {
            // Log.i(getClass().getName(), "File " + offersArchive.getPath()
            // + " don't unpack");
            // }
            // while (zipEntry != null) {
            // String fileName = zipEntry.getName();
            // unzippedOffers = new File(offersArchive.getParent()
            // + File.separator + fileName);
            // fileOutputStream = new FileOutputStream(unzippedOffers);
            // int len;
            // while ((len = zipInputStream.read(buffer)) > 0) {
            // fileOutputStream.write(buffer, 0, len);
            // }
            // fileOutputStream.close();
            // zipEntry = zipInputStream.getNextEntry();
            // }
            // zipInputStream.closeEntry();
            // zipInputStream.close();
            // if (fileOutputStream != null) {
            // fileOutputStream.close();
            // offersArchive.delete();
            // error = true;
            // }
            // } catch (Exception e) {
            // e.printStackTrace();
            // error = true;
            // }
            // }
            //
            // // 9.3 Parse and save offers list to DB
            // OffersParser offersParser = new OffersParser();
            //
            // try {
            // XmlPullParserFactory factory =
            // XmlPullParserFactory.newInstance();
            // factory.setNamespaceAware(true);
            // XmlPullParser xmlPullParser = factory.newPullParser();
            // xmlPullParser.setInput(new FileInputStream(unzippedOffers),
            // null);
            // offersParser.parseXmlToDB(xmlPullParser, itemDAO);
            //
            // unzippedOffers.delete();
            // } catch (XmlPullParserException e) {
            // e.printStackTrace();
            // } catch (FileNotFoundException e) {
            // e.printStackTrace();
            // }

            // 10. Update featured
            // 10.1. Download featured
            LogUtils.i("", "Downloading featured");
            File featuredArchive = webServiceManager.downloadFile(loadFileDataList.get(
                    UpdateFile.OFFERS.getUpdateFileTag()).getFileUrl());

            // 10.2. Unzip featured list
            LogUtils.i("", "Unzipping featured");
            File unzippedFeatured = unzipFile(featuredArchive);

            // 10.3 Parse and save featured list to DB
            LogUtils.i("", "Parsing featured to db");
            FeaturedItemsParser featuredParser = new FeaturedItemsParser();

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new FileInputStream(unzippedFeatured), null);
                featuredParser.parseXmlToDB(xmlPullParser, itemDAO);

                unzippedFeatured.delete();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                error = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                error = true;
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            syncDataTask = null;

            if (!error) {

            } else {
                SharedPreferencesManager.setPreparationUpdate(ISimpleApp.getInstantce(), false);
            }
        }
        
        private File unzipFile(File archive) {
            File unzipped = null;
            if (archive != null) {
                try {
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(
                            archive));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    byte[] buffer = new byte[4096];
                    FileOutputStream fileOutputStream = null;
                    if (zipEntry == null) {
                        Log.i(getClass().getName(), "File " + archive.getPath()
                                + " don't unpack");
                    }
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        unzipped = new File(archive.getParent()
                                + File.separator + fileName);
                        fileOutputStream = new FileOutputStream(unzipped);
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                        fileOutputStream.close();
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.closeEntry();
                    zipInputStream.close();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                        archive.delete();
                        error = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                }
            }
            
            return unzipped;
        }

    }
}
