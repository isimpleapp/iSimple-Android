package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import com.treelev.isimple.domain.db.DeliveryZone;

import java.util.List;

public class DeliveryZoneDAO extends BaseDAO {

    public DeliveryZoneDAO(Context context) {
        super(context);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public int getTableDataCount() {
        int count = -1;
        open();
        String formatSelectScript = "select count(*) from %s";
        String selectSql = String.format(formatSelectScript, DatabaseSqlHelper.ITEM_TABLE);
        Cursor c = getDatabase().rawQuery(selectSql, null);
        if (c != null) {
            if (c.moveToNext()) {
                count = c.getInt(0);
            }
            c.close();
        }
        close();
        return count;
    }

    public void insertListData(List<DeliveryZone> deprecatedItems) {

    }
}
