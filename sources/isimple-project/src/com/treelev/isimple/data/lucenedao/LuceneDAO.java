package com.treelev.isimple.data.lucenedao;

import android.database.Cursor;
import com.treelev.isimple.data.DatabaseSqlHelper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LuceneDAO {

    private final String DIRECTORY_PATH = "/data/data/com.treelev.isimple/lucene/";
    private  final Version VERSION = Version.LUCENE_31;

    public LuceneDAO(){

    }

    public void update(Cursor items) throws IOException {
        if(items != null){
            if(items.moveToFirst()){
                Analyzer analyzer = new StandardAnalyzer(VERSION);
                IndexWriterConfig config = new IndexWriterConfig(VERSION, analyzer);
                Directory directory = getDirectory();
                IndexWriter indexWriter =  new IndexWriter(directory, config);
                do{
                    addElement(indexWriter, items);
                } while(items.moveToNext());
                indexWriter.close();
            }
        }
    }

    private void addElement(IndexWriter indexWriter, Cursor item) throws IOException {
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

        Document doc = new Document();

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

    private Directory getDirectory() throws IOException {
        File dir = new File(DIRECTORY_PATH);
        if(!dir.isDirectory()){
            dir.mkdir();
        }
        return  FSDirectory.open(dir);
    }

    public String query(String query) throws ParseException, IOException {
        Analyzer analyzer = new StandardAnalyzer(VERSION);

        Term name = new Term(DatabaseSqlHelper.ITEM_NAME, query);
        Term localizedName = new Term(DatabaseSqlHelper.ITEM_LOCALIZED_NAME, query);
        Term manufacture = new Term(DatabaseSqlHelper.ITEM_MANUFACTURER, query);
        Term localizedManufacture = new Term(DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER, query);
        Term country = new Term(DatabaseSqlHelper.ITEM_COUNTRY, query);
        Term region = new Term(DatabaseSqlHelper.ITEM_REGION, query);
        Term style = new Term(DatabaseSqlHelper.ITEM_STYLE, query);

        PhraseQuery phraseQuery = new PhraseQuery();
        phraseQuery.add(name);
        phraseQuery.add(localizedName);
        phraseQuery.add(manufacture);
        phraseQuery.add(localizedManufacture);
        phraseQuery.add(country);
        phraseQuery.add(region);
        phraseQuery.add(style);

        Directory directory = getDirectory();
        IndexReader indexReader = IndexReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs docs = indexSearcher.search(phraseQuery, 100);
        Document doc;
        List<String> listID = new ArrayList<String>();
        for(ScoreDoc scoreDoc : docs.scoreDocs){
            doc = indexSearcher.doc(scoreDoc.doc);
            listID.add(doc.get(DatabaseSqlHelper.ITEM_ID));
        }
        return getStringIDs(listID);
    }

    private String getStringIDs(List<String> listID){
        StringBuilder strBuilderResult = new StringBuilder();
        for(int i = 0; i < listID.size() - 1; ++i){
            strBuilderResult.append(String.format("%s, ", listID.get(i)));
        }
        if(listID.size() - 1 > 0){
            strBuilderResult.append(listID.get(listID.size() - 1));
        }
        return  strBuilderResult.toString();
    }
}
