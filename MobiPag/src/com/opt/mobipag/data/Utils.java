package com.opt.mobipag.data;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.opt.mobipag.R;
import com.opt.mobipag.database.OccasionalDataSource;
import com.opt.mobipag.database.SignatureDataSource;

import java.math.BigDecimal;
import java.security.*;
import com.opt.mobipag.data.Signature;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utils {

    private final static LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public static String SHA256(String original) throws NoSuchAlgorithmException {
        if (original == null)
            return null;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(original.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String[] RSA() throws NoSuchAlgorithmException {
        //Convert keys to string: http://j2stuff.blogspot.pt/2012/03/generate-public-and-private-keys-to.html
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair key = gen.generateKeyPair();
        PrivateKey privateKey = key.getPrivate();
        PublicKey publicKey = key.getPublic();
        return new String[]{Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT), Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT)};
    }

    public static double round(double unrounded) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return rounded.doubleValue();
    }

    public static String parseTime(int time) {
        String t = "";
        int h = time / 60;
        int m = time % 60;
        if (h > 0)
            t += h + "h";
        if (!t.equals("") && m < 10)
            t += "0";
        t += m + "m";

        return t;
    }

    public static long dateFromJSON(String dob) {
        int pos = dob.indexOf("+");
        return Long.parseLong(dob.substring(6, pos));
    }

    public static String parseDate(Date date, Boolean full) {
        int y = date.getYear() + 1900;
        int m = date.getMonth() + 1;
        int d = date.getDate();
        if (date.getTime() < 0)
            d++;
        int h = date.getHours();
        int min = date.getMinutes();
        String dt = y + "/";
        if (m < 10)
            dt += 0;
        dt += m + "/";
        if (d < 10)
            dt += 0;
        dt += d + " ";
        if (full) {
            if (h < 10)
                dt += 0;
            dt += h + ":";
            if (min < 10)
                dt += 0;
            dt += min;
        }

        return dt;
    }

    public static String currentDate() {
        Date date = new Date();
        int y = date.getYear() + 1900;
        int m = date.getMonth() + 1;
        int d = date.getDate();
        int h = date.getHours();
        int min = date.getMinutes();
        String dt = y + "/";
        if (m < 10)
            dt += 0;
        dt += m + "/";
        if (d < 10)
            dt += 0;
        dt += d + " ";
        if (h < 10)
            dt += 0;
        dt += h + ":";
        if (min < 10)
            dt += 0;
        dt += min;

        return dt;
    }

    public static boolean checkValidity(Ticket t) {

        int time = t.getTempoviagem();

        int diff = getTimeDiff(t);
        return (time > diff);
    }

    public static int getTimeDiff(Ticket t) {
        if (t.getFirstval() == null)
            return 999999;
        Date date = new Date();
        String valDate = t.getFirstval();
        String[] s = valDate.split("/|:| ");
        int yy = Integer.parseInt(s[0]) - 1900;
        int mm = Integer.parseInt(s[1]) - 1;
        int dd = Integer.parseInt(s[2]);
        int hh = Integer.parseInt(s[3]);
        int minn = Integer.parseInt(s[4]);
        Date val = new Date(yy, mm, dd, hh, minn);

        return (int) (date.getTime() - val.getTime()) / (1000 * 60);
    }

    public static void populateTable(User user, SignatureDataSource datasource, OccasionalDataSource datasource2, TableLayout table, Context c) {
        final Signature s = datasource.getSignature(user.getId(), true);

        int[] ocasional = new int[13];
        Arrays.fill(ocasional, 0);
        int[] andante24 = new int[13];
        Arrays.fill(andante24, 0);
        List<Occasional> occasionals = datasource2.getAllAvailableOccasionals(user.getId());
        for (Occasional o : occasionals)
            if (o.getTempoviagem() == 24 * 60)
                andante24[o.getNumzonas()]++;
            else
                ocasional[o.getNumzonas()]++;

        TableRow row = new TableRow(c);

        TextView tv = new TextView(c);
        TextView tv2 = new TextView(c);

        tv.setPadding(15, 0, 25, 10);
        tv.setTypeface(null, Typeface.BOLD);
        tv2.setPadding(15, 0, 15, 10);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setGravity(Gravity.RIGHT);

        tv.setText(c.getText(R.string.tipo));
        tv2.setText(c.getText(R.string.detalhes));

        row.addView(tv);
        row.addView(tv2);

        table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        if (s != null) {
            TableRow r = new TableRow(c);

            TextView t = new TextView(c);
            TextView t2 = new TextView(c);

            t.setPadding(15, 5, 25, 5);
            t2.setPadding(15, 5, 15, 5);
            t2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            t.setText(c.getText(R.string.buy_signature));
            t2.setText(s.getListZonas());

            r.addView(t);
            r.addView(t2);

            table.addView(r, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }

        for (int i = 2; i < 13; i++) {
            if (ocasional[i] > 0) {
                TableRow r = new TableRow(c);

                TextView t = new TextView(c);
                TextView t2 = new TextView(c);

                t.setPadding(15, 5, 25, 5);
                t2.setPadding(15, 5, 15, 5);
                t2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

                if (ocasional[i] > 1) {
                    t.setText(c.getText(R.string.ocasional).toString() + i);
                    t2.setText(ocasional[i] + c.getText(R.string.titulos).toString());
                } else if (ocasional[i] > 0) {
                    t.setText(c.getText(R.string.ocasional).toString() + i);
                    t2.setText(ocasional[i] + c.getText(R.string.titulo).toString());
                }
                r.addView(t);
                r.addView(t2);

                table.addView(r, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }
        }

        for (int i = 2; i < 13; i++) {
            if (andante24[i] > 0) {
                TableRow r = new TableRow(c);

                TextView t = new TextView(c);
                TextView t2 = new TextView(c);

                t.setPadding(15, 5, 25, 5);
                t2.setPadding(15, 5, 15, 5);
                t2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

                if (andante24[i] > 1) {
                    t.setText(c.getText(R.string.andante24).toString() + i);
                    t2.setText(andante24[i] + c.getText(R.string.titulos).toString());
                } else if (andante24[i] > 0) {
                    t.setText(c.getText(R.string.andante24).toString() + i);
                    t2.setText(andante24[i] + c.getText(R.string.titulo).toString());
                }
                r.addView(t);
                r.addView(t2);

                table.addView(r, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }
        }
    }
}