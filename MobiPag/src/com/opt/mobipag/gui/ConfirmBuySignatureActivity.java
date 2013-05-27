package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.opt.mobipag.R;
import org.json.JSONException;
import org.json.JSONObject;
import com.opt.mobipag.data.User;
import com.opt.mobipag.data.Utils;
import com.opt.mobipag.data.WebServiceHandler;
import com.opt.mobipag.data.Zone;
import com.opt.mobipag.database.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConfirmBuySignatureActivity extends Activity {
    private AlertDialog m_Alerter;
    private AlertDialog m_Success;
    private final SignatureDataSource datasource = new SignatureDataSource(this);
    private final UtilsDataSource datasource2 = new UtilsDataSource(this);
    private final UserDataSource datasource3 = new UserDataSource(this);
    private final ZoneDataSource datasource4 = new ZoneDataSource(this);
    private final HistoryDataSource datasource5 = new HistoryDataSource(this);
    private final List<Zone> zones = new ArrayList<Zone>();
    private int num;
    private String email;
    private String zone;
    private double value;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmbuyoc);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 9);
                startActivity(i);
            }
        });

        email = getIntent().getStringExtra("USER_EMAIL");
        zone = getIntent().getStringExtra("Zone");
        assert zone != null;
        String[] zn = zone.split(", ");
        datasource4.open();
        for (String z : zn)
            zones.add(datasource4.getZoneByDescriptor(z));
        datasource4.close();
        value = getIntent().getDoubleExtra("Value", -1);
        num = getIntent().getIntExtra("Num", -1);

        ((TextView) findViewById(R.id.t_BZone)).setText(getText(R.string.buy_sigconf_details) + zone);
        ((TextView) findViewById(R.id.t_BValue)).setText(getText(R.string.buy_oc_total).toString() + value + getText(R.string.euro).toString());

        m_Success = new AlertDialog.Builder(this).create();
        m_Success.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setResult(2);
                finish();
            }
        });

        m_Alerter = new AlertDialog.Builder(this).create();
        m_Alerter.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        Button b_Confirm = (Button) findViewById(R.id.b_ConfirmBOC);
        b_Confirm.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Context c = view.getContext();
                if (c != null) {
                    dialog = ProgressDialog.show(c, "", getText(R.string.loading), true);
                    dialog.setCancelable(false);
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });

                    new AddTicket().execute(email, zone);
                }
            }
        });
    }

    private class AddTicket extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {
            String Key = "temp";
            String param = null;

            try {
                param = "Email=" + URLEncoder.encode(arg0[0], "UTF-8") +
                        "&zones=" + URLEncoder.encode(arg0[1], "UTF-8") +
                        "&PublicKey=" + URLEncoder.encode(Key, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                //JSONObject serviceResult = WebServiceHandler.RequestPOST(getText(R.string.ADDSIGNATUREURL).toString(), param);
                JSONObject serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.ADDSIGNATUREURL).toString() + "?" + param);

                boolean result = false;
                if (serviceResult != null) {
                    int code = (Integer) serviceResult.get("Code");
                    switch (code) {
                        case 5000:
                            result = true;
                            break;
                        case 5001:
                            result = false;
                            m_Alerter.setMessage(getText(R.string.buy_sigconf_5001));
                            break;
                        case 5002:
                            result = false;
                            m_Alerter.setMessage(getText(R.string.buy_no_balance));
                            break;
                    }
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Boolean.FALSE;
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Date date = new Date();
                int y = date.getYear() + 1900;
                int m = date.getMonth() + 1;
                int d = date.getDate();
                if (d > 15)
                    m++;
                if (m > 12) {
                    m = 1;
                    y++;
                }
                String dt = y + "/" + m;
                datasource.open();
                datasource2.open();
                datasource3.open();
                datasource.createSignature(dt, datasource2.getPriceByTypologyAndType("Z" + num, datasource2.PRICE_SIGNATURE), 0, datasource2.getTravelTimeByTypology("Z" + num), zones, datasource3.getUserByEmail(email).getId());
                datasource.close();
                datasource2.close();
                datasource3.UpdateBalance(email, value);
                User u = datasource3.getUserByEmail(email);
                datasource3.close();
                datasource5.open();
                datasource5.createHist(u.getId(), Utils.currentDate(), -value, getText(R.string.assinatura) + getResources().getStringArray(R.array.meses)[m - 1] + "\n(" + zone + ")");
                datasource5.close();
                m_Success.setMessage(getText(R.string.buy_success));
                m_Success.show();
            } else {
                m_Alerter.setMessage(getText(R.string.verifyConnection));
                m_Alerter.show();
            }
            dialog.dismiss();
        }
    }
}
