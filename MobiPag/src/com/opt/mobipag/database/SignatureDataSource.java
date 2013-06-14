package com.opt.mobipag.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.opt.mobipag.data.Signature;
import com.opt.mobipag.data.Validation;
import com.opt.mobipag.data.Zone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignatureDataSource {
    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;

    public SignatureDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createSignature(String details, double price, int status, int tempoviagem, List<Zone> zonas, int iduser) {

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_DETAILS, details);
        values.put(SQLiteHelper.COLUMN_PRICE, price);
        values.put(SQLiteHelper.COLUMN_STATUS, status);
        values.put(SQLiteHelper.COLUMN_ID_USER2, iduser);

        long insertId = database.insert(SQLiteHelper.TABLE_TICKETS, null, values);

        ContentValues values2 = new ContentValues();
        values2.put(SQLiteHelper.COLUMN_NUMVALID, 0);
        values2.put(SQLiteHelper.COLUMN_SIGNATURE_TRAVELTIME, tempoviagem);
        values2.put(SQLiteHelper.COLUMN_ID_TICKET2, insertId);

        database.insert(SQLiteHelper.TABLE_SIGNATURES, SQLiteHelper.COLUMN_SECURITY, values2);

        for (Zone z : zonas) {
            ContentValues valuesZone = new ContentValues();
            valuesZone.put(SQLiteHelper.COLUMN_IDSIGNATURE, insertId);
            valuesZone.put(SQLiteHelper.COLUMN_IDZONE, z.getId());

            database.insert(SQLiteHelper.TABLE_SIGNATURES_ZONES, null, valuesZone);
        }
    }

    public Signature getSignature(int userid, boolean current) {
        Date d = new Date();
        int y = d.getYear() + 1900;
        int m = d.getMonth() + 1;
        if (!current) {
            m++;
            if (m > 12) {
                m = 1;
                y++;
            }
        }
        String date = y + "/" + m;

        Cursor c = database.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_TICKETS + ", " + SQLiteHelper.TABLE_SIGNATURES +
                " WHERE " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_ID_TICKET + " = " + SQLiteHelper.TABLE_SIGNATURES + "." + SQLiteHelper.COLUMN_ID_TICKET2 +
                " AND " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_ID_USER2 + " = " + userid +
                " AND " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_DETAILS + " = '" + date + "'", null);
        Signature s = null;

        if (c.moveToLast()) {
            ArrayList<Validation> validacoes = new ArrayList<Validation>();
            Cursor cursor2 = database.query(SQLiteHelper.TABLE_VALIDATIONS, null, SQLiteHelper.COLUMN_IDTICKET + " =?", new String[]{String.valueOf(c.getInt(0))}, null, null, SQLiteHelper.COLUMN_IDVALIDATION + " DESC");
            cursor2.moveToFirst();
            while (!cursor2.isAfterLast()) {
                validacoes.add(new Validation(cursor2.getInt(2), cursor2.getInt(3), cursor2.getString(5), cursor2.getInt(4)));
                cursor2.moveToNext();
            }
            // Make sure to close the cursor
            cursor2.close();
            s = cursorToSignature(c, validacoes);
        }
        c.close();
        return s;
    }

    public Signature getActiveSignature(int userid) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_TICKETS + ", " + SQLiteHelper.TABLE_SIGNATURES +
                " WHERE " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_ID_TICKET + " = " + SQLiteHelper.TABLE_SIGNATURES + "." + SQLiteHelper.COLUMN_ID_TICKET2 +
                " AND " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_ID_USER2 + " = " + userid +
                " AND " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_STATUS + " = " + 1, null);

        Signature ticket = null;
        if (cursor.moveToFirst()) {
            ArrayList<Validation> validacoes = new ArrayList<Validation>();
            Cursor cursor2 = database.query(SQLiteHelper.TABLE_VALIDATIONS, null, SQLiteHelper.COLUMN_IDTICKET + " =?", new String[]{String.valueOf(cursor.getInt(0))}, null, null, SQLiteHelper.COLUMN_IDVALIDATION + " DESC");
            cursor2.moveToFirst();
            while (!cursor2.isAfterLast()) {
                validacoes.add(new Validation(cursor2.getInt(2), cursor2.getInt(3), cursor2.getString(5), cursor2.getInt(4)));
                cursor2.moveToNext();
            }
            // Make sure to close the cursor
            cursor2.close();
            ticket = cursorToSignature(cursor, validacoes);
        }

        // Make sure to close the cursor
        cursor.close();
        return ticket;
    }

    private List<Zone> getZonesByCursor(Cursor cursor) {
        List<Zone> zones = new ArrayList<Zone>();

        Cursor c = database.rawQuery(
                "SELECT * FROM " + SQLiteHelper.TABLE_ZONES +
                        " WHERE EXISTS (SELECT * FROM " + SQLiteHelper.TABLE_SIGNATURES_ZONES +
                        " WHERE " + SQLiteHelper.TABLE_SIGNATURES_ZONES + "." + SQLiteHelper.COLUMN_IDZONE + " = " + SQLiteHelper.TABLE_ZONES + "." + SQLiteHelper.COLUMN_ID_ZONE +
                        " AND EXISTS (SELECT * FROM " + SQLiteHelper.TABLE_SIGNATURES +
                        " WHERE " + SQLiteHelper.TABLE_SIGNATURES_ZONES + "." + SQLiteHelper.COLUMN_IDSIGNATURE + " = " + SQLiteHelper.TABLE_SIGNATURES + "." + SQLiteHelper.COLUMN_ID_TICKET2 +
                        " AND " + SQLiteHelper.TABLE_SIGNATURES + "." + SQLiteHelper.COLUMN_ID_TICKET2 + " = " + cursor.getInt(0) +
                        "))"
                , null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            zones.add(new Zone(c.getInt(0), c.getString(1)));
            c.moveToNext();
        }
        // Make sure to close the cursor
        c.close();
        return zones;
    }

    private Signature cursorToSignature(Cursor cursor, ArrayList<Validation> validacoes) {
        List<Zone> zones = getZonesByCursor(cursor);

        return new Signature(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getInt(8), cursor.getInt(9), zones, validacoes, cursor.getString(6));
    }
}
