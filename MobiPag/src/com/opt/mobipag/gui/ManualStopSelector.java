package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import com.opt.mobipag.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.opt.mobipag.data.Stop;
import com.opt.mobipag.data.WebServiceHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ManualStopSelector extends Activity {

    private Spinner spOperador;
    private Spinner spLinha;
    private Spinner spParagem;
    private ArrayAdapter<String> adapter;
    private final ArrayList<String> operadores = new ArrayList<String>();
    private ArrayAdapter<String> adapter2;
    private final ArrayList<String> linhasNames = new ArrayList<String>();
    private final ArrayList<String> linhas = new ArrayList<String>();
    private ArrayAdapter<String> adapter3;
    private final ArrayList<Stop> stops = new ArrayList<Stop>();
    private final ArrayList<String> paragens = new ArrayList<String>();
    private final ArrayList<String> paragens2 = new ArrayList<String>();
    private ProgressDialog dialog;
    private String paragem = "";
    private String linha = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manualstopchooser);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 19);
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

        spOperador = (Spinner) findViewById(R.id.sp_operador);
        spLinha = (Spinner) findViewById(R.id.sp_linha);
        spParagem = (Spinner) findViewById(R.id.sp_paragem);
        Button b_Confirmar = (Button) findViewById(R.id.b_confirmar);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operadores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOperador.setAdapter(adapter);
        spOperador.setEnabled(false);
        spOperador.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                dialog.show();
                new RetrieveLines().execute(operadores.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, linhasNames);
        adapter2.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
        spLinha.setAdapter(adapter2);
        spLinha.setEnabled(false);
        spLinha.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                dialog.show();
                linha = linhas.get(position);
                new RetrieveStops().execute(linhas.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                linha = linhas.get(0);
            }
        });

        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paragens);
        adapter3.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
        spParagem.setAdapter(adapter3);
        spParagem.setEnabled(false);
        spParagem.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                paragem = paragens2.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                paragem = paragens2.get(0);
            }
        });

        b_Confirmar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = paragem + "%%" + linha;
                Intent i = new Intent(s);
                setResult(1, i);
                finish();
            }
        });

        new RetrieveOperadores().execute();
    }

    private class RetrieveOperadores extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg0) {

            JSONArray serviceResult = WebServiceHandler.RequestGETArray(getText(R.string.SERVER).toString()+getText(R.string.OPERATORURL) + "?username=MOBIPAG");

            operadores.clear();
            if (serviceResult != null) {
                for (int i = 0; i < serviceResult.length(); i++) {
                    try {
                        operadores.add(serviceResult.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(String result) {
            adapter.notifyDataSetChanged();
            if (!operadores.isEmpty()) {
                spOperador.setEnabled(true);
                int i = 0;
                while (i < operadores.size() && !operadores.get(i).equals("STCP"))
                    i++;
                spOperador.setSelection(i);
            }
            dialog.dismiss();
        }
    }

    private class RetrieveLines extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg0) {

            JSONArray serviceResult = null;
            try {
                serviceResult = WebServiceHandler.RequestGETArray(getText(R.string.SERVER)+getText(R.string.LINEBYOPURL).toString() + "?provider=" + URLEncoder.encode(arg0[0], "UTF-8") + "&username=MOBIPAG");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            linhasNames.clear();
            linhas.clear();
            if (serviceResult != null)
                try {
                    for (int i = 0; i < serviceResult.length(); i++) {
                        String nome = serviceResult.getString(i);
                        linhas.add(nome);
                        if (nome.split("_").length == 1)
                            linhasNames.add(serviceResult.getString(i));
                        else
                            linhasNames.add(nome.split("_")[1]);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            return null;
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(String result) {
            adapter2.notifyDataSetChanged();
            if (!linhasNames.isEmpty()) {
                linha = linhas.get(0);
                spLinha.setEnabled(true);
                new RetrieveStops().execute(linhas.get(0));
            } else
                dialog.dismiss();
        }
    }

    private class RetrieveStops extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg0) {

            JSONArray serviceResult = null;
            try {
                serviceResult = WebServiceHandler.RequestGETArray(getText(R.string.SERVER).toString()+getText(R.string.STOPSBYLINEURL) + "?line=" + URLEncoder.encode(arg0[0], "UTF-8") + "&username=MOBIPAG");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            stops.clear();
            paragens.clear();
            paragens2.clear();
            if (serviceResult != null) {
                for (int i = 0; i < serviceResult.length(); i++) {
                    try {
                        JSONObject j = (JSONObject) serviceResult.get(i);
                        stops.add(new Stop(-1, j.getString("name"), j.getString("code"), j.getString("provider"), j.getDouble("coordX"), j.getDouble("coordY")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Collections.sort(stops, new Comparator<Stop>() {
                    @Override
                    public int compare(Stop lhs, Stop rhs) {
                        return lhs.getNome().compareTo(rhs.getNome());
                    }
                });

                for (Stop s : stops) {
                    String[] cod = s.getCodsms().split("_");
                    String name = s.getNome();
                    if (cod.length > 1)
                        name += " [" + cod[1] + "]";
                    paragens.add(name);
                    paragens2.add(s.getCodsms());
                }
            }
            return null;
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(String result) {

            adapter3.notifyDataSetChanged();
            if (!paragens.isEmpty()) {
                paragem = paragens2.get(0);
                spParagem.setEnabled(true);
            }
            dialog.dismiss();
        }
    }
}
