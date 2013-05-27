package com.opt.mobipag.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.opt.mobipag.R;
import com.opt.mobipag.data.User;
import com.opt.mobipag.data.Utils;
import com.opt.mobipag.database.UserDataSource;

import java.security.NoSuchAlgorithmException;

public class LoginPinActivity extends Activity {
    private AlertDialog m_Alerter;
    private final UserDataSource datasource = new UserDataSource(this);
    private String email = "";

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpin);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("USER_EMAIL");
        }

        m_Alerter = new AlertDialog.Builder(this).create();
        m_Alerter.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ImageButton b_Logout = (ImageButton) findViewById(R.id.b_Logout);
        b_Logout.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                setResult(4);
                finish();
            }
        });

        Button b_Login = (Button) findViewById(R.id.b_Login);
        b_Login.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (VerifyLogin()) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else
                        m_Alerter.show();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean VerifyLogin() throws NoSuchAlgorithmException {
        String pin = ((EditText) findViewById(R.id.tf_LoginPin)).getText().toString();
        datasource.open();
        User user = datasource.getUserByEmail(email);
        datasource.close();
        if (user.getPin().equals(Utils.SHA256(pin)))
            return true;

        m_Alerter.setMessage(getText(R.string.pin_incorreto));
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        return dialogBounds.contains((int) ev.getX(), (int) ev.getY()) && super.dispatchTouchEvent(ev);
    }
}