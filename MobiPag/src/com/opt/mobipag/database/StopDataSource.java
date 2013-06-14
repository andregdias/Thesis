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
            SQLiteHelper.COLUMN_COORDY,
            SQLiteHelper.COLUMN_HISTORY};

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
        if (s != null){
            UpdateStop(codsms);
            return s.getId();
        }

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_STOPNAME, nome);
        values.put(SQLiteHelper.COLUMN_CODSMS, codsms);
        values.put(SQLiteHelper.COLUMN_OPERATOR, operador);
        values.put(SQLiteHelper.COLUMN_COORDX, coordx);
        values.put(SQLiteHelper.COLUMN_COORDY, coordy);
        long id = database.insert(SQLiteHelper.TABLE_STOPS, null, values);
        UpdateStop(codsms);
        return id;
    }

    public Stop getStopByCodsms(String codsms) {
        Cursor c = database.query(SQLiteHelper.TABLE_STOPS, allColumns, SQLiteHelper.COLUMN_CODSMS + " =?", new String[]{codsms}, null, null, null);
        Stop s = null;
        if (c.moveToFirst())
            s = cursorToStop(c);
        c.close();
        return s;
    }

    public Stop getLastStop() {
        Cursor c = database.query(SQLiteHelper.TABLE_STOPS, allColumns, SQLiteHelper.COLUMN_HISTORY + " =?", new String[]{"1"}, null, null, null);
        Stop s = null;
        if (c.moveToFirst())
            s = cursorToStop(c);
        c.close();
        return s;
    }

    public Stop getSecondLastStop() {
        Cursor c = database.query(SQLiteHelper.TABLE_STOPS, allColumns, SQLiteHelper.COLUMN_HISTORY + " =?", new String[]{"2"}, null, null, null);
        Stop s = null;
        if (c.moveToFirst())
            s = cursorToStop(c);
        c.close();
        return s;
    }

    public void UpdateStop(String codsms) {
        Stop last = getLastStop();
        Stop second = getSecondLastStop();
        if(last!= null && last.getCodsms().equals(codsms)){
            //DO NOTHING
        }
        else if(second!=null && second.getCodsms().equals(codsms)){
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.COLUMN_HISTORY, 1);
            database.update(SQLiteHelper.TABLE_STOPS, values, SQLiteHelper.COLUMN_CODSMS + " =?", new String[]{codsms});

            if(last!=null){
                ContentValues values2 = new ContentValues();
                values2.put(SQLiteHelper.COLUMN_HISTORY, 2);
                database.update(SQLiteHelper.TABLE_STOPS, values2, SQLiteHelper.COLUMN_CODSMS + " =?", new String[]{last.getCodsms()});
            }
        }
        else{
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.COLUMN_HISTORY, 1);
            database.update(SQLiteHelper.TABLE_STOPS, values, SQLiteHelper.COLUMN_CODSMS + " =?", new String[]{codsms});

            if(last!=null){
                ContentValues values2 = new ContentValues();
                values2.put(SQLiteHelper.COLUMN_HISTORY, 2);
                database.update(SQLiteHelper.TABLE_STOPS, values2, SQLiteHelper.COLUMN_CODSMS + " =?", new String[]{last.getCodsms()});
            }
            if(second!=null){
                ContentValues values3 = new ContentValues();
                values3.put(SQLiteHelper.COLUMN_HISTORY, 0);
                database.update(SQLiteHelper.TABLE_STOPS, values3, SQLiteHelper.COLUMN_CODSMS + " =?", new String[]{second.getCodsms()});
            }
        }

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
