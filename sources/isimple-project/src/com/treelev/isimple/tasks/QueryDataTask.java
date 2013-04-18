package com.treelev.isimple.tasks;

import android.os.AsyncTask;
import android.widget.TextView;
import com.treelev.isimple.data.ShopDAO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class QueryDataTask extends AsyncTask<ShopDAO, Void, Void> {

    private TextView startLoad;
    private TextView endLoad;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public QueryDataTask(TextView startLoad, TextView endLoad) {
        this.startLoad = startLoad;
        this.endLoad = endLoad;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startLoad.setText("Начата выгрузка: " + dateFormat.format(Calendar.getInstance().getTime()));
    }

    @Override
    protected Void doInBackground(ShopDAO... params) {
        List<String> shops = params[0].getShopsWithWine("84067");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        endLoad.setText("Данные выгружены: " + dateFormat.format(Calendar.getInstance().getTime()));
    }
}
