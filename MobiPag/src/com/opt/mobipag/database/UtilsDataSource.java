package com.opt.mobipag.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class UtilsDataSource {
    public final int PRICE_OCCASIONAL = 0;
    public final int PRICE_ANDANTE24 = 1;
    public final int PRICE_SIGNATURE = 2;
    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private final String[] allColumns = {
            SQLiteHelper.COLUMN_TYPE,
            SQLiteHelper.COLUMN_PRICE_OCCASIONAL,
            SQLiteHelper.COLUMN_PRICE_ANDANTE24,
            SQLiteHelper.COLUMN_PRICE_SIGNATURE,
            SQLiteHelper.COLUMN_NUM_ZONES,
            SQLiteHelper.COLUMN_TRAVEL_TIME};

    public UtilsDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void deleteAll() {
        database.delete(SQLiteHelper.TABLE_PRICES, null, null);
    }

    public void insertData(String type, double occ, double a24, double sig, int numZones, int time) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TYPE, type);
        values.put(SQLiteHelper.COLUMN_PRICE_OCCASIONAL, occ);
        values.put(SQLiteHelper.COLUMN_PRICE_ANDANTE24, a24);
        values.put(SQLiteHelper.COLUMN_PRICE_SIGNATURE, sig);
        values.put(SQLiteHelper.COLUMN_NUM_ZONES, numZones);
        values.put(SQLiteHelper.COLUMN_TRAVEL_TIME, time);

        database.insert(SQLiteHelper.TABLE_PRICES, null, values);
    }

    public List<String> getTypologies() {
        List<String> types = new ArrayList<String>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_PRICES, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            types.add(cursor.getString(0));
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return types;
    }

    public int getNumZonesByTypology(String typology) {
        Cursor c = database.query(SQLiteHelper.TABLE_PRICES, allColumns, SQLiteHelper.COLUMN_TYPE + " =?", new String[]{typology}, null, null, null);
        int numzones = 0;
        if (c.moveToFirst())
            numzones = c.getInt(4);
        c.close();
        return numzones;
    }

    public int getTravelTimeByTypology(String typology) {
        Cursor c = database.query(SQLiteHelper.TABLE_PRICES, allColumns, SQLiteHelper.COLUMN_TYPE + " =?", new String[]{typology}, null, null, null);
        int traveltime = 0;
        if (c.moveToFirst())
            traveltime = c.getInt(5);
        c.close();
        return traveltime;
    }

    double[] getPricesByTypology(String typology) {
        Cursor c = database.query(SQLiteHelper.TABLE_PRICES, allColumns, SQLiteHelper.COLUMN_TYPE + " =?", new String[]{typology}, null, null, null);
        double[] prices = null;
        if (c.moveToFirst())
            prices = cursorToPrice(c);
        c.close();
        return prices;
    }

    public double getPriceByTypologyAndType(String typology, int type) {
        double[] prices = getPricesByTypology(typology);
        switch (type) {
            case 0:
                return prices[0];
            case 1:
                return prices[1];
            case 2:
                return prices[2];
            default:
                return 0;
        }
    }

    private double[] cursorToPrice(Cursor cursor) {
        double[] prices = new double[3];
        prices[0] = cursor.getDouble(1);
        prices[1] = cursor.getDouble(2);
        prices[2] = cursor.getDouble(3);
        return prices;
    }
} 
