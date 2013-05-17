package com.treelev.isimple.domain.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public abstract class FilterItem {
    public final static int ITEM_ACTIVITY = 1;
    public final static int ITEM_INLINE = 2;

    private Context context;
    private int itemType;
    private String label;
    private Class activityClass;

    public FilterItem(Context context, int itemType, String label, Class activityClass) {
        this.label = label;
        this.activityClass = activityClass;
    }

    protected FilterItem(Context context, int itemType) {
        this.context = context;
        this.itemType = itemType;
    }

    public int getItemType() {
        return itemType;
    }

    public Context getContext() {
        return context;
    }

    public String getLabel() {
        return label;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public void process() {
        //TODO: как передавать параметры??
        if (itemType == ITEM_ACTIVITY) {
            Intent intent = new Intent(context, activityClass);
            context.startActivity(intent);
        }
    }

    public abstract View renderView(View convertView);

}
