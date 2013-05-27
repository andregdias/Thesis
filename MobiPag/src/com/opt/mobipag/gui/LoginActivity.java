package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.opt.mobipag.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.opt.mobipag.data.User;
import com.opt.mobipag.data.Utils;
import com.opt.mobipag.data.WebServiceHandler;
import com.opt.mobipag.database.UserDataSource;
import com.opt.mobipag.database.UtilsDataSource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class LoginActivity extends Activity {
    private AlertDialog m_Alerter;
    private final UserDataSource datasource = new UserDataSource(this);
    private final UtilsDataSource datasource2 = new UtilsDataSource(this);
    private ProgressDialog dialog;
    private String email = null;
    private Context c;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(2);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 18);
                startActivity(i);
            }
        });

        c = this;
        m_Alerter = new AlertDialog.Builder(this).create();
        m_Alerter.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dialog = ProgressDialog.show(this, "", getText(R.string.loading), true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                setResult(2);
                finish();
            }
        });

        Button b_Login = (Button) findViewById(R.id.b_Login);
        b_Login.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Context c = view.getContext();
                if (c != null) {
                    dialog = ProgressDialog.show(c, "", getText(R.string.loading), true);
                    email = ((EditText) findViewById(R.id.tf_Email)).getText().toString();
                    new LoginUser().execute(email, ((EditText) findViewById(R.id.tf_Password)).getText().toString());
                }
            }
        });

        Button b_Register = (Button) findViewById(R.id.b_Register);
        b_Register.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                setResult(1);
                finish();
            }
        });
        new Prices().execute();
    }

    private void writeFile(String filename, String content) throws IOException {
        FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
        fos.write(content.getBytes());
        fos.close();
    }

    private class LoginUser extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {
            String email = arg0[0];
            String passwordHash = "";
            try {
                passwordHash = Utils.SHA256(arg0[1]);
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }

            try {
                JSONObject serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP).toString()+getText(R.string.LOGINURL) + "?email=" + email + "&password=" + passwordHash);
                Boolean result = false;
                if (serviceResult != null) {
                    int code = (Integer) serviceResult.get("Code");
                    switch (code) {
                        case 2100:
                            result = true;
                            break;
                        case 2101:
                            m_Alerter.setMessage(getText(R.string.email_inexistente));
                            result = false;
                            break;
                        case 2102:
                            m_Alerter.setMessage(getText(R.string.wrong_pass));
                            result = false;
                            break;
                    }
                }
                return result;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return Boolean.FALSE;
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                datasource.open();
                User user = datasource.getUserByEmail(email);
                datasource.close();
                if (user == null)
                    new GetUser().execute(email);
                else
                    try {
                        writeFile(getText(R.string.filename_user).toString(), email);
                        setResult(Activity.RESULT_OK);
                        dialog.dismiss();
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            } else {
                //m_Alerter.setMessage(getText(R.string.verifyConnection));
                m_Alerter.show();
                dialog.dismiss();
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
                        String address = null;
                        Double balance = 0.0;
                        String dob = null;
                        String email = null;
                        int maxAmount = 0;
                        String mobile = null;
                        String nome = null;
                        String password = null;
                        String pin = null;
                        String nif = null;
                        try {
                            JSONObject o = serviceResult.getJSONObject("UserInfo");
                            address = o.getString("Address");
                            if (address.equals("null"))
                                address = null;
                            balance = o.getDouble("Balance");
                            dob = o.getString("BirthDate");
                            if (dob.equals("null"))
                                dob = null;
                            else {
                                long mili = Utils.dateFromJSON(dob);
                                dob = Utils.parseDate(new Date(mili), false);
                            }
                            email = o.getString("Email");
                            maxAmount = o.getInt("MaxAmount");
                            mobile = o.getString("Mobile");
                            if (mobile.equals("null"))
                                mobile = null;
                            nif = o.getString("NIF");
                            if (nif.equals("null"))
                                nif = null;
                            nome = o.getString("Name");
                            password = o.getString("Password");
                            pin = o.getString("Pin");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            datasource.open();
                            datasource.createUser(email,
                                    password,
                                    pin,
                                    nome,
                                    address,
                                    mobile,
                                    nif,
                                    dob,
                                    balance,
                                    maxAmount);
                            datasource.close();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        result = true;
                        break;
                    case 3251:
                        m_Alerter.setMessage(getText(R.string.email_inexistente));
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
                try {
                    writeFile(getText(R.string.filename_user).toString(), email);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                m_Alerter.show();
                dialog.dismiss();
            }
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

                            datasource2.open();
                            datasource2.deleteAll();
                            for (int i = 0; i < zonas.length; i++) {
                                datasource2.insertData(zonas[i], occ[i], and24[i], sig[i], numzones[i], traveltime[i]);
                            }
                            datasource2.close();
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
            dialog.dismiss();
            if (!result) {
                Toast.makeText(c, getText(R.string.verifyConnection), Toast.LENGTH_SHORT).show();
                setResult(2);
                finish();
            }
        }
    }
}