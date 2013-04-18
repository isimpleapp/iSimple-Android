package com.treelev.isimple.utils.managers;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class WebServiceManager {

    public File downloadFile(String requestString) {
        File downloadingFile = null;
        try {
            URL downloadUrl = new URL(requestString);
            URLConnection urlConnection = downloadUrl.openConnection();
            urlConnection.connect();
            int fileLength = urlConnection.getContentLength();
            InputStream input = new BufferedInputStream(downloadUrl.openStream());
            downloadingFile = new File("/sdcard/isimple_data.xmlz");
            OutputStream output = new FileOutputStream(downloadingFile);
            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return downloadingFile;
    }
}
