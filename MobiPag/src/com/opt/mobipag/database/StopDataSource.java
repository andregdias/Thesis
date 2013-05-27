package com.opt.mobipag.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.opt.mobipag.data.Stop;

public class StopDataSource {
    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private final String[] allColumns = {
            SQLiteHelper.COLUMN_ID_STOP,
            SQLiteHelper.COLUMN_STOPNAME,
            SQLiteHelper.COLUMN_CODSMS,
            SQLiteHelper.COLUMN_OPERATOR,
            SQLiteHelper.COLUMN_COORDX,
            SQLiteHelper.COLUMN_COORDY};

    public StopDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createStop(String nome, String codsms, String operador, Double coordx, Double coordy) {
        Stop s = getStopByCodsms(codsms);
        if (s != null)
            return s.getId();

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_STOPNAME, nome);
        values.put(SQLiteHelper.COLUMN_CODSMS, codsms);
        values.put(SQLiteHelper.COLUMN_OPERATOR, operador);
        values.put(SQLiteHelper.COLUMN_COORDX, coordx);
        values.put(SQLiteHelper.COLUMN_COORDY, coordy);

        return database.insert(SQLiteHelper.TABLE_STOPS, null, values);
    }

    public Stop getStopByCodsms(String codsms) {
        Cursor c = database.query(SQLiteHelper.TABLE_STOPS, allColumns, SQLiteHelper.COLUMN_CODSMS + " =?", new String[]{codsms}, null, null, null);
        Stop s = null;
        if (c.moveToFirst())
            s = cursorToStop(c);
        c.close();
        return s;
    }

    public Stop getStopById(int id) {
        Cursor c = database.query(SQLiteHelper.TABLE_STOPS, allColumns, SQLiteHelper.COLUMN_ID_STOP + " =?", new String[]{String.valueOf(id)}, null, null, null);
        Stop s = null;
        if (c.moveToFirst())
            s = cursorToStop(c);
        c.close();
        return s;
    }

    private Stop cursorToStop(Cursor cursor) {
        return new Stop(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5));
    }
} 
