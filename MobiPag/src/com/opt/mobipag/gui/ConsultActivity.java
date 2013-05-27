package com.opt.mobipag.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.opt.mobipag.R;

public class ConsultActivity extends Activity {
    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consult);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 10);
                startActivity(i);
            }
        });

        email = getIntent().getStringExtra("USER_EMAIL");

        Button b_TicketBalance = (Button) findViewById(R.id.b_TicketBalance);
        b_TicketBalance.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TicketBalanceActivity.class);
                myIntent.putExtra("USER_EMAIL", email);
                startActivityForResult(myIntent, 0);
            }
        });

        Button b_AccountMovements = (Button) findViewById(R.id.b_AccountMovements);
        b_AccountMovements.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), AccountMovementsActivity.class);
                myIntent.putExtra("USER_EMAIL", email);
                startActivityForResult(myIntent, 0);
            }
        });

        ImageButton b_TicketValidation = (ImageButton) findViewById(R.id.b_TicketValidation);
        b_TicketValidation.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TicketValidationActivity.class);
                myIntent.putExtra("USER_EMAIL", email);
                myIntent.putExtra("SIGNATURE", 0);
                startActivityForResult(myIntent, 0);
            }
        });

        ImageButton b_TicketValidationSig = (ImageButton) findViewById(R.id.b_TicketValidationSig);
        b_TicketValidationSig.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TicketValidationActivity.class);
                myIntent.putExtra("USER_EMAIL", email);
                myIntent.putExtra("SIGNATURE", 1);
                startActivityForResult(myIntent, 0);
            }
        });
    }
}