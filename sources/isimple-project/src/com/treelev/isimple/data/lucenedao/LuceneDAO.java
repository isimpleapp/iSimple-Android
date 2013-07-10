package com.treelev.isimple.data.lucenedao;

import android.database.Cursor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

public class LuceneDAO {

    private IndexWriter mIndexWriter;

    public LuceneDAO(){

    }

    public void update(Cursor items) throws IOException {
        if(items != null){
            if(items.moveToFirst()){
                Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
                IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_31, analyzer);
                File file = new File("/data/data/com.treelev.isimple/databases/");
                Directory directory = FSDirectory.open(file);
                mIndexWriter =  new IndexWriter(directory, config);
                do{
                    addElement(items);
                } while(items.moveToNext());
            }
        }
    }

    private void addElement(Cursor item){
        Document doc = new Document();


    }
}
