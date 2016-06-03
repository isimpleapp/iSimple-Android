package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

public class LoadBannerItems extends BaseCursorLoader {

    private List<Long> bannerItemsIds;
    public LoadBannerItems(Context context, List<Long> bannerItemsIds) {
        super(context);
        
        this.bannerItemsIds = bannerItemsIds;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = getProxyManager().getItemsByIds(bannerItemsIds);
        return cursor;
    }
}
