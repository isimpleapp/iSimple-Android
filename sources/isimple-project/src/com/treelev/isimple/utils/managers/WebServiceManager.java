package com.treelev.isimple.utils.managers;

import android.os.Environment;
import android.util.Log;

import com.treelev.isimple.domain.LoadFileData;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServiceManager {

	public final static String FILE_URL_FORMAT = "%s/Simple";
	private final static String REG_FILENAME_FORMAT = ".+/(.+.xmlz)";
	private final static String DEFAULT_FILENAME = "isimple_data.xmlz";

	public File downloadFile(String fileUrl) {
		File downloadingFile = null;
		try {
			URL downloadUrl = new URL(fileUrl);
			URLConnection urlConnection = downloadUrl.openConnection();
			urlConnection.connect();
			int fileLength = urlConnection.getContentLength();
			InputStream input = new BufferedInputStream(downloadUrl.openStream());
			File directory = new File(String.format(FILE_URL_FORMAT, Environment.getExternalStorageDirectory()));
			directory.mkdir();
			downloadingFile = new File(directory.getPath() + File.separator + getFileName(fileUrl));
			OutputStream output = new FileOutputStream(downloadingFile);
			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				output.write(data, 0, count);
			}
			output.close();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(requestString);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		Document document = new SAXBuilder().build(httpResponse.getEntity().getContent());
		List<Element> contentList = document.getRootElement().getChildren();
		for (Element element : contentList) {
			Element urlElement = element.getChild(LoadFileData.FILE_URL_TAG_NAME);
			Element dateElement = element.getChild(LoadFileData.FILE_DATE_TAG_NAME);
			String url = urlElement.getValue();
			String zipfileName = url.substring( url.lastIndexOf('/') + 1, url.length());
			loadFileDataList.add(new LoadFileData(LoadFileData.FILE_DATE_FORMAT.parse(dateElement.getValue()),
					urlElement.getValue()));
			SharedPreferencesManager.putUpdateFileName(zipfileName, element.getName());
		}
		return loadFileDataList;
	}
	
	public Map<String, LoadFileData> getLoadFileDataMap(String requestString) throws Exception {
	    Map<String, LoadFileData> loadFileDataList = new HashMap<String, LoadFileData>();

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(requestString);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        Document document = new SAXBuilder().build(httpResponse.getEntity().getContent());
        List<Element> contentList = document.getRootElement().getChildren();
        for (Element element : contentList) {
            Element urlElement = element.getChild(LoadFileData.FILE_URL_TAG_NAME);
            Element dateElement = element.getChild(LoadFileData.FILE_DATE_TAG_NAME);
            String url = urlElement.getValue();
            String zipfileName = url.substring( url.lastIndexOf('/') + 1, url.length());
            loadFileDataList.put(element.getName(), new LoadFileData(LoadFileData.FILE_DATE_FORMAT.parse(dateElement.getValue()),
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
