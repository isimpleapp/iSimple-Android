package com.treelev.isimple.parser;

import android.text.TextUtils;

import com.treelev.isimple.data.BaseDAO;
import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.enumerable.UpdateFile;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class UpdateFileParser implements Parser {

    private final static String UPDATE_OBJECT_TAG = "UpdateIndex";
    private final static String UPDATE_URL_TAG = "UpdateURL";
    private final static String UPDATE_DATE_TAG = "UpdateDate";
    private final static String CATALOG_UPDATES_TAG = "CatalogUpdates";
    private final static String ITEM_PRICES_TAG = "ItemPrices";
    private final static String ITEM_AVAILABILITY_TAG = "ItemAvailability";
    private final static String LOCATIONS_AND_CHAINS_UPDATES_TAG = "LocationsAndChainsUpdates";
    private final static String DELIVERY_TAG = "Delivery";
    private final static String FEATURED_TAG = "Featured";
    private final static String DISCOUNT_TAG = "Discount";
    private final static String DEPRECATED_TAG = "Deprecated";
    private final static String OFFERSLIST_TAG = "OffersList";
    private final static String DAILY_UPDATES_TAG = "DailyUpdates";
    
    @Override
    public void parseXmlToDB(XmlPullParser xmlPullParser, BaseDAO... daoList) {
        try {
            Map<String, LoadFileData> updateElemetsList = new HashMap<String, LoadFileData>();
            LoadFileData loadFileData = null;
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(UPDATE_OBJECT_TAG)) {
                    xmlPullParser.next();
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(UPDATE_OBJECT_TAG)) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(CATALOG_UPDATES_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(CATALOG_UPDATES_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(CATALOG_UPDATES_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(ITEM_PRICES_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(ITEM_PRICES_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(ITEM_PRICES_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(ITEM_AVAILABILITY_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(ITEM_AVAILABILITY_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(ITEM_AVAILABILITY_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(LOCATIONS_AND_CHAINS_UPDATES_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(LOCATIONS_AND_CHAINS_UPDATES_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(LOCATIONS_AND_CHAINS_UPDATES_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(DELIVERY_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(DELIVERY_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(DELIVERY_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(FEATURED_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(FEATURED_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(FEATURED_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(DISCOUNT_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(DISCOUNT_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(DISCOUNT_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(DEPRECATED_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(DEPRECATED_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(DEPRECATED_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(OFFERSLIST_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(OFFERSLIST_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(OFFERSLIST_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(DAILY_UPDATES_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(DAILY_UPDATES_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            updateElemetsList.put(DAILY_UPDATES_TAG, loadFileData);
                        }
                        
                        xmlPullParser.next();
                    }
                }
                xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Map<String, LoadFileData> parseXml(XmlPullParser xmlPullParser) {
        Map<String, LoadFileData> updateElemetsList = new HashMap<String, LoadFileData>();
        try {
            LoadFileData loadFileData = null;
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(UPDATE_OBJECT_TAG)) {
                    xmlPullParser.next();
                    while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(UPDATE_OBJECT_TAG)) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(CATALOG_UPDATES_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(CATALOG_UPDATES_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(CATALOG_UPDATES_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(ITEM_PRICES_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(ITEM_PRICES_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(ITEM_PRICES_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(ITEM_AVAILABILITY_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(ITEM_AVAILABILITY_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(ITEM_AVAILABILITY_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(LOCATIONS_AND_CHAINS_UPDATES_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(LOCATIONS_AND_CHAINS_UPDATES_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(LOCATIONS_AND_CHAINS_UPDATES_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(DELIVERY_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(DELIVERY_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(DELIVERY_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(FEATURED_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(FEATURED_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(FEATURED_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(DISCOUNT_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(DISCOUNT_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(DISCOUNT_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(DEPRECATED_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(DEPRECATED_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(DEPRECATED_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(OFFERSLIST_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(OFFERSLIST_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(OFFERSLIST_TAG, loadFileData);
                        } else if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals(DAILY_UPDATES_TAG)) {
                            xmlPullParser.next();
                            loadFileData = new LoadFileData();
                            while (xmlPullParser.getEventType() != XmlPullParser.END_TAG && !xmlPullParser.getName().equals(DAILY_UPDATES_TAG)) {
                                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                                    if (xmlPullParser.getName().equals(UPDATE_URL_TAG)) {
                                        loadFileData.setFileUrl(xmlPullParser.nextText());
                                    } else if (xmlPullParser.getName().equals(UPDATE_DATE_TAG)) {
                                        try {
                                            loadFileData.setLoadDate(LoadFileData.FILE_DATE_FORMAT.parse(xmlPullParser.nextText()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                xmlPullParser.next();
                            }
                            updateElemetsList.put(DAILY_UPDATES_TAG, loadFileData);
                        }
                        
                        xmlPullParser.next();
                    }
                }
                xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return updateElemetsList;
    }

	public static String getFileName() {
		String name = SharedPreferencesManager.getUpdateFileName(UpdateFile.CATALOG_UPDATES.getUpdateFileTag()); 
		return TextUtils.isEmpty(name) ? "Catalog-Update.xml" : name;
	}
}
