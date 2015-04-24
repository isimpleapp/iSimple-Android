
package com.treelev.isimple.utils.parse;

import java.util.ArrayList;
import java.util.HashMap;

public class SyncLogEntity {
    
    public MetaData meta = new MetaData();
    public ArrayList<SyncPhaseLog> logs;
    public ArrayList<Object> duration;

    public class MetaData {
        public int updateCount;
        public int deleteCount;
        public String startTime;
        public String build;
        public String version;
        public int insertCount;
    }
    
    public static class SyncPhaseLog {
        public String log;
        public HashMap<String, Long> object;
    }
    
    public static class DailyUpdateDuration {
        public double DailyUpdateDuration;

        public DailyUpdateDuration(double dailyUpdateDuration) {
            super();
            DailyUpdateDuration = dailyUpdateDuration;
        }
    }
    
    public static class PriceDiscountsUpdateDuration {
        public double PriceDiscountsUpdateDuration;

        public PriceDiscountsUpdateDuration(double priceDiscountsUpdateDuration) {
            super();
            PriceDiscountsUpdateDuration = priceDiscountsUpdateDuration;
        }
    }
    
    public static class FeaturedUpdateDuration {
        public double FeaturedUpdateDuration;

        public FeaturedUpdateDuration(double featuredUpdateDuration) {
            super();
            FeaturedUpdateDuration = featuredUpdateDuration;
        }
    }
    
    public static class OffersUpdateDuration {
        public double OffersUpdateDuration;

        public OffersUpdateDuration(double offersUpdateDuration) {
            super();
            OffersUpdateDuration = offersUpdateDuration;
        }
    }
    
    public static class FullUpdateDuration {
        public double FullUpdateDuration;

        public FullUpdateDuration(double fullUpdateDuration) {
            super();
            FullUpdateDuration = fullUpdateDuration;
        }
    }
    
//  {
//  "meta": {
//    "updateCount": 5,
//    "deleteCount": 84,
//    "startTime": "2015-04-24 14:00:06.829",
//    "build": "3.2.0",
//    "version": "3.2.0",
//    "insertCount": 88
//  },
//  "logs": [
//    {
//      "log": "Update Begin"
//    },
//    {
//      "log": "Checking connection to http://s1.isimpleapp.ru completed, time 0.327862"
//    },
//    {
//      "log": "NEW",
//      "object": {
//        "86921": 3140,
//      }
//    },
//    {
//      "log": "UPDATE",
//      "object": {
//        "76284": 3690,
//        "92615": 2290,
//        "86364": 1990,
//        "90093": 2290,
//        "91541": 1990
//      }
//    },
//    {
//      "log": "DELETE",
//      "object": {
//        "92540": 1440,
//      }
//    },
//    {
//      "log": "Download completed http:/s1.isimpleapp.ru/xml/ver0/Catalog-Update/86/86921.xml"
//    },
//  ],
//  "duration": [
//    {
//      "DailyUpdateDuration": 8.511307954788208
//    },
//    {
//      "PriceDiscountsUpdateDuration": 0.31165802478790283
//    },
//    {
//      "FeaturedUpdateDuration": 0.058302998542785645
//    },
//    {
//      "OffersUpdateDuration": 0.34438103437423706
//    },
//    {
//      "FullUpdateDuration": 11.272548019886017
//    }
//  ]
//}

}
