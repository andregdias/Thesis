package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.opt.mobipag.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.opt.mobipag.data.History;
import com.opt.mobipag.data.User;
import com.opt.mobipag.data.Utils;
import com.opt.mobipag.data.WebServiceHandler;
import com.opt.mobipag.database.HistoryDataSource;
import com.opt.mobipag.database.UserDataSource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

public class AccountMovementsActivity extends Activity {

    private final UserDataSource datasource = new UserDataSource(this);
    private final HistoryDataSource datasource2 = new HistoryDataSource(this);
    private String email;
    private User user;
    private List<History> hists;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountmovements);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 12);
                startActivity(i);
            }
        });

        ((TextView) findViewById(R.id.t_CurrentBalance)).setText("");

        email = this.getIntent().getStringExtra("USER_EMAIL");
        datasource.open();
        user = datasource.getUserByEmail(email);
        datasource.close();

        dialog = ProgressDialog.show(this, "", getText(R.string.loading), true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        new GetUser().execute(email);
    }

    private void populateTable(double temp, TableLayout table) {
        TableRow row = new TableRow(this);

        TextView tv = new TextView(this);
        TextView tv2 = new TextView(this);
        TextView tv3 = new TextView(this);

        tv.setPadding(15, 0, 15, 10);
        tv.setTypeface(null, Typeface.BOLD);
        tv2.setPadding(15, 0, 15, 10);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setGravity(Gravity.RIGHT);
        tv3.setPadding(15, 0, 15, 10);
        tv3.setTypeface(null, Typeface.BOLD);
        tv3.setGravity(Gravity.RIGHT);

        tv.setText(getText(R.string.data_detalhes));
        tv2.setText(getText(R.string.valor));
        tv3.setText(getText(R.string.saldo));

        row.addView(tv);
        row.addView(tv2);
        row.addView(tv3);

        table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        int i = 0;
        for (History h : hists) {

            TableRow r = new TableRow(this);

            if (i % 2 == 0) {
                r.setBackgroundColor(Color.WHITE);
            } else {
                r.setBackgroundColor(Color.argb(40, 0, 0, 0));
            }
            i++;

            TextView t = new TextView(this);
            TextView t2 = new TextView(this);
            TextView t3 = new TextView(this);

            t.setPadding(15, 5, 15, 5);
            t2.setPadding(15, 5, 15, 5);
            t2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            t3.setPadding(15, 5, 15, 5);
            t3.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            t.setText(Html.fromHtml(h.getDate() + "<br><b>" + h.getDetails() + "</b>"));
            Display display = getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            t.setMaxWidth((int) Math.round(width * 0.55));
            t2.setText(Double.toString(h.getAmount()) + getText(R.string.euro).toString());
            t3.setText(Double.toString(temp) + getText(R.string.euro).toString());

            r.addView(t);
            r.addView(t2);
            r.addView(t3);

            table.addView(r, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            temp -= h.getAmount();
            temp = Utils.round(temp);
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
                                datasource.open();
                                datasource.setBalance(email, balance);
                                user = datasource.getUserByEmail(email);
                                datasource.close();
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

    class GetHistory extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {

            boolean result = false;

            JSONObject serviceResult = null;
            try {
                serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.GETHISTORYURL).toString() + "?email=" + URLEncoder.encode(arg0[0], "UTF-8") + "&publicKey=" + "temp");
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
                    case 4150:
                        JSONArray o3 = null;
                        try {
                            o3 = serviceResult.getJSONArray("ListAccountMovements");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        datasource2.open();
                        datasource2.deleteAll();
                        datasource.open();
                        User u = datasource.getUserByEmail(email);
                        datasource.close();
                        getMovements(o3, u);
                        datasource2.close();
                        result = true;
                        break;
                }
            }
            return result;
        }

        private void getMovements(JSONArray o3, User u) {
            for (int i = o3.length() - 1; i >= 0; i--) {
                double amount = 0;
                String reason = "";
                String date = "";
                try {
                    JSONObject j = o3.getJSONObject(i);
                    amount = j.getDouble("amount");
                    reason = j.getString("reason");
                    date = j.getString("timeStamp");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                long mili = Utils.dateFromJSON(date);
                Date d = new Date(mili);
                if (reason.contains("BuySignature")) {
                    buySigMovement(u, amount, reason, d);
                } else if (reason.contains("BuyTickets")) {
                    buyOccMovements(u, amount, reason, d);
                } else if (reason.contains("AddMoney")) {
                    datasource2.createHist(u.getId(), Utils.parseDate(d, true), amount, "Carregamento de Conta");
                }
            }
        }

        private void buyOccMovements(User u, double amount, String reason, Date d) {
            String[] ss = reason.split(" ");
            String n = ss[1];
            String z = ss[3];
            String[] zz = z.split("-");
            String zone = zz[0];
            if (reason.contains("Ocasional")) {
                datasource2.createHist(u.getId(), Utils.parseDate(d, true), amount, getText(R.string.buy_occasional) + " " + zone + " (" + n + ")");
            } else if (reason.contains("Andante24")) {
                datasource2.createHist(u.getId(), Utils.parseDate(d, true), amount, getText(R.string.buy_andante24) + " " + zone + " (" + n + ")");
            }
        }

        private void buySigMovement(User u, double amount, String reason, Date d) {
            String[] rr = reason.substring(13).split("\\*");
            String zone = rr[0].substring(0, rr[0].length() - 1);

            String dd = Utils.parseDate(d, true);
            String[] dd_ = dd.split("/");
            String[] ddd_ = dd_[2].split(" ");
            int day = Integer.parseInt(ddd_[0]);
            int m = Integer.parseInt(dd_[1]);
            if (day > 15)
                m++;
            if (m > 12)
                m = 1;

            datasource2.createHist(u.getId(), dd, amount, getText(R.string.assinatura) + getResources().getStringArray(R.array.meses)[m - 1] + "\n(" + zone + ")");
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                datasource2.open();
                hists = datasource2.getHistory(user.getId());
                datasource2.close();

                ((TextView) findViewById(R.id.t_CurrentBalance)).setText(Utils.round(user.getBalance()) + getText(R.string.euro).toString());

                TableLayout table = (TableLayout) findViewById(R.id.tableMovements);
                populateTable(Utils.round(user.getBalance()), table);
            }

            dialog.dismiss();

        }
    }
}
