package android.support.v4.view;

import android.content.Context;
import android.util.AttributeSet;

public class BannersViewPager extends ViewPager {

    public BannersViewPager(Context context) {
        super(context);
    }

    public BannersViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    void smoothScrollTo(int x, int y, int velocity) {
        super.smoothScrollTo(x, y, 1);
    }

}
