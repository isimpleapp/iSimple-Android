package com.treelev.isimple.data;

import android.content.Context;
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

    public int getTableDataCount() {
        return -1;
    }

    public void insertListData(List<DeliveryZone> deprecatedItems) {

    }
}
