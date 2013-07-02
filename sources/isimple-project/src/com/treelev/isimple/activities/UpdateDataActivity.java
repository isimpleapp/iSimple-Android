package com.treelev.isimple.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.domain.FileParseObject;
import com.treelev.isimple.service.UpdateDataService;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateDataActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new View(this);
        view.setBackgroundColor(Color.WHITE);
        view.getBackground().setAlpha(150);
        setContentView(view);
        File directory = new File(String.format(UpdateDataService.FILE_URL_FORMAT, Environment.getExternalStorageDirectory()));
        List<FileParseObject> fileParseObjectList = createFileList(directory.listFiles());
        new UpdateDataTask().execute(fileParseObjectList.toArray(new FileParseObject[fileParseObjectList.size()]));
    }

    private List<FileParseObject> createFileList(File[] fileList) {
        List<FileParseObject> fileParseObjectList = new ArrayList<FileParseObject>();
        for (File file : fileList) {
            fileParseObjectList.add(new FileParseObject(file, this));
        }
        Collections.sort(fileParseObjectList);
        return fileParseObjectList;
    }

    private class UpdateDataTask extends AsyncTask<FileParseObject, Void, Void> {

        private Dialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UpdateDataActivity.this, getString(R.string.update_data_notify_label),
                    getString(R.string.update_data_wait_label), false, false);
        }

        @Override
        protected Void doInBackground(FileParseObject... fileParseObjects) {
            for (FileParseObject fileParseObject : fileParseObjects) {
                fileParseObject.parseObjectDataToDB();
            }
            deleteFileDir();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Intent newIntent = new Intent(UpdateDataActivity.this, CatalogListActivityNew.class);
            newIntent.putExtra(UpdateDataService.NEED_DATA_UPDATE, false);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
        }

        private void deleteFileDir() {
            new File(String.format(UpdateDataService.FILE_URL_FORMAT, Environment.getExternalStorageDirectory())).delete();
        }
    }
}