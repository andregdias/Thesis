package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.Toast;
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

public class BuyActivity extends Activity {
    private final SignatureDataSource datasource = new SignatureDataSource(this);
    private final OccasionalDataSource datasource2 = new OccasionalDataSource(this);
    private final UserDataSource datasource3 = new UserDataSource(this);
    private final UtilsDataSource datasource4 = new UtilsDataSource(this);
    private final ZoneDataSource datasource5 = new ZoneDataSource(this);
    private final TicketDataSource datasource6 = new TicketDataSource(this);
    private ProgressDialog dialog;
    private User user;
    private String email;
    private Context c;
    private final int OCASIONAL = 0;
    private final int ANDANTE24 = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 3);
                startActivity(i);
            }
        });

        c = this;
        email = getIntent().getStringExtra("USER_EMAIL");
        datasource3.open();
        user = datasource3.getUserByEmail(email);
        datasource3.close();

        Button b_BuyOC = (Button) findViewById(R.id.b_Ocasional);
        b_BuyOC.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), BuyOcasionalActivity.class);
                myIntent.putExtra("USER_EMAIL", email);
                myIntent.putExtra("Type", OCASIONAL);
                startActivityForResult(myIntent, 0);
            }
        });

        Button b_BuyA24 = (Button) findViewById(R.id.b_Andante24);
        b_BuyA24.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), BuyOcasionalActivity.class);
                myIntent.putExtra("USER_EMAIL", email);
                myIntent.putExtra("Type", ANDANTE24);
                startActivityForResult(myIntent, 0);
            }
        });

        Button b_BuySI = (Button) findViewById(R.id.b_Assinatura);
        b_BuySI.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), BuySignatureActivity.class);
                myIntent.putExtra("USER_EMAIL", email);
                startActivityForResult(myIntent, 0);
            }
        });

        dialog = ProgressDialog.show(this, "", getText(R.string.loading), true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        new Prices().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 2:
                finish();
                break;
        }
    }

    private class Prices extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                JSONObject serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.GETPRICESURL).toString());
                boolean result = false;
                if (serviceResult != null) {
                    int code = (Integer) serviceResult.get("Code");

                    switch (code) {
                        case 3260:
                            JSONArray jarray = serviceResult.getJSONArray("TicketList");

                            String[] zonas = new String[jarray.length() / 3];
                            double[] occ = new double[jarray.length() / 3];
                            double[] and24 = new double[jarray.length() / 3];
                            double[] sig = new double[jarray.length() / 3];
                            int[] numzones = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
                            int[] traveltime = new int[]{60, 60, 75, 90, 105, 120, 135, 150, 165, 180, 195};

                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject j = jarray.getJSONObject(i);
                                if (i < 11) {
                                    zonas[i] = j.getString("Name");
                                    occ[i] = j.getDouble("Price");
                                } else if (i < 22) {
                                    and24[i - 11] = j.getDouble("Price");
                                } else {
                                    sig[i - 22] = j.getDouble("Price");
                                }
                            }

                            datasource4.open();
                            datasource4.deleteAll();
                            for (int i = 0; i < zonas.length; i++) {
                                datasource4.insertData(zonas[i], occ[i], and24[i], sig[i], numzones[i], traveltime[i]);
                            }
                            datasource4.close();

                            result = true;
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
                datasource6.open();
                datasource6.deleteAll();
                datasource6.close();
                new GetUser().execute(email);
            }
            if (!result) {
                dialog.dismiss();
                Toast.makeText(c, getText(R.string.verifyConnection), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private class GetUser extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {

            boolean result = false;

            JSONObject serviceResult = null;
            try {
                serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.GETUSERURL).toString() + "?email=" + URLEncoder.encode(arg0[0], "UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            if (serviceResult != null) {
                int code = 0;
                try {
                    code = (Integer) serviceResult.get("Code");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                switch (code) {
                    case 3250:
                        JSONObject o = null;
                        try {
                            o = serviceResult.getJSONObject("UserInfo");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        if (o != null) {
                            Double balance = null;
                            try {
                                balance = o.getDouble("Balance");
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            if (balance != null) {
                                datasource3.open();
                                datasource3.setBalance(email, balance);
                                user = datasource3.getUserByEmail(email);
                                datasource3.close();
                            }
                        }
                        result = true;
                        break;
                    case 3251:
                        result = false;
                        break;
                }
            }
            return result;
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                new GetHistory().execute(email);
            }
        }
    }

    private class GetHistory extends AsyncTask<String, Integer, Boolean> {
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
                        createTickets(serviceResult);
                        result = true;
                        break;
                    case 4102:
                        result = false;
                        break;
                }

            }
            return result;
        }

        private void createTickets(JSONObject serviceResult) {
            JSONArray o2 = null;
            try {
                o2 = serviceResult.getJSONArray("ListTickets");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            assert o2 != null;
            for (int i = 0; i < o2.length(); i++) {
                try {
                    JSONObject j = o2.getJSONObject(i);
                    int amount = j.getInt("Amount");
                    String name = j.getString("Name");
                    String type = j.getString("Type");
                    if (type.equals("Assinatura")) {
                        createSig(j);
                    } else if (type.equals("Andante24")) {
                        createA24(amount, name);
                    } else {
                        createOcc(amount, name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void createOcc(int amount, String name) {
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

        private void createA24(int amount, String name) {
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

        private void createSig(JSONObject j) throws JSONException {
            int month = j.getInt("SigMonth");
            int year = j.getInt("SigYear");
            String zones = j.getString("SigZones");

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

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                datasource.open();
                datasource2.open();
                TableLayout table = (TableLayout) findViewById(R.id.table_balance);
                Utils.populateTable(user, datasource, datasource2, table, c);
                datasource.close();
                datasource2.close();
            }
            dialog.dismiss();
        }
    }
}
