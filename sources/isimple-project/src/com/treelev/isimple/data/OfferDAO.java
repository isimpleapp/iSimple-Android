
package com.treelev.isimple.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.treelev.isimple.domain.db.Offer;

public class OfferDAO extends BaseDAO {

    public OfferDAO(Context context) {
        super(context);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    public List<Offer> getOffers() {
        open();
        List<Offer> offers = new ArrayList<Offer>();
        Cursor c = getDatabase().query(DatabaseSqlHelper.OFFER_TABLE, null, null, null, null, null,
                null);

        if (c.getCount() != 0) {
            Offer offer = null;
            while (c.moveToNext()) {
                offer = parseOfferCursor(c);
                offers.add(offer);
            }
        }
        c.close();
        return offers;
    }
    
    public List<String> getOffersImagesUrls() {
        open();
        List<String> offersImagesUrls = new ArrayList<String>();
        Cursor c = getDatabase().query(DatabaseSqlHelper.OFFER_TABLE, new String[] {DatabaseSqlHelper.OFFER_IMAGE1200}, null, null, null, null,
                null);

        if (c.getCount() != 0) {
            while (c.moveToNext()) {
                offersImagesUrls.add(c.getString(0));
            }
        }
        c.close();
        return offersImagesUrls;
    }

    private Offer parseOfferCursor(Cursor c) {
        Offer offer = new Offer();
        offer.setId(c.getLong(c.getColumnIndex(DatabaseSqlHelper.OFFER_ID)));
        offer.setName(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_NAME)));
        offer.setUrl(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_URL)));
        offer.setExpired(c.getInt(c.getColumnIndex(DatabaseSqlHelper.OFFER_EXPIRED)));
        offer.setImage(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_IMAGE)));
        offer.setImage(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_IMAGE1200)));
        offer.setImagehdpi(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_IMAGEHDPI)));
        offer.setImage2x(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_IMAGE2X)));
        offer.setImageipad(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_IMAGEIPAD)));
        offer.setImageipad2x(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_IMAGEIPAD2X)));
        offer.setDescription(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_DESCRIPTION)));
        offer.setPrioritized(c.getInt(c.getColumnIndex(DatabaseSqlHelper.OFFER_PRIORITIZED)));
        offer.setItemsList(c.getString(c.getColumnIndex(DatabaseSqlHelper.OFFER_ITEMS_LIST)));
        return offer;
    }

    public void insertOffers(List<Offer> offers) {
        open();
        getDatabase().beginTransaction();
        try {
            String insertSql = "INSERT OR REPLACE INTO "
                    + DatabaseSqlHelper.OFFER_TABLE
                    + " ("
                    +
                    DatabaseSqlHelper.OFFER_ID
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_NAME
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_URL
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_EXPIRED
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_IMAGE
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_IMAGE1200
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_IMAGEHDPI
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_IMAGE2X
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_IMAGEIPAD
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_IMAGEIPAD2X
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_DESCRIPTION
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_PRIORITIZED
                    + ", "
                    +
                    DatabaseSqlHelper.OFFER_ITEMS_LIST
                    +
                    ") VALUES "
                    +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement insertStatement = getDatabase().compileStatement(insertSql);
            for (Offer offer : offers) {
                insertStatement = bindLong(insertStatement, 1, offer.getId());
                insertStatement = bindString(insertStatement, 2, offer.getName());
                insertStatement = bindString(insertStatement, 3, offer.getUrl());
                insertStatement = bindBoolean(insertStatement, 4, offer.isExpired());
                insertStatement = bindString(insertStatement, 5, offer.getImage());
                insertStatement = bindString(insertStatement, 6, offer.getImage1200());
                insertStatement = bindString(insertStatement, 7, offer.getImagehdpi());
                insertStatement = bindString(insertStatement, 8, offer.getImage2x());
                insertStatement = bindString(insertStatement, 9, offer.getImageipad());
                insertStatement = bindString(insertStatement, 10, offer.getImageipad2x());
                insertStatement = bindString(insertStatement, 11, offer.getDescription());
                insertStatement = bindInteger(insertStatement, 12, offer.getPrioritized());
                StringBuilder sb = new StringBuilder();
                if (offer.getItemsList() != null && !offer.getItemsList().isEmpty()) {
                    for (Long itemId : offer.getItemsList()) {
                        sb.append(itemId).append(',');
                    }
                    sb.deleteCharAt(sb.lastIndexOf(","));
                }
                insertStatement = bindString(insertStatement, 13, sb.toString());
                insertStatement.execute();
            }
            getDatabase().setTransactionSuccessful();
        } finally {
            getDatabase().endTransaction();
        }
    }

    public void deleteAllData() {
        open();
        String deleteSql = " DELETE FROM " + DatabaseSqlHelper.OFFER_TABLE;
        getDatabase().execSQL(deleteSql);
    }

}
