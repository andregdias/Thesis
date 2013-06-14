package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import com.opt.mobipag.R;
import org.json.JSONArray;
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
import java.util.List;

public class TicketBalanceActivity extends Activity {
    private final SignatureDataSource datasource = new SignatureDataSource(this);
    private final OccasionalDataSource datasource2 = new OccasionalDataSource(this);
    private final UserDataSource datasource3 = new UserDataSource(this);
    private final UtilsDataSource datasource4 = new UtilsDataSource(this);
    private final ZoneDataSource datasource5 = new ZoneDataSource(this);
    private final TicketDataSource datasource6 = new TicketDataSource(this);
    private User user;
    private String email;
    private ProgressDialog dialog;
    private Context c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticketbalance);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 11);
                startActivity(i);
            }
        });

        dialog = ProgressDialog.show(this, "", getText(R.string.loading), true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        c = this;

        email = getIntent().getStringExtra("USER_EMAIL");
        datasource3.open();
        user = datasource3.getUserByEmail(email);
        datasource3.close();

        datasource6.open();
        datasource6.deleteAll();
        datasource6.close();
        new GetUser().execute(email);
    }

    class GetUser extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {

            boolean result = false;

            JSONObject serviceResult = null;
            try {
                serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.GETTICKETSURL).toString() + "?email=" + URLEncoder.encode(arg0[0], "UTF-8") + "&publicKey=" + "temp");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            if (serviceResult != null) {
                int code2 = 0;
                try {
                    code2 = (Integer) serviceResult.get("Code");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                switch (code2) {
                    case 4100:
                        JSONArray o2 = null;
                        try {
                            o2 = serviceResult.getJSONArray("ListTickets");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        assert o2 != null;
                        for (int i = 0; i < o2.length(); i++) {
                            JSONObject j = null;
                            String type = "";
                            int amount = 0;
                            String name = null;
                            try {
                                j = o2.getJSONObject(i);
                                amount = j.getInt("Amount");
                                name = j.getString("Name");
                                type = j.getString("Type");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (type.equals("Assinatura")) {
                                createSignature(j);
                            } else if (type.equals("Andante24")) {
                                createAndante24(amount, name);
                            } else if (type.equals("Ocasional")) {
                                createOccasional(amount, name);
                            }
                        }
                        result = true;
                        break;
                    case 4102:
                        result = false;
                        break;
                }

            }
            return result;
        }

        private void createSignature(JSONObject j) {
            int month = 0;
            try {
                month = j.getInt("SigMonth");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int year = 0;
            try {
                year = j.getInt("SigYear");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String zones = null;
            try {
                zones = j.getString("SigZones");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert zones != null;
            String[] ss = zones.split(", ");
            List<Zone> zonas = new ArrayList<Zone>();
            datasource5.open();
            for (String s : ss)
                zonas.add(datasource5.getZoneByDescriptor(s));
            datasource5.close();
            datasource.open();
            datasource2.open();
            datasource3.open();
            datasource4.open();
            datasource.createSignature(year + "/" + month, datasource4.getPriceByTypologyAndType("Z" + ss.length, datasource4.PRICE_SIGNATURE), 1, datasource4.getTravelTimeByTypology("Z" + ss.length), zonas, datasource3.getUserByEmail(email).getId());
            datasource.close();
            datasource2.close();
            datasource3.close();
            datasource4.close();
        }

        private void createOccasional(int amount, String name) {
            datasource.open();
            datasource2.open();
            datasource3.open();
            datasource4.open();
            for (int k = 0; k < amount; k++)
                datasource2.createOccasional(name, datasource4.getPriceByTypologyAndType(name, datasource4.PRICE_OCCASIONAL), 0, datasource4.getNumZonesByTypology(name), datasource4.getTravelTimeByTypology(name), datasource3.getUserByEmail(email).getId());
            datasource.close();
            datasource2.close();
            datasource3.close();
            datasource4.close();
        }

        private void createAndante24(int amount, String name) {
            datasource.open();
            datasource2.open();
            datasource3.open();
            datasource4.open();
            for (int k = 0; k < amount; k++)
                datasource2.createOccasional(name, datasource4.getPriceByTypologyAndType(name, datasource4.PRICE_ANDANTE24), 0, datasource4.getNumZonesByTypology(name), 24 * 60, datasource3.getUserByEmail(email).getId());
            datasource.close();
            datasource2.close();
            datasource3.close();
            datasource4.close();
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                datasource.open();
                datasource2.open();
                TableLayout table = (TableLayout) findViewById(R.id.tableTickets);
                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                Utils.populateTable(user, datasource, datasource2, table, c, width);
                datasource.close();
                datasource2.close();
            }
            dialog.dismiss();
        }
    }
}
