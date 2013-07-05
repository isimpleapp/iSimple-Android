package com.treelev.isimple.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import com.treelev.isimple.R;
import com.treelev.isimple.data.*;
import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.tasks.ParseDataTask;
import com.treelev.isimple.utils.managers.WebServiceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestParsingActivity extends Activity {

    private File[] files2 = new File[]{
            new File("sdcard/iSimple/archive/Catalog-Update.xml"),
            new File("sdcard/iSimple/archive/Locations-And-Chains-Update.xml"),
            new File("sdcard/iSimple/archive/Item-Availability.xml"),
            new File("sdcard/iSimple/archive/Item-Prices.xml"),
            new File("sdcard/iSimple/archive/Featured.xml"),
            new File("sdcard/iSimple/archive/Deprecated.xml"),
            new File("sdcard/iSimple/archive/Delivery.xml")
    };

    private BaseDAO[] daoList = new BaseDAO[]{
            new ItemDAO(this),
            new ChainDAO(this),
            new ShopDAO(this),
            new ItemAvailabilityDAO(this),
            new DeprecatedItemDAO(this),
            new DeliveryZoneDAO(this)
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        new ParseDataTask((TextView) findViewById(R.id.textView), (TextView) findViewById(R.id.textView1), daoList).execute(files2);
        List<LoadFileData> loadFileDataList = new WebServiceManager().getLoadFileData("http://s1.isimpleapp.ru/xml/ver0/Update-Index.xml");
        List<String> urlList = new ArrayList<String>();
        for (LoadFileData loadFileData : loadFileDataList) {
            urlList.add(loadFileData.getFileUrl());
        }
        putFileDatesInPref(urlList);
    }

    private void putFileDatesInPref(List<String> urlList) {
        SharedPreferences.Editor prefEditor = getPreferences(MODE_PRIVATE).edit();
        for (String url : urlList) {
            prefEditor.putLong(url, new Date().getTime());
        }
        prefEditor.commit();
//        setContentView(R.layout.test_layout);
//        new ParseDataTask((TextView) findViewById(R.id.textView), (TextView) findViewById(R.id.textView1), daoList).execute(files2);
    }
}