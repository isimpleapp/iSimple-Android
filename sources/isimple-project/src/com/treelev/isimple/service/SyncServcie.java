
package com.treelev.isimple.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParseFile;
import com.treelev.isimple.R;
import com.treelev.isimple.app.ISimpleApp;
import com.treelev.isimple.data.ChainDAO;
import com.treelev.isimple.data.DeliveryZoneDAO;
import com.treelev.isimple.data.ItemAvailabilityDAO;
import com.treelev.isimple.data.ItemDAO;
import com.treelev.isimple.data.OfferDAO;
import com.treelev.isimple.data.ShopDAO;
import com.treelev.isimple.data.ShoppingCartDAO;
import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.domain.db.ItemPriceWrapper;
import com.treelev.isimple.enumerable.SyncPhase;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.parser.CatalogItemParser;
import com.treelev.isimple.parser.DeliveryZoneParser;
import com.treelev.isimple.parser.FeaturedItemsParser;
import com.treelev.isimple.parser.ItemAvailabilityParser;
import com.treelev.isimple.parser.ItemPriceDiscountParser;
import com.treelev.isimple.parser.ItemPricesParser;
import com.treelev.isimple.parser.OffersParser;
import com.treelev.isimple.parser.ShopAndChainsParser;
import com.treelev.isimple.utils.Constants;
import com.treelev.isimple.utils.DownloadFileResponse;
import com.treelev.isimple.utils.LogUtils;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.managers.WebServiceManager;
import com.treelev.isimple.utils.parse.ParseLogUtils;
import com.treelev.isimple.utils.parse.SyncLogEntity;
import com.treelev.isimple.utils.parse.SyncLogEntity.DailyUpdateDuration;
import com.treelev.isimple.utils.parse.SyncLogEntity.FeaturedUpdateDuration;
import com.treelev.isimple.utils.parse.SyncLogEntity.FullUpdateDuration;
import com.treelev.isimple.utils.parse.SyncLogEntity.OffersUpdateDuration;
import com.treelev.isimple.utils.parse.SyncLogEntity.PriceDiscountsUpdateDuration;
import com.treelev.isimple.utils.parse.SyncLogEntity.SyncPhaseLog;

public class SyncServcie extends Service {

    public final static String LAST_UPDATED_DATE = "date_for_last_update";
    public static final int BUNCH_SIZE = 50;
    private static SyncDataTask syncDataTask;
    private SyncLogEntity syncLogEntity;

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

    public class SyncDataTask extends AsyncTask<Void, String, Void> {

        private WebServiceManager webServiceManager;
        private Context context;

        private boolean error;
        private SyncPhase failedSyncPhase;

        public SyncDataTask(Context context) {
            this.webServiceManager = new WebServiceManager();
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            error = false;
        }

        @Override
        protected Void doInBackground(Void... params) {

            long dailyUpdateDuration = 0;
            long priceDiscountsUpdateDuration = 0;
            long featuredUpdateDuration = 0;
            long offersUpdateDuration = 0;

            long dailyUpdateDurationTemp = 0;
            long priceDiscountsUpdateDurationTemp = 0;
            long featuredUpdateDurationTemp = 0;
            long offersUpdateDurationTemp = 0;

            long syncStartTimestamp = System.currentTimeMillis();
            initSyncLogEntity(syncStartTimestamp);

            syncLogEntity.logs.add(new SyncPhaseLog("Started sync"));
            if (WebServiceManager.getDownloadDirectory() != null
                    && WebServiceManager.getDownloadDirectory().listFiles() != null
                    && WebServiceManager.getDownloadDirectory().listFiles().length > 0) {
                syncLogEntity.logs.add(new SyncPhaseLog("Deleting previous sync files."));
                for (File file : WebServiceManager.getDownloadDirectory().listFiles()) {
                    file.delete();
                }
            }

            ItemDAO itemDAO = new ItemDAO(ISimpleApp.getInstantce());
            ShoppingCartDAO shoppingCartDAO = new ShoppingCartDAO(ISimpleApp.getInstantce());
            ItemAvailabilityDAO itemAvailabilityDAO = new ItemAvailabilityDAO(
                    ISimpleApp.getInstantce());
            ChainDAO chainDAO = new ChainDAO(ISimpleApp.getInstantce());
            ShopDAO shopDAO = new ShopDAO(ISimpleApp.getInstantce());
            DeliveryZoneDAO deliveryZoneDAO = new DeliveryZoneDAO(ISimpleApp.getInstantce());
            OfferDAO offerDAO = new OfferDAO(ISimpleApp.getInstantce());

            LogUtils.i("", "Downloading update urls file");
            Map<String, LoadFileData> loadFileDataList = null;
            try {
                syncLogEntity.logs.add(new SyncPhaseLog(
                        "Downloading and parsing update urls file - "
                                + SharedPreferencesManager
                                        .getUpdateFileUrl()));
                loadFileDataList = webServiceManager.getLoadFileDataMap(SharedPreferencesManager
                        .getUpdateFileUrl());
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                syncLogEntity.logs.add(new SyncPhaseLog(
                        "Failed downloading or parsing update urls file. "
                                + e1.getMessage()));
            }

            if (loadFileDataList == null) {
                // TODO Handle download exception
                error = true;
                return null;
            }

            LogUtils.i("", "DONE Downloading update urls file, size = " + loadFileDataList.size()
                    + " list = " + loadFileDataList);
            syncLogEntity.logs.add(new SyncPhaseLog("DONE Downloading update urls file, size = "
                    + loadFileDataList.size()
                    + " list = " + loadFileDataList));

            // 1. Download ITEMS_PRICE
            LogUtils.i("", "Downloading ITEMS_PRICE file");
            syncLogEntity.logs.add(new SyncPhaseLog("Downloading ITEMS_PRICE file"));
            publishProgress(context.getString(R.string.sync_state_prices_downloading));
            File itemsPriceArchive;
            try {
                itemsPriceArchive = webServiceManager.downloadFile(loadFileDataList.get(
                        UpdateFile.ITEM_PRICES.getUpdateFileTag()).getFileUrl()).getDownloadedFile();
            } catch (IOException e1) {
                e1.printStackTrace();
                error = true;
                failedSyncPhase = SyncPhase.CATALOG_UPDATE;
                return null;
            }

            // 2. Unzip itemsPriceArchive
            LogUtils.i("", "Unzipping ITEMS_PRICE file");
            syncLogEntity.logs.add(new SyncPhaseLog("Unzipping ITEMS_PRICE file"));
            File unzippedItemsPrice = unzipFile(itemsPriceArchive);
            if (unzippedItemsPrice.length() == 0) {
                error = true;
                failedSyncPhase = SyncPhase.ITEM_PRICES;
                return null;
            }

            // 3. Parse itemsPrice xml. Get items to be updated
            LogUtils.i("", "Parsing ITEMS_PRICE file");
            syncLogEntity.logs.add(new SyncPhaseLog("Parsing ITEMS_PRICE file"));
            ItemPriceWrapper itemPriceWrapper = null;
            ItemPricesParser itemPricesParser = new ItemPricesParser();
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new FileInputStream(unzippedItemsPrice), null);
                itemPriceWrapper = itemPricesParser.parseListItems(xmlPullParser);
            } catch (XmlPullParserException e) {
                LogUtils.i("", "Failed to parse ITEMS_PRICE file " + e);
                syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse ITEMS_PRICE file "
                        + e.getMessage()));
                error = true;
                failedSyncPhase = SyncPhase.ITEM_PRICES;
            } catch (FileNotFoundException e) {
                LogUtils.i("", "Failed to parse ITEMS_PRICE file " + e);
                syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse ITEMS_PRICE file "
                        + e.getMessage()));
                error = true;
                failedSyncPhase = SyncPhase.ITEM_PRICES;
            }
            LogUtils.i("", "DONE Parsing ITEMS_PRICE file, ids list size = "
                    + itemPriceWrapper.getItemsIds().size() + ", items list size = "
                    + itemPriceWrapper.getItemsPricesList().size());
            syncLogEntity.logs.add(new SyncPhaseLog(
                    "DONE Parsing ITEMS_PRICE file, ids list size = "
                            + itemPriceWrapper.getItemsIds().size() + ", items list size = "
                            + itemPriceWrapper.getItemsPricesList().size()));

            // 3.1 Get items ids from db
            LogUtils.i("", "Getting itemsIds from db");
            syncLogEntity.logs.add(new SyncPhaseLog("Getting itemsIds from db"));
            List<String> itemsIds = itemDAO.getItemsIds();
            LogUtils.i("", "DONE Getting itemsIds from db, list size = " + itemsIds.size());
            syncLogEntity.logs.add(new SyncPhaseLog("DONE Getting itemsIds from db, list size = "
                    + itemsIds.size()));

            // 3.2 Get new items list
            LogUtils.i("", "Getting new items ids list");
            syncLogEntity.logs.add(new SyncPhaseLog("Getting new items ids list"));
            List<String> newItemsIds = null;
            if (itemPriceWrapper != null && itemPriceWrapper.getItemsIds() != null) {
                newItemsIds = new ArrayList<String>(itemPriceWrapper.getItemsIds());
                newItemsIds.removeAll(itemsIds);
            }
            int newItemsSize = newItemsIds.size();
            LogUtils.i("", "DONE Getting new items ids list, size = " + newItemsSize);
            syncLogEntity.logs.add(new SyncPhaseLog("DONE Getting new items ids list, size = "
                    + newItemsSize));
            syncLogEntity.meta.insertCount = newItemsSize;
            syncLogEntity.meta.updateCount = itemPriceWrapper.getItemsIds().size() - newItemsSize;

            // 3.3 Delete missing items
            LogUtils.i("", "Deleting missing items");
            syncLogEntity.logs.add(new SyncPhaseLog("Deleting missing items"));
            List<String> deletedItemsIds = null;
            if (itemPriceWrapper != null && itemPriceWrapper.getItemsIds() != null) {
                itemsIds.removeAll(itemPriceWrapper.getItemsIds());
                deletedItemsIds = new ArrayList<String>(itemsIds);
            }
            LogUtils.i("", "Missing items list size = " + deletedItemsIds.size());
            syncLogEntity.logs.add(new SyncPhaseLog("Missing items list size = "
                    + deletedItemsIds.size()));
            syncLogEntity.meta.deleteCount = deletedItemsIds.size();
            LogUtils.i("", "Deleting missing items from db items table");
            syncLogEntity.logs.add(new SyncPhaseLog("Deleting missing items from db items table"));
            publishProgress(context.getString(R.string.sync_state_old_items_deleting));
            itemDAO.deleteItems(deletedItemsIds);
            LogUtils.i("", "Deleting missing items from db shopping cart table");
            syncLogEntity.logs.add(new SyncPhaseLog(
                    "Deleting missing items from db shopping cart table"));
            shoppingCartDAO.deleteItems(deletedItemsIds);

            // 4. Delete unzippedItemsPrize file
            LogUtils.i("", "Deleting missing items file");
            syncLogEntity.logs.add(new SyncPhaseLog("Deleting missing items file"));
            unzippedItemsPrice.delete();

            // 5. Download, parse and update in db new items
            LogUtils.i("", "Downloading new items xml files");
            syncLogEntity.logs.add(new SyncPhaseLog("Downloading new items xml files"));
            int downloadedFilesCount = 0;
            // Old mechanism
            
//            File itemArchive = null;
//            StringBuffer sb = new StringBuffer();
//            if (newItemsIds != null && !newItemsIds.isEmpty()) {
//                ArrayList<Item> items = new ArrayList<Item>();
//                for (String itemId : newItemsIds) {
//                    sb = new StringBuffer();
//                    sb.append(loadFileDataList
//                            .get(UpdateFile.CATALOG_UPDATES.getUpdateFileTag())
//                            .getFileUrl()
//                            .replace(".xmlz", "/")).append(itemId.substring(0,
//                            2)).append("/")
//                            .append(itemId).append(".xmlz");
//                    LogUtils.i("", "Downloading " + sb.toString());
//                    syncLogEntity.logs.add(new SyncPhaseLog("Downloading " +
//                            sb.toString()));
//                    publishProgress(String.format(
//                            context.getString(R.string.sync_state_new_items_downloading),
//                            downloadedFilesCount, newItemsSize));
//                    try {
//                        itemArchive = webServiceManager
//                                .downloadNewCatalogItemFile(sb.toString()).getDownloadedFile();
//                    } catch (IOException e1) {
//                        LogUtils.i("", "Failed downloading file");
//                        syncLogEntity.logs.add(new
//                                SyncPhaseLog("Failed downloading file"));
//                        e1.printStackTrace();
//                        continue;
//                    }
//                    LogUtils.i("", "Finished downloading file, itemArchive = " +
//                            itemArchive);
//                    if (itemArchive != null) {
//                        downloadedFilesCount++;
//                    } else {
//                        continue;
//                    }
//
//                    LogUtils.i("", "Unzipping file " + itemArchive.getName());
//                    syncLogEntity.logs.add(new SyncPhaseLog("Unzipping file "
//                            + itemArchive.getName()));
//                    File uzippedFile = unzipFile(itemArchive);
//                    itemArchive.delete();
//
//                    LogUtils.i("", "Parsing file " + itemArchive.getName());
//                    syncLogEntity.logs
//                            .add(new SyncPhaseLog("Parsing file " + itemArchive.getName()));
//                    CatalogItemParser catalogParser = new CatalogItemParser();
//
//                    try {
//                        XmlPullParserFactory factory =
//                                XmlPullParserFactory.newInstance();
//                        factory.setNamespaceAware(true);
//                        XmlPullParser xmlPullParser = factory.newPullParser();
//                        xmlPullParser.setInput(new FileInputStream(uzippedFile), null);
//                        items.addAll(catalogParser.parseXml(xmlPullParser, itemDAO));
//
//                    } catch (XmlPullParserException e) {
//                        LogUtils.i("", "Failed to parse file " + uzippedFile.getName() +
//                                e);
//                        syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
//                                + uzippedFile.getName() + e.getMessage()));
//                        error = true;
//                    } catch (FileNotFoundException e) {
//                        LogUtils.i("", "Failed to parse file " + uzippedFile.getName() +
//                                e);
//                        syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
//                                + uzippedFile.getName() + e.getMessage()));
//                        error = true;
//                    }
//
//                    uzippedFile.delete();
//
//                    if (items.size() == BUNCH_SIZE) {
//                        itemDAO.insertListData(items);
//                        items.clear();
//                    }
//                }
//
//                if (items.size() != 0) {
//                    itemDAO.insertListData(items);
//                    items.clear();
//                }
//            }

            // New mechanism
            int successfullyDownloadedItems = 0;
            StringBuffer sb = new StringBuffer(Constants.URL_NEW_ITEMS);
            DownloadFileResponse response;
            File newItemsFile = null;
            if (newItemsIds != null && !newItemsIds.isEmpty()) {
                int n = 0;
                int handledItems = 0;
                for (String itemId : newItemsIds) {
                    sb.append(itemId).append(",");
                    n++;
                    handledItems++;
                    if (n == Constants.NEW_ITEMS_BLOCK_SIZE || handledItems == newItemsSize) {
                        downloadedFilesCount = downloadedFilesCount + n;
                        successfullyDownloadedItems = successfullyDownloadedItems + n;
                        n = 0;
                        sb.deleteCharAt(sb.lastIndexOf(","));

                        LogUtils.i("", "Downloading " + sb.toString());
                        syncLogEntity.logs.add(new SyncPhaseLog("Downloading " +
                                sb.toString()));
                        try {
                            response = webServiceManager
                                    .downloadNewCatalogItemFile(sb.toString());
                            newItemsFile = response.getDownloadedFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogUtils.i("", "Failed downloading file");
                            syncLogEntity.logs.add(new
                                    SyncPhaseLog("Failed downloading file"));
                            e.printStackTrace();
                            continue;
                        }
                        
                        publishProgress(String.format(
                                context.getString(R.string.sync_state_new_items_downloading),
                                downloadedFilesCount, newItemsSize));
                        
                        Map<String, List<String>> headers = response.getHeaders();
                        if (headers != null) {
                            List<String> warningHeaders = headers.get("Warning");
                            if (warningHeaders != null) {
                                String failedIds = warningHeaders.get(0);
                                String[] failedItemsIds = failedIds.split(",");
                                
                                LogUtils.i("", "Some items were not found, ids: " + failedIds);
                                syncLogEntity.logs.add(new
                                        SyncPhaseLog("Some items were not found, ids: " + failedIds));
                                successfullyDownloadedItems = successfullyDownloadedItems - failedItemsIds.length;
                            }
                        }
                        
                        CatalogItemParser catalogParser = new CatalogItemParser();
                        try {
                            XmlPullParserFactory factory =
                                    XmlPullParserFactory.newInstance();
                            factory.setNamespaceAware(true);
                            XmlPullParser xmlPullParser = factory.newPullParser();
                            xmlPullParser.setInput(new FileInputStream(newItemsFile), null);
                            catalogParser.parseXmlToDB(xmlPullParser, itemDAO);

                        } catch (XmlPullParserException e) {
                            LogUtils.i("", "Failed to parse file " + newItemsFile.getName() +
                                    e);
                            syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                                    + newItemsFile.getName() + e.getMessage()));
                            error = true;
                        } catch (FileNotFoundException e) {
                            LogUtils.i("", "Failed to parse file " + newItemsFile.getName() +
                                    e);
                            syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                                    + newItemsFile.getName() + e.getMessage()));
                            error = true;
                        }
                        
                        sb = new StringBuffer(Constants.URL_NEW_ITEMS);
                    }
                }
            }
            
            LogUtils.i("",
                    "DONE Download, parse and update new items xml files, files list size = "
                            + successfullyDownloadedItems);
            syncLogEntity.logs.add(new SyncPhaseLog(
                    "DONE Download, parse and update new items xml files, files list size = "
                            + successfullyDownloadedItems));
            
//            LogUtils.i("",
//                    "DONE Download, parse and update new items xml files, files list size = "
//                            + downloadedFilesCount);
//            syncLogEntity.logs.add(new SyncPhaseLog(
//                    "DONE Download, parse and update new items xml files, files list size = "
//                            + downloadedFilesCount));

            dailyUpdateDuration = System.currentTimeMillis() - syncStartTimestamp;
            if (syncStartTimestamp - SharedPreferencesManager.getLastSyncTimestamp(context) >= Constants.SYNC_LONG_PERIOD) {
                // 12. Update LocationsAndChainsUpdates
                // 12.1. Download LocationsAndChainsUpdates
                LogUtils.i("", "Downloading LocationsAndChainsUpdates");
                syncLogEntity.logs.add(new SyncPhaseLog("Downloading LocationsAndChainsUpdates"));
                publishProgress(context.getString(R.string.sync_state_locations_and_chains_update));
                File locationsAndChainsUpdatesArchive = null;
                try {
                    locationsAndChainsUpdatesArchive = webServiceManager
                            .downloadFile(loadFileDataList.get(
                                    UpdateFile.LOCATIONS_AND_CHAINS_UPDATES.getUpdateFileTag())
                                    .getFileUrl()).getDownloadedFile();
                } catch (IOException e1) {
                    LogUtils.i("", "Failed downloading file");
                    syncLogEntity.logs.add(new SyncPhaseLog("Failed downloading file"));
                    e1.printStackTrace();
                }

                // 12.2. Unzip LocationsAndChainsUpdates list
                if (locationsAndChainsUpdatesArchive != null) {
                    LogUtils.i("", "Unzipping LocationsAndChainsUpdates");
                    syncLogEntity.logs.add(new SyncPhaseLog("Unzipping LocationsAndChainsUpdates"));
                    File unzippedLocationsAndChainsUpdates = unzipFile(locationsAndChainsUpdatesArchive);

                    // 12.3 Parse and save LocationsAndChainsUpdates list to DB
                    LogUtils.i("", "Parsing LocationsAndChainsUpdates to db");
                    syncLogEntity.logs.add(new SyncPhaseLog(
                            "Parsing LocationsAndChainsUpdates to db"));
                    ShopAndChainsParser locationsAndChainsUpdatesParser = new ShopAndChainsParser();

                    try {
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xmlPullParser = factory.newPullParser();
                        xmlPullParser.setInput(new FileInputStream(
                                unzippedLocationsAndChainsUpdates),
                                null);
                        locationsAndChainsUpdatesParser.parseXmlToDB(xmlPullParser, chainDAO,
                                shopDAO);

                        unzippedLocationsAndChainsUpdates.delete();
                    } catch (XmlPullParserException e) {
                        LogUtils.i(
                                "",
                                "Failed to parse file "
                                        + unzippedLocationsAndChainsUpdates.getName()
                                        + e);
                        syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                                + unzippedLocationsAndChainsUpdates.getName()
                                + e.getMessage()));
                        error = true;
                    } catch (FileNotFoundException e) {
                        LogUtils.i(
                                "",
                                "Failed to parse file "
                                        + unzippedLocationsAndChainsUpdates.getName()
                                        + e);
                        syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                                + unzippedLocationsAndChainsUpdates.getName()
                                + e.getMessage()));
                        error = true;
                    }
                    unzippedLocationsAndChainsUpdates.delete();
                }
            }

            if (syncStartTimestamp - SharedPreferencesManager.getLastSyncTimestamp(context) >= Constants.SYNC_LONG_PERIOD) {
                // 11. Update ItemAvailability
                // 11.1. Download ItemAvailability
                LogUtils.i("", "Downloading ItemAvailability");
                syncLogEntity.logs.add(new SyncPhaseLog("Downloading ItemAvailability"));
                publishProgress(context.getString(R.string.sync_state_item_availability_update));
                File itemAvailabilityArchive = null;
                try {
                    itemAvailabilityArchive = webServiceManager.downloadFile(loadFileDataList.get(
                            UpdateFile.ITEM_AVAILABILITY.getUpdateFileTag()).getFileUrl()).getDownloadedFile();
                } catch (IOException e1) {
                    LogUtils.i("", "Failed downloading file");
                    syncLogEntity.logs.add(new SyncPhaseLog("Failed downloading file"));
                    e1.printStackTrace();
                }

                // 11.2. Unzip ItemAvailability list
                if (itemAvailabilityArchive != null) {
                    LogUtils.i("", "Unzipping ItemAvailability");
                    syncLogEntity.logs.add(new SyncPhaseLog("Unzipping ItemAvailability"));
                    File unzippedItemAvailability = unzipFile(itemAvailabilityArchive);

                    // 11.3 Parse and save ItemAvailability list to DB
                    LogUtils.i("", "Parsing ItemAvailability to db");
                    syncLogEntity.logs.add(new SyncPhaseLog("Parsing ItemAvailability to db"));
                    ItemAvailabilityParser itemAvailabilityParser = new ItemAvailabilityParser();

                    try {
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xmlPullParser = factory.newPullParser();
                        xmlPullParser.setInput(new FileInputStream(unzippedItemAvailability), null);
                        itemAvailabilityParser.parseXmlToDB(xmlPullParser, itemAvailabilityDAO);

                        unzippedItemAvailability.delete();
                    } catch (XmlPullParserException e) {
                        LogUtils.i("", "Failed to parse file " + unzippedItemAvailability.getName()
                                + e);
                        syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                                + unzippedItemAvailability.getName() + e.getMessage()));
                        error = true;
                    } catch (FileNotFoundException e) {
                        LogUtils.i("", "Failed to parse file " + unzippedItemAvailability.getName()
                                + e);
                        syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                                + unzippedItemAvailability.getName() + e.getMessage()));
                        error = true;
                    }
                    unzippedItemAvailability.delete();
                }
            }

            dailyUpdateDurationTemp = System.currentTimeMillis();
            // 7. Update items prices
            LogUtils.i("", "Updating items prices in dp");
            syncLogEntity.logs.add(new SyncPhaseLog("Updating items prices in dp"));
            publishProgress(context.getString(R.string.sync_state_prices_installing));
            if (itemPriceWrapper.getItemsPricesList() != null
                    && !itemPriceWrapper.getItemsPricesList().isEmpty()) {
                itemDAO.updatePriceList(itemPriceWrapper.getItemsPricesList());
            }

            priceDiscountsUpdateDurationTemp = System.currentTimeMillis();
            // 8. Update discounts
            // 8.1. Download price discounts
            LogUtils.i("", "Downloading price discounts");
            syncLogEntity.logs.add(new SyncPhaseLog("Downloading price discounts"));
            publishProgress(context.getString(R.string.sync_state_discounts_update));
            File priceDiscountsArchive = null;
            try {
                priceDiscountsArchive = webServiceManager
                        .downloadFile(loadFileDataList.get(UpdateFile.DISCOUNT.getUpdateFileTag())
                                .getFileUrl()).getDownloadedFile();
            } catch (IOException e1) {
                LogUtils.i("", "Failed downloading file");
                syncLogEntity.logs.add(new SyncPhaseLog("Failed downloading file"));
                e1.printStackTrace();
            }

            // 8.2. Unzip price discounts
            if (priceDiscountsArchive != null) {
                LogUtils.i("", "Unzipping price discounts");
                syncLogEntity.logs.add(new SyncPhaseLog("Unzipping price discounts"));
                File unzippedPriceDiscounts = unzipFile(priceDiscountsArchive);

                // 8.3 Parse and save price discounts to DB
                LogUtils.i("", "Parsing price discounts to db");
                syncLogEntity.logs.add(new SyncPhaseLog("Parsing price discounts to db"));
                ItemPriceDiscountParser discountParser = new ItemPriceDiscountParser();

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xmlPullParser = factory.newPullParser();
                    xmlPullParser.setInput(new FileInputStream(unzippedPriceDiscounts), null);
                    discountParser.parseXmlToDB(xmlPullParser, itemDAO);

                    unzippedPriceDiscounts.delete();
                } catch (XmlPullParserException e) {
                    LogUtils.i("", "Failed to parse file " + unzippedPriceDiscounts.getName() + e);
                    syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                            + unzippedPriceDiscounts.getName() + e.getMessage()));
                    error = true;
                } catch (FileNotFoundException e) {
                    LogUtils.i("", "Failed to parse file " + unzippedPriceDiscounts.getName() + e);
                    syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                            + unzippedPriceDiscounts.getName() + e.getMessage()));
                    error = true;
                }
                unzippedPriceDiscounts.delete();
            }

            priceDiscountsUpdateDuration = System.currentTimeMillis()
                    - priceDiscountsUpdateDurationTemp;
            syncLogEntity.duration.add(new PriceDiscountsUpdateDuration(
                    priceDiscountsUpdateDuration));

            // 9. Update offers list
            // 9.1. Download offers list
            LogUtils.i("", "Downloading offers, url = " + loadFileDataList.get(
                    UpdateFile.OFFERS.getUpdateFileTag()).getFileUrl());
            syncLogEntity.logs.add(new SyncPhaseLog("Downloading offers"));
            publishProgress(context.getString(R.string.sync_state_offers_update));
            File offersArchive = null;
            try {
                offersArchive = webServiceManager.downloadFile(loadFileDataList.get(
                        UpdateFile.OFFERS.getUpdateFileTag()).getFileUrl()).getDownloadedFile();
            } catch (IOException e2) {
                LogUtils.i("", "Failed downloading file");
                syncLogEntity.logs.add(new SyncPhaseLog("Failed downloading file"));
                e2.printStackTrace();
            }

            // 9.2. Unzip offers list
            File unzippedOffers = null;
            if (offersArchive != null) {
                LogUtils.i("", "Unzipping offers");
                syncLogEntity.logs.add(new SyncPhaseLog("Unzipping offers"));
                unzippedOffers = unzipFile(offersArchive);

                // 9.3 Parse and save offers list to DB
                OffersParser offersParser = new OffersParser();
                LogUtils.i("", "Parsing offers to db");
                syncLogEntity.logs.add(new SyncPhaseLog("Parsing offers to db"));
                try {
                    XmlPullParserFactory factory =
                            XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xmlPullParser = factory.newPullParser();
                    xmlPullParser.setInput(new FileInputStream(unzippedOffers),
                            null);
                    offersParser.parseXmlToDB(xmlPullParser, offerDAO);

                    unzippedOffers.delete();
                } catch (XmlPullParserException e) {
                    LogUtils.i("", "Failed to parse file " + unzippedOffers.getName() + e);
                    syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                            + unzippedOffers.getName() + e.getMessage()));
                    error = true;
                } catch (FileNotFoundException e) {
                    LogUtils.i("", "Failed to parse file " + unzippedOffers.getName() + e);
                    syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                            + unzippedOffers.getName() + e.getMessage()));
                    error = true;
                }
            }

            featuredUpdateDurationTemp = System.currentTimeMillis();
            // 10. Update featured
            // 10.1. Download featured
            LogUtils.i("", "Downloading featured");
            syncLogEntity.logs.add(new SyncPhaseLog("Downloading featured"));
            publishProgress(context.getString(R.string.sync_state_featured_update));
            File featuredArchive = null;
            try {
                featuredArchive = webServiceManager.downloadFile(loadFileDataList.get(
                        UpdateFile.FEATURED.getUpdateFileTag()).getFileUrl()).getDownloadedFile();
            } catch (IOException e1) {
                LogUtils.i("", "Failed downloading file");
                syncLogEntity.logs.add(new SyncPhaseLog("Failed downloading file"));
                e1.printStackTrace();
            }

            // 10.2. Unzip featured list
            if (featuredArchive != null) {
                LogUtils.i("", "Unzipping featured");
                syncLogEntity.logs.add(new SyncPhaseLog("Unzipping featured"));
                File unzippedFeatured = unzipFile(featuredArchive);

                // 10.3 Parse and save featured list to DB
                LogUtils.i("", "Parsing featured to db");
                syncLogEntity.logs.add(new SyncPhaseLog("Parsing featured to db"));
                FeaturedItemsParser featuredParser = new FeaturedItemsParser();

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xmlPullParser = factory.newPullParser();
                    xmlPullParser.setInput(new FileInputStream(unzippedFeatured), null);
                    featuredParser.parseXmlToDB(xmlPullParser, itemDAO);

                    unzippedFeatured.delete();
                } catch (XmlPullParserException e) {
                    LogUtils.i("", "Failed to parse file " + unzippedFeatured.getName() + e);
                    syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                            + unzippedFeatured.getName() + e.getMessage()));
                    error = true;
                } catch (FileNotFoundException e) {
                    LogUtils.i("", "Failed to parse file " + unzippedFeatured.getName() + e);
                    syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                            + unzippedFeatured.getName() + e.getMessage()));
                    error = true;
                }
                unzippedFeatured.delete();
            }

            featuredUpdateDuration = System.currentTimeMillis() - featuredUpdateDurationTemp;
            syncLogEntity.duration.add(new FeaturedUpdateDuration(featuredUpdateDuration));

            dailyUpdateDuration = dailyUpdateDuration + System.currentTimeMillis()
                    - dailyUpdateDurationTemp;
            syncLogEntity.duration.add(new DailyUpdateDuration(dailyUpdateDuration));
            if (syncStartTimestamp - SharedPreferencesManager.getLastSyncTimestamp(context) >= Constants.SYNC_LONG_PERIOD) {
                offersUpdateDurationTemp = System.currentTimeMillis();
                // 13. Update Delivery
                // 13.1. Download Delivery
                LogUtils.i("", "Downloading Delivery");
                syncLogEntity.logs.add(new SyncPhaseLog("Downloading Delivery"));
                publishProgress(context.getString(R.string.sync_state_delivery_update));
                File deliveryArchive = null;
                try {
                    deliveryArchive = webServiceManager.downloadFile(loadFileDataList.get(
                            UpdateFile.DELIVERY.getUpdateFileTag()).getFileUrl()).getDownloadedFile();
                } catch (IOException e1) {
                    LogUtils.i("", "Failed downloading file");
                    syncLogEntity.logs.add(new SyncPhaseLog("Failed downloading file"));
                    e1.printStackTrace();
                }

                // 13.2. Unzip Delivery list
                if (deliveryArchive != null) {
                    LogUtils.i("", "Unzipping Delivery");
                    syncLogEntity.logs.add(new SyncPhaseLog("Unzipping Delivery"));
                    File unzippedDelivery = unzipFile(deliveryArchive);

                    // 13.3 Parse and save Delivery list to DB
                    LogUtils.i("", "Parsing Delivery to db");
                    syncLogEntity.logs.add(new SyncPhaseLog("Parsing Delivery to db"));
                    DeliveryZoneParser deliveryParser = new DeliveryZoneParser();

                    try {
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xmlPullParser = factory.newPullParser();
                        xmlPullParser.setInput(new FileInputStream(unzippedDelivery), null);
                        deliveryParser.parseXmlToDB(xmlPullParser, deliveryZoneDAO);

                        unzippedDelivery.delete();
                    } catch (XmlPullParserException e) {
                        LogUtils.i("", "Failed to parse file " + unzippedDelivery.getName() + e);
                        syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                                + unzippedDelivery.getName() + e.getMessage()));
                        error = true;
                    } catch (FileNotFoundException e) {
                        LogUtils.i("", "Failed to parse file " + unzippedDelivery.getName() + e);
                        syncLogEntity.logs.add(new SyncPhaseLog("Failed to parse file "
                                + unzippedDelivery.getName() + e.getMessage()));
                        error = true;
                    }
                    unzippedDelivery.delete();
                }
                offersUpdateDuration = System.currentTimeMillis() - offersUpdateDurationTemp;
                syncLogEntity.duration.add(new OffersUpdateDuration(offersUpdateDuration));
            }

            syncLogEntity.duration.add(new FullUpdateDuration(System.currentTimeMillis()
                    - syncStartTimestamp));

            return null;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            sendProgressBroadcast(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            syncDataTask = null;

            LogUtils.i("", "Finished sync, error = " + error);
            syncLogEntity.logs.add(new SyncPhaseLog("Finished sync, error = " + error));
            if (!error) {
                SharedPreferencesManager.setPreparationUpdate(context, false);
                SharedPreferencesManager.setLastSyncTimestamp(context, System.currentTimeMillis());
            } else {

            }

            ParseFile syncFile = ParseLogUtils.createSyncLogFile(syncLogEntity);

            ParseLogUtils.logToParse(syncLogEntity.meta.deleteCount,
                    syncLogEntity.meta.insertCount,
                    syncLogEntity.meta.updateCount, syncFile);
            Intent intent = new Intent(Constants.INTENT_ACTION_SYNC_FINISHED);
            intent.putExtra(Constants.INTENT_ACTION_SYNC_SUCCESSFULL, !error);
            intent.putExtra(Constants.INTENT_ACTION_SYNC_FAILED_PHASE, failedSyncPhase);
            sendBroadcast(intent);
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
                    }
                    archive.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                }
            }

            return unzipped;
        }

        private void sendProgressBroadcast(String text) {
            Intent intent = new Intent(Constants.INTENT_ACTION_SYNC_STATE_UPDATE);
            intent.putExtra(Constants.INTENT_EXTRA_SYNC_STATE, text);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }

    private void initSyncLogEntity(long syncStartTimestamp) {
        syncLogEntity = new SyncLogEntity();

        String version = "";
        int versionCode = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        syncLogEntity.meta.build = version;
        syncLogEntity.meta.version = String.valueOf(versionCode);
        syncLogEntity.meta.setStartTime(syncStartTimestamp);

        syncLogEntity.logs = new ArrayList<SyncLogEntity.SyncPhaseLog>();
        syncLogEntity.duration = new ArrayList<Object>();
    }

    // private void testSyncLog() {
    // String version = "";
    // int versionCode = 0;
    // try {
    // PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(),
    // 0);
    // version = pInfo.versionName;
    // versionCode = pInfo.versionCode;
    // } catch (NameNotFoundException e) {
    // e.printStackTrace();
    // }
    //
    // SyncLogEntity logEntity = new SyncLogEntity();
    // logEntity.meta.build = version;
    // logEntity.meta.deleteCount = 12;
    // logEntity.meta.insertCount = 13;
    // logEntity.meta.setStartTime(System.currentTimeMillis());
    // logEntity.meta.updateCount = 14;
    // logEntity.meta.version = String.valueOf(versionCode);
    //
    // logEntity.logs = new ArrayList<SyncLogEntity.SyncPhaseLog>();
    // for (int i = 0; i < 10; i++) {
    // SyncLogEntity.SyncPhaseLog syncPhase = new SyncPhaseLog("Sync phase " +
    // i);
    // if (i % 2 == 0) {
    // syncPhase.object = new HashMap<String, Long>();
    // syncPhase.object.put("93938", 123l);
    // syncPhase.object.put("1111111", 554l);
    // syncPhase.object.put("22", 22200l);
    // }
    // logEntity.logs.add(syncPhase);
    // }
    //
    // logEntity.duration = new ArrayList<Object>();
    // logEntity.duration.add(new SyncLogEntity.DailyUpdateDuration(125));
    // logEntity.duration.add(new SyncLogEntity.FeaturedUpdateDuration(11));
    // logEntity.duration.add(new SyncLogEntity.FullUpdateDuration(5));
    // logEntity.duration.add(new SyncLogEntity.OffersUpdateDuration(100));
    // logEntity.duration.add(new
    // SyncLogEntity.PriceDiscountsUpdateDuration(1));
    //
    // File syncFile = ParseLogUtils.createSyncLogFile(logEntity);
    //
    // ParseLogUtils.logToParse(logEntity.meta.deleteCount,
    // logEntity.meta.insertCount,
    // logEntity.meta.updateCount, syncFile);
    // }

}
