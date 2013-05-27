package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.*;
import com.opt.mobipag.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.opt.mobipag.data.*;
import com.opt.mobipag.database.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketValidationActivity extends Activity {
    private final OccasionalDataSource datasource = new OccasionalDataSource(this);
    private final UserDataSource datasource2 = new UserDataSource(this);
    private final StopDataSource datasource3 = new StopDataSource(this);
    private final LineDataSource datasource4 = new LineDataSource(this);
    private final ZoneDataSource datasource6 = new ZoneDataSource(this);
    private final UtilsDataSource datasource7 = new UtilsDataSource(this);
    private final TicketDataSource datasource10 = new TicketDataSource(this);
    private final StopDataSource datasource8 = new StopDataSource(this);
    private final LineDataSource datasource9 = new LineDataSource(this);
    private String email;
    private final List<Occasional> occ = new ArrayList<Occasional>();
    private Signature s;
    private Context c;
    private ProgressDialog dialog;
    private int sig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validationtickets);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();

        c = this;
        email = getIntent().getStringExtra("USER_EMAIL");
        sig = getIntent().getIntExtra("SIGNATURE", 0);

        if (sig != 0) {
            header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), HelpActivity.class);
                    i.putExtra("HELP", 14);
                    startActivity(i);
                }
            });

            ((TextView) findViewById(R.id.textView1)).setText(getText(R.string.buy_signature));

        } else {
            header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), HelpActivity.class);
                    i.putExtra("HELP", 13);
                    startActivity(i);
                }
            });
        }

        dialog = ProgressDialog.show(this, "", getText(R.string.loading), true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        datasource10.open();
        datasource10.deleteAll();
        datasource10.close();
        new GetTickets().execute(email);

    }

    private void populateTableSig(TableLayout table) {
        TableRow row = new TableRow(this);

        TextView tv = new TextView(this);
        TextView tv2 = new TextView(this);

        tv.setPadding(15, 0, 10, 10);
        tv.setTypeface(null, Typeface.BOLD);
        tv2.setPadding(10, 0, 10, 10);
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

        t.setPadding(15, 5, 10, 5);
        t2.setPadding(10, 5, 10, 5);
        t2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        String val = "";
        for (Validation v : s.getValidacoes()) {
            val += "<b>" + v.getDate() + "</b><br>\tPar: " + stringTruncate(stopName(v)) + "<br>\tLin: " + stringTruncate(lineName(v)) + "<br><br>";
        }
        t.setText(Html.fromHtml(val));
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        t.setMaxWidth((int) Math.round(width * 0.62));
        t2.setMaxWidth((int) Math.round(width * 0.35));

        Date d = new Date();
        t2.setText(getText(R.string.assinatura).subSequence(0, getText(R.string.assinatura).length() - 1) + "\n" + getResources().getStringArray(R.array.meses)[d.getMonth()] + "\n" + s.getListZonas() + "\nValidações: " + s.getNumvalidacoes());

        r.addView(t);
        r.addView(t2);

        table.addView(r, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    }

    private void populateTableOcc(TableLayout table) {
        TableRow row = new TableRow(this);

        TextView tv = new TextView(this);
        TextView tv2 = new TextView(this);

        tv.setPadding(15, 0, 10, 10);
        tv.setTypeface(null, Typeface.BOLD);
        tv2.setPadding(10, 0, 10, 10);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setGravity(Gravity.RIGHT);

        tv.setText(getText(R.string.data_detalhes));
        tv2.setText(getText(R.string.bilhete));

        row.addView(tv);
        row.addView(tv2);

        table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        int i = 0;
        for (Occasional o : occ) {
            TableRow r = new TableRow(this);

            if (i % 2 == 0) {
                r.setBackgroundColor(Color.WHITE);
            } else {
                r.setBackgroundColor(Color.argb(40, 0, 0, 0));
            }
            i++;

            TextView t = new TextView(this);
            TextView t2 = new TextView(this);

            t.setPadding(15, 5, 10, 5);
            t2.setPadding(10, 5, 10, 5);
            t2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            String val = "";
            for (Validation v : o.getValidacoes()) {
                val += "<b>" + v.getDate() + "</b><br>\tPar: " + stringTruncate(stopName(v)) + "<br>\tLin: " + stringTruncate(lineName(v)) + "<br>";
            }
            t.setText(Html.fromHtml(val));
            Display display = getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            t.setMaxWidth((int) Math.round(width * 0.67));
            t2.setMaxWidth((int) Math.round(width * 0.35));

            if (o.getTempoviagem() < 24 * 60)
                t2.setText(getText(R.string.buy_occasional) + "\n" + o.getDetails());
            else
                t2.setText(getText(R.string.buy_andante24) + "\n" + o.getDetails());

            r.addView(t);
            r.addView(t2);

            table.addView(r, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private String stopName(Validation v) {
        datasource3.open();
        Stop s = datasource3.getStopById(v.getIdStop());
        datasource3.close();
        String codsms = s.getCodsms();
        String nome = s.getNome();
        if (codsms.split("_").length > 1)
            return "<b>[" + codsms.split("_")[1] + "]</b> " + nome;
        return "<b>[" + codsms + "]</b> " + nome;
    }

    private String lineName(Validation v) {
        datasource4.open();
        String[] ss = datasource4.getLineById(v.getIdLine()).getName().split("_");
        datasource4.close();

        if (ss.length > 1) {
            String s = ss[1];
            String[] ss1 = s.split(" -> ");

            return "<b>[" + ss1[0] + "]</b> " + ss1[1];
        }
        String s = ss[0];
        String[] ss1 = s.split(" -> ");

        return "<b>[" + ss1[0] + "]</b> " + ss1[1];
    }

    private String stringTruncate(String s) {
        int len = 25;
        if (s.length() <= len)
            return s;
        return s.substring(0, len) + "…";
    }

    class GetTickets extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {

            boolean result = false;

            if (sig == 1) {
                JSONObject serviceResult = null;
                try {
                    serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.GETTICKETSURL).toString() + "?email=" + URLEncoder.encode(arg0[0], "UTF-8") + "&publicKey=" + "temp");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                if (serviceResult != null) {
                    try {
                        int code = (Integer) serviceResult.get("Code");

                        switch (code) {
                            case 4100:
                                JSONArray o = serviceResult.getJSONArray("ListTickets");
                                getCurrentSignature(o);
                                break;
                            case 4102:
                                return false;
                        }
                    } catch (JSONException j) {
                        j.printStackTrace();
                    }
                }
            }

            try {
                JSONObject serviceResult2 = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.GETVALHISTORYURL).toString() + "?email=" + URLEncoder.encode(arg0[0], "UTF-8") + "&publicKey=" + "temp");

                if (serviceResult2 != null) {
                    int code2 = (Integer) serviceResult2.get("Code");

                    switch (code2) {
                        case 4150:
                            JSONArray o2 = serviceResult2.getJSONArray("ListAccountValidations");

                            getValidacoes(o2);
                            result = true;
                            break;
                        default:
                            result = false;
                            break;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return result;
        }

        private void getValidacoes(JSONArray o2) throws JSONException {
            int ticketId = -1;
            for (int i = o2.length() - 1; i >= 0; i--) {
                JSONObject j = o2.getJSONObject(i);

                Boolean signature = j.getBoolean("Signature");

                String date = j.getString("TimeStamp");
                long mili = Utils.dateFromJSON(date);
                date = Utils.parseDate(new Date(mili), true);

                if (signature && s != null && sig != 0) {
                    getSigValidations(j, date);
                } else if (sig == 0 && !signature) {
                    ticketId = getOccValidations(ticketId, i, j, date);
                }
            }
        }

        private int getOccValidations(int ticketId, int i, JSONObject j, String date) throws JSONException {
            String PathCode = j.getString("PathCode");
            String StopCode = j.getString("StopCode");
            String ticketType = j.getString("TicketType");
            String ticketName = j.getString("TicketName");
            Boolean isNewVal = j.getBoolean("isNewValidation");
            int seqId = j.getInt("Id");

            if (isNewVal) {
                datasource7.open();
                if (ticketType.equals("Ocasional")) {
                    Occasional o = new Occasional(-1, ticketName, datasource7.getPriceByTypologyAndType(ticketName, datasource7.PRICE_OCCASIONAL), datasource7.getNumZonesByTypology(ticketName), datasource7.getTravelTimeByTypology(ticketName), new ArrayList<Validation>(), null);
                    occ.add(0, o);
                    ticketId = 0;
                } else if (ticketType.equals("Andante24")) {
                    Occasional o = new Occasional(-1, ticketName, datasource7.getPriceByTypologyAndType(ticketName, datasource7.PRICE_OCCASIONAL), datasource7.getNumZonesByTypology(ticketName), 24 * 60, new ArrayList<Validation>(), null);
                    occ.add(0, o);
                    ticketId = 0;
                }
                datasource7.close();
            }

            long sid = -1;
            long lid = -1;

            sid = addStop(StopCode, sid);
            lid = addLine(PathCode, StopCode, lid);

            if (sid != -1 && lid != -1 && ticketId != -1) {
                Occasional oc = occ.get(ticketId);
                oc.getValidacoes().add(0, new Validation((int) sid, (int) lid, date, seqId));

                if (i == 0) {
                    oc.setFirstval(date);
                    if (Utils.checkValidity(oc)) {
                        datasource2.open();
                        User user = datasource2.getUserByEmail(email);
                        datasource2.close();
                        datasource.open();
                        long tId = datasource.createOccasional(oc.getDetails(), oc.getPrice(), 0, oc.getNumzonas(), oc.getTempoviagem(), user.getId());
                        datasource.close();
                        datasource10.open();
                        for (int k = oc.getValidacoes().size() - 1; k >= 0; k--) {
                            datasource10.addValidation((int) tId, oc.getValidacoes().get(k).getIdStop(), oc.getValidacoes().get(k).getIdLine(), oc.getValidacoes().get(k).getSeqId(), oc.getValidacoes().get(k).getDate());
                        }
                        datasource10.changeTicketStatusById((int) tId, 3, -1, -1, -1);
                        datasource10.close();

                        Intent i1 = new Intent(c, TravelTimer.class);
                        i1.putExtra("USER_EMAIL", email);
                        if (oc.getTempoviagem() == 24 * 60)
                            i1.putExtra("title", "Andante 24 " + oc.getDetails());
                        else
                            i1.putExtra("title", "Ocasional " + oc.getDetails());
                        i1.putExtra("titleid", 1);
                        i1.putExtra("time", oc.getTempoviagem());
                        i1.putExtra("date", oc.getFirstval());
                        startService(i1);
                    }
                }
            }
            return ticketId;
        }

        private void getSigValidations(JSONObject j, String date) throws JSONException {
            Date d = new Date();
            int y = d.getYear() + 1900;
            int m = d.getMonth() + 1;
            String[] date_parts = date.split("/");
            int year = Integer.parseInt(date_parts[0]);
            int month = Integer.parseInt(date_parts[1]);
            if (year == y && month == m) {
                String PathCode = j.getString("PathCode");

                String StopCode = j.getString("StopCode");

                int seqId = j.getInt("Id");

                long sid = -1;
                long lid = -1;

                sid = addStop(StopCode, sid);
                lid = addLine(PathCode, StopCode, lid);

                if (sid != -1 && lid != -1) {
                    s.getValidacoes().add(0, new Validation((int) sid, (int) lid, date, seqId));
                    s.setNumvalidacoes(s.getNumvalidacoes() + 1);
                }
            }
        }

        private void getCurrentSignature(JSONArray o) throws JSONException {
            for (int i = 0; i < o.length(); i++) {
                JSONObject j = o.getJSONObject(i);
                String type = j.getString("Type");
                if (type.equals("Assinatura")) {
                    int month = j.getInt("SigMonth");
                    int year = j.getInt("SigYear");

                    Date d = new Date();
                    int y = d.getYear() + 1900;
                    int m = d.getMonth() + 1;
                    if (year == y && month == m) {
                        String zones = j.getString("SigZones");

                        String[] ss = zones.split(", ");
                        List<Zone> zonas = new ArrayList<Zone>();
                        datasource6.open();
                        for (String s : ss)
                            zonas.add(datasource6.getZoneByDescriptor(s));
                        datasource6.close();
                        datasource2.open();
                        datasource7.open();
                        s = new Signature(-1, year + "/" + month, datasource7.getPriceByTypologyAndType("Z" + ss.length, datasource7.PRICE_SIGNATURE), 0, datasource7.getTravelTimeByTypology("Z" + ss.length), zonas, new ArrayList<Validation>(), null);
                        datasource7.close();
                        datasource2.close();
                        break;
                    }
                }
            }
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                TableLayout table = (TableLayout) findViewById(R.id.tableValidations);

                if (sig == 0) {
                    populateTableOcc(table);
                } else {
                    if (s != null) {
                        ((TextView) findViewById(R.id.textView1)).setText(getText(R.string.consultar_validacoes_sig) + s.getDetails() + ")");
                        populateTableSig(table);
                    } else {
                        Toast.makeText(c, getText(R.string.sem_assinatura), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
            dialog.dismiss();
        }

        private long addStop(String StopCode, long sid) {
            datasource8.open();
            Stop stop = datasource8.getStopByCodsms(StopCode);
            if (stop != null)
                sid = stop.getId();
            else {
                JSONArray serviceResult5 = WebServiceHandler.RequestGETArray(getText(R.string.SERVER).toString()+getText(R.string.STOPWORDURL) + "?word=" + StopCode + "&username=MOBIPAG");

                if (serviceResult5 != null) {
                    JSONObject json_stop = null;
                    try {
                        json_stop = serviceResult5.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        assert json_stop != null;
                        sid = datasource8.createStop(json_stop.getString("name"), json_stop.getString("code"), json_stop.getString("provider"), json_stop.getDouble("coordX"), json_stop.getDouble("coordY"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            datasource8.close();
            return sid;
        }

        private long addLine(String PathCode, String StopCode, long lid) {
            datasource9.open();
            String[] linecode = PathCode.split("-");
            Line line = datasource9.getLineByDescriptor(linecode[0]);
            if (line != null)
                lid = line.getId();
            else {

                JSONArray serviceResult6 = WebServiceHandler.RequestGETArray(getText(R.string.SERVER).toString()+getText(R.string.LINEURL) + "?stop=" + URLEncoder.encode(StopCode) + "&username=MOBIPAG");
                if (serviceResult6 != null)
                    for (int k = 0; k < serviceResult6.length(); k++) {
                        JSONObject json_line = null;
                        try {
                            json_line = serviceResult6.getJSONObject(k);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String lc = null;
                        try {
                            assert json_line != null;
                            lc = json_line.getString("GoPathCode");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        assert lc != null;
                        if (lc.equals(PathCode))
                            try {
                                lid = datasource9.createLine(json_line.getString("LineCode"), json_line.getString("LineName"), json_line.getString("GoPathCode"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
            }
            datasource9.close();
            return lid;
        }
    }
}
