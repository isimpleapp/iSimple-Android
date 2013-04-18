package com.treelev.isimple.tasks;

import android.os.AsyncTask;
import android.widget.TextView;
import com.treelev.isimple.data.ItemDAO;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipTask extends AsyncTask<File, Integer, XmlPullParser> {

    private TextView startTime;
    private TextView endTime;
    private TextView startLoad;
    private TextView endLoad;
    private ItemDAO itemDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public UnzipTask(TextView startTime, TextView endTime, TextView startLoad, TextView endLoad, ItemDAO itemDAO) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLoad = startLoad;
        this.endLoad = endLoad;
        this.itemDAO = itemDAO;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime.setText("StartUnZip: " + dateFormat.format(Calendar.getInstance().getTime()));
    }

    @Override
    protected XmlPullParser doInBackground(File... params) {
        XmlPullParser xmlPullParser = null;
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(params[0]));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            File newFile = null;
            byte[] buffer = new byte[4096];
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                newFile = new File("/sdcard/" + fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len);
                }
                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
            zipInputStream.close();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new FileInputStream(newFile), null);
            if (newFile != null) {
                newFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return xmlPullParser;
    }

    @Override
    protected void onPostExecute(XmlPullParser xmlPullParser) {
        super.onPostExecute(xmlPullParser);
        endTime.setText("EndUnZip: " + dateFormat.format(Calendar.getInstance().getTime()));
        //new ParseDataTask(startLoad, endLoad, itemDAO).execute(xmlPullParser);
    }
}
