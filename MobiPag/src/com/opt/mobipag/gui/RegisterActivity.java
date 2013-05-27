package com.opt.mobipag.gui;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import com.opt.mobipag.R;
import org.json.JSONException;
import org.json.JSONObject;
import com.opt.mobipag.data.DatePickerFragment;
import com.opt.mobipag.data.User;
import com.opt.mobipag.data.Utils;
import com.opt.mobipag.data.WebServiceHandler;
import com.opt.mobipag.database.HistoryDataSource;
import com.opt.mobipag.database.UserDataSource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends FragmentActivity implements OnDateSetListener {
    private static EditText b_Dob;
    private static String dob = null;
    private AlertDialog m_Alerter;
    private AlertDialog m_Success;
    private final UserDataSource datasource = new UserDataSource(this);
    private final HistoryDataSource datasource2 = new HistoryDataSource(this);
    private Button b_Photo;
    private ProgressDialog dialog;
    private EditText field_nome;
    private EditText field_email;
    private EditText field_password;
    private EditText field_pin;
    private EditText field_mobile;
    private EditText field_address;
    private EditText field_nif;
    private EditText field_max;
    private String email = null;
    private String password = null;
    private String pin = null;
    private String Key = null;
    private int amount = 0;
    private String name = null;
    private String address = null;
    private String mobile = null;
    private String nif = null;
    private String max = "0";
    private Bitmap bitmap = null;

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
        setContentView(R.layout.register);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 16);
                startActivity(i);
            }
        });

        m_Alerter = new AlertDialog.Builder(this).create();
        m_Success = new AlertDialog.Builder(this).create();

        m_Alerter.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        m_Success.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        field_nome = (EditText) findViewById(R.id.tf_RName);
        field_email = (EditText) findViewById(R.id.tf_REmail);
        field_password = (EditText) findViewById(R.id.tf_RPassword);
        field_pin = (EditText) findViewById(R.id.tf_RPin);
        field_mobile = (EditText) findViewById(R.id.tf_RPhone);
        field_address = (EditText) findViewById(R.id.tf_RAddress);
        field_nif = (EditText) findViewById(R.id.tf_RNIF);
        field_max = (EditText) findViewById(R.id.tf_RMax);
        field_max.setVisibility(View.GONE);
        b_Dob = (EditText) findViewById(R.id.tf_RBirth);

        b_Dob.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                Bundle b = new Bundle();
                b.putBoolean("Register", true);
                b.putString("Date", b_Dob.getText().toString());
                newFragment.setArguments(b);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        b_Dob.setOnFocusChangeListener(new Button.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    Bundle b = new Bundle();
                    b.putBoolean("Register", true);
                    b.putString("Date", b_Dob.getText().toString());
                    newFragment.setArguments(b);
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });

        b_Photo = (Button) findViewById(R.id.buttonPhoto);
        b_Photo.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, 2);
            }
        });

        Button b_Register = (Button) findViewById(R.id.b_RRegister);
        b_Register.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                try {
                    if ((field_email.getText().toString().length() == 0) ||
                            (field_password.getText().toString().length() == 0) ||
                            (field_pin.getText().toString().length() == 0) ||
                            (field_nome.getText().toString().length() == 0) ||
                            (field_nif.getText().toString().length() == 0)) {
                        m_Alerter.setMessage(getText(R.string.reg_allfields));
                        m_Alerter.show();
                    } else if (field_pin.getText().toString().length() < 4 && field_pin.getText().toString().length() > 0) {
                        m_Alerter.setMessage(getText(R.string.reg_pin4));
                        m_Alerter.show();
                    } else {
                        datasource.open();
                        User user = datasource.getUserByEmail(field_email.getText().toString());
                        datasource.close();
                        if (user != null) {
                            m_Alerter.setMessage(getText(R.string.reg_email_already));
                            m_Alerter.show();
                        } else {
                            Context c = view.getContext();
                            if (c != null) {
                                dialog = ProgressDialog.show(c, "", getText(R.string.loading), true);
                                dialog.setCancelable(true);
                                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    public void onCancel(DialogInterface dialog) {
                                        finish();
                                    }
                                });

                                if (field_email.getText().length() != 0)
                                    email = field_email.getText().toString();
                                if (field_password.getText().length() != 0)
                                    password = Utils.SHA256(field_password.getText().toString());
                                if (field_pin.getText().length() != 0)
                                    pin = Utils.SHA256(field_pin.getText().toString());
                                if (field_nome.getText().length() != 0)
                                    name = field_nome.getText().toString();
                                if (field_address.getText().length() != 0)
                                    address = field_address.getText().toString();
                                if (field_mobile.getText().length() != 0)
                                    mobile = field_mobile.getText().toString();
                                if (field_nif.getText().length() != 0)
                                    nif = field_nif.getText().toString();
                                if (field_max.getText().length() != 0)
                                    max = field_max.getText().toString();

                                new RegisterUser().execute();
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void writeFile(String filename, String content) throws IOException {
        FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
        fos.write(content.getBytes());
        fos.close();
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        String date = String.valueOf(year) + "/";
        month++;
        if (month < 10)
            date += 0;
        date += String.valueOf(month) + "/";
        if (day < 10)
            date += 0;
        date += String.valueOf(day);

        dob = date;
        b_Dob.setText(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    BitmapDrawable image = (BitmapDrawable) getResources().getDrawable(R.drawable.profile_image);

                    // Get the dimensions of the View

                    assert image != null;
                    Bitmap b = image.getBitmap();

                    int targetW = b.getWidth();
                    int targetH = b.getHeight();
                    bitmap = getResizedBitmap(bitmap, targetH, targetW);

                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    b_Photo.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
                    break;
            }
        }
    }

    private void savePhoto() {
        if (bitmap != null) {
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(field_email.getText().toString() + "_foto.jpg", Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fos != null) {
                bitmap.compress(CompressFormat.JPEG, 100, fos);
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(Math.min(scaleWidth, scaleHeight), Math.min(scaleWidth, scaleHeight));

        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    private class RegisterUser extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {
            Key = "temp";
            String param = null;

            try {
                param = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8") +
                        "&pin=" + URLEncoder.encode(pin, "UTF-8") +
                        "&name=" + URLEncoder.encode(name, "UTF-8");

                if (address != null)
                    param += "&address=" + URLEncoder.encode(address, "UTF-8");
                if (mobile != null)
                    param += "&mobile=" + URLEncoder.encode(mobile, "UTF-8");
                if (nif != null)
                    param += "&NIF=" + URLEncoder.encode(nif, "UTF-8");
                if (dob != null)
                    param += "&birthDate=" + URLEncoder.encode(dob, "UTF-8");
                if (max != null)
                    param += "&maxAmount=" + URLEncoder.encode(max, "UTF-8");
                param += "&publicKey=" + URLEncoder.encode(Key, "UTF-8");

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                //JSONObject serviceResult = WebServiceHandler.RequestPOST(getText(R.string.REGISTERURL).toString(), param);
                JSONObject serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.REGISTERURL).toString() + "?" + param);
                boolean result = false;
                if (serviceResult != null) {
                    int code = (Integer) serviceResult.get("Code");

                    switch (code) {
                        case 3000:
                            result = true;
                            break;
                        case 3001:
                            m_Alerter.setMessage(getText(R.string.reg_email_already));
                            result = false;
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
            if (result) {
                try {
                    datasource.open();
                    datasource.createUser(email,
                            password,
                            pin,
                            name,
                            address,
                            mobile,
                            nif,
                            dob,
                            0.0,
                            Integer.parseInt(max));
                    datasource.close();

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                try {
                    writeFile(getText(R.string.filename_user).toString(), field_email.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                savePhoto();

                m_Success.setMessage(getText(R.string.reg_success));
                m_Success.show();
                //TODO REMOVER
                new AddMoney().execute(email, Integer.toString(70), Key);
            } else {
                m_Alerter.setMessage(getText(R.string.verifyConnection));
                m_Alerter.show();
            }
            dialog.dismiss();

        }
    }

    private class AddMoney extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {
            String param = null;
            amount = Integer.parseInt(arg0[1]);

            try {
                param = "email=" + URLEncoder.encode(arg0[0], "UTF-8") +
                        "&amountToAdd=" + URLEncoder.encode(arg0[1], "UTF-8") +
                        "&publicKey=" + URLEncoder.encode(arg0[2], "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                //JSONObject serviceResult = WebServiceHandler.RequestPOST(getText(R.string.REGISTERURL).toString(), param);
                JSONObject serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.ADDMONEYURL).toString() + "?" + param);

                boolean result = false;
                if (serviceResult != null) {
                    int code = (Integer) serviceResult.get("Code");
                    switch (code) {
                        case 6000:
                            result = true;
                            break;
                        case 6001:
                            m_Alerter.setMessage(getText(R.string.email_inexistente));
                            result = false;
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
            datasource.open();
            datasource2.open();
            if (result) {
                datasource.UpdateBalance(email, -amount);
                User u = datasource.getUserByEmail(email);
                datasource2.createHist(u.getId(), Utils.currentDate(), amount, getText(R.string.charge_account).toString());
            }
            datasource.close();
            datasource2.close();
            dialog.dismiss();

        }
    }
}
