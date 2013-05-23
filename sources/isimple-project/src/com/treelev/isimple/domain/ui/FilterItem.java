package com.treelev.isimple.domain.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.CatalogByCategoryActivity;

public abstract class FilterItem {
    public final static int ITEM_ACTIVITY = 1;
    public final static int ITEM_INLINE = 2;

    private Context context;
    private int itemType;
    private String label;
    private Class activityClass;

    public final static String EXTRA_CATEGORY_ID = "categoryId";
    public final static String EXTRA_POSITION = "position";

    public FilterItem(Context context, int itemType, String label, Class activityClass) {
        this(context, itemType);
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

    protected Class getActivityClass() {
        return activityClass;
    }

    protected Intent createIntent() {
        return new Intent(getContext(), getActivityClass());
    }

    public void process() {
        if (itemType == ITEM_ACTIVITY) {
            ((Activity) context).startActivityForResult(createIntent(), CatalogByCategoryActivity.RESULT_REQUEST_CODE);
            ((Activity) context).overridePendingTransition(R.anim.start_show_anim, R.anim.start_back_anim);
        }
    }

    public abstract View renderView(View convertView);

}
