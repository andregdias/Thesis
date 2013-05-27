package com.opt.mobipag.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.opt.mobipag.R;

public class ActionBar extends RelativeLayout {

    public ImageButton helpButton;
    public ImageButton logoutButton;

    public ActionBar(Context context) {
        super(context);
    }

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initHeader() {
        inflateHeader();
    }

    private void inflateHeader() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.actionbarmainmenu, this);
        helpButton = (ImageButton) findViewById(R.id.help);
        helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
            }
        });
        logoutButton = (ImageButton) findViewById(R.id.logout);
        logoutButton.setVisibility(GONE);
    }
}