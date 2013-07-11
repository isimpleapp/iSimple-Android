package com.treelev.isimple.data.lucenedao;

import android.database.Cursor;
import com.treelev.isimple.data.DatabaseSqlHelper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

public class LuceneDAO {

    private final String DIRECTORY_PATH = "/data/data/com.treelev.isimple/lucene/";

    public LuceneDAO(){

    }

    public void update(Cursor items) throws IOException {
        if(items != null){
            if(items.moveToFirst()){
                Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
                IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_31, analyzer);
                Directory directory = FSDirectory.open(getDirectory());
                IndexWriter indexWriter =  new IndexWriter(directory, config);
                do{
                    addElement(indexWriter, items);
                } while(items.moveToNext());
                indexWriter.close();
            }
        }
    }

    private void addElement(IndexWriter indexWriter, Cursor item) throws IOException {
        Document doc = new Document();

        int indexItemID = item.getColumnIndex(DatabaseSqlHelper.ITEM_ID);
        int indexName = item.getColumnIndex(DatabaseSqlHelper.ITEM_NAME);
        int indexLocalizedName = item.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_NAME);
        int indexManufacture = item.getColumnIndex(DatabaseSqlHelper.ITEM_MANUFACTURER);
        int indexLocalizedManufacture = item.getColumnIndex(DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER);
        int indexCountry = item.getColumnIndex(DatabaseSqlHelper.ITEM_COUNTRY);
        int indexRegion = item.getColumnIndex(DatabaseSqlHelper.ITEM_REGION);
        int indexStyle = item.getColumnIndex(DatabaseSqlHelper.ITEM_STYLE);

        Field itemID  = new Field(DatabaseSqlHelper.ITEM_ID, item.getString(indexItemID), Field.Store.YES, Field.Index.ANALYZED);
        Field name = new Field(DatabaseSqlHelper.ITEM_NAME, item.getString(indexName), Field.Store.YES, Field.Index.ANALYZED);
        Field localizedName = new Field(DatabaseSqlHelper.ITEM_LOCALIZED_NAME, item.getString(indexLocalizedName), Field.Store.YES, Field.Index.ANALYZED);
        Field manufacture = new Field(DatabaseSqlHelper.ITEM_MANUFACTURER, item.getString(indexManufacture), Field.Store.YES, Field.Index.ANALYZED);
        Field localizedManufacture = new Field(DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER, item.getString(indexLocalizedManufacture), Field.Store.YES, Field.Index.ANALYZED);
        Field country = new Field(DatabaseSqlHelper.ITEM_COUNTRY, item.getString(indexCountry), Field.Store.YES, Field.Index.ANALYZED);
        Field region = new Field(DatabaseSqlHelper.ITEM_REGION, item.getString(indexRegion), Field.Store.YES, Field.Index.ANALYZED);
        Field style = new Field(DatabaseSqlHelper.ITEM_STYLE, item.getString(indexStyle), Field.Store.YES, Field.Index.ANALYZED);

        doc.add(itemID);
        doc.add(name);
        doc.add(localizedName);
        doc.add(manufacture);
        doc.add(localizedManufacture);
        doc.add(country);
        doc.add(region);
        doc.add(style);

        indexWriter.addDocument(doc);
    }

    private File getDirectory(){
        File dir = new File(DIRECTORY_PATH);
        if(!dir.isDirectory()){
            dir.mkdir();
        }
        return  dir;
    }

    public String query(String query){
        return "";
    }
}
