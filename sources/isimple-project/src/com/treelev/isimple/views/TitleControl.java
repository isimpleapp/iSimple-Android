package com.treelev.isimple.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.treelev.isimple.R;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.PopupMenu;

public class TitleControl extends LinearLayout implements View.OnClickListener {

    public TitleControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        View rootView = View.inflate(context, R.layout.title_view, this);
        rootView.findViewById(R.id.main_menu_butt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.main_menu);
        popupMenu.show();
    }
}
