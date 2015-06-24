
package com.treelev.isimple.utils.managers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Environment;
import android.util.Log;

import com.treelev.isimple.domain.LoadFileData;
import com.treelev.isimple.parser.UpdateFileParser;
import com.treelev.isimple.utils.DownloadFileResponse;
import com.treelev.isimple.utils.LogUtils;

public class WebServiceManager {

    public final static String FILE_URL_FORMAT = "%s/Simple";
    public final static String NEW_CATALOG_ITEM_FILE_URL_FORMAT = "%s/Simple/new";
    private final static String REG_FILENAME_FORMAT = ".+/(.+.xmlz)";
    private final static String DEFAULT_FILENAME = "isimple_data.xmlz";
    
    public static File getTempFilesPath() {
        String state = Environment.getExternalStorageState();
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            externalStorageAvailable = externalStorageWriteable = false;
        }
        
        if (externalStorageAvailable && externalStorageWriteable) {
            return Environment.getExternalStorageDirectory();
        } else {
            return Environment.getDownloadCacheDirectory();
        }
    }

    public DownloadFileResponse downloadFile(String fileUrl) throws IOException {
        return downloadFile(fileUrl,
                String.format(FILE_URL_FORMAT, getTempFilesPath()));
    }

    public DownloadFileResponse downloadNewCatalogItemFile(String fileUrl) throws IOException {
        return downloadFile(
                fileUrl,
                String.format(NEW_CATALOG_ITEM_FILE_URL_FORMAT, getTempFilesPath()));
    }

    private DownloadFileResponse downloadFile(String fileUrl, String path) throws IOException {
        File downloadingFile = null;
        HttpURLConnection urlConnection = null;
        URL downloadUrl = new URL(fileUrl);
        urlConnection = (HttpURLConnection) downloadUrl.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(5000);
        urlConnection.connect();
        InputStream input = new BufferedInputStream(urlConnection.getInputStream());
        File directory = new File(path);
        directory.mkdir();
        downloadingFile = new File(directory.getPath() + File.separator + getFileName(fileUrl));
        FileOutputStream output = new FileOutputStream(downloadingFile);
        byte data[] = new byte[1024];
        int count;
        while ((count = input.read(data)) != -1) {
            output.write(data, 0, count);
        }
        Map<String, List<String>> headers = urlConnection.getHeaderFields();
        output.close();
        input.close();

        DownloadFileResponse response = new DownloadFileResponse();
        response.setDownloadedFile(downloadingFile);
        response.setHeaders(headers);
        return response;
    }

    public static void deleteDownloadDirectory() {
        File directory = getDownloadDirectory();
        if (directory.exists()) {
            boolean flag = directory.delete();
            Log.v("Test log delete directory", String.valueOf(flag));
        }
    }

    public static File getDownloadDirectory() {
        Log.v("Test log getDownloadDirectory",
                String.format(FILE_URL_FORMAT, getTempFilesPath()));
        return new File(String.format(FILE_URL_FORMAT, getTempFilesPath()));
    }

    public Map<String, LoadFileData> getLoadFileDataMap(String requestString) throws Exception {
        Map<String, LoadFileData> loadFileDataList = null;

        URL url;
        HttpURLConnection urlConnection = null;

        String response = null;
        try {
            url = new URL(requestString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(30 * 1000);
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == 200) {
                response = readStream(urlConnection.getInputStream());
                Log.v("", "getLoadFileDataMap = " + response);
            } else {
                Log.v("", "Response code:" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        
        UpdateFileParser updateFileParser = new UpdateFileParser();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new ByteArrayInputStream(response.getBytes()), null);
            loadFileDataList = updateFileParser.parseXml(xmlPullParser);
        } catch (XmlPullParserException e) {
            LogUtils.i("", "Failed to parse UPDATE file " + e);
        }

        return loadFileDataList;
    }

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private String getFileName(String fileUrl) {
        Pattern pattern = Pattern.compile(REG_FILENAME_FORMAT);
        Matcher matcher = pattern.matcher(fileUrl);
        String fileName = DEFAULT_FILENAME;
        while (matcher.find()) {
            fileName = matcher.group(1);
        }
        return fileName;
    }
}
