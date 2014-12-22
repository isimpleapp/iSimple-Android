
package com.treelev.isimple.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.data.ShoppingCartDAO;
import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.domain.db.ItemPrice;
import com.treelev.isimple.domain.db.ItemPriceWrapper;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.parser.CatalogItemParser;
import com.treelev.isimple.parser.FeaturedItemsParser;
import com.treelev.isimple.parser.ItemPriceDiscountParser;
import com.treelev.isimple.parser.ItemPricesParser;
import com.treelev.isimple.parser.OffersParser;
import com.treelev.isimple.utils.Constants;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.managers.WebServiceManager;

import org.holoeverywhere.util.ArrayUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SyncServcie extends Service {

    public final static String LAST_UPDATED_DATE = "date_for_last_update";
    private static SyncDataTask mDownloadDataTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isExternalStorageAvailable()) {
            Context context = getApplication();
            if (!SharedPreferencesManager.isPreparationUpdate(context)) {
                Log.v("Test log start update", "start update");
                SharedPreferencesManager.setUpdateReady(context, false);
                SharedPreferencesManager.setPreparationUpdate(context, true);
                mDownloadDataTask = new SyncDataTask(context);
                mDownloadDataTask.execute();
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    public static boolean isDownloadDataTaskRunning() {
        if (mDownloadDataTask == null || mDownloadDataTask.getStatus() != AsyncTask.Status.RUNNING) {
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

            ItemDAO itemDAO = new ItemDAO(ISimpleApp.getInstantce());
            ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO(ISimpleApp.getInstantce());

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

            // 1. Download ITEMS_PRICE
            File itemsPriceArchive = webServiceManager.downloadFile(loadFileDataList.get(
                    UpdateFile.ITEM_PRICES.getUpdateFileTag()).getFileUrl());

            // 2. Unzip itemsPriceArchive
            File unzippedItemsPrice = null;
            if (itemsPriceArchive != null) {
                try {
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(
                            itemsPriceArchive));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    byte[] buffer = new byte[4096];
                    FileOutputStream fileOutputStream = null;
                    if (zipEntry == null) {
                        Log.i(getClass().getName(), "File " + itemsPriceArchive.getPath()
                                + " don't unpack");
                    }
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        unzippedItemsPrice = new File(itemsPriceArchive.getParent()
                                + File.separator + fileName);
                        fileOutputStream = new FileOutputStream(unzippedItemsPrice);
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
                        itemsPriceArchive.delete();
                        error = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                }
            } else {
                error = true;
            }

            // 3. Parse itemsPrice xml. Get items to be updated
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

            // 3.1 Get items ids from db
            List<String> itemsIds = itemDAO.getItemsIds();

            // 3.2 Get new items list
            List<String> newItemsIds = null;
            if (itemPriceWrapper != null && itemPriceWrapper.getItemsIds() != null) {
                newItemsIds = new ArrayList<String>(itemPriceWrapper.getItemsIds());
                newItemsIds.removeAll(itemsIds);
            }

            // 3.3 Delete missing items
            List<String> deletedItemsIds = null;
            if (itemPriceWrapper != null && itemPriceWrapper.getItemsIds() != null) {
                itemsIds.removeAll(itemPriceWrapper.getItemsIds());
                deletedItemsIds = new ArrayList<String>(itemsIds);
            }
            itemDAO.deleteItems(deletedItemsIds);
            shoppingCartDAO.deleteItems(deletedItemsIds);

            // 4. Delete unzippedItemsPrize file
            unzippedItemsPrice.delete();

            // 5. Download new items
            List<File> catalogItemXmlFilesList = new ArrayList<File>();
            if (newItemsIds != null && !newItemsIds.isEmpty()) {
                for (String itemId : newItemsIds) {
                    File catalogItemXml = webServiceManager
                            .downloadFile(loadFileDataList
                                    .get(UpdateFile.CATALOG_UPDATES.getUpdateFileTag())
                                    .getFileUrl()
                                    .replace(".xmlz", "/")
                                    + itemId.substring(0, 2) + "/"
                                    + itemId);
                    if (catalogItemXml != null) {
                        catalogItemXmlFilesList.add(catalogItemXml);
                    } else {
                        error = true;
                    }
                }
            }

            // 6. Parse and update/add items
            for (File itemPriceXmlFile : catalogItemXmlFilesList) {
                CatalogItemParser catalogParser = new CatalogItemParser();

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xmlPullParser = factory.newPullParser();
                    xmlPullParser.setInput(new FileInputStream(itemPriceXmlFile), null);
                    catalogParser.parseXmlToDB(xmlPullParser, itemDAO);

                    itemPriceXmlFile.delete();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    error = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    error = true;
                }
            }

            // 7. Update items prices
            if (itemPriceWrapper.getItemsPricesList() != null
                    && !itemPriceWrapper.getItemsPricesList().isEmpty()) {
                itemDAO.updatePriceList(itemPriceWrapper.getItemsPricesList());
            }

            // 8. Update discounts
            // 8.1. Download price discounts
            File priceDiscountsArchive = webServiceManager
                    .downloadFile(loadFileDataList.get(UpdateFile.DISCOUNT.getUpdateFileTag())
                            .getFileUrl());

            // 8.2. Unzip itemsPriceArchive
            File unzippedPriceDiscounts = null;
            if (priceDiscountsArchive != null) {
                try {
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(
                            priceDiscountsArchive));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    byte[] buffer = new byte[4096];
                    FileOutputStream fileOutputStream = null;
                    if (zipEntry == null) {
                        Log.i(getClass().getName(), "File " + priceDiscountsArchive.getPath()
                                + " don't unpack");
                    }
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        unzippedPriceDiscounts = new File(priceDiscountsArchive.getParent()
                                + File.separator + fileName);
                        fileOutputStream = new FileOutputStream(unzippedPriceDiscounts);
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
                        priceDiscountsArchive.delete();
                        error = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                }
            } else {
                error = true;
            }

            // 8.3 Parse and save price discounts to DB
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
            File featuredArchive = webServiceManager.downloadFile(loadFileDataList.get(
                    UpdateFile.OFFERS.getUpdateFileTag()).getFileUrl());

            // 10.2. Unzip featured list
            File unzippedFeatured = null;
            if (featuredArchive != null) {
                try {
                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(
                            featuredArchive));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    byte[] buffer = new byte[4096];
                    FileOutputStream fileOutputStream = null;
                    if (zipEntry == null) {
                        Log.i(getClass().getName(), "File " + featuredArchive.getPath()
                                + " don't unpack");
                    }
                    while (zipEntry != null) {
                        String fileName = zipEntry.getName();
                        unzippedFeatured = new File(featuredArchive.getParent()
                                + File.separator + fileName);
                        fileOutputStream = new FileOutputStream(unzippedFeatured);
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
                        featuredArchive.delete();
                        error = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                }
            }

            // 10.3 Parse and save featured list to DB
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
            mDownloadDataTask = null;

            if (!error) {

            } else {
                SharedPreferencesManager.setPreparationUpdate(ISimpleApp.getInstantce(), false);
            }
        }

    }
}
