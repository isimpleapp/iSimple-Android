package com.treelev.isimple.utils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class DownloadFileResponse {
    
    private File downloadedFile;
    private Map<String, List<String>> headers;
    
    public File getDownloadedFile() {
        return downloadedFile;
    }
    public void setDownloadedFile(File downloadedFile) {
        this.downloadedFile = downloadedFile;
    }
    public Map<String, List<String>> getHeaders() {
        return headers;
    }
    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

}
