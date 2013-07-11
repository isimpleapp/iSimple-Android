package com.treelev.isimple.data.lucenedao;

import android.database.Cursor;
import android.util.Log;
import com.treelev.isimple.data.DatabaseSqlHelper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.SnowballProgram;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneDAO {

    private final String DIRECTORY_PATH = "/data/data/com.treelev.isimple/lucene/";
    private final Version VERSION = Version.LUCENE_31;
    private final float defaultMinSimilarity = 0.6f;
    public final static int defaultPrefixLength = 3;

    public LuceneDAO(){

    }

    public boolean isExist(){
        File dir = new File(DIRECTORY_PATH);
        return dir.isDirectory();
    }

    public void update(Cursor items) {
        try{
            if(items != null){
                if(items.moveToFirst()){
                    Analyzer analyzer = getAnalyzer();
                    IndexWriterConfig config = new IndexWriterConfig(VERSION, analyzer);
                    Directory directory = getDirectory();
                    IndexWriter indexWriter =  new IndexWriter(directory, config);
                    do{
                        addElement(indexWriter, items);
                    } while(items.moveToNext());
                    indexWriter.close();
                }
            }
        } catch (IOException e){
            Log.v("Lucene update", e.getMessage());

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

        Field itemID  = new Field(DatabaseSqlHelper.ITEM_ID, checkData(item.getString(indexItemID)), Field.Store.YES, Field.Index.ANALYZED);
        Field name = new Field(DatabaseSqlHelper.ITEM_NAME, checkData(item.getString(indexName)), Field.Store.YES, Field.Index.ANALYZED);
        Field localizedName = new Field(DatabaseSqlHelper.ITEM_LOCALIZED_NAME, checkData(item.getString(indexLocalizedName)), Field.Store.YES, Field.Index.ANALYZED);
        Field manufacture = new Field(DatabaseSqlHelper.ITEM_MANUFACTURER, checkData(item.getString(indexManufacture)), Field.Store.YES, Field.Index.ANALYZED);
        Field localizedManufacture = new Field(DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER, checkData(item.getString(indexLocalizedManufacture)), Field.Store.YES, Field.Index.ANALYZED);
        Field country = new Field(DatabaseSqlHelper.ITEM_COUNTRY, checkData(item.getString(indexCountry)), Field.Store.YES, Field.Index.ANALYZED);
        Field region = new Field(DatabaseSqlHelper.ITEM_REGION, checkData(item.getString(indexRegion)), Field.Store.YES, Field.Index.ANALYZED);
        Field style = new Field(DatabaseSqlHelper.ITEM_STYLE, checkData(item.getString(indexStyle)), Field.Store.YES, Field.Index.ANALYZED);

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

    private String checkData(String str){
        return str != null ? str : "";
    }

    public String query(String query){
        try{
            Log.v("Lucene query", query);
            BooleanQuery bq = getBooleanQuery(query);
            Directory directory = getDirectory();
            IndexReader indexReader = IndexReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            TopDocs docs = indexSearcher.search(bq, 1000);
            Document doc;
            List<String> listID = new ArrayList<String>();
            for(ScoreDoc scoreDoc : docs.scoreDocs){
                doc = indexSearcher.doc(scoreDoc.doc);
                listID.add(doc.get(DatabaseSqlHelper.ITEM_ID));
            }
            return getStringIDs(listID);
        } catch (IOException e){
            Log.v("Lucene query", e.getMessage());
            return null;
        }
    }

    private Directory getDirectory() throws IOException {
        File dir = new File(DIRECTORY_PATH);
        if(!dir.isDirectory()){
            dir.mkdir();
        }
        return  FSDirectory.open(dir);
    }

    private BooleanQuery  getBooleanQuery (String query ){
        Analyzer analyzer = getAnalyzer();

        Term name = new Term(DatabaseSqlHelper.ITEM_NAME, query);
        Term localizedName = new Term(DatabaseSqlHelper.ITEM_LOCALIZED_NAME, query);
        Term manufacture = new Term(DatabaseSqlHelper.ITEM_MANUFACTURER, query);
        Term localizedManufacture = new Term(DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER, query);
//        Term country = new Term(DatabaseSqlHelper.ITEM_COUNTRY, query);
//        Term region = new Term(DatabaseSqlHelper.ITEM_REGION, query);
//        Term style = new Term(DatabaseSqlHelper.ITEM_STYLE, query);

        Query nameQuery = new FuzzyQuery(name, defaultMinSimilarity, defaultPrefixLength);
        Query localizedNameQuery = new FuzzyQuery(localizedName, defaultMinSimilarity, defaultPrefixLength);
        Query manufactureQuery = new FuzzyQuery(manufacture, defaultMinSimilarity, defaultPrefixLength);
        Query localizedManufactureQuery = new FuzzyQuery(localizedManufacture, defaultMinSimilarity, defaultPrefixLength);
//        Query countryQuery = new FuzzyQuery(country, defaultMinSimilarity);
//        Query regionQuery = new FuzzyQuery(region, defaultMinSimilarity);
//        Query styleQuery = new FuzzyQuery(style, defaultMinSimilarity);

        BooleanQuery bq = new BooleanQuery();

        bq.add(nameQuery, BooleanClause.Occur.SHOULD);
        bq.add(localizedNameQuery, BooleanClause.Occur.SHOULD);
        bq.add(manufactureQuery, BooleanClause.Occur.SHOULD);
        bq.add(localizedManufactureQuery, BooleanClause.Occur.SHOULD);
//        bq.add(countryQuery, BooleanClause.Occur.SHOULD);
//        bq.add(regionQuery, BooleanClause.Occur.SHOULD);
//        bq.add(styleQuery, BooleanClause.Occur.SHOULD);

        return bq;
    }

    private Analyzer getAnalyzer(){
        return new RussianAnalyzer(VERSION);
    }

    private PhraseQuery getPhraseQuery(String query){
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

        return phraseQuery;
    }

    private String getStringIDs(List<String> listID){
        StringBuilder strBuilderResult = new StringBuilder();
        Log.v("Lucene count", String.valueOf(listID.size()));
        for(int i = 0; i < listID.size() - 1; ++i){
            strBuilderResult.append(String.format("%s, ", listID.get(i)));
        }
        if(listID.size() - 1 > 0){
            strBuilderResult.append(listID.get(listID.size() - 1));
        }
        Log.v("Lucene find item", strBuilderResult.toString());
        return  strBuilderResult.toString();
    }
}
