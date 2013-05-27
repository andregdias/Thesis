package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import com.opt.mobipag.R;
import com.opt.mobipag.data.Occasional;
import com.opt.mobipag.data.Signature;
import com.opt.mobipag.data.User;
import com.opt.mobipag.data.Validation;
import com.opt.mobipag.database.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class RevisorActivity extends Activity {
    private final SignatureDataSource datasource = new SignatureDataSource(this);
    private final OccasionalDataSource datasource2 = new OccasionalDataSource(this);
    private final UserDataSource datasource3 = new UserDataSource(this);
    private final StopDataSource datasource5 = new StopDataSource(this);
    private final LineDataSource datasource4 = new LineDataSource(this);
    private User user;
    private String email;
    private Occasional o = null;
    private Signature s = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.revisor);

        LinearLayout v = (LinearLayout) findViewById(R.id.revisor);
        applyBG(v);

        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 17);
                startActivity(i);
            }
        });

        TableLayout table = (TableLayout) findViewById(R.id.tableValidations);

        email = this.getIntent().getStringExtra("USER_EMAIL");
        datasource3.open();
        user = datasource3.getUserByEmail(email);
        datasource3.close();

        datasource.open();
        datasource2.open();
        o = datasource2.getActiveOccasional(user.getId());
        s = datasource.getActiveSignature(user.getId());
        datasource.close();
        datasource2.close();

        if (o != null)
            populateTableOcc(table);
        else if (s != null) {
            populateTableSig(table);
        } else {
            AlertDialog m_Alerter = new AlertDialog.Builder(this).create();
            m_Alerter.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            m_Alerter.setMessage(getText(R.string.noActiveTitle));
            m_Alerter.show();
        }
    }

    private void applyBG(LinearLayout v) {
        Date d = new Date();
        int wd = -1;
        switch (d.getDay()) {
            case 0:
                wd = R.drawable.day0;
                break;
            case 1:
                wd = R.drawable.day1;
                break;
            case 2:
                wd = R.drawable.day2;
                break;
            case 3:
                wd = R.drawable.day3;
                break;
            case 4:
                wd = R.drawable.day4;
                break;
            case 5:
                wd = R.drawable.day5;
                break;
            case 6:
                wd = R.drawable.day6;
                break;
        }

        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(wd);
        if (bg != null) {
            bg.setAlpha(45);
            bg.setTileModeY(TileMode.REPEAT);
            bg.setTileModeX(TileMode.REPEAT);
            v.setBackgroundDrawable(bg);
        }
    }

    private void populateTableSig(TableLayout table) {
        TableRow row = new TableRow(this);

        TextView tv = new TextView(this);
        TextView tv2 = new TextView(this);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

        LinearLayout comp = new LinearLayout(this);
        comp.setOrientation(LinearLayout.VERTICAL);

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        ImageView img = new ImageView(this);
        img.setAdjustViewBounds(true);
        img.setMaxWidth((int) Math.round(width * 0.40));
        retrieveFoto(img);

        tv.setPadding(15, 0, 25, 10);
        tv.setTypeface(null, Typeface.BOLD);
        tv2.setPadding(15, 0, 15, 10);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setGravity(Gravity.RIGHT);

        tv.setText(getText(R.string.data_detalhes));
        tv2.setText(getText(R.string.bilhete));

        row.addView(tv);
        row.addView(tv2);

        table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        TableRow r = new TableRow(this);

        TextView t = new TextView(this);
        TextView t2 = new TextView(this);
        t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        t2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

        t.setPadding(15, 5, 25, 5);
        t2.setPadding(15, 5, 15, 5);
        t2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        String val = "";
        datasource5.open();
        datasource4.open();
        ArrayList<Validation> valid = s.getValidacoes();
        val += validacoes(valid, true);
        datasource5.close();
        datasource4.close();
        t.setText(Html.fromHtml(val));


        t.setMaxWidth((int) Math.round(width * 0.55));
        t2.setMaxWidth((int) Math.round(width * 0.40));

        Date d = new Date();
        t2.setText(getText(R.string.assinatura).subSequence(0, getText(R.string.assinatura).length() - 1) + "\n" + getResources().getStringArray(R.array.meses)[d.getMonth()] + "\n" + s.getListZonas() + "\n" + getName() + "\n" + user.getNif());

        r.addView(t);
        comp.addView(t2);
        comp.addView(img);
        r.addView(comp);

        table.addView(r, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    private String getName() {
        String[] n = user.getName().split(" ");
        if (n.length == 1)
            return user.getName();
        return n[0] + " " + n[n.length - 1];
    }

    private void populateTableOcc(TableLayout table) {
        TableRow row = new TableRow(this);

        TextView tv = new TextView(this);
        TextView tv2 = new TextView(this);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

        tv.setPadding(15, 0, 25, 10);
        tv.setTypeface(null, Typeface.BOLD);
        tv2.setPadding(15, 0, 15, 10);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setGravity(Gravity.RIGHT);

        tv.setText(getText(R.string.data_detalhes));
        tv2.setText(getText(R.string.bilhete));

        row.addView(tv);
        row.addView(tv2);

        table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        TableRow r = new TableRow(this);

        TextView t = new TextView(this);
        TextView t2 = new TextView(this);
        t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        t2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

        t.setPadding(15, 5, 25, 5);
        t2.setPadding(15, 5, 15, 5);
        t2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        String val = "";
        datasource5.open();
        datasource4.open();
        ArrayList<Validation> valid = o.getValidacoes();
        val += validacoes(valid, false);
        datasource5.close();
        datasource4.close();
        t.setText(Html.fromHtml(val));
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        t.setMaxWidth((int) Math.round(width * 0.70));

        if (o.getTempoviagem() < 24 * 60)
            t2.setText(Html.fromHtml(getText(R.string.buy_occasional) + "<br><b>" + o.getDetails() + "</b>"));
        else
            t2.setText(Html.fromHtml(getText(R.string.buy_andante24) + "<br><b>" + o.getDetails() + "</b>"));

        r.addView(t);
        r.addView(t2);

        table.addView(r, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    private String validacoes(ArrayList<Validation> valid, boolean signature) {
        String val = "";
        for (Validation v : valid) {
            val += getDate(v) + "<br>\t" + getParagem(v) + "<br>\t" + getLinha(v) + "<br>\tSeq: <b>" + v.getSeqId() + "</b><br><br>";
            if (signature)
                break;
        }
        return val;
    }

    private String getParagem(Validation v) {
        String cod = datasource5.getStopById(v.getIdStop()).getCodsms();
        return "<b>" + cod + "</b> ";
    }

    private String getLinha(Validation v) {
        String cod = datasource4.getLineById(v.getIdLine()).getDescritor();
        return "<b>" + cod + "</b> ";
    }

    private String getDate(Validation v) {
        String data = v.getDate();
        String[] detalhes = data.split(" ");
        return detalhes[0] + " <b>" + detalhes[1] + "</b>";
    }

    private void retrieveFoto(ImageView img) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(email + "_foto.jpg");
        } catch (FileNotFoundException e) {
            img.setVisibility(View.GONE);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        img.setImageBitmap(bitmap);
        try {
            if (fis != null)
                fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
