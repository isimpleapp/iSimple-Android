package com.treelev.isimple.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.treelev.isimple.R;
import com.treelev.isimple.data.*;
import com.treelev.isimple.tasks.ParseDataTask;

import java.io.File;

public class TestParsingActivity extends Activity {

    private File[] files2 = new File[]{
            new File("sdcard/iSimple/archive/Catalog-Update.xml"),
            new File("sdcard/iSimple/archive/Locations-And-Chains-Update.xml"),
            new File("sdcard/iSimple/archive/Item-Availability.xml"),
            new File("sdcard/iSimple/archive/Item-Prices.xml"),
            new File("sdcard/iSimple/archive/featured.xml"),
            new File("sdcard/iSimple/archive/Deprecated.xml"),
            new File("sdcard/iSimple/archive/delivery.xml")
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
//        setContentView(R.layout.test_layout);
//        new ParseDataTask((TextView) findViewById(R.id.textView), (TextView) findViewById(R.id.textView1), daoList).execute(files2);
    }
}