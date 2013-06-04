package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.opt.mobipag.R;
import com.opt.mobipag.data.*;
import com.opt.mobipag.database.OccasionalDataSource;
import com.opt.mobipag.database.SignatureDataSource;
import com.opt.mobipag.database.TicketDataSource;
import com.opt.mobipag.database.UserDataSource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    private String email;
    private final UserDataSource datasource = new UserDataSource(this);
    private final TicketDataSource datasource2 = new TicketDataSource(this);
    private final SignatureDataSource datasource3 = new SignatureDataSource(this);
    private final OccasionalDataSource datasource4 = new OccasionalDataSource(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final String nopin = this.getIntent().getStringExtra("NoPin");
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 1);
                startActivity(i);
            }
        });

        // returns true if mock location enabled, false if not enabled.
        if (!Settings.Secure.getString(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            mockLocationWarning();
        } else {
            email = readFile(getText(R.string.filename_user).toString());

            if (email.length() != 0) {
                datasource.open();
                User user = datasource.getUserByEmail(email);
                datasource.close();
                if (user != null && nopin == null) {
                    checkActiveTicket(user);
                    Intent myIntent = new Intent(this, LoginPinActivity.class);
                    myIntent.putExtra("USER_EMAIL", email);
                    startActivityForResult(myIntent, 0);
                }
            } else {
                Intent myIntent = new Intent(this, LoginActivity.class);
                startActivityForResult(myIntent, 0);
            }

            Button b_Validate = (Button) findViewById(R.id.b_ValidateMenu);
            b_Validate.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    email = readFile(getText(R.string.filename_user).toString());
                    Intent myIntent = new Intent(view.getContext(), ValidationActivity.class);
                    myIntent.putExtra("USER_EMAIL", email);
                    startActivityForResult(myIntent, 0);
                }
            });

            Button b_Buy = (Button) findViewById(R.id.b_BuyMenu);
            b_Buy.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    email = readFile(getText(R.string.filename_user).toString());
                    Intent myIntent = new Intent(view.getContext(), BuyActivity.class);
                    myIntent.putExtra("USER_EMAIL", email);
                    startActivityForResult(myIntent, 0);
                }
            });

            Button b_Consult = (Button) findViewById(R.id.b_ConsultMenu);
            b_Consult.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    email = readFile(getText(R.string.filename_user).toString());
                    Intent myIntent = new Intent(view.getContext(), ConsultActivity.class);
                    myIntent.putExtra("USER_EMAIL", email);
                    startActivityForResult(myIntent, 0);
                }
            });

            Button b_Settings = (Button) findViewById(R.id.b_SettingsMenu);
            b_Settings.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    email = readFile(getText(R.string.filename_user).toString());
                    Intent myIntent = new Intent(view.getContext(), SettingsActivity.class);
                    myIntent.putExtra("USER_EMAIL", email);
                    startActivityForResult(myIntent, 0);
                }
            });

            Button b_About = (Button) findViewById(R.id.b_AboutMenu);
            b_About.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), AboutActivity.class);
                    startActivityForResult(myIntent, 0);
                }
            });
        }
    }

    private void mockLocationWarning() {
        AlertDialog m_Alerter = new AlertDialog.Builder(this).create();
        m_Alerter.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        m_Alerter.setMessage(getText(R.string.fake));
        m_Alerter.show();
    }

    private void checkActiveTicket(User user) {
        datasource3.open();
        datasource4.open();
        Signature s = datasource3.getActiveSignature(user.getId());
        Occasional o = datasource4.getActiveOccasional(user.getId());
        datasource3.close();
        datasource4.close();
        datasource2.open();
        if (s != null)
            if (!Utils.checkValidity(s))
                datasource2.changeTicketStatusById(s.getId(), 0, -1, -1, -1);
            else {
                Intent i = new Intent(this, TravelTimer.class);
                i.putExtra("USER_EMAIL", email);
                i.putExtra("title", "Assinatura (" + s.getListZonas() + ")");
                i.putExtra("titleid", s.getId());
                i.putExtra("time", s.getTempoviagem());
                i.putExtra("date", s.getFirstval());
                startService(i);
            }
        if (o != null)
            if (!Utils.checkValidity(o))
                datasource2.changeTicketStatusById(o.getId(), 2, -1, -1, -1);
            else {
                Intent i = new Intent(this, TravelTimer.class);
                i.putExtra("USER_EMAIL", email);
                if (o.getTempoviagem() == 24 * 60)
                    i.putExtra("title", "Andante 24 " + o.getDetails());
                else
                    i.putExtra("title", "Ocasional " + o.getDetails());
                i.putExtra("titleid", 1);
                i.putExtra("time", o.getTempoviagem());
                i.putExtra("date", o.getFirstval());
                startService(i);
            }
        datasource2.close();
    }

    String readFile(String filename) {
        String data = "";
        try {
            FileInputStream fIn = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();
            while (readString != null) {
                data = data + readString;
                readString = buffreader.readLine();
            }

            isr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                Intent myIntent = new Intent(this.getBaseContext(), RegisterActivity.class);
                startActivityForResult(myIntent, 0);
                break;
            case 2:
                finish();
                break;
            case 3:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.gps_not_found_title);  // GPS not found
                builder.setMessage(R.string.gps_not_found_message); // Want to enable?
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                builder.create().show();
                break;
            case 4:
                moveTaskToBack(true);
                deleteFile(getText(R.string.filename_user).toString());
                stopService(new Intent(MainActivity.this, TravelTimer.class));
                finish();
        }
    }
}