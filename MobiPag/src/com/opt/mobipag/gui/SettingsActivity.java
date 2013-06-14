package com.opt.mobipag.gui;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
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
import com.opt.mobipag.database.UserDataSource;

import java.io.*;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class SettingsActivity extends FragmentActivity implements OnDateSetListener {
    private AlertDialog m_Alerter;
    private AlertDialog m_Success;
    private UserDataSource datasource = new UserDataSource(this);
    private static EditText b_Dob;
    private Button b_Photo;
    private static String dob = null;
    private ProgressDialog dialog;
    private EditText field_nome;
    private EditText field_email;
    private EditText field_password;
    private EditText field_pin;
    private EditText field_mobile;
    private EditText field_address;
    private EditText field_nif;
    private EditText field_max;
    private User user;
    private String email = null;
    private String password = null;
    private String pin = null;
    private String name = null;
    private String address = null;
    private String mobile = null;
    private String nif = null;
    private String max = "0";
    private Bitmap bitmap = null;
    private boolean finish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ActionBar header = (ActionBar) findViewById(R.id.header);
        header.initHeader();
        header.helpButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("HELP", 15);
                startActivity(i);
            }
        });
        header.logoutButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(4);
                finish();
            }
        });
        header.logoutButton.setVisibility(View.VISIBLE);

        m_Alerter = new AlertDialog.Builder(this).create();
        m_Success = new AlertDialog.Builder(this).create();

        m_Alerter.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (finish)
                    finish();
            }
        });

        m_Success.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        field_nome = (EditText) findViewById(R.id.tf_EName);
        field_email = (EditText) findViewById(R.id.tf_EEmail);
        field_password = (EditText) findViewById(R.id.tf_EPassword);
        field_pin = (EditText) findViewById(R.id.tf_EPin);
        field_mobile = (EditText) findViewById(R.id.tf_EPhone);
        field_address = (EditText) findViewById(R.id.tf_EAddress);
        field_nif = (EditText) findViewById(R.id.tf_ENIF);
        field_max = (EditText) findViewById(R.id.tf_EMax);
        field_max.setVisibility(View.GONE);
        b_Dob = (EditText) findViewById(R.id.tf_RBirth);

        datasource = new UserDataSource(this);
        email = this.getIntent().getStringExtra("USER_EMAIL");


        b_Dob.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                Bundle b = new Bundle();
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

        if (retrieveFoto()) {
            b_Photo.setText("Para alterar a fotografia, contacte o administrador.");
            b_Photo.setClickable(false);
        }

        Button b_Register = (Button) findViewById(R.id.b_EEdituser);
        b_Register.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (field_pin.getText().toString().length() < 4 && field_pin.getText().toString().length() > 0) {
                        m_Alerter.setMessage(getText(R.string.reg_pin4));
                        m_Alerter.show();
                    } else {
                        User user = null;
                        if (!email.equals(field_email.getText().toString())) {
                            datasource.open();
                            user = datasource.getUserByEmail(field_email.getText().toString());
                            datasource.close();
                        }

                        if (user != null) {
                            m_Alerter.setMessage(getText(R.string.reg_email_already));
                            m_Alerter.show();
                        } else {
                            dialog.show();

                            if (field_email.getText().length() != 0)
                                email = field_email.getText().toString();
                            if (field_password.getText().length() != 0)
                                password = field_password.getText().toString();
                            if (field_pin.getText().length() != 0)
                                pin = field_pin.getText().toString();
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

                            new AlterUser().execute();

                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog = ProgressDialog.show(this, "", getText(R.string.loading), true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        new GetUser().execute(email);
    }

    private class AlterUser extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... arg0) {
            String key = "temp";
            String param = null;

            try {
                param = "email=" + URLEncoder.encode(email, "UTF-8");
                if (password != null)
                    param += "&password=" + URLEncoder.encode(Utils.SHA256(password), "UTF-8");
                else
                    param += "&password=" + URLEncoder.encode(user.getPassword(), "UTF-8");
                if (pin != null)
                    param += "&pin=" + URLEncoder.encode(Utils.SHA256(pin), "UTF-8");
                else
                    param += "&pin=" + URLEncoder.encode(user.getPin(), "UTF-8");
                if (name != null)
                    param += "&name=" + URLEncoder.encode(name, "UTF-8");
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
                param += "&publicKey=" + URLEncoder.encode(key, "UTF-8");

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
                //JSONObject serviceResult = WebServiceHandler.RequestPOST(getText(R.string.REGISTERURL).toString(), param);
                JSONObject serviceResult = WebServiceHandler.RequestGET(getText(R.string.SERVERMP)+getText(R.string.EDITUSERURL).toString() + "?" + param);

                boolean result = false;
                if (serviceResult != null) {
                    int code = (Integer) serviceResult.get("Code");
                    switch (code) {
                        case 3200:
                            result = true;
                            break;
                        case 3201:
                            //m_Alerter.setMessage(getText(R.string.reg_email_already));
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
                    datasource.UpdateUser(email,
                            Utils.SHA256(password),
                            Utils.SHA256(pin),
                            name,
                            address,
                            mobile,
                            nif,
                            dob,
                            max);
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
                m_Success.setMessage(getText(R.string.change_success));
                m_Success.show();
            } else {
                m_Alerter.setMessage(getText(R.string.verifyConnection));
                m_Alerter.show();
            }
            dialog.dismiss();
        }
    }

    class GetUser extends AsyncTask<String, Integer, Boolean> {
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
                        JSONObject o = null;
                        try {
                            o = serviceResult.getJSONObject("UserInfo");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        createUser(o);
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

        private void createUser(JSONObject o) {
            String address = null;
            String nome = null;
            int maxAmount = 0;
            String birthDate = null;
            String email = null;
            String password = null;
            String pin = null;
            String mobile = null;
            String nif = null;
            try {
                address = o.getString("Address");
                if (address.equals("null"))
                    address = null;
                birthDate = o.getString("BirthDate");
                if (birthDate.equals("null"))
                    birthDate = null;
                else {
                    birthDate = Utils.parseDate(birthDate, false);
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
                datasource.UpdateUser(email,
                        password,
                        pin,
                        nome,
                        address,
                        mobile,
                        nif,
                        birthDate,
                        Integer.toString(maxAmount));
                datasource.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Called once the background activity has completed
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                dialog.dismiss();
                datasource.open();
                user = datasource.getUserByEmail(email);
                datasource.close();

                ((EditText) findViewById(R.id.tf_EName)).setText(user.getName());
                ((EditText) findViewById(R.id.tf_EEmail)).setText(email);
                findViewById(R.id.tf_EEmail).setEnabled(false);
                ((EditText) findViewById(R.id.tf_EPhone)).setText(user.getMobile());
                ((EditText) findViewById(R.id.tf_EAddress)).setText(user.getAddress());
                ((EditText) findViewById(R.id.tf_ENIF)).setText("" + user.getNif());
                findViewById(R.id.tf_ENIF).setEnabled(false);
                ((EditText) findViewById(R.id.tf_EMax)).setText("" + user.getMaxamount());
                dob = user.getDob();
                if (dob != null)
                    b_Dob.setText(dob);
                else
                    b_Dob.setHint(getText(R.string.reg_dob));
            } else {
                m_Alerter.setMessage(getText(R.string.verifyConnection));
                finish = true;
                m_Alerter.show();
                dialog.dismiss();
            }
        }
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

    void writeFile(String filename, String content) throws IOException {
        FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
        fos.write(content.getBytes());
        fos.close();
    }

    private boolean retrieveFoto() {
        FileInputStream fis;
        try {
            fis = openFileInput(email + "_foto.jpg");
        } catch (FileNotFoundException e) {
            return false;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        b_Photo.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);

        try {
            if (fis != null)
                fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    BitmapDrawable image = (BitmapDrawable) getResources().getDrawable(R.drawable.profile_image);

                    // Get the dimensions of the View
                    int targetW = image.getBitmap().getWidth();
                    int targetH = image.getBitmap().getHeight();
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
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
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
}