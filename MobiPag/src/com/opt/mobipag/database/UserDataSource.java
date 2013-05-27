package com.opt.mobipag.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.opt.mobipag.data.User;
import com.opt.mobipag.data.Utils;

import java.security.NoSuchAlgorithmException;

public class UserDataSource {
    // Database fields
    private SQLiteDatabase database;
    private final SQLiteHelper dbHelper;
    private final String[] allColumns = {
            SQLiteHelper.COLUMN_ID_USER,
            SQLiteHelper.COLUMN_EMAIL,
            SQLiteHelper.COLUMN_PASSWORD,
            SQLiteHelper.COLUMN_PIN,
            SQLiteHelper.COLUMN_NAME,
            SQLiteHelper.COLUMN_ADDRESS,
            SQLiteHelper.COLUMN_MOBILE,
            SQLiteHelper.COLUMN_NIF,
            SQLiteHelper.COLUMN_DOB,
            SQLiteHelper.COLUMN_BALANCE,
            SQLiteHelper.COLUMN_MAXAMOUNT,
            SQLiteHelper.COLUMN_PRIVATE,
            SQLiteHelper.COLUMN_PUBLIC};

    public UserDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createUser(String email, String password, String pin, String name, String address, String mobile, String nif, String dob, double balance, int maxamount) throws NoSuchAlgorithmException {
        String[] key = Utils.RSA();

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_EMAIL, email);
        values.put(SQLiteHelper.COLUMN_PASSWORD, password);
        values.put(SQLiteHelper.COLUMN_PIN, pin);
        values.put(SQLiteHelper.COLUMN_NAME, name);
        values.put(SQLiteHelper.COLUMN_ADDRESS, address);
        values.put(SQLiteHelper.COLUMN_MOBILE, mobile);
        values.put(SQLiteHelper.COLUMN_NIF, nif);
        values.put(SQLiteHelper.COLUMN_DOB, dob);
        values.put(SQLiteHelper.COLUMN_BALANCE, balance);
        values.put(SQLiteHelper.COLUMN_MAXAMOUNT, maxamount);
        values.put(SQLiteHelper.COLUMN_PRIVATE, key[0]);
        values.put(SQLiteHelper.COLUMN_PUBLIC, key[1]);
        long insertId = database.insert(SQLiteHelper.TABLE_USERS, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_USERS,
                allColumns, SQLiteHelper.COLUMN_ID_USER + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public void UpdateUser(String email, String password, String pin, String name, String address, String mobile, String nif, String dob, String max) {
        ContentValues values = new ContentValues();
        if (password != null)
            values.put(SQLiteHelper.COLUMN_PASSWORD, password);
        if (pin != null)
            values.put(SQLiteHelper.COLUMN_PIN, pin);
        if (name != null)
            values.put(SQLiteHelper.COLUMN_NAME, name);
        if (address != null)
            values.put(SQLiteHelper.COLUMN_ADDRESS, address);
        if (mobile != null)
            values.put(SQLiteHelper.COLUMN_MOBILE, mobile);
        if (nif != null)
            values.put(SQLiteHelper.COLUMN_NIF, nif);
        if (dob != null)
            values.put(SQLiteHelper.COLUMN_DOB, dob);
        if (max != null)
            values.put(SQLiteHelper.COLUMN_MAXAMOUNT, max);
        database.update(SQLiteHelper.TABLE_USERS, values, SQLiteHelper.COLUMN_EMAIL + " =?", new String[]{email});
    }

    public void UpdateBalance(String email, double amount) {
        User u = getUserByEmail(email);
        double b = u.getBalance() - amount;
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_BALANCE, b);

        database.update(SQLiteHelper.TABLE_USERS, values, SQLiteHelper.COLUMN_EMAIL + " =?", new String[]{email});
    }

    public void setBalance(String email, double amount) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_BALANCE, amount);

        database.update(SQLiteHelper.TABLE_USERS, values, SQLiteHelper.COLUMN_EMAIL + " =?", new String[]{email});
    }

    public User getUserByEmail(String email) {
        Cursor c = database.query(SQLiteHelper.TABLE_USERS, allColumns, SQLiteHelper.COLUMN_EMAIL + " =?", new String[]{email}, null, null, null);
        User user = null;
        if (c.moveToFirst())
            user = cursorToUser(c);
        c.close();
        return user;
    }

    private User cursorToUser(Cursor cursor) {
        return new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7), cursor.getString(8), cursor.getFloat(9), cursor.getInt(10), cursor.getString(11), cursor.getString(12));
    }
} 
