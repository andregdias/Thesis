package com.opt.mobipag.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.opt.mobipag.data.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryDataSource {
    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private final String[] allColumns = {
            SQLiteHelper.COLUMN_ID_HIST,
            SQLiteHelper.COLUMN_ID_USER3,
            SQLiteHelper.COLUMN_DATE,
            SQLiteHelper.COLUMN_AMOUNT,
            SQLiteHelper.COLUMN_DETAILS2};

    public HistoryDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createHist(int userid, String date, double amount, String details) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_ID_USER3, userid);
        values.put(SQLiteHelper.COLUMN_DATE, date);
        values.put(SQLiteHelper.COLUMN_AMOUNT, amount);
        values.put(SQLiteHelper.COLUMN_DETAILS2, details);

        long insertId = database.insert(SQLiteHelper.TABLE_HISTORY, null, values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_HISTORY, allColumns, SQLiteHelper.COLUMN_ID_HIST + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        cursor.close();
    }

    public void deleteAll() {
        database.delete(SQLiteHelper.TABLE_HISTORY, null, null);
    }

    public List<History> getHistory(int userid) {
        List<History> hists = new ArrayList<History>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_HISTORY, allColumns, SQLiteHelper.COLUMN_ID_USER3 + " = " + userid, null, null, null, SQLiteHelper.COLUMN_ID_HIST + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            History hist = cursorToHist(cursor);
            hists.add(hist);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return hists;
    }

    private History cursorToHist(Cursor cursor) {
        return new History(cursor.getString(2), cursor.getDouble(3), cursor.getString(4));
    }
} 
