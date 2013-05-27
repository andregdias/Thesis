package com.opt.mobipag.gui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.opt.mobipag.R;

public class HelpActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView t = (TextView) findViewById(R.id.textView1);
        int help = getIntent().getIntExtra("HELP", -1);
        setText(t, help);

        Button b_Ok = (Button) findViewById(R.id.b_Ok);
        b_Ok.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setText(TextView t, int help) {
        String s = "";

        switch (help) {
            case 1:
                //MainActivity
                s = getResources().getStringArray(R.array.help)[0];
                break;
            case 2:
                //ValidateActivity
                s = getResources().getStringArray(R.array.help)[1];
                break;
            case 3:
                //BuyActivity
                s = getResources().getStringArray(R.array.help)[2];
                break;
            case 4:
                //BuyOccasionalActivity(Occasional)
                s = getResources().getStringArray(R.array.help)[3];
                break;
            case 5:
                //BuyOccasionalActivity(Andante24)
                s = getResources().getStringArray(R.array.help)[4];
                break;
            case 6:
                //ConfirmBuyOCActivity(Occasional)
                s = getResources().getStringArray(R.array.help)[5];
                break;
            case 7:
                //ConfirmBuyOCActivity(Andante24)
                s = getResources().getStringArray(R.array.help)[6];
                break;
            case 8:
                //BuySignatureActivity
                s = getResources().getStringArray(R.array.help)[7];
                break;
            case 9:
                //ConfirmBuySignatureActivity
                s = getResources().getStringArray(R.array.help)[8];
                break;
            case 10:
                //ConsultActivity
                s = getResources().getStringArray(R.array.help)[9];
                break;
            case 11:
                //TicketBalanceActivity
                s = getResources().getStringArray(R.array.help)[10];
                break;
            case 12:
                //AccountMovementsActivity
                s = getResources().getStringArray(R.array.help)[11];
                break;
            case 13:
                //TicketValidationActivity(Occ+A24)
                s = getResources().getStringArray(R.array.help)[12];
                break;
            case 14:
                //TicketValidationActivity(Signature)
                s = getResources().getStringArray(R.array.help)[13];
                break;
            case 15:
                //SettingsActivity
                s = getResources().getStringArray(R.array.help)[14];
                break;
            case 16:
                //RegisterActivity
                s = getResources().getStringArray(R.array.help)[15];
                break;
            case 17:
                //RevisorActivity
                s = getResources().getStringArray(R.array.help)[16];
                break;
            case 18:
                //LoginActivity
                s = getResources().getStringArray(R.array.help)[17];
                break;
            case 19:
                //ManualStopSelector
                s = getResources().getStringArray(R.array.help)[18];
                break;
        }
        t.setText(Html.fromHtml(s));
    }
}