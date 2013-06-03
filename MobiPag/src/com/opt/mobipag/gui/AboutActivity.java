package com.opt.mobipag.gui;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.opt.mobipag.R;

public class AboutActivity extends Activity {
    private PackageInfo mPackageInfo ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView t = (TextView) findViewById(R.id.version);
        try {
            mPackageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        t.setText(getText(R.string.versao).toString() + mPackageInfo.versionName);
        t.setVisibility(View.VISIBLE);

        Button b_Ok = (Button) findViewById(R.id.b_Ok);
        b_Ok.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}
