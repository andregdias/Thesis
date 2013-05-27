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
import com.opt.mobipag.data.WebServiceHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ConfirmBuyOCActivity extends Activity {
    private AlertDialog m_Alerter;
    private AlertDialog m_Success;
    private int num;
    private String email;
    private ProgressDialog dialog;
    private int type;
    private String zone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmbuyoc);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();

        email = getIntent().getStringExtra("USER_EMAIL");
        type = getIntent().getIntExtra("Type", -1);
        zone = getIntent().getStringExtra("Zone");
        num = getIntent().getIntExtra("Num", -1);
        double value = getIntent().getDoubleExtra("Value", -1);

        final int OCASIONAL = 0;
        final int ANDANTE24 = 1;
        switch (type) {

            case OCASIONAL:
                header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), HelpActivity.class);
                        i.putExtra("HELP", 6);
                        startActivity(i);
                    }
                });
                ((TextView) findViewById(R.id.t_BZone)).setText(getText(R.string.buy_occonf_oc) + zone);
                break;
            case ANDANTE24:
                header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), HelpActivity.class);
                        i.putExtra("HELP", 7);
                        startActivity(i);
                    }
                });
                ((TextView) findViewById(R.id.t_BZone)).setText(getText(R.string.buy_occonf_a24) + zone);
                break;
        }

        if (num == 10)
            ((TextView) findViewById(R.id.t_BNum)).setText(getText(R.string.buy_oc_numtitles).toString() + num + getText(R.string.buy_oc_free));
        else
            ((TextView) findViewById(R.id.t_BNum)).setText(getText(R.string.buy_oc_numtitles).toString() + num);
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
                    if (type == 0)
                        new AddTicket().execute(email, "Ocasional", zone, Integer.toString(num));
                    else if (type == 1)
                        new AddTicket().execute(email, "Andante24", zone, Integer.toString(num));
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
                param = "email=" + URLEncoder.encode(arg0[0], "UTF-8") +
                        "&type=" + URLEncoder.encode(arg0[1], "UTF-8") +
                        "&name=" + URLEncoder.encode(arg0[2], "UTF-8") +
                        "&amount=" + URLEncoder.encode(arg0[3], "UTF-8") +
                        "&publicKey=" + URLEncoder.encode(Key, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                //JSONObject serviceResult = WebServiceHandler.RequestPOST(getText(R.string.ADDTICKETURL).toString(), param);
                JSONObject serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.ADDTICKETURL).toString() + "?" + param);

                boolean result = false;
                if (serviceResult != null) {
                    int code = (Integer) serviceResult.get("Code");
                    switch (code) {
                        case 4000:
                            result = true;
                            break;
                        case 4001:
                            result = false;
                            m_Alerter.setMessage(getText(R.string.buy_occonf_4001));
                            break;
                        case 4002:
                            result = false;
                            m_Alerter.setMessage(getText(R.string.buy_no_balance));
                            break;
                    }
                }
                return result;
            } catch (JSONException e) {
                // handle exception
            }

            return Boolean.FALSE;
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
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
