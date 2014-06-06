package com.treelev.isimple.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.treelev.isimple.enumerable.UpdateFile;

public class LoadFileData {

    public final static String FILE_DATE_TAG_NAME = "UpdateDate";
    private Date loadDate;
    public final static String FILE_URL_TAG_NAME = "UpdateURL";
    private String fileUrl;
    public final static SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public LoadFileData(Date loadDate, String fileUrl) {
        this.loadDate = loadDate;
        this.fileUrl = fileUrl;
    }

    public Date getLoadDate() {
        return loadDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
