package com.opt.mobipag.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.opt.mobipag.data.Zone;

import java.util.ArrayList;
import java.util.List;

public class ZoneDataSource {
    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private final String[] allColumns = {
            SQLiteHelper.COLUMN_ID_ZONE,
            SQLiteHelper.COLUMN_DESCRIPTOR};

    public ZoneDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Zone getZoneByDescriptor(String descriptor) {
        Cursor c = database.query(SQLiteHelper.TABLE_ZONES, allColumns, SQLiteHelper.COLUMN_DESCRIPTOR + " =?", new String[]{descriptor}, null, null, null);
        Zone z = null;
        if (c.moveToFirst())
            z = cursorToZone(c);
        c.close();
        return z;
    }

    List<Zone> getAllZones() {
        List<Zone> zones = new ArrayList<Zone>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_ZONES, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Zone zone = cursorToZone(cursor);
            zones.add(zone);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return zones;
    }

    public List<String> getAllZoneDescriptors() {
        List<Zone> zones = getAllZones();
        List<String> zone_desc = new ArrayList<String>();

        for (Zone z : zones)
            zone_desc.add(z.getDescritor());

        return zone_desc;
    }

    private Zone cursorToZone(Cursor cursor) {
        return new Zone(cursor.getInt(0), cursor.getString(1));
    }
} 