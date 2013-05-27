package com.opt.mobipag.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.opt.mobipag.data.Occasional;
import com.opt.mobipag.data.Validation;

import java.util.ArrayList;
import java.util.List;

public class OccasionalDataSource {
    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;

    public OccasionalDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createOccasional(String details, double price, int status, int numzonas, int tempoviagem, int iduser) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_DETAILS, details);
        values.put(SQLiteHelper.COLUMN_PRICE, price);
        values.put(SQLiteHelper.COLUMN_STATUS, status);
        values.put(SQLiteHelper.COLUMN_ID_USER2, iduser);

        long insertId = database.insert(SQLiteHelper.TABLE_TICKETS, SQLiteHelper.COLUMN_SECURITY, values);

        ContentValues values2 = new ContentValues();
        values2.put(SQLiteHelper.COLUMN_NUMZONES, numzonas);
        values2.put(SQLiteHelper.COLUMN_OCCASIONAL_TRAVELTIME, tempoviagem);
        values2.put(SQLiteHelper.COLUMN_ID_TICKET2, insertId);

        database.insert(SQLiteHelper.TABLE_OCCASIONALS, null, values2);

        return insertId;
    }

    public List<Occasional> getAllAvailableOccasionals(int userid) {
        List<Occasional> occasionals = new ArrayList<Occasional>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_TICKETS + ", " + SQLiteHelper.TABLE_OCCASIONALS +
                " WHERE " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_ID_TICKET + " = " + SQLiteHelper.TABLE_OCCASIONALS + "." + SQLiteHelper.COLUMN_ID_TICKET2 +
                " AND " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_ID_USER2 + " = " + userid +
                " AND " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_STATUS + " = " + 0, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Occasional ticket = cursorToOccasional(cursor, new ArrayList<Validation>());
            occasionals.add(ticket);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return occasionals;
    }

    public Occasional getActiveOccasional(int userid) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_TICKETS + ", " + SQLiteHelper.TABLE_OCCASIONALS +
                " WHERE " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_ID_TICKET + " = " + SQLiteHelper.TABLE_OCCASIONALS + "." + SQLiteHelper.COLUMN_ID_TICKET2 +
                " AND " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_ID_USER2 + " = " + userid +
                " AND " + SQLiteHelper.TABLE_TICKETS + "." + SQLiteHelper.COLUMN_STATUS + " = " + 1, null);

        Occasional occasional = null;
        ArrayList<Validation> validacoes = new ArrayList<Validation>();

        if (cursor.moveToFirst()) {
            Cursor cursor2 = database.query(SQLiteHelper.TABLE_VALIDATIONS, null, SQLiteHelper.COLUMN_IDTICKET + " =?", new String[]{String.valueOf(cursor.getInt(0))}, null, null, SQLiteHelper.COLUMN_IDVALIDATION + " DESC");
            cursor2.moveToFirst();
            while (!cursor2.isAfterLast()) {
                validacoes.add(new Validation(cursor2.getInt(2), cursor2.getInt(3), cursor2.getString(5), cursor2.getInt(4)));
                cursor2.moveToNext();
            }
            // Make sure to close the cursor
            cursor2.close();

            occasional = cursorToOccasional(cursor, validacoes);
        }

        // Make sure to close the cursor
        cursor.close();
        return occasional;
    }

    private Occasional cursorToOccasional(Cursor cursor, ArrayList<Validation> validacoes) {
        return new Occasional(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getInt(8), cursor.getInt(9), validacoes, cursor.getString(6));
    }
}
