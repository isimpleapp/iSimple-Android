package com.treelev.isimple.cursorloaders;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import com.treelev.isimple.utils.managers.ProxyManager;

public abstract class BaseCursorLoader extends CursorLoader {
    protected Context mContext;
    private ProxyManager mProxyManager;

    public BaseCursorLoader(Context context) {
        super(context);
        mContext = context;
    }

    protected ProxyManager getProxyManager() {
        if (mProxyManager == null) {
            mProxyManager = ProxyManager.getInstanse();
        }
        return mProxyManager;
    }

    @Override
    public void reset() {
        super.reset();
        if (mProxyManager != null) {
            mProxyManager.release();
            mProxyManager = null;
        }
    }
}
