package com.opt.mobipag.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import com.opt.mobipag.R;
import com.opt.mobipag.data.User;
import com.opt.mobipag.data.Utils;
import com.opt.mobipag.database.UserDataSource;
import com.opt.mobipag.database.UtilsDataSource;

import java.util.List;

public class BuyOcasionalActivity extends Activity {
    private int m_CurTickets;
    private int price_type;
    private double m_CurValue;
    private double m_Price;
    private double m_balance;
    private String zone;
    private List<String> zones;
    private final UtilsDataSource datasource = new UtilsDataSource(this);
    private final UserDataSource datasource2 = new UserDataSource(this);
    private String email;
    private User user;
    private int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyocasional);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();

        email = getIntent().getStringExtra("USER_EMAIL");
        datasource2.open();
        user = datasource2.getUserByEmail(email);
        datasource2.close();
        m_balance = Utils.round(user.getBalance());
        m_CurTickets = 1;
        datasource.open();
        zones = datasource.getTypologies();
        zone = zones.get(0);
        type = this.getIntent().getIntExtra("Type", -1);

        final int OCASIONAL = 0;
        final int ANDANTE24 = 1;
        switch (type) {

            case OCASIONAL:
                price_type = datasource.PRICE_OCCASIONAL;
                header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), HelpActivity.class);
                        i.putExtra("HELP", 4);
                        startActivity(i);
                    }
                });
                break;

            case ANDANTE24:
                header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), HelpActivity.class);
                        i.putExtra("HELP", 5);
                        startActivity(i);
                    }
                });
                price_type = datasource.PRICE_ANDANTE24;
                TextView title = (TextView) findViewById(R.id.buyOCtitle);
                title.setText(getText(R.string.buy_oc_title2));
                break;
        }

        m_Price = datasource.getPriceByTypologyAndType(zones.get(0), price_type);
        datasource.close();

        ((TextView) findViewById(R.id.Balance)).setText(getText(R.string.buy_oc_balance).toString() + m_balance + getText(R.string.euro).toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, zones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spZones = (Spinner) findViewById(R.id.sp_Zones);
        spZones.setAdapter(adapter);
        spZones.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                datasource.open();
                m_Price = datasource.getPriceByTypologyAndType(zones.get(position), price_type);
                datasource.close();
                zone = zones.get(position);
                UpdateText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        final Button b_1 = (Button) findViewById(R.id.button1);
        final Button b_2 = (Button) findViewById(R.id.button2);
        final Button b_5 = (Button) findViewById(R.id.button3);
        final Button b_10 = (Button) findViewById(R.id.button4);

        b_1.setPressed(true);
        b_1.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                b_1.setPressed(true);
                b_2.setPressed(false);
                b_5.setPressed(false);
                b_10.setPressed(false);
                m_CurTickets = 1;
                UpdateText();
                return true;
            }
        });

        b_2.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                b_1.setPressed(false);
                b_2.setPressed(true);
                b_5.setPressed(false);
                b_10.setPressed(false);
                m_CurTickets = 2;
                UpdateText();
                return true;
            }
        });

        b_5.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                b_1.setPressed(false);
                b_2.setPressed(false);
                b_5.setPressed(true);
                b_10.setPressed(false);
                m_CurTickets = 5;
                UpdateText();
                return true;
            }
        });

        b_10.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                b_1.setPressed(false);
                b_2.setPressed(false);
                b_5.setPressed(false);
                b_10.setPressed(true);
                m_CurTickets = 10;
                UpdateText();
                return true;
            }
        });

        Button b_Buy = (Button) findViewById(R.id.b_BuyOcTickets);
        b_Buy.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                if (user.getMaxamount() != 0 && m_CurValue > user.getMaxamount()) {
                    Intent myIntent = new Intent(view.getContext(), LoginPinActivity.class);
                    myIntent.putExtra("USER_EMAIL", email);
                    startActivityForResult(myIntent, 0);
                } else if (m_balance < m_CurValue) {
                    Context c = view.getContext();
                    if (c != null) {
                        Toast toast = Toast.makeText(c, getText(R.string.buy_no_balance), Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else
                    callConfirm();
            }
        });
    }

    private void callConfirm() {
        Intent myIntent = new Intent(this, ConfirmBuyOCActivity.class);
        myIntent.putExtra("Zone", zone);
        myIntent.putExtra("Num", m_CurTickets);
        myIntent.putExtra("Value", m_CurValue);
        myIntent.putExtra("USER_EMAIL", user.getEmail());
        myIntent.putExtra("Balance", user.getBalance());
        myIntent.putExtra("Type", type);
        startActivityForResult(myIntent, 0);
    }

    private void UpdateText() {
        m_CurValue = Utils.round(m_Price * m_CurTickets);
        ((TextView) findViewById(R.id.t_ToPay)).setText(getText(R.string.buy_oc_total).toString() + m_CurValue + getText(R.string.euro).toString());

        if (m_CurTickets == 10)
            ((TextView) findViewById(R.id.TextView01)).setText(getText(R.string.buy_oc_numtitles).toString() + m_CurTickets + getText(R.string.buy_oc_free));
        else
            ((TextView) findViewById(R.id.TextView01)).setText(getText(R.string.buy_oc_numtitles).toString() + m_CurTickets);
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
}
