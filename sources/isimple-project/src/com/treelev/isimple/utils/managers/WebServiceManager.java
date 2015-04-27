
package com.treelev.isimple.utils.managers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import android.os.Environment;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.treelev.isimple.domain.LoadFileData;

public class WebServiceManager {

    public final static String FILE_URL_FORMAT = "%s/Simple";
    public final static String NEW_CATALOG_ITEM_FILE_URL_FORMAT = "%s/Simple/new";
    private final static String REG_FILENAME_FORMAT = ".+/(.+.xmlz)";
    private final static String DEFAULT_FILENAME = "isimple_data.xmlz";

    public File downloadFile(String fileUrl) throws IOException {
        return downloadFile(fileUrl,
                String.format(FILE_URL_FORMAT, Environment.getExternalStorageDirectory()));
    }

    public File downloadNewCatalogItemFile(String fileUrl) throws IOException {
        return downloadFile(
                fileUrl,
                String.format(NEW_CATALOG_ITEM_FILE_URL_FORMAT,
                        Environment.getExternalStorageDirectory()));
    }

    private File downloadFile(String fileUrl, String path) throws IOException {
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
            output.close();
            input.close();
        return downloadingFile;
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
                String.format(FILE_URL_FORMAT, Environment.getExternalStorageDirectory()));
        return new File(String.format(FILE_URL_FORMAT, Environment.getExternalStorageDirectory()));
    }

    public List<LoadFileData> getLoadFileData(String requestString) throws Exception {
        List<LoadFileData> loadFileDataList = new ArrayList<LoadFileData>();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(requestString)
                .build();

        Response response = client.newCall(request).execute();
        Document document = new SAXBuilder().build(response.body().string());
        List<Element> contentList = document.getRootElement().getChildren();
        for (Element element : contentList) {
            Element urlElement = element.getChild(LoadFileData.FILE_URL_TAG_NAME);
            Element dateElement = element.getChild(LoadFileData.FILE_DATE_TAG_NAME);
            String url = urlElement.getValue();
            String zipfileName = url.substring(url.lastIndexOf('/') + 1, url.length());
            loadFileDataList.add(new LoadFileData(LoadFileData.FILE_DATE_FORMAT.parse(dateElement
                    .getValue()),
                    urlElement.getValue()));
            SharedPreferencesManager.putUpdateFileName(zipfileName, element.getName());
        }
        return loadFileDataList;
    }

    public Map<String, LoadFileData> getLoadFileDataMap(String requestString) throws Exception {
        Map<String, LoadFileData> loadFileDataList = new HashMap<String, LoadFileData>();

        Document document = null;
        try {
            document = new SAXBuilder().build(requestString);
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new Exception();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        }
        List<Element> contentList = document.getRootElement().getChildren();
        for (Element element : contentList) {
            Element urlElement = element.getChild(LoadFileData.FILE_URL_TAG_NAME);
            Element dateElement = element.getChild(LoadFileData.FILE_DATE_TAG_NAME);
            String url = urlElement.getValue();
            String zipfileName = url.substring(url.lastIndexOf('/') + 1, url.length());
            loadFileDataList.put(element.getName(),
                    new LoadFileData(LoadFileData.FILE_DATE_FORMAT.parse(dateElement.getValue()),
                            urlElement.getValue()));
            SharedPreferencesManager.putUpdateFileName(zipfileName, element.getName());
        }
        return loadFileDataList;
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
