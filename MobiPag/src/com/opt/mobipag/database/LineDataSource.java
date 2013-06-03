package com.opt.mobipag.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.opt.mobipag.data.Line;

public class LineDataSource {

    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private final String[] allColumns = {SQLiteHelper.COLUMN_ID_LINE,
            SQLiteHelper.COLUMN_LINECODE,
            SQLiteHelper.COLUMN_LINENAME,
            SQLiteHelper.COLUMN_PATHCODE};

    public LineDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createLine(String code, String name, String pathcode) {
        Line l = getLineByPathCode(pathcode);
        if (l != null)
            return l.getId();

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_LINECODE, code);
        values.put(SQLiteHelper.COLUMN_LINENAME, name);
        values.put(SQLiteHelper.COLUMN_PATHCODE, pathcode);

        return database.insert(SQLiteHelper.TABLE_LINES, null, values);

    }

    public Line getLineByPathCode(String descriptor) {
        Cursor c = database.query(SQLiteHelper.TABLE_LINES, allColumns, SQLiteHelper.COLUMN_PATHCODE + " =?", new String[]{descriptor}, null, null, null);
        Line l = null;
        if (c.moveToFirst())
            l = cursorToLine(c);
        c.close();
        return l;
    }

    public Line getLineByDescriptor(String descriptor) {
        Cursor c = database.query(SQLiteHelper.TABLE_LINES, allColumns, SQLiteHelper.COLUMN_LINECODE + " =?", new String[]{descriptor}, null, null, null);
        Line l = null;
        if (c.moveToFirst())
            l = cursorToLine(c);
        c.close();
        return l;
    }

    public Line getLineById(int id) {
        Cursor c = database.query(SQLiteHelper.TABLE_LINES, allColumns, SQLiteHelper.COLUMN_ID_LINE + " =?", new String[]{String.valueOf(id)}, null, null, null);
        Line l = null;
        if (c.moveToFirst())
            l = cursorToLine(c);
        c.close();
        return l;
    }

    private Line cursorToLine(Cursor cursor) {
        return new Line(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
    }
} 
