package com.treelev.isimple.utils.managers;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipManager {

    public XmlPullParser unZipFile(File zipFile) {
        XmlPullParser xmlPullParser = null;
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
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
            zipFile.delete();
            if (newFile != null) {
                newFile.delete();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlPullParser;
    }
}
