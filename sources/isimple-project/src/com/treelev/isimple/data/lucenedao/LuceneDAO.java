package com.treelev.isimple.data.lucenedao;

import android.database.Cursor;

import android.util.Log;

import com.treelev.isimple.data.DatabaseSqlHelper;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuceneDAO {

    private final String DIRECTORY_PATH = "/data/data/com.treelev.isimple/lucene/";
    private final Version VERSION = Version.LUCENE_31;
    private final int COUNT_RECORD = 2000;

    private enum TypeQuery{Primary, Secondary,  Tertiary, Quaternary};

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
                    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
                    Directory directory = getDirectory();
                    IndexWriter indexWriter =  new IndexWriter(directory, config);
                    do{
                        addElement(indexWriter, items);
                    } while(items.moveToNext());
                    indexWriter.close();
                }
            }
        } catch (IOException e){
        }
    }

    public String query(String query){
        try{
            Directory directory = getDirectory();
            IndexReader indexReader = IndexReader.open(directory);
            List<String> listID = getListID(indexReader, TypeQuery.Primary, query);
            if(listID == null) {
                listID = getListID(indexReader, TypeQuery.Secondary, query);
                if(listID == null){
                    listID = getListID(indexReader, TypeQuery.Tertiary, query);
                    if(listID == null){
                        listID = getListID(indexReader, TypeQuery.Quaternary, query);
                    }
                }
            }
            return getStringIDs(listID);
        } catch (IOException e){
            return null;
        }

    }

    private List<String> getListID(IndexReader indexReader, TypeQuery typeQuery, String query) throws IOException {
        Query queryObj = getQuery(query, typeQuery);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Document doc;
        TopDocs docs = indexSearcher.search(queryObj, COUNT_RECORD);
        List<String> listID = null;
        if( docs.totalHits > 0 ){
            listID = new ArrayList<String>();
            for(ScoreDoc scoreDoc : docs.scoreDocs){
                doc = indexSearcher.doc(scoreDoc.doc);
                listID.add(doc.get(DatabaseSqlHelper.ITEM_ID));
            }
        }
        return listID;
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
        int indexDrinkType = item.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_TYPE);
        int indexStyleDescription = item.getColumnIndex(DatabaseSqlHelper.ITEM_STYLE_DESCRIPTION);
        int indexTasteQualities = item.getColumnIndex(DatabaseSqlHelper.ITEM_TASTE_QUALITIES);
        int indexVintageReport = item.getColumnIndex(DatabaseSqlHelper.ITEM_VINTAGE_REPORT);
        int indexAgingProcess = item.getColumnIndex(DatabaseSqlHelper.ITEM_AGING_PROCESS);
        int indexProductionProcess = item.getColumnIndex(DatabaseSqlHelper.ITEM_PRODUCTION_PROCESS);
        int indexInterestingFacts = item.getColumnIndex(DatabaseSqlHelper.ITEM_INTERESTING_FACTS);
        int indexLabelHistory = item.getColumnIndex(DatabaseSqlHelper.ITEM_LABEL_HISTORY);
        int indexGastronomy = item.getColumnIndex(DatabaseSqlHelper.ITEM_GASTRONOMY);
        int indexVineyard = item.getColumnIndex(DatabaseSqlHelper.ITEM_VINEYARD);
        int indexGrapesUsed = item.getColumnIndex(DatabaseSqlHelper.ITEM_GRAPES_USED);

        Field itemID  = new Field(DatabaseSqlHelper.ITEM_ID, checkData(item.getString(indexItemID)), Field.Store.YES, Field.Index.NOT_ANALYZED);
        Field name = new Field(DatabaseSqlHelper.ITEM_NAME, checkData(item.getString(indexName)), Field.Store.YES, Field.Index.ANALYZED);
        Field localizedName = new Field(DatabaseSqlHelper.ITEM_LOCALIZED_NAME, checkData(item.getString(indexLocalizedName)), Field.Store.YES, Field.Index.ANALYZED);
        Field manufacture = new Field(DatabaseSqlHelper.ITEM_MANUFACTURER, checkData(item.getString(indexManufacture)), Field.Store.YES, Field.Index.ANALYZED);
        Field localizedManufacture = new Field(DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER, checkData(item.getString(indexLocalizedManufacture)), Field.Store.YES, Field.Index.ANALYZED);
        Field country = new Field(DatabaseSqlHelper.ITEM_COUNTRY, checkData(item.getString(indexCountry)), Field.Store.YES, Field.Index.ANALYZED);
        Field region = new Field(DatabaseSqlHelper.ITEM_REGION, checkData(item.getString(indexRegion)), Field.Store.YES, Field.Index.ANALYZED);
        Field style = new Field(DatabaseSqlHelper.ITEM_STYLE, checkData(item.getString(indexStyle)), Field.Store.YES, Field.Index.ANALYZED);
        Field drinkType = new Field(DatabaseSqlHelper.ITEM_DRINK_TYPE, checkData(item.getString(indexDrinkType)), Field.Store.YES, Field.Index.ANALYZED);
        Field styleDescription = new Field(DatabaseSqlHelper.ITEM_STYLE_DESCRIPTION, checkData(item.getString(indexStyleDescription)), Field.Store.YES, Field.Index.ANALYZED);
        Field tasteQualities = new Field(DatabaseSqlHelper.ITEM_TASTE_QUALITIES, checkData(item.getString(indexTasteQualities)), Field.Store.YES, Field.Index.ANALYZED);
        Field vintageReport = new Field(DatabaseSqlHelper.ITEM_VINTAGE_REPORT, checkData(item.getString(indexVintageReport)), Field.Store.YES, Field.Index.ANALYZED);
        Field agingProcess = new Field(DatabaseSqlHelper.ITEM_AGING_PROCESS, checkData(item.getString(indexAgingProcess)), Field.Store.YES, Field.Index.ANALYZED);
        Field productionProcess = new Field(DatabaseSqlHelper.ITEM_PRODUCTION_PROCESS, checkData(item.getString(indexProductionProcess)), Field.Store.YES, Field.Index.ANALYZED);
        Field interestingFacts = new Field(DatabaseSqlHelper.ITEM_INTERESTING_FACTS, checkData(item.getString(indexInterestingFacts)), Field.Store.YES, Field.Index.ANALYZED);
        Field labelHistory = new Field(DatabaseSqlHelper.ITEM_LABEL_HISTORY, checkData(item.getString(indexLabelHistory)), Field.Store.YES, Field.Index.ANALYZED);
        Field gastronomy = new Field(DatabaseSqlHelper.ITEM_GASTRONOMY, checkData(item.getString(indexGastronomy)), Field.Store.YES, Field.Index.ANALYZED);
        Field vineyard = new Field(DatabaseSqlHelper.ITEM_VINEYARD, checkData(item.getString(indexVineyard)), Field.Store.YES, Field.Index.ANALYZED);
        Field grapesUsed = new Field(DatabaseSqlHelper.ITEM_GRAPES_USED, checkData(item.getString(indexGrapesUsed)), Field.Store.YES, Field.Index.ANALYZED);

        Document doc = new Document();

        doc.add(itemID);
        doc.add(name);
        doc.add(localizedName);
        doc.add(manufacture);
        doc.add(localizedManufacture);
        doc.add(country);
        doc.add(region);
        doc.add(style);
        doc.add(drinkType);
        doc.add(styleDescription);
        doc.add(tasteQualities);
        doc.add(vintageReport);
        doc.add(agingProcess);
        doc.add(productionProcess);
        doc.add(labelHistory);
        doc.add(interestingFacts);
        doc.add(gastronomy);
        doc.add(vineyard);
        doc.add(grapesUsed);

        indexWriter.addDocument(doc);
    }

    private String checkData(String str){
        return str != null ? str.trim() : "";
    }

    private Directory getDirectory() throws IOException {
        File dir = new File(DIRECTORY_PATH);
        if(!dir.isDirectory()){
            dir.mkdir();
        }
        return  FSDirectory.open(dir);
    }

    private Query  getQuery (String query, TypeQuery typeQuery ) {
        Analyzer analyzer = getAnalyzer();
        String defaultField = "";
        String luceneQuery;
        switch (typeQuery){
            case Primary:
                luceneQuery = getWildcardQueryPrimary(query);
                defaultField = DatabaseSqlHelper.ITEM_LOCALIZED_NAME;
                break;
            case Secondary:
                luceneQuery = getWildcardQuerySecondary(query);
                defaultField = DatabaseSqlHelper.ITEM_COUNTRY;
                break;
            case Tertiary:
                luceneQuery = getWildcardQueryTertiary(query);
                defaultField = DatabaseSqlHelper.ITEM_DRINK_TYPE;
                break;
            case Quaternary:
                luceneQuery  = getWildcardQueryQuaternary(query);
                defaultField = DatabaseSqlHelper.ITEM_TASTE_QUALITIES;
                break;
            default:
                luceneQuery  = null;
        }
        Log.v("Lucene getQuery", luceneQuery);
        QueryParser parser = new QueryParser(VERSION, defaultField, analyzer);
        parser.setLowercaseExpandedTerms(true);
        Query queryObj = null;
        try {
           queryObj = parser.parse(luceneQuery);
        } catch (ParseException e){

        }
        return queryObj;
    }

    private String getWildcardQueryPrimary(String query){
        String luceneQuery = String.format("%2$s: %1$s OR %3$s: %1$s " +
                "OR %4$s: %1$s OR %5$s: %1$s ",
                query,
                DatabaseSqlHelper.ITEM_NAME,
                DatabaseSqlHelper.ITEM_LOCALIZED_NAME,
                DatabaseSqlHelper.ITEM_MANUFACTURER,
                DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER);
        return luceneQuery;
    }

    private String getWildcardQuerySecondary(String query){
        String luceneQuery = String.format("%2$s: %1$s OR %3$s: %1$s ",
                query,
                DatabaseSqlHelper.ITEM_COUNTRY,
                DatabaseSqlHelper.ITEM_REGION);
        return luceneQuery;
    }

    private String getWildcardQueryTertiary(String query){
        String luceneQuery = String.format("%2$s: %1$s OR %3$s: %1$s " +
                "OR %4$s: %1$s OR %5$s: %1$s ",
                query,
                DatabaseSqlHelper.ITEM_STYLE,
                DatabaseSqlHelper.ITEM_DRINK_TYPE,
                DatabaseSqlHelper.ITEM_STYLE_DESCRIPTION,
                DatabaseSqlHelper.ITEM_GRAPES_USED);
        return luceneQuery;
    }

    private String getWildcardQueryQuaternary(String query){
        String luceneQuery = String.format("%2$s: %1$s* OR %3$s: %1$s* " +
                "OR %4$s: %1$s* OR %5$s: %1$s* " +
                "OR %6$s: %1$s* OR %7$s: %1$s* " +
                "OR %8$s: %1$s*",
                query,
                DatabaseSqlHelper.ITEM_TASTE_QUALITIES,
                DatabaseSqlHelper.ITEM_VINTAGE_REPORT,
                DatabaseSqlHelper.ITEM_AGING_PROCESS,
                DatabaseSqlHelper.ITEM_PRODUCTION_PROCESS,
                DatabaseSqlHelper.ITEM_INTERESTING_FACTS,
                DatabaseSqlHelper.ITEM_LABEL_HISTORY,
                DatabaseSqlHelper.ITEM_GASTRONOMY);
        return luceneQuery;
    }

    private Analyzer getAnalyzer(){
        Map<String,Analyzer> analyzerMap = new HashMap<String, Analyzer>();
        analyzerMap.put(DatabaseSqlHelper.ITEM_NAME, new EnglishAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_LOCALIZED_NAME, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_MANUFACTURER, new EnglishAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_LOCALIZED_MANUFACTURER, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_COUNTRY, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_REGION, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_STYLE, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_DRINK_TYPE, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_STYLE_DESCRIPTION, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_TASTE_QUALITIES, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_AGING_PROCESS, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_PRODUCTION_PROCESS, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_INTERESTING_FACTS, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_LABEL_HISTORY, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_GASTRONOMY, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_VINEYARD, new RussianAnalyzer(VERSION));
        analyzerMap.put(DatabaseSqlHelper.ITEM_GRAPES_USED, new RussianAnalyzer(VERSION));

        return new PerFieldAnalyzerWrapper(new StandardAnalyzer(VERSION), analyzerMap);
    }

    private String getStringIDs(List<String> listID){
        String stringIDs = "";
        if(listID != null){
            StringBuilder strBuilderResult = new StringBuilder();
            for(int i = 0; i < listID.size() - 1; ++i){
                strBuilderResult.append(String.format("'%s', ", listID.get(i)));
            }
            if(listID.size() - 1 > 0){
                strBuilderResult.append(listID.get(listID.size() - 1));
            }
            stringIDs = strBuilderResult.toString();
        }
        return  stringIDs;
    }
}
