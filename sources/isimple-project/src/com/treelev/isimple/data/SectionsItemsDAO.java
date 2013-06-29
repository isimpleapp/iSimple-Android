package com.treelev.isimple.data;

import android.content.Context;
import android.database.Cursor;
import com.treelev.isimple.R;

import static com.treelev.isimple.utils.managers.ProxyManager.TYPE_SECTION;
import static com.treelev.isimple.utils.managers.ProxyManager.TYPE_SECTION_FILTRATION_SEARCH;

public class SectionsItemsDAO extends  BaseDAO{

    public final static int ID = 31;

    private Context mContext;

    public SectionsItemsDAO(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    public Cursor getSectionsItems(int typeSection){
        String sqlSelect;
        switch (typeSection){
            case TYPE_SECTION_FILTRATION_SEARCH:
                sqlSelect = String.format("SELECT '0' AS _id, 'empty' AS name " +
                        "UNION SELECT '1' AS _id, '%s' AS name " +
                        "UNION SELECT '2' AS _id, '%s' AS name " +
                        "UNION SELECT '3' AS _id, '%s' AS name ",
                        mContext.getString(R.string.section_featured),
                        mContext.getString(R.string.section_all_in_stock),
                        mContext.getString(R.string.section_pre_order));
            break;
            case TYPE_SECTION:
                sqlSelect = String.format("SELECT '0' AS _id, 'empty' AS name " +
                        "UNION SELECT '1' AS _id, '%s' AS name " +
                        "UNION SELECT '2' AS _id, '%s' AS name ",
                        mContext.getString(R.string.section_featured),
                        mContext.getString(R.string.section_all_in_stock));
            break;
            default:
                return null;
        }
        open();
        return getDatabase().rawQuery(sqlSelect, null);
    }
}
