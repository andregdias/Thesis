package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.opt.mobipag.R;
import com.opt.mobipag.data.MultiSpinner;
import com.opt.mobipag.data.MultiSpinner.MultiSpinnerListener;
import com.opt.mobipag.data.Signature;
import com.opt.mobipag.data.User;
import com.opt.mobipag.data.Utils;
import com.opt.mobipag.database.SignatureDataSource;
import com.opt.mobipag.database.UserDataSource;
import com.opt.mobipag.database.UtilsDataSource;
import com.opt.mobipag.database.ZoneDataSource;

import java.util.Date;
import java.util.List;

public class BuySignatureActivity extends Activity {
    private double m_CurValue;
    private double m_balance;
    private double m_Price;
    private int count;
    private final Context c = this;
    private MultiSpinner multiSpinner;
    private List<String> zones;
    private final UtilsDataSource datasource = new UtilsDataSource(this);
    private final ZoneDataSource datasource2 = new ZoneDataSource(this);
    private final UserDataSource datasource3 = new UserDataSource(this);
    private final SignatureDataSource datasource4 = new SignatureDataSource(this);
    private String email;
    private User user;
    private String zonelist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buysignature);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 8);
                startActivity(i);
            }
        });

        Date d = new Date();
        int day = d.getDate();

        email = getIntent().getStringExtra("USER_EMAIL");
        datasource3.open();
        user = datasource3.getUserByEmail(email);
        datasource3.close();
        m_balance = Utils.round(user.getBalance());
        datasource4.open();
        Signature s = datasource4.getSignature(user.getId(), true);
        Signature s2 = datasource4.getSignature(user.getId(), false);
        datasource4.close();

        AlertDialog m_Alerter = new AlertDialog.Builder(this).create();
        m_Alerter.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setResult(2);
                finish();
            }
        });

        if (s != null && day <= 15) {
            m_Alerter.setMessage(getText(R.string.buy_sig_curr));
            m_Alerter.show();
        } else if (s2 != null) {
            m_Alerter.setMessage(getText(R.string.buy_sig_next));
            m_Alerter.show();
        } else {
            datasource.open();
            zones = datasource.getTypologies();
            datasource2.open();
            List<String> zone_desc = datasource2.getAllZoneDescriptors();
            datasource2.close();
            m_Price = datasource.getPriceByTypologyAndType(zones.get(0), datasource.PRICE_SIGNATURE);
            datasource.close();
            UpdateText();

            int m = d.getMonth();
            if (day > 15)
                m++;
            ((TextView) findViewById(R.id.buyOCtitle)).setText(getText(R.string.buy_signature) + ": " + getResources().getStringArray(R.array.meses)[m]);
            ((TextView) findViewById(R.id.Balance)).setText(getText(R.string.buy_oc_balance).toString() + m_balance + getText(R.string.euro).toString());

            MultiSpinnerListener mslistener = new MultiSpinnerListener() {
                @Override
                public void onItemsSelected(boolean[] selected) {
                    count = 0;
                    for (boolean aSelected : selected)
                        if (aSelected)
                            count++;

                    if (count < 2) {
                        count = 2;
                        Toast toast = Toast.makeText(c, getText(R.string.buy_sig_min2), Toast.LENGTH_LONG);
                        toast.show();
                    }
                    datasource.open();
                    m_Price = datasource.getPriceByTypologyAndType(zones.get(count - 2), datasource.PRICE_SIGNATURE);
                    datasource.close();
                    UpdateText();
                }
            };

            multiSpinner = (MultiSpinner) findViewById(R.id.sp_Zones);
            multiSpinner.setItems(zone_desc, mslistener);

            Button b_Buy = (Button) findViewById(R.id.b_BuyOcTickets);
            b_Buy.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    zonelist = multiSpinner.getZones();
                    Context c = view.getContext();
                    if (c != null)
                        if (!zonelist.contains(",")) {
                            Toast toast = Toast.makeText(c, getText(R.string.buy_sig_min2), Toast.LENGTH_LONG);
                            toast.show();
                        } else if (user.getMaxamount() != 0 && m_CurValue > user.getMaxamount()) {
                            Intent myIntent = new Intent(view.getContext(), LoginPinActivity.class);
                            myIntent.putExtra("USER_EMAIL", email);
                            startActivityForResult(myIntent, 0);
                        } else if (m_balance < m_CurValue) {
                            Toast toast = Toast.makeText(c, getText(R.string.buy_no_balance), Toast.LENGTH_LONG);
                            toast.show();
                        } else
                            callConfirm();
                }
            });
        }
    }

    private void callConfirm() {
        Intent myIntent = new Intent(this, ConfirmBuySignatureActivity.class);
        myIntent.putExtra("Zone", zonelist);
        myIntent.putExtra("Num", count);
        myIntent.putExtra("Value", m_CurValue);
        myIntent.putExtra("USER_EMAIL", email);
        myIntent.putExtra("Balance", user.getBalance());
        startActivityForResult(myIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                callConfirm();
                break;
            case 2:
                setResult(2);
                finish();
                break;
        }
    }

    private void UpdateText() {
        m_CurValue = Utils.round(m_Price);
        ((TextView) findViewById(R.id.t_ToPay)).setText(getText(R.string.buy_oc_total).toString() + m_CurValue + getText(R.string.euro).toString());
    }
}
