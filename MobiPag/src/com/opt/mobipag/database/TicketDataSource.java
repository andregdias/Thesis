package com.opt.mobipag.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.opt.mobipag.data.Utils;

public class TicketDataSource {
    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;

    public TicketDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addValidation(int id, long sid, long lid, int seqId, String date) {

        ContentValues values = new ContentValues();
        Cursor cursor = database.query(SQLiteHelper.TABLE_TICKETS, null, SQLiteHelper.COLUMN_ID_TICKET + " =?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst() && cursor.isNull(6)) {
            values.put(SQLiteHelper.COLUMN_FIRST_VALIDATION, date);
            database.update(SQLiteHelper.TABLE_TICKETS, values, SQLiteHelper.COLUMN_ID_TICKET + " = \"" + id + "\"", null);
        }
        cursor.close();

        ContentValues values3 = new ContentValues();
        values3.put(SQLiteHelper.COLUMN_IDTICKET, id);
        values3.put(SQLiteHelper.COLUMN_IDSTOP2, sid);
        values3.put(SQLiteHelper.COLUMN_IDLINE, lid);
        values3.put(SQLiteHelper.COLUMN_SEQ_ID, seqId);
        values3.put(SQLiteHelper.COLUMN_VALIDATIONDATE, date);
        database.insert(SQLiteHelper.TABLE_VALIDATIONS, null, values3);

        Cursor cursor2 = database.query(SQLiteHelper.TABLE_SIGNATURES, null, SQLiteHelper.COLUMN_ID_TICKET2 + " =?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor2.moveToFirst()) {
            values.put(SQLiteHelper.COLUMN_FIRST_VALIDATION, date);
            database.update(SQLiteHelper.TABLE_TICKETS, values, SQLiteHelper.COLUMN_ID_TICKET + " = \"" + id + "\"", null);

            ContentValues values2 = new ContentValues();
            values2.put(SQLiteHelper.COLUMN_NUMVALID, cursor2.getInt(0) + 1);
            database.update(SQLiteHelper.TABLE_SIGNATURES, values2, SQLiteHelper.COLUMN_ID_TICKET2 + " = \"" + id + "\"", null);
        }
        cursor2.close();
    }

    public void deleteAll() {
        Cursor cursor = database.query(SQLiteHelper.TABLE_TICKETS, null, SQLiteHelper.COLUMN_STATUS + " <>?", new String[]{String.valueOf(1)}, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            database.delete(SQLiteHelper.TABLE_VALIDATIONS, SQLiteHelper.COLUMN_IDTICKET + " = \"" + id + "\"", null);
            database.delete(SQLiteHelper.TABLE_OCCASIONALS, SQLiteHelper.COLUMN_ID_TICKET2 + " = \"" + id + "\"", null);
            database.delete(SQLiteHelper.TABLE_SIGNATURES, SQLiteHelper.COLUMN_ID_TICKET2 + " = \"" + id + "\"", null);
            database.delete(SQLiteHelper.TABLE_TICKETS, SQLiteHelper.COLUMN_ID_TICKET + " = \"" + id + "\"", null);
        }
        cursor.close();
    }

    public void changeTicketStatusById(int id, int status, long sid, long lid, int seqId) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_STATUS, status);

        Cursor cursor = database.query(SQLiteHelper.TABLE_TICKETS, null, SQLiteHelper.COLUMN_ID_TICKET + " =?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst() && cursor.getInt(3) == 0) {
            values.put(SQLiteHelper.COLUMN_FIRST_VALIDATION, Utils.currentDate());
        }
        cursor.close();


        if (status == 1) {
            ContentValues values2 = new ContentValues();
            values2.put(SQLiteHelper.COLUMN_STATUS, 2);
            database.update(SQLiteHelper.TABLE_TICKETS, values2, SQLiteHelper.COLUMN_STATUS + " =?", new String[]{String.valueOf(1)});

            ContentValues values3 = new ContentValues();
            values3.put(SQLiteHelper.COLUMN_IDTICKET, id);
            values3.put(SQLiteHelper.COLUMN_IDSTOP2, sid);
            values3.put(SQLiteHelper.COLUMN_IDLINE, lid);
            values3.put(SQLiteHelper.COLUMN_SEQ_ID, seqId);
            values3.put(SQLiteHelper.COLUMN_VALIDATIONDATE, Utils.currentDate());

            database.insert(SQLiteHelper.TABLE_VALIDATIONS, null, values3);
        } else if (status == 3) {
            values.put(SQLiteHelper.COLUMN_STATUS, 1);
            ContentValues values2 = new ContentValues();
            values2.put(SQLiteHelper.COLUMN_STATUS, 2);
            database.update(SQLiteHelper.TABLE_TICKETS, values2, SQLiteHelper.COLUMN_STATUS + " =?", new String[]{String.valueOf(1)});
        }


        database.update(SQLiteHelper.TABLE_TICKETS, values, SQLiteHelper.COLUMN_ID_TICKET + " = \"" + id + "\"", null);

        Cursor cursor2 = database.query(SQLiteHelper.TABLE_SIGNATURES, null, SQLiteHelper.COLUMN_ID_TICKET2 + " =?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor2.moveToFirst()) {
            values.put(SQLiteHelper.COLUMN_FIRST_VALIDATION, Utils.currentDate());
            database.update(SQLiteHelper.TABLE_TICKETS, values, SQLiteHelper.COLUMN_ID_TICKET + " = \"" + id + "\"", null);
            ContentValues values2 = new ContentValues();
            values2.put(SQLiteHelper.COLUMN_NUMVALID, cursor2.getInt(0) + 1);
            database.update(SQLiteHelper.TABLE_SIGNATURES, values2, SQLiteHelper.COLUMN_ID_TICKET2 + " = \"" + id + "\"", null);
        }
        cursor2.close();
    }
}
